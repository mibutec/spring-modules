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
package com.werum.example.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import com.werum.example.course.dao.Course;
import com.werum.example.course.dao.CourseRepository;
import com.werum.example.course.service.CourseService;
import com.werum.example.student.controller.StudentController;
import com.werum.example.student.dao.Student;
import com.werum.example.student.dao.StudentDaoComponent;
import com.werum.example.student.dao.StudentRepository;
import com.werum.example.student.service.StudentService;
import com.werum.springmodules.testsupport.ModuleTest;

/**
 * Testcase testing the dao layer of student domain isolated without any other components
 *
 */
@ModuleTest
@DataJpaTest
@Import(StudentDaoComponent.class)
public class StudentDaoComponentTest {
    @Autowired
    private StudentRepository testee;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ApplicationContext context;

    private Course exampleCourse;

    @BeforeEach
    public void setup() {
        exampleCourse = courseRepository.save(new Course("TestCourse", 2.0f));
    }

    @Test
    public void shouldCreateAndLoadStudents() {
        testee.save(new Student("1", "Michael", exampleCourse));

        Student loaded = testee.findByMatrikelNo("1");
        assertEquals(loaded.getMatrikelNo(), "1");
        assertEquals(loaded.getName(), "Michael");
        assertEquals(loaded.getCourse().getName(), "TestCourse");
    }

    @Test
    public void shouldLoadOnlyRelevantPartsIntoApplicationContext() {
        assertBeanDoesNotExist(context, StudentController.class);
        assertBeanDoesNotExist(context, StudentService.class);
        assertBeanDoesNotExist(context, CourseService.class);
    }

    public static void assertBeanDoesNotExist(ApplicationContext context, Class<?> beanClass) {
        try {
            context.getBean(beanClass);
            fail("expected bean of type" + beanClass.getName() + " not to exist");
        } catch (NoSuchBeanDefinitionException e) {
            // fine
        }
    }
}
