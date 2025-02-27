package com.example.Open_Position_Hub;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import com.example.Open_Position_Hub.db.JobPostingRepository;
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

    public Page<JobPosting> getAllJobPostings(Pageable pageable) {

        Page<JobPostingEntity> jobPostingEntityList = jobPostingRepository.findAll(pageable);

        List<JobPosting> jobPostings = jobPostingEntityList.get()
            .map(job -> {
                String companyName = companyRepository.findById(job.getCompanyId())
                    .map(CompanyEntity::getName)
                    .orElseThrow(() -> new RuntimeException("Not Found Company By CompanyId while getAllJobPostings."));
                return new JobPosting(
                    companyName,
                    job.getTitle(),
                    job.getExperienceLevel(),
                    job.getEmploymentType(),
                    job.getLocation(),
                    job.getDetailUrl()
                );
            })
            .toList();

        return new PageImpl<>(jobPostings, pageable, jobPostingEntityList.getTotalElements());
    }

    public Page<JobPosting> getJobPostingsByCompanyNames(List<String> companyNames, Pageable pageable) {

        Map<Long, String> companyMap = companyRepository.findByNameIn(companyNames).stream()
            .collect(Collectors.toMap(CompanyEntity::getId, CompanyEntity::getName));

        Page<JobPostingEntity> jobPostingEntityList = jobPostingRepository.findByCompanyIdIn(companyMap.keySet(), pageable);

        List<JobPosting> jobPostings = jobPostingEntityList.get()
            .map(job -> new JobPosting(
                companyMap.get(job.getCompanyId()),
                job.getTitle(),
                job.getExperienceLevel(),
                job.getEmploymentType(),
                job.getLocation(),
                job.getDetailUrl()
            ))
            .toList();

        return new PageImpl<>(jobPostings, pageable, jobPostingEntityList.getTotalElements());
    }

    public Page<JobPosting> getJobPostingsByTitles(List<String> titles, Pageable pageable) {

        Page<JobPostingEntity> jobPostingEntityList = jobPostingRepository.findByTitleIn(titles, pageable);

        List<JobPosting> jobPostings = jobPostingEntityList.get()
            .map(job -> {

                String companyName = companyRepository.findById(job.getCompanyId())
                    .map(CompanyEntity::getName)
                    .orElseThrow(() -> new RuntimeException("Not Found Company By CompanyId while getJobPostingsByTitle."));

                return new JobPosting(
                    companyName,
                    job.getTitle(),
                    job.getExperienceLevel(),
                    job.getEmploymentType(),
                    job.getLocation(),
                    job.getDetailUrl()

                );
            })
            .toList();

        return new PageImpl<>(jobPostings, pageable, jobPostingEntityList.getTotalElements());
    }

    public Page<JobPosting> getJobPostingsByTitlesAndCompanyNames(List<String> titles, List<String> companyNames, Pageable pageable) {

        Map<Long, String> companyMap = companyRepository.findByNameIn(companyNames).stream()
            .collect(Collectors.toMap(CompanyEntity::getId, CompanyEntity::getName));

        Page<JobPostingEntity> jobPostingEntityList = jobPostingRepository.findByTitleInAndCompanyIdIn(titles, companyMap.keySet(), pageable);

        List<JobPosting> jobPostings = jobPostingEntityList.get()
            .map(job -> new JobPosting(
                companyMap.get(job.getCompanyId()),
                job.getTitle(),
                job.getExperienceLevel(),
                job.getEmploymentType(),
                job.getLocation(),
                job.getDetailUrl()
            ))
            .toList();

        return new PageImpl<>(jobPostings, pageable, jobPostingEntityList.getTotalElements());
    }
}
