package com.example.Open_Position_Hub.db;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobPostingRepository extends JpaRepository<JobPostingEntity, Long> {

    Page<JobPostingEntity> findByTitleIn(Collection<String> titles, Pageable pageable);

    @Query("select distinct j.title from JobPostingEntity j where j.title is not null ")
    List<String> findDistinctTitles();

    @Query("select distinct j.companyId from JobPostingEntity j where j.companyId is not null ")
    List<Long> findDistinctCompanyIds();

    List<JobPostingEntity> findByCompanyId(Long companyId);

    @Query("select j from JobPostingEntity j where function('mod', j.id, :k) = :bucket")
    List<JobPostingEntity> findShard(@Param("k") int k, @Param("bucket") int bucket);
}
