package com.example.Open_Position_Hub.db;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    List<CompanyEntity> findByNameIn(List<String> companyNames);

    List<CompanyEntity> findAllByRecruitmentPlatform(String recruitmentPlatform);
}
