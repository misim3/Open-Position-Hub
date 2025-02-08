package com.example.Open_Position_Hub.db;

import jakarta.persistence.Entity;

@Entity
public class CompanyEntity extends BaseEntity {

    private String name;
    private String recruitmentPlatform;
    private String recruitmentUrl;

    protected CompanyEntity() {}

    public CompanyEntity(String name, String recruitmentPlatform, String recruitmentUrl) {
        this.name = name;
        this.recruitmentPlatform = recruitmentPlatform;
        this.recruitmentUrl = recruitmentUrl;
    }

    public String getName() {
        return name;
    }

    public String getRecruitmentPlatform() {
        return recruitmentPlatform;
    }

    public String getRecruitmentUrl() {
        return recruitmentUrl;
    }

    @Override
    public String toString() {
        return "CompanyEntity{" +
            "name='" + name + '\'' +
            ", recruitmentPlatform='" + recruitmentPlatform + '\'' +
            ", recruitmentUrl='" + recruitmentUrl + '\'' +
            '}';
    }
}
