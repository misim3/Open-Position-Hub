package com.example.Open_Position_Hub.db;

import jakarta.persistence.Entity;

@Entity
public class JobPostingEntity extends BaseEntity {

    private String title;
    private String category;
    private String experienceLevel;
    private String employmentType;
    private String location;
    private String detailUrl;

    protected JobPostingEntity() {}

    public JobPostingEntity(String title, String category, String experienceLevel, String employmentType,
        String location, String detailUrl) {
        this.title = title;
        this.category = category;
        this.experienceLevel = experienceLevel;
        this.employmentType = employmentType;
        this.location = location;
        this.detailUrl = detailUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public String getLocation() {
        return location;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    @Override
    public String toString() {
        return "JobPosting{" + "title='" + title + '\'' + ", category='" + category + '\''
            + ", experienceLevel='" + experienceLevel + '\'' + ", employmentType='" + employmentType
            + '\'' + ", location='" + location + '\'' + ", detailUrl='" + detailUrl + '\'' + '}';
    }
}
