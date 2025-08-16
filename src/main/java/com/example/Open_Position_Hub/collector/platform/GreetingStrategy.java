package com.example.Open_Position_Hub.collector.platform;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GreetingStrategy implements PlatformStrategy {

    private static final String key = "그리팅";

    @Override
    public boolean supports(String platform) {
        return platform.equals(key);
    }

    @Override
    public List<JobPostingEntity> scrape(CompanyEntity company) throws Exception {

        return List.of();
    }

    @Override
    public String platformKey() {
        return key;
    }
}
