package com.example.Open_Position_Hub;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobPostingController {

    private final JobPostingService jobPostingService;

    public JobPostingController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }

    @GetMapping("/jobs")
    public ResponseEntity<Page<JobPosting>> getFilteredJobPostings(
        @RequestParam(name = "roles", required = false) List<String> roles,
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "size", required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JobPosting> jobPostings;

        if (roles != null && !roles.isEmpty()) {
            jobPostings = jobPostingService.getJobPostingsByTitles(roles, pageable);
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
