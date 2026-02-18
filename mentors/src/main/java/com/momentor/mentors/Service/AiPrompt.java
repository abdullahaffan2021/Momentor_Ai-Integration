package com.momentor.mentors.Service;

public class AiPrompt {

    public static final String PROMPT = """
You are an AI meeting assistant.

Read the meeting transcript carefully.

Return output strictly in this format:

Minutes:
<One clear paragraph summary of the meeting>

Tasks:
- Person Name | Task description
- Person Name | Task description

Rules:
- Do NOT create tables
- Do NOT use markdown
- Do NOT add numbering
- Only use the dash format shown above
- Only extract tasks that are clearly assigned

Meeting Transcript:
<<<TRANSCRIPT>>>
""";
}
