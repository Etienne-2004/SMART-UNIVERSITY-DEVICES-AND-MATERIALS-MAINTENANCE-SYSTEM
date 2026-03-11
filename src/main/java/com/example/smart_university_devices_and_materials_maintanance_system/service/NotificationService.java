package com.example.smart_university_devices_and_materials_maintanance_system.service;

import com.example.smart_university_devices_and_materials_maintanance_system.entities.*;
import com.example.smart_university_devices_and_materials_maintanance_system.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification send(User recipient, String title, String message,
            Notification.NotificationType type,
            MaintenanceRequest request) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .type(type)
                .maintenanceRequest(request)
                .read(false)
                .build();
        Notification saved = notificationRepository.save(notification);
        log.info("📨 Notification sent to {}: {}", recipient.getEmail(), title);
        return saved;
    }

    public List<Notification> getUnreadForUser(Long userId) {
        return notificationRepository.findByRecipientIdAndRead(userId, false);
    }

    public List<Notification> getAllForUser(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByRecipientIdAndRead(userId, false);
    }

    public void markAllRead(Long userId) {
        List<Notification> unread = notificationRepository.findByRecipientIdAndRead(userId, false);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    public void markRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
