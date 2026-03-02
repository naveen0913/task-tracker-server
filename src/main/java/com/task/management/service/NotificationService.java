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

    public void sendTaskCreatedNotification(User user, String taskTitle) {

        Notification notification = Notification.builder()
                .message("New Task Created: " + taskTitle)
                .createdAt(LocalDateTime.now())
                .user(user)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        messagingTemplate.convertAndSendToUser(
                user.getEmail(),   // unique user identifier
                "/queue/notifications",
                notification
        );
    }


}
