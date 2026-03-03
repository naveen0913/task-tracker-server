package com.task.management.service;

import com.task.management.model.Notification;
import com.task.management.model.User;
import com.task.management.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepo notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;


    // Send notification when a task is created
    public void sendTaskCreatedNotification(User user, String taskTitle) {
        sendNotification(user, "New Task Created: " + taskTitle);
    }

    // Send notification when a task is deleted
    public void sendTaskDeletedNotification(User user, String taskTitle) {
        sendNotification(user, "Task Deleted: " + taskTitle);
    }

    // Send notification when a task status is changed
    public void sendTaskStatusChangedNotification(User user, String taskTitle) {
        sendNotification(user, "Task Status Updated: " + taskTitle);
    }

    public void sendTaskDetailsUpdate(User user, String title) {
        sendNotification(user, "Task Details Updated: " + title);
    }

    private void sendNotification(User user, String message) {
        Notification notification = Notification.builder()
                .message(message)
                .createdAt(LocalDateTime.now())
                .user(user)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/notifications",
                notification
        );
    }


}
