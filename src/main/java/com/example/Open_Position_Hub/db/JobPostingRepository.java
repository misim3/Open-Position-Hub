package com.example.Open_Position_Hub.db;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobPostingRepository extends JpaRepository<JobPostingEntity, Long> {

    Page<JobPostingEntity> findByCompanyIdIn(Collection<Long> companyIds, Pageable pageable);

    Page<JobPostingEntity> findByTitleIn(Collection<String> titles, Pageable pageable);

    Page<JobPostingEntity> findByTitleInAndCompanyIdIn(Collection<String> titles, Collection<Long> longs, Pageable pageable);

    @Query("select distinct j.title from JobPostingEntity j where j.title is not null ")
    List<String> findDistinctTitles();

    @Query("select distinct j.companyId from JobPostingEntity j where j.companyId is not null ")
    List<Long> findDistinctCompanyIds();
}
