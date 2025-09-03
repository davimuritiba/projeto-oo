package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class GroupMessage {
    private UUID id;
    private UUID groupId;
    private UUID senderId;
    private String content;
    private LocalDateTime sentAt;
    
    public GroupMessage(UUID groupId, UUID senderId, String content) {
        this.id = UUID.randomUUID();
        this.groupId = groupId;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getGroupId() {
        return groupId;
    }
    
    public UUID getSenderId() {
        return senderId;
    }
    
    public String getContent() {
        return content;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return sentAt.format(formatter);
    }
    
    public String getRelativeDate() {
        LocalDateTime now = LocalDateTime.now();
        long days = java.time.Duration.between(sentAt, now).toDays();
        
        if (days == 0) {
            long hours = java.time.Duration.between(sentAt, now).toHours();
            if (hours == 0) {
                long minutes = java.time.Duration.between(sentAt, now).toMinutes();
                return minutes + " min atrás";
            }
            return hours + "h atrás";
        } else if (days == 1) {
            return "Ontem";
        } else if (days < 7) {
            return days + " dias atrás";
        } else {
            return getFormattedDate();
        }
    }
    
    public String getTruncatedContent(int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s", getFormattedDate(), content);
    }
}
