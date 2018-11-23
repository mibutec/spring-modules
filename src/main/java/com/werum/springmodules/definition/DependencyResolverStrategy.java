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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 *
 * Strategy defining how a {@link ModuleDefinition} should be resolved to Configuration classes. The default strategy is to use the configuration
 * defined in the {@link ModuleDefinition}. However, for tests it might be useful to change the Configuration class for a Module, i.e. to use
 * a mock configuration.
 *
 * TODO: get rid of {@link AlternativeComponentConfigurations} handling here and move to its own Strategy
 */
public class DependencyResolverStrategy {
    public Set<Class<?>> resolveDependency(ModuleDefinition bcs, AnnotationMetadata importingClassMetadata,
            TreePrinter<String> treePrinter) {
        Map<Class<? extends ModuleDefinition>, Class<?>> defaultConf2alternativeConfMap = getAlternativeConfigurations(
                importingClassMetadata);

        Set<Class<?>> ret = new HashSet<>();
        Class<?> overriddenConfig = defaultConf2alternativeConfMap.get(bcs.getClass());
        if (overriddenConfig != null) {
            TreePrinter<String> newChild = new TreePrinter<>(overriddenConfig.getSimpleName());
            treePrinter.getChildren().add(newChild);
            ret.add(overriddenConfig);
        } else {
            ret.add(bcs.getComponentConfiguration());
            TreePrinter<String> newChild = new TreePrinter<>(bcs.getComponentConfiguration().getSimpleName());
            treePrinter.getChildren().add(newChild);
            for (Class<? extends ModuleDefinition> child : bcs.getDependendComponents()) {
                ret.addAll(resolveDependency(BeanUtils.instantiateClass(child, ModuleDefinition.class),
                        importingClassMetadata, newChild));
            }
        }

        return ret;
    }

    /**
     * reads from Annotations ({@link AlternativeComponentConfigurations} and {@link ReplacesConfigurationClass})
     * which default configurations should be replaced by which alternative configurations
     *
     * @param importingClassMetadata annotations, can be null
     * @return Map containing the information which Component class (key) should be overridden by which alternative
     * configuration class (value)
     */
    private Map<Class<? extends ModuleDefinition>, Class<?>> getAlternativeConfigurations(
            AnnotationMetadata importingClassMetadata) {
        if (importingClassMetadata == null) {
            return Collections.emptyMap();
        }

        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata
                .getAnnotationAttributes(AlternativeComponentConfigurations.class.getName(), false));

        Map<Class<? extends ModuleDefinition>, Class<?>> ret = new HashMap<>();

        if (annotationAttributes == null) {
            return ret;
        }

        // detect alternative configurations
        Class<?>[] subcomponentsToBeReplaced = annotationAttributes.getClassArray("value");

        // assign alternative configurations
        for (Class<?> alternativeConfiguration : subcomponentsToBeReplaced) {
            ReplacesConfigurationClass annotation = alternativeConfiguration
                    .getAnnotation(ReplacesConfigurationClass.class);
            if (annotation == null) {
                throw new IllegalArgumentException("Alternative Configuration Class [" + alternativeConfiguration
                        + "] requires annotation @ReplacesConfigurationClass to indicate what to replace.");
            }

            Class<? extends ModuleDefinition> defaultConfiguration = annotation.value();
            ret.put(defaultConfiguration, alternativeConfiguration);
        }

        return ret;
    }

    /**
     * Annotation to define spring configuration classes to replace other.
     * Provided classes have to be annotated with <tt>ReplacesConfigurationClass</tt>
     *
     * The annotation will be evaluated by <tt>BaseComponentSelector</tt>
     *
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @DependenciesResolvedBy(DependencyResolverStrategy.class)
    public @interface AlternativeComponentConfigurations {
        Class<?>[] value();
    }
}