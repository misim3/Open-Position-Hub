package com.example.Open_Position_Hub.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPostingEntity, Long> {

    Page<JobPostingEntity> findByCompanyId(Long companyId, Pageable pageable);

    Page<JobPostingEntity> findByTitle(String title, Pageable pageable);

    Page<JobPostingEntity> findByTitleAndCompanyId(String title, Long companyId, Pageable pageable);
}
