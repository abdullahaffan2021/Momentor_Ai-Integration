package com.momentor.mentors.Service;

import java.util.List;

public class AiPrompt {
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
        }  else {
            sb.append("Assign tasks to individuals mentioned in the transcript (use the exact person name as appears in transcript or as provided by the system).\n\n");
            // NEW: require resources for solo meetings
            sb.append("IMPORTANT (SOLO meetings): For each task line include the Resources field (third field). If no resources apply, keep the third field empty (i.e. end with the second pipe). Always include the third field even if empty.\n\n");
        }

        // New strong rules to prefer explicit names and handle ASR variants
        sb.append("Important rules:\n");
        sb.append("- If the transcript explicitly names a person for a task (e.g., \"Adrija, complete X\" or \"Adreja, do Y\"), use that exact person name in the first field.\n");
        sb.append("- If a name appears with minor spelling variants, prefer the most frequent form from the transcript or the canonical roster if provided.\n");
        sb.append("- Do NOT use a generic label like 'students', 'everyone', or 'class' if a specific individual's name is present for that task.\n");
        sb.append("- If a task is directed to a group (e.g., \"everyone\", \"the team\"), assign it to the most appropriate role group.\n");
        sb.append("- If multiple individuals are explicitly named for the same task, create separate task lines for each person.\n\n");

        sb.append("Return output strictly in this format:\n\n");
        sb.append("Minutes:\n");
        sb.append("<One clear paragraph summary of the meeting>\n\n");
        sb.append("Tasks:\n");
        sb.append("- Person or Team Name | Task description | Resource1, Resource2 | Deadline(optional) | URGENT(optional)\n");
        sb.append("- Person or Team Name | Task description | Resource1, Resource2 | Deadline(optional) | URGENT(optional)\n\n");
        sb.append("Rules:\n");
        sb.append("- Use the pipe symbol (|) to separate up to five fields per task line.\n");
        sb.append("- Field order: AssignedTo | Task | Resources | Deadline(optional) | URGENT(optional)\n");
        sb.append("- Task description must be action-oriented and preserve important details such as page numbers, chapter names, question numbers, or definitions.\n");
        sb.append("- If deadline is mentioned in transcript, include it.\n");
        sb.append("- Only mark URGENT if the transcript explicitly contains words like urgent, immediately, ASAP, today, or right now.\n");
        sb.append("- Resources field rules:\n");
        sb.append("- Include up to 3 relevant resources per task.\n");
        sb.append("- If resources are mentioned in the transcript, include them.\n");
        sb.append("- If none are mentioned, suggest helpful resources related to the task.\n");
        sb.append("- Prefer official documentation, books, or trusted educational websites.\n");
        sb.append("- If no useful resource exists, leave the resources field empty.\n");
        sb.append("- Do NOT create tables, do NOT use markdown, do NOT add numbering.\n");
        sb.append("- Do NOT invent tasks that are not supported by the transcript.\n");
        sb.append("- Extract tasks only if the transcript clearly indicates that someone needs to do something after the meeting.\n");
        sb.append("- Only extract tasks that clearly require someone to perform an action.\n");
        sb.append("- Do not extract reminders, announcements, or general discussions as tasks unless someone is clearly responsible for completing them.\n");
        sb.append("- Ignore explanations, discussions, opinions, or background information that do not require action.\n");
        sb.append("- Do not create separate tasks for small sub-instructions such as 'note down', 'write', 'solve', or 'review' if they belong to the same assignment. Merge them into a single clear task.\n");
        sb.append("- Prefer fewer clear tasks instead of many fragmented tasks.\n");
        sb.append("- If instructions refer to the same assignment (for example 'complete homework' and 'submit homework'), merge them into a single task describing the full action.\n");
        sb.append("- Do not create separate tasks for preparation steps such as writing, noting down, solving, or submitting if they belong to the same homework or assignment.\n");
        sb.append("- If the speaker says 'I will', assign the task to that speaker.\n");
        sb.append("- If a specific person is mentioned with the task, assign it to that person.\n");
        sb.append("- If no specific owner is mentioned, assign the task to 'Team'.\n");

        sb.append("- Use the most relevant role or group mentioned in the transcript (e.g., Product Managers, Engineering Team, Meeting Participants).\n");
        sb.append("- If no role can be inferred, assign the task to 'Team'.\n");
        sb.append("- If no tasks are assigned, return exactly:\n\n");
        sb.append("Tasks:\nNone\n\n");
        sb.append("Meeting Transcript:\n");
        sb.append(transcript == null ? "" : transcript.trim());
        return sb.toString();
    }
}