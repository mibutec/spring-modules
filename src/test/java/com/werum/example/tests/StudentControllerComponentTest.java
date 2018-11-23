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

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.werum.example.course.controller.CourseController;
import com.werum.example.course.dao.Course;
import com.werum.example.course.dao.CourseRepository;
import com.werum.example.course.service.CourseServiceLogger;
import com.werum.example.student.controller.StudentControllerComponent;
import com.werum.example.student.dao.Student;
import com.werum.example.student.dao.StudentRepository;
import com.werum.example.student.service.StudentService;
import com.werum.example.tests.mockconfigurations.StudentServiceMockConfiguration;
import com.werum.springmodules.definition.DependencyResolverStrategy.AlternativeComponentConfigurations;
import com.werum.springmodules.testsupport.ModuleTest;

/**
 * Testcase testing the controller layer of student domain isolated without any other components via MockMvc
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ModuleTest
@Import(StudentControllerComponent.class)
@AlternativeComponentConfigurations(StudentServiceMockConfiguration.class)
@EnableAutoConfiguration // how to start MockMvc without Jpa?
@AutoConfigureMockMvc
public class StudentControllerComponentTest {
    @Autowired
    private ApplicationContext context;
    @Autowired
    private StudentService studentServiceMock;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        when(studentServiceMock.getAll())
                .thenReturn(Arrays.asList(new Student("1", "Michael", new Course("Example Course", 2.0f))));
    }

    @Test
    public void shouldListStudents() throws Exception {
        mockMvc.perform(get("/student/list")).andExpect(status().isOk())
                .andExpect(content().json("[{'name': 'Michael', course: {name: 'Example Course'}}]"));
    }

    @Test
    public void shouldCreateStudent() throws Exception {
        mockMvc.perform(get("/student/create?name=Michael&actualNc=1.0&course=TestCourse")).andExpect(status().isOk());
        verify(studentServiceMock).createStudent("Michael", 1.0f, "TestCourse");
    }

    @Test
    public void shouldLoadOnlyRelevantPartsIntoApplicationContext() {
        assertBeanDoesNotExist(context, CourseController.class);
        assertBeanDoesNotExist(context, CourseServiceLogger.class);
        assertBeanDoesNotExist(context, StudentRepository.class);
        assertBeanDoesNotExist(context, CourseRepository.class);
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
