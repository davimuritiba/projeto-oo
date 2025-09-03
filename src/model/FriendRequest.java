package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FriendRequest {
    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private RequestStatus status;
    private LocalDateTime timestamp;
    
    public enum RequestStatus {
        PENDING("Pendente"),
        ACCEPTED("Aceita"),
        REJECTED("Rejeitada");
        
        private final String displayName;
        
        RequestStatus(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public FriendRequest(UUID senderId, UUID receiverId) {
        this.id = UUID.randomUUID();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = RequestStatus.PENDING;
        this.timestamp = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getSenderId() { return senderId; }
    public UUID getReceiverId() { return receiverId; }
    public RequestStatus getStatus() { return status; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setStatus(RequestStatus status) { this.status = status; }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return timestamp.format(formatter);
    }
    
    public String getRelativeDate() {
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(timestamp, now).toHours();
        
        if (hours < 1) {
            return "Agora mesmo";
        } else if (hours < 24) {
            return hours + "h atrás";
        } else {
            long days = hours / 24;
            return days + " dias atrás";
        }
    }
    
    @Override
    public String toString() {
        return String.format("Solicitação %s - %s", status, getRelativeDate());
    }
}
