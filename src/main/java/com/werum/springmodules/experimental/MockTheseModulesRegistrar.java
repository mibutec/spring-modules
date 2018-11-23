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

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;

import com.werum.springmodules.definition.ModuleDefinition;
import com.werum.springmodules.experimental.MockTheseModulesDependencyResolver.MockTheseComponents;
import com.werum.springmodules.testsupport.MockitoFactoryBean;

/**
 * {@link ImportBeanDefinitionRegistrar} responsible to add all the mocks to {@link ApplicationContext} provided by
 * {@link  MockTheseComponents}
 *
 */
public class MockTheseModulesRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        MockTheseComponents mockThisComponents = AnnotationUtils.synthesizeAnnotation(
                importingClassMetadata.getAnnotationAttributes(MockTheseComponents.class.getName(), false),
                MockTheseComponents.class, null);

        for (Class<?> classFromMockThese : mockThisComponents.value()) {
            ModuleDefinition selector = BeanUtils.instantiateClass(classFromMockThese, ModuleDefinition.class);
            Map<String, Class<?>> classesToMock = new MockConfigurationCreator()
                    .beansFromConfiguration(selector.getComponentConfiguration());
            for (Entry<String, Class<?>> entry : classesToMock.entrySet()) {
                RootBeanDefinition beanDefinition = new RootBeanDefinition(MockitoFactoryBean.class);
                beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(entry.getValue());
                registry.registerBeanDefinition(entry.getKey(), beanDefinition);
            }
        }
    }
}