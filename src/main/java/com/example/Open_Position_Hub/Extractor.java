package com.example.Open_Position_Hub;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Extractor {

    private CssSelector selector;

    public Extractor(CssSelector selector) {
        this.selector = selector;
    }

    public void extractCompanyN(Document doc) {

        String company = "CompanyN";

        Map<String, String> htmlSelector = selector.getCssQuery(company);

        Elements jobCards = doc.select(htmlSelector.get("job_cards"));

        System.out.println("Extracted Job Information:");
        for (Element card : jobCards) {
            String title = card.select(htmlSelector.get("job_title")).text();
            Elements details = card.select(htmlSelector.get("details"));

            System.out.println("Title: " + title);
            for (Element detail : details) {
                System.out.println("Detail: " + detail.text());
            }

            System.out.println("---------------------------------");
        }

    }

    public void extractCompanyD(Document doc) {

        String company = "CompanyD";

        Map<String, String> htmlSelector = selector.getCssQuery(company);

        Elements jobCards = doc.select(htmlSelector.get("job_cards"));

        System.out.println("Extracted Job Information:");
        for (Element card : jobCards) {
            String title = card.select(htmlSelector.get("job_title")).text();
            Elements details = card.select(htmlSelector.get("details"));

            String[] detailLabels = {"직군", "경력사항", "고용형태", "근무지"};
            String[] detailValues = new String[detailLabels.length];

            for (int i = 0; i < detailValues.length; i++) {
                if (i < details.size()) {
                    detailValues[i] = details.get(i).text();
                } else {
                    detailValues[i] = "정보 없음";
                }
            }

            System.out.println("---------------------------------");
            System.out.println("직무: " + title);
            for (int i = 0; i < detailLabels.length; i++) {
                System.out.println(detailLabels[i] + ": " + detailValues[i]);
            }

        }

    }

    public void extractGreeting(Document doc) {

        Elements listViewA = doc.select("div[listviewtype='a']");
        Elements listViewB = doc.select("div[listviewtype='b']");
        System.out.println("============================");

        if (!listViewA.isEmpty()) {
            handleListViewA(listViewA);
        } else if (!listViewB.isEmpty()) {
            handleListViewB(listViewB);
        } else {
            System.out.println("fail to extract greeting");
        }

    }

    private void handleListViewA(Elements listViewA) {
        handleFilterBar(listViewA.select("div.sc-df4c3229-0.dPCaxA.sc-7b0260df-0.gRdgwV"));
        System.out.println("============================");
        handleJobCards(listViewA.select("div.sc-9b56f69e-0.enoHnQ"));
    }

    private void handleListViewB(Elements listViewB) {
        handleSideBar(listViewB.select("div.sc-4384c63b-0.dpoYEo"));
        System.out.println("============================");
        handleJobCards(listViewB.select("div.sc-9b56f69e-0.enoHnQ"));
    }

    private void handleFilterBar(Elements e) {

        Elements categories = e.select("div.sc-2050f279-0.gczZbu");

        System.out.println("Filter Bar:");

        for (Element category : categories) {
            String text = category.select("span.sc-86b147bc-0.ghZIoe").text();
            String name = text.replaceAll("\\s*\\(\\d+\\)", "");
            System.out.println("----------------------------");
            System.out.println("name: " + name);

        }

    }

    private void handleSideBar(Elements e) {

        Elements categories = e.select("div.sc-f960cb4f-0.fyUmrl");

        System.out.println("Sidebar:");

        for (Element category : categories) {
            String name = category.select("span.sc-86b147bc-0.jrtDxx").text();
            System.out.println("----------------------------");
            System.out.println("name: " + name);

            Elements checkboxes = category.select("label[role='checkbox']");
            for (Element checkbox : checkboxes) {
                System.out.println("values: " + checkbox.text());
            }

        }
    }

    private void handleJobCards(Elements e) {

        Elements jobCards = e.select("div.sc-9b56f69e-0.jlntFl");

        System.out.println("Job Cards:");
        for (Element jobCard : jobCards) {
            String title = jobCard.select("span.sc-86b147bc-0.gIOkaZ.sc-d200d649-1.dKCwbm").text();
            System.out.println("----------------------------");
            System.out.println("title: " + title);

            Elements details = jobCard.select("span.sc-be6466ed-3.bDOHei");

            for (Element detail : details) {
                System.out.println("details: " + detail.text());
            }
        }


    }

    public static void main(String[] args) {

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Scraper scraper = new Scraper();
        String urlCompanyN = "https://recruit.navercorp.com/rcrt/list.do?lang=ko";
        String urlCompanyD1_B = "https://www.doodlin.co.kr/career#3276397a-a988-4ca5-ab47-9aa05e9cce30";
        String urlCompanyD2_A = "https://teamdoeat.career.greetinghr.com/home#323ea93b-ce52-45c9-bbbf-0b85ad135508";

        Extractor extractor = new Extractor(new CssSelector());

        try {
            Document doc = scraper.fetchHtml(urlCompanyD2_A);
            extractor.extractGreeting(doc);
        } catch (IOException e) {
            System.err.println("fail: " + e.getMessage());
        }
    }
}
