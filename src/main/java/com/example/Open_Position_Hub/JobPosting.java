package com.example.Open_Position_Hub;

public class JobPosting {

    private String companyName;
    private String title;
    private String experienceLevel;
    private String employmentType;
    private String location;
    private String detailUrl;

    public JobPosting(String companyName, String title, String experienceLevel, String employmentType, String location, String detailUrl) {
        this.companyName = companyName;
        this.title = title;
        this.experienceLevel = experienceLevel;
        this.employmentType = employmentType;
        this.location = location;
        this.detailUrl = detailUrl;
    }
}
