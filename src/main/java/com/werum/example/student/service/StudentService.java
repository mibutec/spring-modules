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
package com.werum.example.student.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.werum.example.course.dao.Course;
import com.werum.example.course.service.CourseService;
import com.werum.example.student.dao.Student;
import com.werum.example.student.dao.StudentRepository;

/**
 * Example service for student domain
 */
@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private MatrikelNoGenerator matrikelNoGenerator;
    @Autowired
    private NumerusClaususChecker ncChecker;
    @Autowired
    private CourseService courseService;

    public void createStudent(String studentName, Float actualNc, String courseName) {
        Course course = courseService.getCourse(courseName);
        if (course == null) {
            throw new RuntimeException("course " + courseName + " doesn't exist");
        }

        ncChecker.checkNc(course, actualNc);

        Student student = new Student(matrikelNoGenerator.createMatrikelNo(), studentName, course);
        studentRepository.save(student);
    }

    public List<Student> getAll() {
        return StreamSupport.stream(studentRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }
}
