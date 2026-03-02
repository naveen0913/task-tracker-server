package com.task.management.controller;

import com.task.management.config.AuthHelper;
import com.task.management.model.Notification;
import com.task.management.model.User;
import com.task.management.repository.NotificationRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepo notificationRepository;
    private final AuthHelper authHelper;

    @GetMapping
    public List<Notification> getUserNotifications(
            HttpServletRequest request) {
        User user = authHelper.getUserFromRequest(request);
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @GetMapping("/unread-count")
    public long unreadCount(HttpServletRequest request) {
        User user = authHelper.getUserFromRequest(request);
        return notificationRepository
                .countByUserIdAndIsReadFalse(user.getId());
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id,
                           HttpServletRequest request) {

        User user = authHelper.getUserFromRequest(request);

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow();

        // security check
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

}
