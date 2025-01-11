package org.supershop.shopcore.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SentinelServiceTest {

    private static final String BASE_URL = "http://localhost:8080/seckills";

    @Test
    void loadTestSeckillsEndpoint() throws InterruptedException {
        final int requestsPerSecond = 100;
        final int testDurationSeconds = 5;
        final int threadPoolSize = 20;
        RestTemplate restTemplate = new RestTemplate();
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

        for (int second = 0; second < testDurationSeconds; second++) {
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < requestsPerSecond; i++) {
                executorService.submit(() -> {
                    try {
                        String response = restTemplate.getForObject(BASE_URL, String.class);
                    } catch (Exception e) {
                        System.err.println("Request failed: " + e.getMessage());
                    }
                });
            }

            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = 1000 - elapsed;
            if (sleepTime > 0) {
                // Night-night :)
                Thread.sleep(sleepTime);
            }
        }

        executorService.shutdown();
        executorService.awaitTermination(2, TimeUnit.MINUTES);
    }
}
