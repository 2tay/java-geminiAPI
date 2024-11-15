package com.example;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class APITest {
    public static void main(String[] args) {
        // Replace with the API endpoint you want to test
        String apiEndpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent";
        String apiKey = "AIzaSyDFnl4KzLfRJRuwaoeBlEbJLTrkyM_PTUs";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiEndpoint + "?key=" + apiKey);
            httpPost.setHeader("Content-Type", "application/json");

            // Example request body (adjust according to the API documentation)
            String requestBody = """
            {
              "contents": [{
                "parts": [{
                  "text": "Hello, world!"
                }]
              }]
            }
            """;
            httpPost.setEntity(new StringEntity(requestBody));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                if (statusCode == 200) {
                    System.out.println("API call successful! Response:");
                    System.out.println(responseBody);
                } else {
                    System.out.println("API call failed with status code: " + statusCode);
                    System.out.println("Response: " + responseBody);
                }
            }
        } catch (Exception e) {
            System.out.println("Error during API call:");
            e.printStackTrace();
        }
    }
}

