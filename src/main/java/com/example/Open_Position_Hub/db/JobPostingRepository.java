package com.example.Open_Position_Hub.db;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPostingEntity, Long> {

    List<JobPostingEntity> findByCompanyId(Long companyId);

    List<JobPostingEntity> findByTitle(String title);
}
