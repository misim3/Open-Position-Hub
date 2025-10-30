package com.example.Open_Position_Hub.db;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobPostingRepository extends JpaRepository<JobPostingEntity, Long> {

    void deleteByCompanyIdIn(List<Long> companyIds);

    List<JobPostingEntity> findByCompanyId(Long companyId);

    @Query("select j from JobPostingEntity j where function('mod', j.id, :k) = :bucket")
    List<JobPostingEntity> findShard(@Param("k") int k, @Param("bucket") int bucket);

}
