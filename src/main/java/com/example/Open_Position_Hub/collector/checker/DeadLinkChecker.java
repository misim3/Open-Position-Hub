package com.example.Open_Position_Hub.collector.checker;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DeadLinkChecker {

    private static final Logger logger = LoggerFactory.getLogger(DeadLinkChecker.class);

    public List<JobPostingDto> checkDeadLinks(List<JobPostingDto> jobPostings) {

        List<JobPostingDto> deadJobPostings = new ArrayList<>();

        for (JobPostingDto jobPosting : jobPostings) {
            try {
                if (!isAccessible(jobPosting.detailUrl())) {
                    logger.warn("Dead links are not accessible {}", jobPosting.detailUrl());
                    deadJobPostings.add(jobPosting);
                }
            } catch (IOException e) {
                logger.error("[DeadLinkChecker] Error: {}", jobPosting.detailUrl(), e.fillInStackTrace());
            }
        }

        return deadJobPostings;
    }

    private boolean isAccessible(String url) throws IOException {

        Response response = Jsoup.connect(url).response();
        return response.statusCode() == 200;
    }
}
