package com.example.Open_Position_Hub.collector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Scraper {

    private static final Logger logger = LoggerFactory.getLogger(Scraper.class);

    public Document fetchHtml(String url) throws IOException {

        try {
            return Jsoup.connect(url)
                .followRedirects(false)
                .timeout(5000)
                .get();

        } catch (HttpStatusException e) {
            int statusCode = e.getStatusCode();
            switch (statusCode) {
                case 301, 308 -> logger.warn("[Scraper] 301/308 Permanent Redirect detected - URL: {}", url);
                case 302, 307 -> logger.warn("[Scraper] 302/307 Temporary Redirect detected - URL: {}", url);
                case 303 -> logger.warn("[Scraper] 303 See Other - URL: {}", url);

                case 400 -> logger.warn("[Scraper] 400 Bad Request - Check URL formatting. URL: {}", url);
                case 401 -> logger.error("[Scraper] 401 Unauthorized - Authentication required for URL: {}", url);
                case 403 -> logger.error("[Scraper] 403 Forbidden - Access denied to URL: {}", url);
                case 404 -> logger.warn("[Scraper] 404 Not Found - URL might be outdated: {}", url);
                case 405 -> logger.warn("[Scraper] 405 Method Not Allowed - Check HTTP method for URL: {}", url);

                case 500 -> logger.error("[Scraper] 500 Internal Server Error - URL: {}", url);
                case 502 -> logger.error("[Scraper] 502 Bad Gateway - Possible upstream server issue. URL: {}", url);
                case 503 -> logger.warn("[Scraper] 503 Service Unavailable - Temporary server issue. URL: {}", url);
                case 504 -> logger.error("[Scraper] 504 Gateway Timeout - Server response timed out. URL: {}", url);

                default -> logger.error("[Scraper] Unhandled HTTP status code: {} for URL: {}", statusCode, url);
            }
            throw e;

        } catch (SocketTimeoutException e) {
            logger.error("Connection timed out while accessing: {}", url);
            throw e;

        } catch (UnknownHostException e) {
            logger.error("Unknown host - Possible domain change or deletion: {}", url);
            throw e;

        } catch (MalformedURLException e) {
            logger.error("Malformed URL - Check the URL format: {}", url);
            throw e;

        } catch (IOException e) {
            logger.error("General IO Exception occurred while fetching HTML: {} for URL: {}", e.getMessage(), url);
            throw e;
        }
    }
}
