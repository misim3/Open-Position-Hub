package com.example.Open_Position_Hub.collector.parser.ninehire;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import com.example.Open_Position_Hub.collector.parser.JobParser;
import com.example.Open_Position_Hub.db.CompanyEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NinehireV1Parser implements JobParser {

    private static final String key = "나인하이어/V1";
    private static final Logger logger = LoggerFactory.getLogger(NinehireV1Parser.class);
    private static final Pattern PREFIX_BRACKET_BLOCKS =
        Pattern.compile("^(?:\\s*(?:\\[[^]]*]|\\([^)]*\\)|\\{[^}]*}|<[^>]*>))+\\s*");

    @Override
    public String layoutKey() {
        return key;
    }

    @Override
    public List<JobPostingDto> parse(Document doc, CompanyEntity company) {

        Map<String, List<String>> options = handleSideBar(
            doc.select("div.JobPostingsSidebarFilters__FiltersLayout-sc-2062e5e7-0.hMKvYs")
                .select("div.JobPostingsSidebarFilter__Layout-sc-6112d8f1-0.khLaJL"));
        if (options.isEmpty()) {
            logger.error(
                "HTML structure Error: Unable to find elements in NinehireV1Parser.handleSideBar for Company: {}, URL: {}",
                company.getName(), company.getRecruitmentUrl());
            return null;
        }

        Element container = doc.selectFirst("div.JobPostingsSidebarTypeLayoutBody__Content-sc-c28e05fb-1.eSfHWV");
        if (container == null) {
            logger.error("Element not found in NinehireV1Parser.parse.container for Company: {}",
                company.getName());
            return null;
        }

        List<JobPostingDto> jobPostings = handleJobCards(container, options, company.getId());
        if (jobPostings.isEmpty()) {
            logger.error(
                "HTML structure Error: Unable to find elements in NinehireV1Parser.handleJobCards for Company: {}, URL: {}",
                company.getName(), company.getRecruitmentUrl());
            return null;
        }
        return jobPostings;
    }

    private Map<String, List<String>> handleSideBar(Elements categories) {

        Map<String, List<String>> options = new HashMap<>();

        for (Element category : categories) {
            String name = category.select("span.Body-sc-b30a2c4-0.JobPostingsSidebarFilter__Title-sc-6112d8f1-4.kOYzOz").text();

            Elements checks = category.select("div.JobPostingsSidebarFilter__FilterRadioLayout-sc-6112d8f1-8.emJXFB");

            List<String> values = new ArrayList<>();
            for (Element check : checks) {
                values.add(check.select("span.Body-sc-b30a2c4-0.JobPostingsSidebarFilter__FilterRadioTitle-sc-6112d8f1-13.kHpiRr.deIPOu").text());
            }
            options.put(name, values);
        }

        return options;
    }

    private List<JobPostingDto> handleJobCards(Element container, Map<String, List<String>> options, Long companyId) {

    }
}
