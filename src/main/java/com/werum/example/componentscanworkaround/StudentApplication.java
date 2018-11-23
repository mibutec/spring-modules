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
package com.werum.example.componentscanworkaround;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import com.werum.example.course.controller.CourseControllerComponent;
import com.werum.example.course.dao.Course;
import com.werum.example.course.dao.CourseRepository;
import com.werum.example.student.controller.StudentControllerComponent;

/**
 *
 * Main for example application
 * TODO: How can this be moved to base package without magically adding all sub-packages to {@link ApplicationContext}
 *
 */
@SpringBootApplication
@Import({StudentControllerComponent.class, CourseControllerComponent.class})
public class StudentApplication {

    @Autowired
    private CourseRepository courseRepository;

    public static void main(String[] args) {
        SpringApplication.run(StudentApplication.class, args);
    }

    @PostConstruct
    public void init() {
        courseRepository.save(new Course("computerScience", 1.0f));
        courseRepository.save(new Course("psychology", 2.0f));
        courseRepository.save(new Course("medicine", 3.0f));
        courseRepository.save(new Course("businessStudies", 4.0f));
    }
}
