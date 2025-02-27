package com.example.Open_Position_Hub.db;

import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPostingEntity, Long> {

    Page<JobPostingEntity> findByCompanyIdIn(Collection<Long> companyIds, Pageable pageable);

    Page<JobPostingEntity> findByTitleIn(Collection<String> titles, Pageable pageable);

    Page<JobPostingEntity> findByTitleInAndCompanyIdIn(Collection<String> titles, Collection<Long> longs, Pageable pageable);
}
