package com.momentor.mentors.Service;

import com.momentor.mentors.DTO.AiResult;
import com.momentor.mentors.entity.Moms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AiMeetingService {
    @Autowired
    private SpeechToTextService speechToTextService;
    @Autowired
    private AiService aiService;
    @Autowired
    private MomService momService;
    @Autowired
    private TaskService taskService;
    @Async //We didnt use this annotation audio uploads may be delay.
    public void processmeeting(Long meetingId, String path) {
        //1.Whisper Ai Agent
        String transcript=speechToTextService.transcribe(new File(path));
        //2.GPT
        AiResult aiResult=aiService.analyze(transcript);
        //3.Save Mom
        Moms mom=momService.save(aiResult.getMom(),meetingId);
        //4.Save Ai Task as Draft
        taskService.saveAiTasks(aiResult.getTasks(),mom);
    }
}
