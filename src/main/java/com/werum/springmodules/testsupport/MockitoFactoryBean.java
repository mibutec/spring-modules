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
package com.werum.springmodules.testsupport;

import static org.mockito.Mockito.mock;

import org.springframework.beans.factory.FactoryBean;

/**
 * Factory class for mocks. Allows using mocks of components to be used in Applicatoin Context,
 * see http://www.jayway.com/2011/11/30/spring-integration-tests-part-i-creating-mock-objects/
 *
 */
public class MockitoFactoryBean<T> implements FactoryBean<T> {

    private T mockToReturn;

    private Class<T> objectType;

    /**
     * Creates a Mockito mock instance of the provided class.
     * @param classToBeMocked The class to be mocked.
     */
    public MockitoFactoryBean(Class<T> classToBeMocked) {
        this(classToBeMocked, mock(classToBeMocked));
    }

    public MockitoFactoryBean(Class<T> objectType, T mock) {
        this.mockToReturn = mock;
        this.objectType = objectType;
    }

    @Override
    public T getObject() throws Exception {
        return mockToReturn;
    }

    @Override
    public Class<?> getObjectType() {
        return objectType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}