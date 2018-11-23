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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.werum.springmodules.definition.DependenciesResolvedBy;
import com.werum.springmodules.definition.DependencyResolverStrategy;
import com.werum.springmodules.definition.ModuleDefinition;
import com.werum.springmodules.definition.TreePrinter;

/**
 * {@link DependencyResolverStrategy} responsible to find all the Component configurations for a given component, but cut off mocked components
 * provided by @{@link MockTheseComponents} (those will be provided by {@link MockTheseModulesRegistrar}
 *
 */
public class MockTheseModulesDependencyResolver extends DependencyResolverStrategy {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Set<Class<?>> resolveDependency(ModuleDefinition bcs, AnnotationMetadata importingClassMetadata,
            TreePrinter<String> treePrinter) {
        Set<Class<?>> componentsToMock = Collections.emptySet();
        if (importingClassMetadata != null) {
            AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(
                    importingClassMetadata.getAnnotationAttributes(MockTheseComponents.class.getName(), false));
            componentsToMock = (Set) new HashSet<Class>(Arrays.asList(annotationAttributes.getClassArray("value")));
        }

        Set<Class<?>> resolvedDependency = resolveDependency(bcs, componentsToMock, new HashSet<>(), treePrinter);
        resolvedDependency.add(MockTheseModulesRegistrar.class);
        return resolvedDependency;
    }

    private Set<Class<?>> resolveDependency(ModuleDefinition bcs, Set<Class<?>> componentsToMock,
            Set<Class<?>> alreadyHandled, TreePrinter<String> treePrinter) {
        if (!alreadyHandled.contains(bcs.getClass())) {
            alreadyHandled.add(bcs.getClass());
            Set<Class<?>> ret = new HashSet<>();
            if (componentsToMock.contains(bcs.getClass())) {
                treePrinter.getChildren()
                        .add(new TreePrinter<>(bcs.getComponentConfiguration().getSimpleName() + "Mock"));
            } else {
                TreePrinter<String> newChild = new TreePrinter<>(bcs.getComponentConfiguration().getSimpleName());
                treePrinter.getChildren().add(newChild);
                ret.add(bcs.getComponentConfiguration());
                for (Class<? extends ModuleDefinition> child : bcs.getDependendComponents()) {
                    ret.addAll(resolveDependency(BeanUtils.instantiateClass(child), componentsToMock, alreadyHandled,
                            newChild));
                }
            }

            return ret;
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Annotation defining which components to mock when writing component tests
     *
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @DependenciesResolvedBy(MockTheseModulesDependencyResolver.class)
    public @interface MockTheseComponents {
        Class<? extends ModuleDefinition>[] value();
    }
}
