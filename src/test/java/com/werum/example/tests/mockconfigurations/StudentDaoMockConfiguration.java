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

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;

import com.werum.example.student.dao.StudentDaoModule;
import com.werum.example.student.dao.StudentRepository;
import com.werum.springmodules.definition.ReplacesConfigurationClass;

/**
 * Mock component configuration for {@link StudentDaoModule}
 */
@ReplacesConfigurationClass(StudentDaoModule.class)
public class StudentDaoMockConfiguration {
    @Bean
    public StudentRepository studentRepository() {
        return mock(StudentRepository.class);
    }
}
