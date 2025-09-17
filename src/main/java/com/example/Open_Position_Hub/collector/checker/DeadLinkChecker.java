package com.example.Open_Position_Hub.collector.checker;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DeadLinkChecker {

    private static final Logger logger = LoggerFactory.getLogger(DeadLinkChecker.class);
    private static final String UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Safari/537.36";
    private static final int TIMEOUT_MS = 3000;

    public List<JobPostingDto> checkDeadLinks(List<JobPostingDto> jobPostings) {

        List<JobPostingDto> dead = new ArrayList<>();

        for (JobPostingDto j : jobPostings) {
            try {
                if (!isAccessible(j.detailUrl())) {
                    logger.warn("Dead link: {}", j.detailUrl());
                    dead.add(j);
                }
            } catch (IOException e) {
                try {
                    if (!isAccessible(j.detailUrl())) {
                        logger.warn("Dead links are not accessible {}", j.detailUrl());
                        dead.add(j);
                    }
                } catch (IOException e1) {
                    logger.error("[DeadLinkChecker] IOException on {}", j.detailUrl(), e.fillInStackTrace());
                }
            }
        }

        return dead;
    }

    private boolean isAccessible(String url) throws IOException {

        Connection base =  Jsoup.connect(url)
            .userAgent(UA)
            .timeout(TIMEOUT_MS)
            .followRedirects(true)
            .ignoreHttpErrors(true);

        Response res = base.method(Method.HEAD).execute();
        int code = res.statusCode();
        if (code == 405 || code == 403) {
            res = base.method(Method.GET).maxBodySize(64 * 1024).execute();
            code = res.statusCode();
        }
        return code >= 200 && code < 300;
    }
}
