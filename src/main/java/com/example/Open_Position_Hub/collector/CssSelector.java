package com.example.Open_Position_Hub.collector;

import java.util.HashMap;
import java.util.Map;

public class CssSelector {

    public Map<String, String> getCssQuery(String company) {

        if (company.equals("CompanyD")) {
            return getCompanyD();
        } else if (company.equals("CompanyN")) {
            return getCompanyN();
        }
        return null;
    }

    private Map<String, String> getCompanyD() {

        Map<String, String> companyD = new HashMap<>();

        companyD.put("job_cards", "div.sc-9b56f69e-0.jlntFl");
        companyD.put("job_title", "span.sc-86b147bc-0.gIOkaZ.sc-d200d649-1.dKCwbm");
        companyD.put("details", "span.sc-be6466ed-3.bDOHei");

        return companyD;
    }

    private Map<String, String> getCompanyN() {

        Map<String, String> companyN = new HashMap<>();

        companyN.put("job_cards", "li.card_item");
        companyN.put("job_title", "h4.card_title");
        companyN.put("details", "dl.card_info dd.info_text");

        return companyN;
    }
}
