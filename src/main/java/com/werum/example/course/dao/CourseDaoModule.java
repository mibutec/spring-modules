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
package com.werum.example.course.dao;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.werum.springmodules.definition.ModuleDefinition;
import com.werum.springmodules.definition.ModuleConfiguration;

/**
 * Component definition for dao layer of course domain
 */
public class CourseDaoModule extends ModuleDefinition {

    public CourseDaoModule() {
        super(CourseDaoComponentConfiguration.class);
    }

    @ModuleConfiguration
    @ComponentScan
    @EnableJpaRepositories
    @EntityScan
    static class CourseDaoComponentConfiguration {

    }

}
