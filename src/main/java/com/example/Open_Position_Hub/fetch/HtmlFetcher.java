package com.example.Open_Position_Hub.fetch;

import org.jsoup.nodes.Document;

public interface HtmlFetcher {

    Document fetch(String url, FetchProfile fetchProfile) throws Exception;
}
