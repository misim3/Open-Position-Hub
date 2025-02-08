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
    private Long companyId;

    protected JobPostingEntity() {
    }

    public JobPostingEntity(String title, String category, String experienceLevel,
        String employmentType,
        String location, String detailUrl, Long companyId) {
        this.title = title;
        this.category = category;
        this.experienceLevel = experienceLevel;
        this.employmentType = employmentType;
        this.location = location;
        this.detailUrl = detailUrl;
        this.companyId = companyId;
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

    public Long getCompanyId() {
        return companyId;
    }

    @Override
    public String toString() {
        return "JobPostingEntity{" +
            "title='" + title + '\'' +
            ", category='" + category + '\'' +
            ", experienceLevel='" + experienceLevel + '\'' +
            ", employmentType='" + employmentType + '\'' +
            ", location='" + location + '\'' +
            ", detailUrl='" + detailUrl + '\'' +
            ", companyId=" + companyId +
            '}';
    }
}
