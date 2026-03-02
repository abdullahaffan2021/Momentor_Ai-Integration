package com.momentor.mentors.Service;

import com.momentor.mentors.DTO.AiResult;
import com.momentor.mentors.entity.Meeting;
import com.momentor.mentors.entity.Moms;
import com.momentor.mentors.entity.Team;
import com.momentor.mentors.repository.MeetingRepository;
import com.momentor.mentors.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiMeetingService {
    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private SpeechToTextService speechToTextService;
    @Autowired
    private AiService aiService;
    @Autowired
    private MomService momService;
    @Autowired
    private TaskService taskService;
    @Async
    public void processmeeting(Long meetingId, String path) {
        try {
            String transcript = speechToTextService.transcribe(new File(path));
            Meeting meeting = meetingRepository.findById(meetingId).orElseThrow();

            // Fetch teams eagerly BEFORE passing to TaskService
            List<Team> teams = "TEAM".equals(meeting.getType()) ?
                    teamRepository.findByMeetingIdWithParticipants(meetingId) :
                    List.of();

            // Extract team names
            List<String> teamNames = teams.stream().map(Team::getName).collect(Collectors.toList());

            AiResult aiResult = aiService.analyze(transcript, meeting.getType(), teamNames);
            Moms mom = momService.save(aiResult.getMom(), meetingId);

            // Pass both teams and meeting type to TaskService
            taskService.saveAiTasks(aiResult.getTasks(), mom, meeting.getType(), teams);
        } catch (Exception e) {
            System.err.println("Error processing meeting: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
