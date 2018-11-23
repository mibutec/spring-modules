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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import com.werum.example.course.controller.CourseController;
import com.werum.example.course.dao.Course;
import com.werum.example.course.service.CourseService;
import com.werum.example.course.service.CourseServiceLogger;
import com.werum.example.student.controller.StudentController;
import com.werum.example.student.dao.Student;
import com.werum.example.student.dao.StudentRepository;
import com.werum.example.student.service.StudentService;
import com.werum.example.student.service.StudentServiceComponent;
import com.werum.example.tests.mockconfigurations.CourseServiceMockConfiguration;
import com.werum.example.tests.mockconfigurations.StudentDaoMockConfiguration;
import com.werum.springmodules.definition.DependencyResolverStrategy.AlternativeComponentConfigurations;
import com.werum.springmodules.testsupport.ModuleTest;

/**
 * Testcase testing the service layer of student domain isolated from all other components
 */
@ModuleTest
@Import(StudentServiceComponent.class)
@AlternativeComponentConfigurations({CourseServiceMockConfiguration.class, StudentDaoMockConfiguration.class})
public class StudentServiceComponentTest {
    @Autowired
    private StudentService testee;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private StudentRepository studentRepositoryMock;
    @Autowired
    private CourseService courseServiceMock;

    @BeforeEach
    public void setup() {
        when(courseServiceMock.getCourse(Mockito.any())).thenReturn(new Course("TestCourse", 2.0f));
    }

    @Test
    public void shouldCreateStudents() {
        testee.createStudent("Michael", 1.0f, "TestCourse");
        verify(studentRepositoryMock).save(Mockito.any(Student.class));
    }

    @Test
    public void shouldPreventCreatingStudentWithBadNc() {
        assertThrows(RuntimeException.class, () -> testee.createStudent("Michael", 2.5f, "TestCourse"));
        verifyNoMoreInteractions(studentRepositoryMock);
    }

    @Test
    public void shouldLoadOnlyRelevantPartsIntoApplicationContext() {
        assertBeanDoesNotExist(context, StudentController.class);
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
