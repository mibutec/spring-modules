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
package com.werum.example.course.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.werum.example.course.dao.Course;
import com.werum.example.course.dao.CourseRepository;

/**
 * Example implementation of a simple service
 */
@Component
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseServiceLogger courseServiceLogger;

    public List<Course> getAll() {
        courseServiceLogger.logCourseCall();
        return StreamSupport.stream(courseRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public Course getCourse(String name) {
        return courseRepository.findByName(name);
    }
}
