# Description
spring-modules is a way to declare modules with their dependencies on each other in spring-projects in an expressive way. The focus of spring-modules is providing support for tests on module-level. So there is also build-in support to test your modules in a varity of isolation levels.

# Definition of Modules
A module is divided into two parts
- **Module Configuration**: Provides the @Configuration class to add the beans to ApplicationContext a Module consists of.
- **Dependency Definition**: Describes the dependencies between Modules. A Module-dependency is established if a module contains beans with wiring-dependencies on beans provided by another module. 

Since the focus of spring-modules is testing, the granularity of one module should be "something that can be meaningful tested as a unit". Thinking of one module as one layer of a domain might be a helpful start.

One simple Module might look something like this:

    public class StudentDaoModule extends ModuleDefinition {
        public StudentDaoModule() {
            super(StudentDaoModuleConfiguration.class);
        }
    
        @ModuleConfiguration
        @ComponentScan
        @EnableJpaRepositories
        @EntityScan
        static class StudentDaoModuleConfiguration {
    
        }
    }

`StudentDaoModuleConfiguration` contains all the code to get the dao layer working. In this simple case `StudentDaoModule` is just a wrapper for the Configuration.

    public class StudentServiceModule extends ModuleDefinition {
        public StudentServiceModule() {
            super(StudentServiceModuleConfiguration.class, StudentDaoModule.class, CourseServiceModule.class);
        }
    
        @ModuleConfiguration
        @ComponentScan
        static class StudentServiceModuleConfiguration {
    
        }
    }

In that example `StudentServiceModule` has a dependency to `StudentDaoModule` and `CourseServiceModule`. Those are defined in the constructor of the Module Definition.

# Testing
Writing a ModuleTest allows you to decide on module-level which modules are inside the scope of your test and which modules are outside (so should be mocked).

## MockConfigurations
Inside your testcode you may define a substitute Configuration for your ModuleConfiguration consisting of other bean definition, like mocks, stubs or dummy implementations

    @ReplacesConfigurationClass(StudenDaoModule.class)
    public class StudentDaoMockConfiguration {
        @Bean
        public StudentRepository studentRepository() {
            return mock(StudentRepository.class);
        }
    }

That configuration may be used in your tests as a substitute for the real `StudentServiceModule`, so your test may use the `Repository` mock instead of starting the whole persistence framework.

## ModuleTest
 
    @ModuleTest
    @Import(StudentServiceModule.class)
    @AlternativeModuleConfigurations({CourseServiceMockConfiguration.class, StudentDaoMockConfiguration.class})
    public class StudentServiceModuleTest {
        @Autowired
        private StudentService testee;
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
    }

This test says it will test the `StudentServiceModule`, but use `CourseServiceMockConfiguration` and `StudentDaoMockConfiguration` as substitutes for the real dependencies.

In other cases you might want to test the iteraction of serivce layer and dao layer. This might be done in a test like this

    @ModuleTest
    @DataJpaTest
    @Import(StudentServiceModule.class)
    @AlternativeModuleConfigurations(CourseServiceMockConfiguration.class)
    public class StudentServiceAndDaoModuleTest {
    // ...
 
In the test above test the students service- and dao layer are tested together, but the dependencies to the course domain are still mocked.

## Experimental (even more than the other stuff)
The concept of *MockConfiguration*s forces you write replacements for your existing configuration classes. This might get a cumbersome and type-intensive task. spring-modules comes with support to create MockConfiguration out of your ModuleConfigurations.

`StudentServiceModuleTest` from above could also be written like this

    @ModuleTest
    @Import(StudentServiceModule.class)
    @MockTheseModules({CourseServiceModule.class, StudentDaoModule.class})
    public class StudentServiceModuleTest {
    // ...

This takes the burden from you to write a MockConfiguration for each Module you want to mock

#Example application

Source consist of:
- code to define modules (`src/main/java/com/werum/springmodules/definition`)
- code to write module tests (`src/main/java/com/werum/springmodules/testsupport`)
- experimental code to write module tests (`src/main/java/com/werum/springmodules/experimental`)
- a simple example application (`src/main/java/com/werum/example`)
- example tests (`src/test/java`)