package com.fastcampus.springkafka.clip04.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;

public class Animal {

    private final String name;

    // validation check 를 위해 constraint 를 지정
    @Max(10)
    private final int age;

    @JsonCreator
    public Animal(@JsonProperty("name") String name,
                  @JsonProperty("age") int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

}
