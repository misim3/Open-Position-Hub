package com.example.Open_Position_Hub;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping(value = "/jobs", params = "!titles&!companyNames")
    public ResponseEntity<Page<JobPosting>> getJobPostings(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JobPosting> jobPostings = jobPostingService.getAllJobPostings(pageable);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping(value = "/jobs", params = "titles")
    public ResponseEntity<Page<JobPosting>> getJobPostingsByTitle(@RequestParam List<String> titles, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JobPosting> jobPostings = jobPostingService.getJobPostingsByTitles(titles, pageable);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping(value = "/jobs", params = "companyNames")
    public ResponseEntity<Page<JobPosting>> getJobPostingsByCompanyName(@RequestParam List<String> companyNames, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JobPosting> jobPostings = jobPostingService.getJobPostingsByCompanyNames(companyNames, pageable);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping(value = "/jobs", params = {"titles", "companyNames"})
    public ResponseEntity<Page<JobPosting>> getJobPostingsByTitleAndCompanyName(@RequestParam List<String> titles, @RequestParam List<String> companyNames, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JobPosting> jobPostings = jobPostingService.getJobPostingsByTitlesAndCompanyNames(titles, companyNames, pageable);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping("/filters")
    public ResponseEntity<Map<String, List<String>>> getFilterOptions() {
        return ResponseEntity.ok(jobPostingService.getFilterOptions());
    }
}
