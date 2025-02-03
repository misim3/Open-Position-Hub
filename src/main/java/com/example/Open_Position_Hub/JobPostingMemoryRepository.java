package com.example.Open_Position_Hub;

import java.util.ArrayList;
import java.util.List;

public class JobPostingMemoryRepository implements JobPostingRepository {

    private final static List<JobPosting> jobPostings = new ArrayList<JobPosting>();

    @Override
    public void saveAll(List<JobPosting> jobPostingList) {
        jobPostings.addAll(jobPostingList);
    }

    @Override
    public List<JobPosting> getAllJobPostingList() {
        return List.copyOf(jobPostings);
    }

    @Override
    public List<JobPosting> getJobPostingListByTitle(String title) {
        return List.copyOf(jobPostings).stream()
            .filter(jobPosting -> jobPosting.getTitle().equals(title))
            .toList();
    }

}
