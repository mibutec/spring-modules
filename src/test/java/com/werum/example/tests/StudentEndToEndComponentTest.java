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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import com.werum.example.course.controller.CourseController;
import com.werum.example.course.dao.Course;
import com.werum.example.course.service.CourseServiceLogger;
import com.werum.example.student.controller.StudentControllerComponent;
import com.werum.example.student.dao.Student;
import com.werum.example.student.dao.StudentRepository;
import com.werum.example.tests.mockconfigurations.CourseServiceMockConfiguration;
import com.werum.springmodules.definition.DependencyResolverStrategy.AlternativeComponentConfigurations;
import com.werum.springmodules.testsupport.ModuleTest;

/**
 * Testcase testing all layers of student domain isolated from other domains via MockMvc
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ModuleTest
@Import(StudentControllerComponent.class)
@AlternativeComponentConfigurations(CourseServiceMockConfiguration.class)
@EnableAutoConfiguration // TODO: how to start MockMvc without Jpa?
@Rollback // TODO: why doesn't this work?
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD, methodMode = MethodMode.AFTER_METHOD)
@AutoConfigureMockMvc
public class StudentEndToEndComponentTest {
    @Autowired
    private ApplicationContext context;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        studentRepository.save(new Student("1", "Michael", new Course("TestCourse", 2.0f)));
    }

    @Test
    public void shouldListStudents() throws Exception {
        mockMvc.perform(get("/student/list")).andExpect(status().isOk())
                .andExpect(content().json("[{'name': 'Michael', course: {name: 'TestCourse'}}]"));
    }

    @Test
    public void shouldCreateStudent() throws Exception {
        mockMvc.perform(get("/student/create?name=Michael&actualNc=1.0&course=TestCourse")).andExpect(status().isOk());

        Student student = studentRepository.findAll().iterator().next();
        assertEquals(student.getMatrikelNo(), "1");
        assertEquals(student.getName(), "Michael");
        assertEquals(student.getCourse().getName(), "TestCourse");
    }

    @Test
    public void shouldLoadOnlyRelevantPartsIntoApplicationContext() {
        assertBeanDoesNotExist(context, CourseController.class);
        assertBeanDoesNotExist(context, CourseServiceLogger.class);
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
