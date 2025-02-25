package com.example.Open_Position_Hub;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.CompanyRepository;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import com.example.Open_Position_Hub.db.JobPostingRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class JobPostingService {

    private JobPostingRepository jobPostingRepository;
    private CompanyRepository companyRepository;

    public JobPostingService(JobPostingRepository jobPostingRepository, CompanyRepository companyRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.companyRepository = companyRepository;
    }

    public List<JobPosting> getAllJobPostings() {
        List<JobPosting> jobPostings = new ArrayList<>();
        List<JobPostingEntity> jobPostingEntityList = jobPostingRepository.findAll();
        for (JobPostingEntity jobPostingEntity : jobPostingEntityList) {
            Optional<CompanyEntity> companyName = companyRepository.findById(jobPostingEntity.getCompanyId());
            companyName.ifPresent(company -> jobPostings.add(new JobPosting(company.getName(), jobPostingEntity.getTitle(), jobPostingEntity.getExperienceLevel(), jobPostingEntity.getEmploymentType(), jobPostingEntity.getLocation(), jobPostingEntity.getDetailUrl())));
        }
        return jobPostings;
    }

    public List<JobPosting> getJobPostingsByCompanyName(String companyName) {
        List<JobPosting> jobPostings = new ArrayList<>();
        Long companyId = companyRepository.findByName(companyName);
        List<JobPostingEntity> jobPostingEntityList = jobPostingRepository.findByCompanyId(companyId);
        for (JobPostingEntity jobPostingEntity : jobPostingEntityList) {
            jobPostings.add(new JobPosting(companyName, jobPostingEntity.getTitle(), jobPostingEntity.getExperienceLevel(), jobPostingEntity.getEmploymentType(), jobPostingEntity.getLocation(), jobPostingEntity.getDetailUrl()));
        }
        return jobPostings;
    }

    public List<JobPosting> getJobPostingsByTitle(String title) {
        List<JobPosting> jobPostings = new ArrayList<>();
        List<JobPostingEntity> jobPostingEntityList = jobPostingRepository.findByTitle(title);
        for (JobPostingEntity jobPostingEntity : jobPostingEntityList) {
            Optional<CompanyEntity> companyName = companyRepository.findById(jobPostingEntity.getCompanyId());
            companyName.ifPresent(company -> jobPostings.add(new JobPosting(company.getName(), jobPostingEntity.getTitle(), jobPostingEntity.getExperienceLevel(), jobPostingEntity.getEmploymentType(), jobPostingEntity.getLocation(), jobPostingEntity.getDetailUrl())));
        }
        return jobPostings;
    }
}
