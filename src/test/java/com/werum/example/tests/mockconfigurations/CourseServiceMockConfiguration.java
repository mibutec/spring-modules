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
package com.werum.example.tests.mockconfigurations;

import org.springframework.context.annotation.Bean;

import com.werum.example.course.service.CourseService;
import com.werum.example.course.service.CourseServiceComponent;
import com.werum.springmodules.definition.ReplacesConfigurationClass;
import com.werum.springmodules.testsupport.MockitoFactoryBean;

/**
 * Mock component configuration for {@link CourseServiceComponent}
 */
@ReplacesConfigurationClass(CourseServiceComponent.class)
public class CourseServiceMockConfiguration {
    @Bean
    public MockitoFactoryBean<CourseService> courseService() {
        return new MockitoFactoryBean<>(CourseService.class);
    }
}
