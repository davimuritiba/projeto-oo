package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Notification {
    private UUID id;
    private UUID userId;
    private String type;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
    private UUID relatedId;

    public Notification(UUID userId, String type, String title, String message, UUID relatedId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.relatedId = relatedId;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public UUID getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(UUID relatedId) {
        this.relatedId = relatedId;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return createdAt.format(formatter);
    }

    public String getRelativeTime() {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createdAt, now).toMinutes();
        
        if (minutes < 1) {
            return "Agora mesmo";
        } else if (minutes < 60) {
            return minutes + " min atrás";
        } else if (minutes < 1440) { // 24 horas
            long hours = minutes / 60;
            return hours + "h atrás";
        } else {
            long days = minutes / 1440;
            return days + " dias atrás";
        }
    }

    public String getDisplayText() {
        String status = isRead ? "✅" : "🔔";
        return String.format("%s [%s] %s: %s (%s)", 
            status, type, title, message, getRelativeTime());
    }

    public String getIcon() {
        switch (type) {
            case "FRIEND_REQUEST":
                return "👥";
            case "MESSAGE":
                return "💬";
            case "POST_LIKE":
                return "❤️";
            case "GROUP_INVITE":
                return "👥";
            case "EVENT_REMINDER":
                return "📅";
            default:
                return "🔔";
        }
    }

    @Override
    public String toString() {
        return getDisplayText();
    }
}
