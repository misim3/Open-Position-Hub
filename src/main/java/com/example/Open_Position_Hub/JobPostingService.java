package com.example.Open_Position_Hub;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import com.example.Open_Position_Hub.db.JobPostingRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class JobPostingService {

    private JobPostingRepository jobPostingRepository;
    private CompanyRepository companyRepository;

    public JobPostingService(JobPostingRepository jobPostingRepository, CompanyRepository companyRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.companyRepository = companyRepository;
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

    public Page<JobPosting> getAllJobPostings(Pageable pageable) {

        Page<JobPostingEntity> jobPostingEntityList = jobPostingRepository.findAll(pageable);

        List<JobPosting> jobPostings = jobPostingEntityList.get()
            .map(job -> {
                CompanyEntity company = companyRepository.findById(job.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Not Found Company By CompanyId while getAllJobPostings."));
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

    public Page<JobPosting> getJobPostingsByCompanyNames(List<String> companyNames, Pageable pageable) {

        Map<Long, CompanyEntity> companyMap = companyRepository.findByNameIn(companyNames).stream()
            .collect(Collectors.toMap(CompanyEntity::getId, company -> company));

        Page<JobPostingEntity> jobPostingEntityList = jobPostingRepository.findByCompanyIdIn(companyMap.keySet(), pageable);

        List<JobPosting> jobPostings = jobPostingEntityList.get()
            .map(job -> {
                CompanyEntity company = companyMap.get(job.getCompanyId());
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

        Page<JobPostingEntity> jobPostingEntityList = jobPostingRepository.findByTitleIn(titles, pageable);

        List<JobPosting> jobPostings = jobPostingEntityList.get()
            .map(job -> {

                CompanyEntity company = companyRepository.findById(job.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Not Found Company By CompanyId while getJobPostingsByTitle."));

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

    public Page<JobPosting> getJobPostingsByTitlesAndCompanyNames(List<String> titles, List<String> companyNames, Pageable pageable) {

        Map<Long, CompanyEntity> companyMap = companyRepository.findByNameIn(companyNames).stream()
            .collect(Collectors.toMap(CompanyEntity::getId, company -> company));

        Page<JobPostingEntity> jobPostingEntityList = jobPostingRepository.findByTitleInAndCompanyIdIn(titles, companyMap.keySet(), pageable);

        List<JobPosting> jobPostings = jobPostingEntityList.get()
            .map(job -> {
                CompanyEntity company = companyMap.get(job.getCompanyId());
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

        filters.put("titles", jobPostingRepository.findDistinctTitles().stream()
            .sorted()
            .toList()
        );

        filters.put("companyNames", jobPostingRepository.findDistinctCompanyIds().stream()
            .map(id -> companyRepository.findById(id)
                .map(CompanyEntity::getName)
                .orElseThrow(() -> new RuntimeException("Not Found Company By CompanyId while getFilterOptions.")))
            .sorted()
            .toList()
        );

        return filters;
    }
}
