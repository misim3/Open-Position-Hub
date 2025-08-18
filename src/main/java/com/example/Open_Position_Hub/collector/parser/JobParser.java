package com.example.Open_Position_Hub.collector.parser;

import com.example.Open_Position_Hub.db.CompanyEntity;
import com.example.Open_Position_Hub.db.JobPostingEntity;
import java.util.List;
import org.jsoup.nodes.Document;

public interface JobParser {

    String layoutKey();

    List<JobPostingEntity> parse(Document doc, CompanyEntity company);

}
