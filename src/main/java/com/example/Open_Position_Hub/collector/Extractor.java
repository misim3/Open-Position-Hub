package com.example.Open_Position_Hub.collector;

import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class Extractor {

    private final JobDataExtractorSelenium jobDataExtractorSelenium;

    public Extractor(JobDataExtractorSelenium jobDataExtractorSelenium) {
        this.jobDataExtractorSelenium = jobDataExtractorSelenium;
    }

    public List<JobPostingEntity> extractGreeting2(Document doc, String url, Long companyId) {

        List<JobPostingEntity> list = new ArrayList<>();

        Elements listViewA = doc.select("div[listviewtype='a']");
        Elements listViewB = doc.select("div[listviewtype='b']");

        if (!listViewA.isEmpty()) {
            list = handleListViewA2(listViewA, url, companyId);
        } else if (!listViewB.isEmpty()) {
            list = handleListViewB2(listViewB, companyId);
        } else {
            System.out.println("fail to extract greeting");
        }

        return list;
    }

    private List<JobPostingEntity> handleListViewA2(Elements listViewA, String url,
        Long companyId) {
        Map<String, List<String>> criteria = jobDataExtractorSelenium.handleFilterBar(url);
        return handleJobCards2(listViewA.select("div.sc-9b56f69e-0.enoHnQ"), criteria, companyId);
    }

    private List<JobPostingEntity> handleListViewB2(Elements listViewB, Long companyId) {
        Map<String, List<String>> criteria = handleSideBar2(
            listViewB.select("div.sc-4384c63b-0.dpoYEo"));
        return handleJobCards2(listViewB.select("div.sc-9b56f69e-0.enoHnQ"), criteria, companyId);
    }

    private Map<String, List<String>> handleSideBar2(Elements e) {

        Map<String, List<String>> map = new LinkedHashMap<>();

        Elements categories = e.select("div.sc-f960cb4f-0.fyUmrl");

        for (Element category : categories) {
            String name = category.select("span.sc-86b147bc-0.jrtDxx").text();

            Elements checkboxes = category.select("label[role='checkbox']");
            List<String> list = new ArrayList<>();
            for (Element checkbox : checkboxes) {
                list.add(checkbox.text());
            }

            map.put(name, list);
        }

        return map;
    }

    private List<JobPostingEntity> handleJobCards2(Elements e,
        Map<String, List<String>> criteriaList, Long companyId) {

        List<JobPostingEntity> list = new ArrayList<>();
        Elements links = e.select("a");

        for (Element link : links) {
            String hrefValue = link.attr("href");
            String title = link.select("span.sc-86b147bc-0.gIOkaZ.sc-d200d649-1.dKCwbm").text();
            Elements details = link.select("span.sc-be6466ed-3.bDOHei");

            list.add(Convertor.convertGreeting(hrefValue, title, details, criteriaList, companyId));

        }

        return list;
    }
}
