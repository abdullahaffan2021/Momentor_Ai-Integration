package com.momentor.mentors.Service;

import com.momentor.mentors.DTO.AiResult;
import com.momentor.mentors.DTO.AiTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiService {

    @Value("${groq.api-key}")
    private String apiKey;

    private static final String GROQ_URL =
            "https://api.groq.com/openai/v1/chat/completions";

    private final RestTemplate restTemplate = new RestTemplate();

    public AiResult analyze(String transcript) {

        try {

            String prompt = AiPrompt.PROMPT
                    .replace("<<<TRANSCRIPT>>>", transcript);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of(
                    "model", "llama-3.3-70b-versatile",
                    "temperature", 0.2,
                    "messages", List.of(
                            Map.of(
                                    "role", "user",
                                    "content", prompt
                            )
                    )
            );

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(
                            GROQ_URL,
                            request,
                            Map.class
                    );

            String aiContent = extractMessageContent(response.getBody());

            return parseOutput(aiContent);

        } catch (Exception e) {
            throw new RuntimeException("Groq AI analysis failed", e);
        }
    }

    private String extractMessageContent(Map<String, Object> body) {

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) body.get("choices");

        Map<String, Object> message =
                (Map<String, Object>) choices.get(0).get("message");

        return message.get("content").toString().trim();
    }

    // ===============================
    // Parsing Logic
    // ===============================

    private AiResult parseOutput(String output) {

        AiResult result = new AiResult();
        List<AiTask> taskList = new ArrayList<>();

        if (output == null || output.isEmpty()) {
            result.setMom("No summary generated.");
            result.setTasks(taskList);
            return result;
        }

        String[] parts = output.split("(?i)tasks:");

        String momPart = parts[0]
                .replace("Minutes:", "")
                .trim();

        result.setMom(momPart);

        if (parts.length > 1) {

            String tasksPart = parts[1].trim();
            String[] lines = tasksPart.split("\n");

            for (String line : lines) {

                if (line.trim().startsWith("-")) {

                    String cleaned = line.substring(1).trim();
                    String[] fields = cleaned.split("\\|");

                    if (fields.length >= 2) {

                        AiTask task = new AiTask();
                        task.setAssignedTo(fields[0].trim());
                        task.setTitle(fields[1].trim());

                        taskList.add(task);
                    }
                }
            }
        }

        result.setTasks(taskList);
        return result;
    }
}
