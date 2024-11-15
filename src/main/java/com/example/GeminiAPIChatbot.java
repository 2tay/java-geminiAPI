package com.example;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.helper.GeneralHelper;

import java.io.IOException;
import java.util.Scanner;


public class GeminiAPIChatbot {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Replace with your actual Gemini API key
        String apiKey = "AIzaSyDFnl4KzLfRJRuwaoeBlEbJLTrkyM_PTUs";

        System.out.println("Upload Pdf file:");
        String selectedPdfPath = GeneralHelper.selectPdfFile();
        if (selectedPdfPath == null) {
            System.out.println("No file selected. Exiting...");
            return;
        }
        System.out.println("Selected file: " + selectedPdfPath);
        
        String pdfContent = GeneralHelper.extractTextFromPdf(selectedPdfPath);
        if(pdfContent == null) {
            System.out.println("Failed to extract content from the PDF. Exiting...");
            return;
        }
        System.out.println("PDF content loaded successfully.");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // StringBuilder to store conversation history
            StringBuilder conversationHistory = new StringBuilder();

            while (true) {
                // Get user input
                System.out.print("Enter your question: ");
                String question = scanner.nextLine();

                // Exit the loop if the user types "exit"
                if (question.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting chatbot...");
                    break;
                }

                // Append the user question to the conversation history
                conversationHistory.append("User: ").append(question).append("\n");
                
                // Include the PDF content in the prompt
                String prompt = "You are an AI assistant. Answer the following question only using the context provided from the PDF. If the question is outside the scope of the PDF content, respond with 'Sorry, I can't answer that.'\n\n" 
                                + "PDF Context:\n" + pdfContent + "\n\n"
                                + conversationHistory.toString() + "\n\n"
                                + "Question: " + question;

                // Create HTTP POST request to Gemini API
                HttpPost httpPost = new HttpPost("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey);
                httpPost.setHeader("Content-Type", "application/json");

                // Send the entire conversation history as part of the request
                String requestBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + prompt + "\"}]}]}";
                httpPost.setEntity(new StringEntity(requestBody));

                // Send the request and get the response
                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    String responseBody = EntityUtils.toString(response.getEntity());

                    // Parse the JSON response and extract the text
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (!jsonResponse.has("candidates") || jsonResponse.getJSONArray("candidates").isEmpty()) {
                        System.out.println("Error: No candidates returned by the API.");
                        continue;
                    }
                    
                    JSONArray candidates = jsonResponse.getJSONArray("candidates");
                    JSONObject candidate = candidates.getJSONObject(0);
                    JSONObject content = candidate.getJSONObject("content");
                    JSONArray parts = content.getJSONArray("parts");
                    String responseText = parts.getJSONObject(0).getString("text");

                    // Append the bot's response to the conversation history
                    conversationHistory.append("Bot: ").append(responseText).append("\n");

                    // Print the bot's response
                    System.out.println("Bot: " + responseText);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("An error occurred while processing the response.");
                }
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred with the HTTP client.");
        }
    }

}
