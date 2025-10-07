package com.example.Open_Position_Hub;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import com.example.Open_Position_Hub.db.JobPostingSearchRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class JobPostingService {

    private final JobPostingSearchRepository jobPostingSearchRepository;
    private final CompanyRepository companyRepository;

    public JobPostingService(CompanyRepository companyRepository,
        JobPostingSearchRepository jobPostingSearchRepository) {
        this.companyRepository = companyRepository;
        this.jobPostingSearchRepository = jobPostingSearchRepository;
    }

    public Page<JobPosting> getAllJobPostings(Pageable pageable) {

        Page<JobPostingEntity> jobPostingEntityList = jobPostingSearchRepository.findAll(pageable);

        List<JobPosting> jobPostings = jobPostingEntityList.get()
            .map(job -> {
                CompanyEntity company = companyRepository.findById(job.getCompanyId())
                    .orElseThrow(() -> new RuntimeException(
                        "Not Found Company By CompanyId while getAllJobPostings."));
                return new JobPosting(
                    company.getName(),
                    job.getTitle(),
                    job.getExperienceLevel(),
                    job.getEmploymentType(),
                    job.getLocation(),
                    buildRedirectUrl(company.getRecruitmentUrl(), job.getDetailUrl())
                );
            })
            .toList();

        return new PageImpl<>(jobPostings, pageable, jobPostingEntityList.getTotalElements());
    }

    public Page<JobPosting> getJobPostingsByTitles(List<String> titles, Pageable pageable) {

        Page<JobPostingEntity> jobPostingEntityList = jobPostingSearchRepository.findByTitles(
            titles,
            pageable);

        List<JobPosting> jobPostings = jobPostingEntityList.get()
            .map(job -> {

                CompanyEntity company = companyRepository.findById(job.getCompanyId())
                    .orElseThrow(() -> new RuntimeException(
                        "Not Found Company By CompanyId while getJobPostingsByTitle."));

                return new JobPosting(
                    company.getName(),
                    job.getTitle(),
                    job.getExperienceLevel(),
                    job.getEmploymentType(),
                    job.getLocation(),
                    buildRedirectUrl(company.getRecruitmentUrl(), job.getDetailUrl())
                );
            })
            .toList();

        return new PageImpl<>(jobPostings, pageable, jobPostingEntityList.getTotalElements());
    }

    public Map<String, List<String>> getFilterOptions() {

        Map<String, List<String>> filters = new HashMap<>();

        filters.put("titles", Arrays.stream(Role.values()).map(Enum::name).toList());

        return filters;
    }

    private String buildRedirectUrl(String recruitmentUrl, String detailUrl) {
        try {
            URI baseUri = new URI(recruitmentUrl);
            String domain = baseUri.getScheme() + "://" + baseUri.getHost();
            return domain + detailUrl;
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid recruitment URL: " + recruitmentUrl);
        }
    }
}
