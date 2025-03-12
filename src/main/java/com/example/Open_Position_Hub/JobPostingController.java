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

    @GetMapping("/jobs")
    public ResponseEntity<Page<JobPosting>> getFilteredJobPostings(
        @RequestParam(name = "titles", required = false) List<String> titles,
        @RequestParam(name = "companyNames", required = false) List<String> companyNames,
        @RequestParam(name = "page", required = false, defaultValue = "1") int page,
        @RequestParam(name = "size", required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JobPosting> jobPostings;

        if (titles != null && companyNames != null) {
            jobPostings = jobPostingService.getJobPostingsByTitlesAndCompanyNames(titles, companyNames, pageable);
        } else if (titles != null) {
            jobPostings = jobPostingService.getJobPostingsByTitles(titles, pageable);
        } else if (companyNames != null) {
            jobPostings = jobPostingService.getJobPostingsByCompanyNames(companyNames, pageable);
        } else {
            jobPostings = jobPostingService.getAllJobPostings(pageable);
        }

        return ResponseEntity.ok(jobPostings);
    }

    @GetMapping("/filters")
    public ResponseEntity<Map<String, List<String>>> getFilterOptions() {
        return ResponseEntity.ok(jobPostingService.getFilterOptions());
    }
}
