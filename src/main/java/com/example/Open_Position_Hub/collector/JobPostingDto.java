package com.example.Open_Position_Hub.collector;

import com.example.Open_Position_Hub.db.JobPostingEntity;

public record JobPostingDto(String title, String category, String experienceLevel, String employmentType, String location, String detailUrl, Long companyId) {

    public JobPostingEntity toEntity() {
        return new JobPostingEntity(this.title, this.title, this.category, this.experienceLevel,  this.employmentType, this.location, this.detailUrl, this.companyId);
    }
}
