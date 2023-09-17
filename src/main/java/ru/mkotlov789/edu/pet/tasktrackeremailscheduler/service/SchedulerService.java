package ru.mkotlov789.edu.pet.tasktrackeremailscheduler.service;

import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.mkotlov789.edu.pet.tasktrackeremailscheduler.model.Task;
import ru.mkotlov789.edu.pet.tasktrackeremailscheduler.model.User;
import ru.mkotlov789.edu.pet.tasktrackeremailscheduler.repository.TaskRepository;
import ru.mkotlov789.edu.pet.tasktrackeremailscheduler.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class SchedulerService {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private EmailService emailService;

    //Added for testing purposes
    @PostConstruct
    public void sendDailyTaskNotificationsOnStartup() {
        sendKafkaEmailMessage();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendKafkaEmailMessage() {

        List<User> users = userRepository.findAll();
        for (User user : users) {
            List<Task> userTasks = taskRepository.findByUserId(user.getId());

            Instant twentyFourHoursAgo = Instant.now().minus(Duration.ofHours(24));
            userTasks.removeIf(task -> task.getUpdatedAt().toInstant().isBefore(twentyFourHoursAgo));

            int uncompletedTasksCount = countUncompletedTasks(userTasks);
            String message = buildNotificationMessage(user.getUsername(), userTasks, uncompletedTasksCount);
            emailService.sendEmail(user.getEmail(),message);
        }
    }


    private int countUncompletedTasks(List<Task> tasks) {
        int uncompletedCount = 0;
        for (Task task : tasks) {
            if (!task.isCompleted()) {
                uncompletedCount++;
            }
        }
        return uncompletedCount;
    }

    private String buildNotificationMessage(String username, List<Task> tasks, int uncompletedTasksCount) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Hello, ").append(username).append("!\n");

        if (!tasks.isEmpty()) {
            messageBuilder.append("Today, you completed the following tasks:\n");

            for (Task task : tasks) {
                messageBuilder.append("- ").append(task.getTitle()).append("\n");
            }
        } else {
            messageBuilder.append("You didn't complete any tasks in the last 24 hours.");
        }
        if (uncompletedTasksCount > 0) {
            messageBuilder.append("You have ").append(uncompletedTasksCount).append(" uncompleted tasks.");
        } else if ( uncompletedTasksCount > 0 && !tasks.isEmpty()) {
            messageBuilder.append("Congratulations! All tasks are completed.");
        }

        return messageBuilder.toString();
    }



}
