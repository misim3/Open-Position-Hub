package com.example.Open_Position_Hub.db;

import jakarta.persistence.Entity;

@Entity
public class CompanyEntity extends BaseEntity {

    private String name;

    protected CompanyEntity() {}

    public CompanyEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Company{" +
            "name='" + name + '\'' +
            '}';
    }
}
