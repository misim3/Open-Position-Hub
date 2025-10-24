package com.example.Open_Position_Hub.collector.parser.ninehire;

import com.example.Open_Position_Hub.collector.JobPostingDto;
import com.example.Open_Position_Hub.collector.parser.JobParser;
import com.example.Open_Position_Hub.db.CompanyEntity;
import java.util.List;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class NinehireV2Parser implements JobParser {

    @Override
    public String layoutKey() {
        return "";
    }

    @Override
    public List<JobPostingDto> parse(Document doc, CompanyEntity company) {
        return List.of();
    }
}
