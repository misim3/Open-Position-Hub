package com.example.Open_Position_Hub.collector.parser.Greeting;

import com.example.Open_Position_Hub.collector.parser.JobParser;
import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GreetingV1Parser implements JobParser {

    private static final String key = "그리팅/V1";
    private static final Logger logger = LoggerFactory.getLogger(GreetingV1Parser.class);
    private enum Field { CATEGORY, EXPERIENCE, EMPLOYMENT, LOCATION }

    @Override
    public String layoutKey() {
        return key;
    }

    @Override
    public List<JobPostingEntity> parse(Document doc, CompanyEntity company) {

        Map<String, List<String>> options = handleSideBar(doc.select("div.sc-9b56f69e-0.imkSIw.sc-9b6acf96-0.mgFVD").select("div.sc-c7f48e72-0.biJzyB"));
        if (options.isEmpty()) {
            logger.error("HTML structure changed: Unable to find elements(handleSideBar) for Company: {}, URL: {}", company.getName(), company.getRecruitmentUrl());
            return List.of();
        }

        List<JobPostingEntity> jobPostingEntities = handleJobCards(Objects.requireNonNull(doc.selectFirst("div.sc-9b56f69e-0.enoHnQ")), options, company.getId());
        if (jobPostingEntities.isEmpty()) {
            logger.error("HTML structure changed: Unable to find elements(handleJobCards) for Company: {}, URL: {}", company.getName(), company.getRecruitmentUrl());
        }
        return jobPostingEntities;
    }

    private Map<String, List<String>> handleSideBar(Elements categories) {

        Map<String, List<String>> options = new HashMap<>();

        for (Element category : categories) {
            String name = category.select("span.sc-86b147bc-0.jrtDxx").text();

            Elements checks = category.select("label[role='checkbox']");

            List<String> values = new ArrayList<>();
            for (Element check : checks) {
                values.add(check.select("span.sc-86b147bc-0.cvrGje").text());
            }
            options.put(name, values);
        }

        return options;
    }

    private List<JobPostingEntity> handleJobCards(Element container, Map<String, List<String>> options, Long companyId) {

        List<JobPostingEntity> jobPostingEntities = new ArrayList<>();

        Map<String, Field> textToField = buildTextToField(options);

        Elements links = container.select("ul a[href]");

        for (Element link : links) {
            String href = link.attr("href");
            String title = link.select("span.sc-86b147bc-0.gIOkaZ.sc-f484a550-1.gMeHeg").text();
            Elements details = link.select("span.sc-86b147bc-0.bugutw.sc-708ae078-1.gAEjfw");

            String category = "";
            String experienceLevel = "";
            String employmentType = "";
            String location = "";

            for (Element detail : details) {
                String text = detail.select("span.sc-708ae078-3.hBUoLe").text();

                Field f = textToField.get(text);
                if (f != null) {
                    switch (f) {
                        case CATEGORY   -> category = text;
                        case EXPERIENCE -> experienceLevel = text;
                        case EMPLOYMENT -> employmentType = text;
                        case LOCATION   -> location = text;
                    }
                } else if (experienceLevel.isEmpty() && text.contains("경력")) {
                    experienceLevel = text;
                }

                if (!category.isEmpty() && !experienceLevel.isEmpty()
                    && !employmentType.isEmpty() && !location.isEmpty()) {
                    break;
                }
            }

            jobPostingEntities.add(new JobPostingEntity(title, category, experienceLevel, employmentType, location, href, companyId));
        }

        return jobPostingEntities;
    }

    private Map<String, Field> buildTextToField(Map<String, List<String>> options) {
        Map<String, Field> map = new HashMap<>();
        options.forEach((k, values) -> {
            Field f = switch (k) {
                case "직군"   -> Field.CATEGORY;
                case "경력사항" -> Field.EXPERIENCE;
                case "고용형태" -> Field.EMPLOYMENT;
                case "근무지"  -> Field.LOCATION;
                default       -> null;
            };
            if (f != null) {
                for (String v : values) {
                    map.put(v, f); // 필요하면 .trim()·소문자 변환 등 정규화 추가
                }
            }
        });
        return map;
    }
}
