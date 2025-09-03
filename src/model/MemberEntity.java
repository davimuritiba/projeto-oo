package model;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

public abstract class MemberEntity {
    protected UUID id;
    protected String name;
    protected String description;
    protected UUID ownerId;
    protected LocalDateTime createdAt;
    protected Set<UUID> members;

    public MemberEntity(String name, String description, UUID ownerId) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.createdAt = LocalDateTime.now();
        this.members = new HashSet<>();
        
        this.members.add(ownerId);
    }

    public abstract boolean addMember(UUID userId);
    public abstract boolean removeMember(UUID userId);
    public abstract boolean canAddMember(UUID userId);
    public abstract String getEntityType();
    public abstract String getEntityInfo();

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<UUID> getMembers() {
        return new HashSet<>(members);
    }

    public boolean isMember(UUID userId) {
        return members.contains(userId);
    }

    public boolean isOwner(UUID userId) {
        return ownerId.equals(userId);
    }

    public int getMemberCount() {
        return members.size();
    }

    public boolean hasMembers() {
        return !members.isEmpty();
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

    public String getSummary() {
        return String.format("%s: %s (%d membros)", 
            getEntityType(), name, getMemberCount());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s - %d membros (%s)", 
            getEntityType(), name, description, getMemberCount(), getFormattedDate());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MemberEntity entity = (MemberEntity) obj;
        return id.equals(entity.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
