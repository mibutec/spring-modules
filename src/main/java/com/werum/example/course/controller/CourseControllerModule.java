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
package com.werum.example.course.controller;

import org.springframework.context.annotation.ComponentScan;

import com.werum.example.course.service.CourseServiceModule;
import com.werum.springmodules.definition.ModuleDefinition;
import com.werum.springmodules.definition.ModuleConfiguration;

/**
 * Component definition for controller layer of course domain
 */
public class CourseControllerModule extends ModuleDefinition {

    public CourseControllerModule() {
        super(CourseControllerComponentConfiguration.class, CourseServiceModule.class);
    }

    @ModuleConfiguration
    @ComponentScan
    static class CourseControllerComponentConfiguration {

    }

}
