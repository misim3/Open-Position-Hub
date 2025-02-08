package com.example.Open_Position_Hub.collector;

import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Convertor {

    public static JobPostingEntity convertGreeting(String hrefValue, String title, Elements details,
        Map<String, List<String>> criteriaList, Long companyId) {

        String category = "";
        String experienceLevel = "";
        String employmentType = "";
        String location = "";

        for (Element detail : details) {
            for (Entry<String, List<String>> criteria : criteriaList.entrySet()) {
                if (criteria.getValue().contains(detail.text())) {
                    switch (criteria.getKey()) {
                        case "직군":
                            category = detail.text();
                            break;
                        case "경력사항":
                            experienceLevel = detail.text();
                            break;
                        case "고용형태":
                            employmentType = detail.text();
                            break;
                        case "근무지":
                            location = detail.text();
                            break;
                    }
                }
                if (experienceLevel.isEmpty() && detail.text().contains("경력")) {
                    experienceLevel = detail.text();
                    break;
                }
            }
        }

        return new JobPostingEntity(title, category, experienceLevel, employmentType, location,
            hrefValue, companyId);

    }
}
