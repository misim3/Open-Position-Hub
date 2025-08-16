package com.example.Open_Position_Hub.collector.platform;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.util.List;

public interface PlatformStrategy {

    boolean supports(String platform);

    List<JobPostingEntity> scrape(CompanyEntity company) throws Exception;

    String platformKey();
}
