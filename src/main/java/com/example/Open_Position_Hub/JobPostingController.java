package com.example.Open_Position_Hub;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobPostingController {

    private JobPostingService jobPostingService;

    public JobPostingController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<JobPosting>> getJobPostings() {

        List<JobPosting> jobPostings = jobPostingService.getAllJobPostings();

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<JobPosting>> getJobPostingsByTitle(@RequestParam String title) {

        List<JobPosting> jobPostings = jobPostingService.getJobPostingsByTitle(title);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<JobPosting>> getJobPostingsByCompanyName(@RequestParam String companyName) {

        List<JobPosting> jobPostings = jobPostingService.getJobPostingsByCompanyName(companyName);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<JobPosting>> getJobPostingsByTitleAndCompanyName(@RequestParam String title, @RequestParam String companyName) {

        List<JobPosting> jobPostings = jobPostingService.getJobPostingsByTitleAndCompanyName(title, companyName);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }
}
