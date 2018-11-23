/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.werum.springmodules.experimental;

import static org.mockito.Mockito.mock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PublicComponentScanAnnotationParser;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.env.MockEnvironment;

/**
 * Helper class creating Mock instances of all Beans that would be instantiated by a Configuration class
 *
 */
public class MockConfigurationCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockConfigurationCreator.class);

    public Map<String, Object> createMocksForConfiguration(Class<?> configurationClass,
            Map<Class<?>, Object> mockOverrides) {
        return beansFromConfiguration(configurationClass).entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, entry -> {
                    Class<?> c = entry.getValue();
                    Object mockBean = getMockFromOverrides(mockOverrides, c);
                    if (mockBean == null) {
                        mockBean = mock(c);
                    }

                    return mockBean;
                }));
    }

    protected Map<String, Class<?>> beansFromConfiguration(Class<?> configurationClass) {
        Map<String, Class<?>> classesFromBeanAnnotation = getClassesFromBeanAnnotation(configurationClass);
        Map<String, Class<?>> repositoryInterfaces = repositories(configurationClass);
        Map<String, Class<?>> classesFromComponentScan = beansFromComponentScan(configurationClass,
                new DefaultBeanNameGenerator(), new DefaultResourceLoader());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found beans to mock for {}", configurationClass.getName());
            LOGGER.debug("=================================================");
            LOGGER.debug("############## from factory methods ##############");
            LOGGER.debug(classesFromBeanAnnotation.entrySet().stream().map(Object::toString)
                    .collect(Collectors.joining("\n")));
            LOGGER.debug("############## from repositories ##############");
            LOGGER.debug(
                    repositoryInterfaces.entrySet().stream().map(Object::toString).collect(Collectors.joining("\n")));
            LOGGER.debug("############## from component scan ##############");
            LOGGER.debug(classesFromComponentScan.entrySet().stream().map(Object::toString)
                    .collect(Collectors.joining("\n")));
        }

        Map<String, Class<?>> ret = new HashMap<>();
        ret.putAll(classesFromBeanAnnotation);
        ret.putAll(classesFromComponentScan);
        ret.putAll(repositoryInterfaces);

        return ret;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Map<String, Class<?>> repositories(Class<?> configurationClass) {
        try {
            Class<?> repositoryClass = Class.forName("org.springframework.data.repository.Repository");
            Class<? extends Annotation> noRepositoryBean = (Class) Class
                    .forName("org.springframework.data.repository.NoRepositoryBean");
            Reflections reflections = new Reflections(configurationClass.getPackage().getName(),
                    new SubTypesScanner(false));
            return reflections.getSubTypesOf(Object.class).stream().filter(repositoryClass::isAssignableFrom)
                    .filter(c -> {
                        try {
                            return !c.isAnnotationPresent(noRepositoryBean);
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    }).collect(Collectors.toMap(Class::getName, c -> c));
        } catch (ClassNotFoundException cnfe) {
            return Collections.emptyMap();
        }
    }

    protected Map<String, Class<?>> beansFromComponentScan(Class<?> configurationClass,
            BeanNameGenerator beanNameGenerator, ResourceLoader resourceLoader) {
        ComponentScan componentScanAnnotation = AnnotatedElementUtils.findMergedAnnotation(configurationClass,
                ComponentScan.class);
        if (componentScanAnnotation == null) {
            return Collections.emptyMap();
        }
        AnnotationAttributes componentScan = AnnotationUtils.getAnnotationAttributes(configurationClass,
                componentScanAnnotation, false, true);

        // We don't want the parser to register found beans. So create a mock to throw registration request away
        BeanDefinitionRegistry registryMock = mock(BeanDefinitionRegistry.class);
        PublicComponentScanAnnotationParser parser = new PublicComponentScanAnnotationParser(new MockEnvironment(),
                resourceLoader, beanNameGenerator, registryMock);
        return parser.parse(componentScan, configurationClass.getName()).stream().collect(Collectors
                .toMap(BeanDefinitionHolder::getBeanName, b -> forNameSafe(b.getBeanDefinition().getBeanClassName())));
    }

    protected Map<String, Class<?>> getClassesFromBeanAnnotation(final Class<?> type) {
        final Map<String, Class<?>> classes = new HashMap<>();
        Class<?> klass = type;
        while (klass != Object.class) {
            final List<Method> allMethods = new ArrayList<>(Arrays.asList(klass.getDeclaredMethods()));
            for (final Method method : allMethods) {
                if (method.isAnnotationPresent(Bean.class)) {
                    Class<?> beanType = method.getReturnType();
                    Bean bean = method.getAnnotation(Bean.class);
                    String[] names = bean.value();
                    if (names.length == 0) {
                        names = bean.name();
                        if (names.length == 0) {
                            names = new String[] {beanType.getName()};
                        }
                    }
                    for (String name : names) {
                        classes.put(name, beanType);
                    }
                }
            }
            klass = klass.getSuperclass();
        }
        return classes;
    }

    private Object getMockFromOverrides(Map<Class<?>, Object> overrides, Class<?> clazz) {
        for (Entry<Class<?>, Object> entry : overrides.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return entry.getValue();
            }
        }

        return null;
    }

    public static class MyClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

        public MyClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
            super(registry, useDefaultFilters);
        }

        @Override
        public Set<BeanDefinitionHolder> doScan(String... basePackages) {
            return super.doScan(basePackages);
        }
    }

    private Class<?> forNameSafe(String className) {
        try {
            return Class.forName(className);
        } catch (Exception e) {
            throw new IllegalStateException("could not lod class " + className);
        }
    }
}
