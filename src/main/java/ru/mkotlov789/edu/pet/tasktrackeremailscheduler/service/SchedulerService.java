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
import java.util.List;
/**
 * SchedulerService is a service class responsible for sending daily task notifications to users.
 * It uses Spring's scheduling capabilities to send notifications at a specified time each day.
 */
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

    /**
     * This method is called after the bean is constructed and is used for testing purposes.
     * It sends daily task notifications immediately upon startup.
     */
    @PostConstruct
    public void sendDailyTaskNotificationsOnStartup() {
        sendDailyTaskNotifications();
    }


    /**
     * Scheduled method that sends daily task notifications to all users at a specified time (midnight by default).
     * It retrieves user data, filters and processes completed tasks within the last 24 hours, and sends email notifications.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void sendDailyTaskNotifications() {

        List<User> users = userRepository.findAll();
        for (User user : users) {
            List<Task> completedTasks = taskRepository.findByUserId(user.getId());

            Instant twentyFourHoursAgo = Instant.now().minus(Duration.ofHours(24));
            completedTasks.removeIf(task -> (task.getUpdatedAt().toInstant().isBefore(twentyFourHoursAgo)
                    ||!task.isCompleted()));

            int uncompletedTasksCount = countUncompletedTasks(completedTasks);
            String message = buildNotificationMessage(user.getUsername(), completedTasks, uncompletedTasksCount);
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

    private String buildNotificationMessage(String username, List<Task> completedTasks, int uncompletedTasksCount) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Hello, ").append(username).append("!\n");

        if (!completedTasks.isEmpty()) {
            messageBuilder.append("Today, you completed the following tasks:\n");

            for (Task task : completedTasks) {
                messageBuilder.append("- ").append(task.getTitle()).append("\n");
            }
        } else {
            messageBuilder.append("You didn't complete any tasks in the last 24 hours.");
        }
        if (uncompletedTasksCount > 0) {
            messageBuilder.append("You have ").append(uncompletedTasksCount).append(" uncompleted tasks.");
        } else if ( uncompletedTasksCount > 0 && !completedTasks.isEmpty()) {
            messageBuilder.append("Congratulations! All tasks are completed.");
        }

        return messageBuilder.toString();
    }
}
