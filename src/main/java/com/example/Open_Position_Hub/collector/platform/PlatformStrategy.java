package com.example.Open_Position_Hub.collector.platform;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.util.List;
import org.jsoup.nodes.Document;

public interface PlatformStrategy {

    boolean supports(String platform);

    List<JobPostingEntity> scrape(Document doc, CompanyEntity company);

    String platformKey();
}
