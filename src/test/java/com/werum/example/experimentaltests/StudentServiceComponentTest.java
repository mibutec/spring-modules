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
package com.werum.example.experimentaltests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import com.werum.example.course.controller.CourseController;
import com.werum.example.course.dao.Course;
import com.werum.example.course.dao.CourseRepository;
import com.werum.example.course.service.CourseService;
import com.werum.example.course.service.CourseServiceComponent;
import com.werum.example.course.service.CourseServiceLogger;
import com.werum.example.student.controller.StudentController;
import com.werum.example.student.dao.Student;
import com.werum.example.student.dao.StudentRepository;
import com.werum.example.student.service.StudentService;
import com.werum.example.student.service.StudentServiceComponent;
import com.werum.springmodules.experimental.MockTheseModulesDependencyResolver.MockTheseComponents;
import com.werum.springmodules.testsupport.ModuleTest;

/**
 * Testcase testing the service layer of student domain combined with dao layer, but mocking other domains.
 * The mock configurations are created automatically out of the existing productive configuration files
 */
@ModuleTest
@DataJpaTest
@Import(StudentServiceComponent.class)
@MockTheseComponents(CourseServiceComponent.class)
public class StudentServiceComponentTest {
    @Autowired
    private StudentService testee;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private StudentRepository studenRepository;
    @Autowired
    private CourseService courseServiceMock;

    private Course exampleCourse;

    @BeforeEach
    public void setup() {
        exampleCourse = courseRepository.save(new Course("TestCourse", 2.0f));
        when(courseServiceMock.getCourse(Mockito.any())).thenReturn(exampleCourse);
    }

    @Test
    public void shouldCreateStudents() {
        testee.createStudent("Michael", 1.0f, "TestCourse");

        Student loaded = studenRepository.findAll().iterator().next();
        assertEquals(loaded.getName(), "Michael");
        assertEquals(loaded.getCourse().getName(), "TestCourse");
    }

    @Test
    public void shouldPreventCreatingStudentWithBadNc() {
        assertThrows(RuntimeException.class, () -> testee.createStudent("Michael", 2.5f, "TestCourse"));
        assertEquals(0, studenRepository.count());
    }

    @Test
    public void shouldLoadOnlyRelevantPartsIntoApplicationContext() {
        assertBeanDoesNotExist(context, StudentController.class);
        assertBeanDoesNotExist(context, CourseController.class);
        assertTrue(MockUtil.isMock(context.getBean(CourseServiceLogger.class)));
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
