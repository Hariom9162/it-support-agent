package com.veridian.it_support_agent.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;

    public GeminiService() {
        this.client = new Client();
    }

    public String classifyIssue(String message) {

        String prompt = """
                You are an IT support issue classifier.

                Classify the employee issue into exactly ONE label.

                Allowed labels:
                PASSWORD
                VPN
                LAPTOP
                SOFTWARE
                PRINTER
                MAILBOX
                GUEST_WIFI
                EXPENSE
                SECURITY
                WFH_EQUIPMENT
                UNKNOWN

                Rules:
                - Return ONLY the label.
                - Do not explain.
                - If the issue is unclear, return UNKNOWN.
                - Security incidents such as phishing, malware, hacking or suspicious emails = SECURITY.

                Employee issue:
                """ + message;

        try {
            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-3.6-flash",
                            prompt,
                            null
                    );

            return response.text().trim().toUpperCase();

        } catch (Exception e) {
            // Existing rule-based system remains the fallback.
            return "UNKNOWN";
        }
    }
}