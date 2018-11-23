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
package com.werum.springmodules.definition;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import com.werum.springmodules.definition.DependencyResolverStrategy.AlternativeComponentConfigurations;

/**
 * Abstract Base class for all component context configurations
 *
 * To be able to exchange specific component context configurations each component has
 * to extend this class and define configuration dependencies.
 *
 * Alternative component context configurations can be configured by {@link AlternativeComponentConfigurations}
 * at type level of a test.
 *
 */
public abstract class ModuleDefinition implements ImportSelector {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleDefinition.class);

    private final Class<?> componentConfiguration;

    private final Class<? extends ModuleDefinition>[] dependingOnComponents;

    /**
     * configure dependencies
     */
    @SafeVarargs
    public ModuleDefinition(Class<?> componentConfiguration,
            Class<? extends ModuleDefinition>... dependingOnComponents) {
        this.componentConfiguration = componentConfiguration;
        this.dependingOnComponents = dependingOnComponents;
    }

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        DependencyResolverStrategy drs = BeanUtils.instantiateClass(findDependencyResolver(importingClassMetadata));
        TreePrinter<String> root = new TreePrinter<>(getClass().getSimpleName());
        String[] ret = drs.resolveDependency(this, importingClassMetadata, root).stream().map(Class::getName)
                .toArray(String[]::new);
        LOGGER.info("created the following dependency tree for " + getClass().getSimpleName() + "\n"
                + root.getDependencyView());

        System.out.println(Arrays.asList(ret));
        return ret;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends DependencyResolverStrategy> findDependencyResolver(
            AnnotationMetadata importingClassMetadata) {
        for (String annotationType : importingClassMetadata.getAnnotationTypes()) {
            try {
                Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) Class
                        .forName(annotationType);
                DependenciesResolvedBy dependenciesResolvedByAnnotation = annotationClass
                        .getAnnotation(DependenciesResolvedBy.class);
                if (dependenciesResolvedByAnnotation != null) {
                    return dependenciesResolvedByAnnotation.value();
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }

        }

        return DependencyResolverStrategy.class;
    }

    public Class<?> getComponentConfiguration() {
        return componentConfiguration;
    }

    public Set<Class<? extends ModuleDefinition>> getDependendComponents() {
        return new HashSet<>(Arrays.asList(dependingOnComponents));
    }
}
