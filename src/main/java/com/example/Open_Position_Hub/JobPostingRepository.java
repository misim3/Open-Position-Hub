package com.example.Open_Position_Hub;

import java.util.List;

public interface JobPostingRepository {

    public void saveAll(List<JobPosting> jobPostingList);

    public List<JobPosting> getAllJobPostingList();

    public List<JobPosting> getJobPostingListByTitle(String title);

}
