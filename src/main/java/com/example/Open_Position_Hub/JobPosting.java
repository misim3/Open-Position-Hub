package com.example.Open_Position_Hub;

public class JobPosting {

    private String title;
    private String category;
    private String experienceLevel;
    private String employmentType;
    private String location;

    public JobPosting(String title, String category, String experienceLevel, String employmentType, String location) {
        this.title = title;
        this.category = category;
        this.experienceLevel = experienceLevel;
        this.employmentType = employmentType;
        this.location = location;
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
}
