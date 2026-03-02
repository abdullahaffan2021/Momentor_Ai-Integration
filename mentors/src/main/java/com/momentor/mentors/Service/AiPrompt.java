package com.momentor.mentors.Service;

import java.util.List;

public class AiPrompt {
    // canonical template example (optional)
    public static final String PROMPT = """
You are an AI meeting assistant.

Read the meeting transcript carefully.

Return output strictly in this format:

Minutes:
<One clear paragraph summary of the meeting>

Tasks:
- Person or Team Name | Task description | Resource1, Resource2, Resource3
- Person or Team Name | Task description | Resource1, Resource2, Resource3

Rules:
- Use the pipe symbol (|) to separate exactly three fields per task line.
- Provide up to 3 useful learning resources for each task, separated by commas.
- Resources must be relevant (official docs, tutorials, articles, courses) or full URLs.
- If no resources apply, keep the third field empty (i.e. end with the second pipe).
- Do NOT create tables, do NOT use markdown, do NOT add numbering.
- Return only tasks that are explicitly assigned in the transcript.
- If no tasks are assigned, return exactly:

Tasks:
None

Meeting Transcript:
<<<TRANSCRIPT>>>
""";

    // ...existing code...
    public static String getPrompt(String meetingType, List<String> teamNames, String transcript) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an AI meeting assistant.\n\n");
        sb.append("Read the meeting transcript carefully.\n\n");

        // If we have explicit team list, include them
        if ("TEAM".equalsIgnoreCase(meetingType) && teamNames != null && !teamNames.isEmpty()) {
            sb.append("Assign tasks only to these teams. Use the team names exactly as provided (case-sensitive match):\n");
            for (String t : teamNames) {
                sb.append("- ").append(t).append("\n");
            }
            sb.append("\n");
            sb.append("When you create tasks, set the first field to the exact team name (do NOT assign to individual student names) UNLESS the transcript explicitly names an individual for that task.\n\n");
        } else {
            sb.append("Assign tasks to individuals mentioned in the transcript (use the exact person name as appears in transcript or as provided by the system).\n\n");
        }

        // New strong rules to prefer explicit names and handle ASR variants
        sb.append("Important rules:\n");
        sb.append("- If the transcript explicitly names a person for a task (e.g., \"Adrija, complete X\" or \"Adreja, do Y\"), use that exact person name in the first field.\n");
        sb.append("- If a name appears with minor spelling variants, prefer the most frequent form from the transcript or the canonical roster if provided.\n");
        sb.append("- Do NOT use a generic label like 'students', 'everyone', or 'class' if a specific individual's name is present for that task.\n");
        sb.append("- Use generic labels (e.g., 'students') ONLY when the speaker explicitly assigns the task to the whole class (e.g., \"everyone\", \"all of you\", \"students\").\n");
        sb.append("- If multiple individuals are explicitly named for the same task, create separate task lines for each person.\n\n");

        sb.append("Return output strictly in this format:\n\n");
        sb.append("Minutes:\n");
        sb.append("<One clear paragraph summary of the meeting>\n\n");
        sb.append("Tasks:\n");
        sb.append("- Person or Team Name | Task description | Resource1, Resource2, Resource3\n");
        sb.append("- Person or Team Name | Task description | Resource1, Resource2, Resource3\n\n");
        sb.append("Rules:\n");
        sb.append("- Use the pipe symbol (|) to separate exactly three fields per task line.\n");
        sb.append("- Provide up to 3 useful learning resources for each task, separated by commas.\n");
        sb.append("- Resources must be relevant (official docs, tutorials, articles, courses) or full URLs.\n");
        sb.append("- If no resources apply, keep the third field empty (i.e. end with the second pipe).\n");
        sb.append("- Do NOT create tables, do NOT use markdown, do NOT add numbering.\n");
        sb.append("- Return only tasks that are explicitly assigned in the transcript.\n");
        sb.append("- If no tasks are assigned, return exactly:\n\n");
        sb.append("Tasks:\nNone\n\n");
        sb.append("Meeting Transcript:\n");
        sb.append(transcript == null ? "" : transcript.trim());
        return sb.toString();
    }
}