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
    public ResponseEntity<Page<JobPosting>> getJobPostings(@RequestParam(name = "page", required = false, defaultValue = "0") int page, @RequestParam(name = "size", required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JobPosting> jobPostings = jobPostingService.getAllJobPostings(pageable);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping(value = "/jobs", params = "titles")
    public ResponseEntity<Page<JobPosting>> getJobPostingsByTitle(@RequestParam(name = "titles") List<String> titles, @RequestParam(name = "page", required = false, defaultValue = "0") int page, @RequestParam(name = "size", required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JobPosting> jobPostings = jobPostingService.getJobPostingsByTitles(titles, pageable);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping(value = "/jobs", params = "companyNames")
    public ResponseEntity<Page<JobPosting>> getJobPostingsByCompanyName(@RequestParam(name = "companyNames") List<String> companyNames, @RequestParam(name = "page", required = false, defaultValue = "0") int page, @RequestParam(name = "size", required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JobPosting> jobPostings = jobPostingService.getJobPostingsByCompanyNames(companyNames, pageable);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping(value = "/jobs", params = {"titles", "companyNames"})
    public ResponseEntity<Page<JobPosting>> getJobPostingsByTitleAndCompanyName(@RequestParam(name = "titles") List<String> titles, @RequestParam(name = "companyNames") List<String> companyNames, @RequestParam(name = "page", required = false, defaultValue = "0") int page, @RequestParam(name = "size", required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JobPosting> jobPostings = jobPostingService.getJobPostingsByTitlesAndCompanyNames(titles, companyNames, pageable);

        return new ResponseEntity<>(jobPostings, HttpStatus.OK);
    }

    @GetMapping("/filters")
    public ResponseEntity<Map<String, List<String>>> getFilterOptions() {
        return ResponseEntity.ok(jobPostingService.getFilterOptions());
    }
}
