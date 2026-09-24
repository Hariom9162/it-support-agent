package com.veridian.it_support_agent.controller;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiTestController {

    private final Client client;

    public GeminiTestController() {
        this.client = new Client();
    }

    @GetMapping("/test")
    public String testGemini(@RequestParam String message) {

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3.6-flash",
                        message,
                        null
                );

        return response.text();
    }
}