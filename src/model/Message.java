package model;

import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message extends Content {
    private UUID receiverId;
    private boolean read;

    public Message(UUID senderId, UUID receiverId, String content) {
        super(senderId, content);
        this.receiverId = receiverId;
        this.read = false;
    }
    
    @Override
    public String getContentType() {
        return "MESSAGE";
    }
    
    @Override
    public String getFormattedContent() {
        return "💬 " + getContent();
    }
    
    @Override
    public boolean isValid() {
        return getContent() != null && !getContent().trim().isEmpty() && receiverId != null;
    }
    
    @Override
    public int getContentSize() {
        return getContent() != null ? getContent().length() : 0;
    }

    public UUID getSenderId() {
        return getAuthorId();
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public LocalDateTime getSentDate() {
        return getCreatedAt();
    }

    public boolean isRead() {
        return read;
    }

    public void markAsRead() {
        this.read = true;
    }

    public void markAsUnread() {
        this.read = false;
    }

    public String getRelativeDate() {
        return getRelativeTime();
    }

    public String getTruncatedContent(int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }

    public boolean isToday() {
        LocalDateTime now = LocalDateTime.now();
        return getCreatedAt().toLocalDate().equals(now.toLocalDate());
    }

    @Override
    public String toString() {
        return String.format("Message{id=%s, sender=%s, receiver=%s, content='%s', read=%s, date=%s}",
                getId().toString().substring(0, 8) + "...",
                getSenderId().toString().substring(0, 8) + "...",
                receiverId.toString().substring(0, 8) + "...",
                getTruncatedContent(30),
                read,
                getFormattedDate());
    }
}
