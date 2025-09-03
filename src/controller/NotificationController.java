package controller;

import model.Notification;
import java.util.*;
import java.util.stream.Collectors;

public class NotificationController {
    private List<Notification> notifications;
    private UserController userController;

    public NotificationController(UserController userController) {
        this.notifications = new ArrayList<>();
        this.userController = userController;
    }

    public Notification createNotification(UUID userId, String type, String title, String message, UUID relatedId) {
        Notification notification = new Notification(userId, type, title, message, relatedId);
        notifications.add(notification);
        return notification;
    }

    public List<Notification> getNotificationsByUser(UUID userId) {
        return notifications.stream()
                .filter(n -> n.getUserId().equals(userId))
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Notification> getUnreadNotificationsByUser(UUID userId) {
        return notifications.stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public boolean markAsRead(UUID notificationId) {
        for (Notification notification : notifications) {
            if (notification.getId().equals(notificationId)) {
                notification.setRead(true);
                return true;
            }
        }
        return false;
    }

    public void markAllAsRead(UUID userId) {
        notifications.stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .forEach(n -> n.setRead(true));
    }

    public int getUnreadCount(UUID userId) {
        return (int) notifications.stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .count();
    }

    public boolean deleteNotification(UUID notificationId) {
        return notifications.removeIf(n -> n.getId().equals(notificationId));
    }

    public void deleteAllNotifications(UUID userId) {
        notifications.removeIf(n -> n.getUserId().equals(userId));
    }

    public List<Notification> getNotificationsByType(UUID userId, String type) {
        return notifications.stream()
                .filter(n -> n.getUserId().equals(userId) && n.getType().equals(type))
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public Notification createFriendRequestNotification(UUID userId, String requesterName) {
        return createNotification(userId, "FRIEND_REQUEST", 
            "Nova solicitação de amizade", 
            requesterName + " quer ser seu amigo!", 
            null);
    }

    public Notification createMessageNotification(UUID userId, String senderName, UUID messageId) {
        return createNotification(userId, "MESSAGE", 
            "Nova mensagem", 
            senderName + " enviou uma mensagem para você", 
            messageId);
    }

    public Notification createPostLikeNotification(UUID userId, String likerName, UUID postId) {
        return createNotification(userId, "POST_LIKE", 
            "Post curtido", 
            likerName + " curtiu seu post", 
            postId);
    }

    public Notification createGroupInviteNotification(UUID userId, String groupName, String inviterName, UUID groupId) {
        return createNotification(userId, "GROUP_INVITE", 
            "Convite para grupo", 
            inviterName + " convidou você para o grupo " + groupName, 
            groupId);
    }

    public Notification createEventReminderNotification(UUID userId, String eventName, UUID eventId) {
        return createNotification(userId, "EVENT_REMINDER", 
            "Lembrete de evento", 
            "O evento " + eventName + " está próximo!", 
            eventId);
    }

    public Map<String, Integer> getNotificationStats(UUID userId) {
        Map<String, Integer> stats = new HashMap<>();
        List<Notification> userNotifications = getNotificationsByUser(userId);
        
        stats.put("total", userNotifications.size());
        stats.put("unread", getUnreadCount(userId));

        Map<String, Long> typeCounts = userNotifications.stream()
                .collect(Collectors.groupingBy(Notification::getType, Collectors.counting()));
        
        for (Map.Entry<String, Long> entry : typeCounts.entrySet()) {
            stats.put(entry.getKey().toLowerCase(), entry.getValue().intValue());
        }
        
        return stats;
    }

    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications);
    }
}
