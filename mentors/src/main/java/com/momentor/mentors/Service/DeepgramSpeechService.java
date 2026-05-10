package com.momentor.mentors.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;

@Service
public class DeepgramSpeechService implements SpeechToTextService {

    @Value("${deepgram.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String transcribe(File audioFile) {

        try {

            // =========================
            // STEP 1: Deepgram URL (Force English)
            // =========================
            String url = "https://api.deepgram.com/v1/listen" +
                    "?model=nova-2" +
                    "&punctuate=true" +
                    "&smart_format=true" +
                    "&language=en";   // Force English

            // =========================
            // STEP 2: Headers
            // =========================
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("Authorization", "Token " + apiKey);

            byte[] audioBytes = Files.readAllBytes(audioFile.toPath());
            HttpEntity<byte[]> request = new HttpEntity<>(audioBytes, headers);

            // =========================
            // STEP 3: Call Deepgram API
            // =========================
            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, request, String.class);

            System.out.println("========== DEEPGRAM STATUS ==========");
            System.out.println(response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Deepgram API Error: " + response.getStatusCode());
            }

            String responseBody = response.getBody();

            // =========================
            // STEP 4: Parse JSON
            // =========================
            JsonNode root = objectMapper.readTree(responseBody);

            JsonNode alternativeNode = root
                    .path("results")
                    .path("channels").get(0)
                    .path("alternatives").get(0);

            // Log confidence score
            double confidence = alternativeNode.path("confidence").asDouble();
            System.out.println("Transcription Confidence: " + confidence);

            // =========================
            // STEP 5: Use Paragraph Transcript (BEST METHOD)
            // =========================
            JsonNode paragraphTranscript = alternativeNode
                    .path("paragraphs")
                    .path("transcript");

            if (paragraphTranscript.isMissingNode()) {
                throw new RuntimeException("No transcript found in Deepgram response");
            }

            String finalTranscript = paragraphTranscript.asText().trim();

            System.out.println("===== FINAL TRANSCRIPT =====");
            System.out.println(finalTranscript);

            return finalTranscript;

        } catch (Exception e) {
            System.out.println("========== DEEPGRAM ERROR ==========");
            e.printStackTrace();
            throw new RuntimeException("Deepgram transcription failed", e);
        }
    }
}