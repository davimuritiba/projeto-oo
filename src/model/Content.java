package model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Content {
    protected UUID id;
    protected UUID authorId;
    protected LocalDateTime createdAt;
    protected String content;

    public Content(UUID authorId, String content) {
        this.id = UUID.randomUUID();
        this.authorId = authorId;
        this.createdAt = LocalDateTime.now();
        this.content = content;
    }

    public abstract String getContentType();
    public abstract String getFormattedContent();
    public abstract boolean isValid();
    public abstract int getContentSize();

    public UUID getId() {
        return id;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormattedDate() {
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getRelativeTime() {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createdAt, now).toMinutes();
        
        if (minutes < 1) {
            return "Agora mesmo";
        } else if (minutes < 60) {
            return minutes + " min atrás";
        } else if (minutes < 1440) {
            long hours = minutes / 60;
            return hours + "h atrás";
        } else {
            long days = minutes / 1440;
            return days + " dias atrás";
        }
    }

    public boolean isRecent() {
        LocalDateTime now = LocalDateTime.now();
        return java.time.Duration.between(createdAt, now).toHours() < 24;
    }

    public boolean isOld() {
        LocalDateTime now = LocalDateTime.now();
        return java.time.Duration.between(createdAt, now).toDays() > 7;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", 
            getContentType(), 
            getFormattedDate(), 
            getFormattedContent());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Content content = (Content) obj;
        return id.equals(content.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
