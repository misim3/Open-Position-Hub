package com.example.Open_Position_Hub.collector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class Scraper {

    public Document fetchHtml(String url) throws IOException {

        try {
            return Jsoup.connect(url)
                .followRedirects(false)
                .timeout(5000)
                .get();

        } catch (HttpStatusException e) {
            int statusCode = e.getStatusCode();
            switch (statusCode) {
                case 301, 308:
                    System.err.println("301/308 Permanent Redirect: " + url);
                    break;

                case 302, 307:
                    System.err.println("302/307 Temporary Redirect: " + url);
                    break;

                case 303:
                    System.err.println("303 See Other: " + url);
                    break;

                case 400:
                    System.err.println("400 Bad Request: Check request format.");
                    break;

                case 401:
                    System.err.println("401 Unauthorized: Authentication required.");
                    break;

                case 403:
                    System.err.println("403 Forbidden: Access denied.");
                    break;

                case 404:
                    System.err.println("404 Not Found: URL invalidated in database.");
                    break;

                case 405:
                    System.err.println("405 Method Not Allowed: Check HTTP method.");
                    break;

                case 500:
                    System.err.println("500 Internal Server Error");
                    break;

                case 502:
                    System.err.println("502 Bad Gateway");
                    break;

                case 503:
                    System.err.println("503 Service Unavailable");
                    break;

                case 504:
                    System.err.println("504 Gateway Timeout");
                    break;

                default:
                    System.err.println("Unhandled HTTP status code: " + statusCode);
            }
            throw e;

        } catch (SocketTimeoutException e) {
            System.err.println("Connection timed out while accessing: " + url);
            throw e;

        } catch (UnknownHostException e) {
            System.err.println("Unknown host - Possible domain change or deletion: " + url);
            throw e;

        } catch (MalformedURLException e) {
            System.err.println("Malformed URL - Check the URL format: " + url);
            throw e;

        } catch (IOException e) {
            System.err.println(
                "General IO Exception occurred while fetching HTML: " + e.getMessage());
            throw e;
        }
    }
}
