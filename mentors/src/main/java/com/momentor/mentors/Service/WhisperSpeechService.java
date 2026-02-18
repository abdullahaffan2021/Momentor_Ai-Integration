package com.momentor.mentors.Service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class WhisperSpeechService implements SpeechToTextService {

    private static final String FFMPEG_PATH =
            "C:\\Users\\ELCOT\\Downloads\\ffmpeg-8.0.1-essentials_build\\bin\\ffmpeg.exe";

    private static final String WHISPER_PATH =
            "C:\\Users\\ELCOT\\AppData\\Local\\Programs\\Python\\Python313\\Scripts\\whisper.exe";

    @Override
    public String transcribe(File audioFile) {

        File cleanedFile = null;
        File transcriptFile = null;

        try {

            // =========================
            // STEP 1: Clean Audio
            // =========================
            cleanedFile = new File("cleaned_" + UUID.randomUUID() + ".wav");

            ProcessBuilder cleanProcess = new ProcessBuilder(
                    FFMPEG_PATH,
                    "-y",
                    "-i", audioFile.getAbsolutePath(),
                    "-ar", "16000",              // Speech optimized
                    "-ac", "1",                  // Mono audio
                    "-af", "afftdn=nf=-25",
                    cleanedFile.getAbsolutePath()
            );

            cleanProcess.redirectErrorStream(true);
            Process clean = cleanProcess.start();
            int cleanExit = clean.waitFor();
            if (cleanExit != 0) {
                throw new RuntimeException("FFmpeg cleaning failed");
            }
            System.out.println("✅ Audio cleaned successfully");

            // =========================
            // STEP 2: Whisper Transcription
            // =========================
            ProcessBuilder whisperProcess = new ProcessBuilder(
                    WHISPER_PATH,
                    cleanedFile.getAbsolutePath(),
                    "--model", "small",
                    "--language", "en",
                    "--fp16", "False",
                    "--output_format", "txt",
                    "--verbose", "False"
            );

            whisperProcess.redirectErrorStream(true);
            Process whisper = whisperProcess.start();

            int whisperExit = whisper.waitFor();
            if (whisperExit != 0) {
                throw new RuntimeException("Whisper failed. Exit code: " + whisperExit);
            }

            System.out.println("✅ Transcription completed");

            // =========================
            // STEP 3: Read Generated TXT File
            // =========================
            String txtFileName = cleanedFile.getName().replace(".wav", ".txt");
            transcriptFile = new File(cleanedFile.getParent(), txtFileName);
            String transcript = new String(
                    java.nio.file.Files.readAllBytes(transcriptFile.toPath()),
                    StandardCharsets.UTF_8
            );

            return transcript.trim();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Local Whisper failed", e);
        } finally {

            // Cleanup temp files
            if (cleanedFile != null && cleanedFile.exists()) {
                cleanedFile.delete();
            }

            if (transcriptFile != null && transcriptFile.exists()) {
                transcriptFile.delete();
            }
        }
    }
    private String captureOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        return output.toString();
    }
}
