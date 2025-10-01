package com.example.Open_Position_Hub.collector;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Fetcher {

    private static final Logger logger = LoggerFactory.getLogger(Fetcher.class);

    private static final int MAX_RETRIES = 3;
    private static final long BASE_DELAY_MS = 500;
    private static final int TIMEOUT_MS = 5000;

    private static final Set<Integer> RETRYABLE_STATUS_CODES = Set.of(
        408, 429, 500, 502, 503, 504
    );

    private static final Set<Class<? extends Exception>> RETRYABLE_EXCEPTIONS = Set.of(
        SocketTimeoutException.class,
        ConnectException.class,
        UnknownHostException.class
    );

    public Document fetchHtml(String url) {
        Exception last = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {

                Response response = Jsoup.connect(url)
                    .timeout(TIMEOUT_MS)
                    .userAgent("Mozilla/5.0 (compatible; JavaBot/1.0)")
                    .header("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                    .execute();

                int statusCode = response.statusCode();

                if (RETRYABLE_STATUS_CODES.contains(statusCode)) {
                    throw new HttpStatusException("HTTP " + statusCode, statusCode, url);
                }

                if (200 <= statusCode && statusCode < 300) {
                    return response.parse();
                }


            } catch (IOException e) {
                last = e;

                boolean shouldRetry = shouldRetryException(e) && attempt < MAX_RETRIES;

                logger.warn("Attempt {} failed, url={} retrying={} cause={}", attempt, url,
                    shouldRetry, e.toString());

                if (shouldRetry) {
                    waitBeForeRetry(attempt);
                } else {
                    break;
                }
            }
        }

        logger.error("Failed to fetch url={} after {} attempts", url, MAX_RETRIES, last);
        return null;
    }

    private boolean shouldRetryException(Throwable t) {

        if (t instanceof HttpStatusException httpStatusException) {
            return RETRYABLE_STATUS_CODES.contains(httpStatusException.getStatusCode());
        }

        // 네트워크 계열 예외는 cause 체인까지 검사
        while (t != null) {
            for (Class<? extends Exception> c : RETRYABLE_EXCEPTIONS) {
                if (c.isInstance(t)) {
                    return true;
                }
            }
            t = t.getCause();
        }
        return false;
    }

    private void waitBeForeRetry(int attempt) {
        long backoff = BASE_DELAY_MS * (1L << (attempt - 1));
        long capped = Math.min(backoff, 2_000L);
        long jitter = ThreadLocalRandom.current().nextLong(0, 100);
        long sleepMs = capped + jitter;
        try {
            TimeUnit.MILLISECONDS.sleep(sleepMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
