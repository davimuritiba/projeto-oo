package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Event extends MemberEntity {
    private LocalDateTime eventDateTime;
    
    public Event(String name, String description, LocalDateTime eventDateTime, UUID creatorId) {
        super(name, description, creatorId);
        this.eventDateTime = eventDateTime;
    }
    
    @Override
    public boolean addMember(UUID userId) {
        if (canAddMember(userId)) {
            return members.add(userId);
        }
        return false;
    }
    
    @Override
    public boolean removeMember(UUID userId) {
        if (isOwner(userId)) {
            return false;
        }
        return members.remove(userId);
    }
    
    @Override
    public boolean canAddMember(UUID userId) {
        if (isMember(userId)) {
            return false;
        }
        if (eventDateTime.isBefore(LocalDateTime.now())) {
            return false;
        }
        return userId != null;
    }
    
    @Override
    public String getEntityType() {
        return "EVENT";
    }
    
    @Override
    public String getEntityInfo() {
        return String.format("Evento %s - %d participantes (Data: %s)", 
            name, getMemberCount(), getFormattedEventDate());
    }
    
    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }
    
    public UUID getCreatorId() {
        return getOwnerId();
    }
    
    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }
    
    public String getFormattedEventDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return eventDateTime.format(formatter);
    }
    
    public String getRelativeEventDate() {
        LocalDateTime now = LocalDateTime.now();
        long days = java.time.Duration.between(now, eventDateTime).toDays();
        
        if (days < 0) {
            return "Evento já passou";
        } else if (days == 0) {
            long hours = java.time.Duration.between(now, eventDateTime).toHours();
            if (hours <= 0) {
                return "Evento acontecendo agora";
            }
            return "Hoje às " + eventDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else if (days == 1) {
            return "Amanhã às " + eventDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else if (days < 7) {
            return "Em " + days + " dias";
        } else {
            return getFormattedEventDate();
        }
    }
    
    public boolean isPast() {
        return eventDateTime.isBefore(LocalDateTime.now());
    }
    
    public boolean isToday() {
        LocalDateTime now = LocalDateTime.now();
        return eventDateTime.toLocalDate().equals(now.toLocalDate());
    }
    
    public boolean isTomorrow() {
        LocalDateTime now = LocalDateTime.now();
        return eventDateTime.toLocalDate().equals(now.toLocalDate().plusDays(1));
    }
    
    public boolean isThisWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekFromNow = now.plusWeeks(1);
        return eventDateTime.isAfter(now) && eventDateTime.isBefore(weekFromNow);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%s)", name, description, getFormattedEventDate());
    }
}
