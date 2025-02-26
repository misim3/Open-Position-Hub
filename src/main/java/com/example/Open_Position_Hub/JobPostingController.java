package com.example.Open_Position_Hub;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<JobPosting>> getJobPostings(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<JobPosting> jobPostings = jobPostingService.getAllJobPostings(pageable);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping("/jobs")
    public ResponseEntity<Page<JobPosting>> getJobPostingsByTitle(@RequestParam String title, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<JobPosting> jobPostings = jobPostingService.getJobPostingsByTitle(title, pageable);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping("/jobs")
    public ResponseEntity<Page<JobPosting>> getJobPostingsByCompanyName(@RequestParam String companyName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<JobPosting> jobPostings = jobPostingService.getJobPostingsByCompanyName(companyName, pageable);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping("/jobs")
    public ResponseEntity<Page<JobPosting>> getJobPostingsByTitleAndCompanyName(@RequestParam String title, @RequestParam String companyName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<JobPosting> jobPostings = jobPostingService.getJobPostingsByTitleAndCompanyName(title, companyName, pageable);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }
}
