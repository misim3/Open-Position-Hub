package com.example.Open_Position_Hub.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    Long findByName(String companyName);
}
