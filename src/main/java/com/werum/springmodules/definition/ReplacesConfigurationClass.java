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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;

/**
 * Annotation to mark a spring configuration class as replacement for the default Component configuration of a {@link ModuleDefinition}
 *
 * Usage:
 * @ReplacesConfigurationClass(SomeComponent.class)
 * public class SomeComponentMockConfiguration {
 *   @Bean
 *   public MockitoFactoryBean<CourseService> courseService() {
 *     return new MockitoFactoryBean<>(CourseService.class);
 *   }
 * }
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Configuration
public @interface ReplacesConfigurationClass {
    Class<? extends ModuleDefinition> value();
}
