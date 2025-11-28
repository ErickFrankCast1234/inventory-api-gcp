package com.inventory.inventory_api.load;

import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class LoadTest {

    @Test
    void testLoad_500Requests() throws Exception {

        int totalRequests = 500;

        ExecutorService executor = Executors.newFixedThreadPool(totalRequests);

        for (int i = 0; i < totalRequests; i++) {
            executor.submit(() -> {
                try {
                    URL url = new URL("http://localhost:8080/api/products");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.getResponseCode(); // Ejecuta la petición
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();

        while (!executor.isTerminated()) {
            Thread.sleep(10);
        }

        System.out.println("🔥 500 requests enviados con éxito");
    }
}
