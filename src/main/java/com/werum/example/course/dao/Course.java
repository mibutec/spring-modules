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
package com.werum.example.course.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Example entity describing a course
 *
 */
@Entity
public class Course {
    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true)
    private String name;

    private Float nc;

    public Course() {
        super();
    }

    public Course(String name, Float nc) {
        super();
        this.name = name;
        this.nc = nc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getNc() {
        return nc;
    }

    public void setNc(Float nc) {
        this.nc = nc;
    }

    public long getId() {
        return id;
    }
}
