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
package com.werum.example.student.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.werum.example.student.dao.Student;
import com.werum.example.student.service.StudentService;

/**
 * controller for student domain
 */
@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @RequestMapping(value = "/create", method = RequestMethod.GET /* using get instead of PUT to simplify linking for examples */)
    public String createStudent(@RequestParam(name = "name") String name,
            @RequestParam(name = "actualNc") Float actualNc, @RequestParam(name = "course") String courseName) {
        try {
            studentService.createStudent(name, actualNc, courseName);
            return "OK";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/list")
    public List<Student> list() {
        return studentService.getAll();
    }
}
