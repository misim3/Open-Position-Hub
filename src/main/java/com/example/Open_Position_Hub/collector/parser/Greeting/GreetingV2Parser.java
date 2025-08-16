package com.example.Open_Position_Hub.collector.parser.Greeting;

import com.example.Open_Position_Hub.collector.parser.JobParser;
import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class GreetingV2Parser implements JobParser {

    private static final String key = "그리팅/V1";
    private enum Field { CATEGORY, EXPERIENCE, EMPLOYMENT, LOCATION };

    @Override
    public String layoutKey() {
        return key;
    }

    @Override
    public List<JobPostingEntity> parse(Document doc, CompanyEntity company) throws Exception {

        Map<String, List<String>> options = handleFilterBar(doc);

        return handleJobCards(doc.select("div.sc-9b56f69e-0.enoHnQ"), options, company.getId());
    }

    private Map<String, List<String>> handleFilterBar(Document doc) {

        Map<String, List<String>> options = new HashMap<>();

        return options;
    }

    private List<JobPostingEntity> handleJobCards(Elements links, Map<String, List<String>> options, Long companyId) throws Exception {

        List<JobPostingEntity> jobPostingEntities  = new ArrayList<>();

        return jobPostingEntities;
    }
}
