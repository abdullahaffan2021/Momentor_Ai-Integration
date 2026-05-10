package com.momentor.mentors.Service;

import com.momentor.mentors.entity.Task;
import com.momentor.mentors.repository.TaskRepository;
import com.momentor.mentors.repository.userrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TaskReminderService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private userrepository userrepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "*/10 * * * * *") // every 10 seconds (testing)
    public void sendDueDateReminders(){

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        LocalDateTime start = tomorrow.atStartOfDay();
        LocalDateTime end = tomorrow.atTime(23,59,59);

        List<Task> tasks = taskRepository.findByDueDateBetween(start,end);

        for(Task task : tasks){
            if(task.isRemindersent()){
                continue;
            }

            String assigned = task.getAssignedTo();

            userrepository.findByNameIgnoreCase(assigned)
                    .ifPresent(user -> {

                        String email = user.getEmail();
                        DateTimeFormatter formatter =
                                DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy - hh:mm a");
                        String formattedDueDate = task.getDueDate().format(formatter);
                        String htmlMessage =
                                "<h2 style='color:#2E86C1'>Task Reminder</h2>" +
                                        "<p>Hello <b>" + user.getName() + "</b>,</p>" +

                                        "<table border='1' cellpadding='8' style='border-collapse:collapse'>" +
                                        "<tr><th>Task Name</th><td>" + task.getTitle() + "</td></tr>" +
                                        "<tr><th>Due Date</th><td>" + formattedDueDate + "</td></tr>" +
                                        "<tr><th>Priority</th><td style='color:red'><b>" + task.getPriority() + "</b></td></tr>" +
                                        "</table>" +

                                        "<br>" +
                                        "<p style='color:red'><b>⚠ Please complete this task before the deadline.</b></p>" +

                                        "<hr>" +
                                        "<p style='font-size:12px'>MoMentor AI Task Management System</p>";

                        emailService.sendHtmlMail(
                                email,
                                "⚠ Task Reminder - Action Required",
                                htmlMessage
                        );
                        task.setRemindersent(true);
                        taskRepository.save(task);
                        System.out.println("Reminder email sent to: " + email);
                    });
        }
    }
}