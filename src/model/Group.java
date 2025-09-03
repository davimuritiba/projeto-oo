package model;

import java.util.UUID;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Group extends MemberEntity 
{
   private Privacy privacy;
   private Set<UUID> moderators;
   
   public Group(String name, String description, UUID ownerId, Privacy privacy) {
       super(name, description, ownerId);
       this.privacy = privacy;
       this.moderators = new HashSet<>();
       
       this.moderators.add(ownerId);
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
       moderators.remove(userId);
       return members.remove(userId);
   }
   
   @Override
   public boolean canAddMember(UUID userId) {
       if (isMember(userId)) {
           return false;
       }
       return userId != null;
   }
   
   @Override
   public String getEntityType() {
       return "GROUP";
   }
   
   @Override
   public String getEntityInfo() {
       return String.format("Grupo %s (%s) - %d membros, %d moderadores", 
           name, privacy, getMemberCount(), moderators.size());
   }
   
   public Privacy getPrivacy() {
       return privacy;
   }
   
   public Set<UUID> getModerators() {
       return new HashSet<>(moderators);
   }
   
   public void setPrivacy(Privacy privacy) {
       this.privacy = privacy;
   }
   
   public void setOwnerId(UUID newOwnerId) {
       if (!members.contains(newOwnerId)) {
           members.add(newOwnerId);
       }
       moderators.add(newOwnerId);
       this.ownerId = newOwnerId;
   }
   
   public boolean addModerator(UUID userId) {
       if (userId != null && members.contains(userId) && !moderators.contains(userId)) {
           moderators.add(userId);
           return true;
       }
       return false;
   }
   
   public boolean removeModerator(UUID userId) {
       if (userId != null && !userId.equals(ownerId) && moderators.contains(userId)) {
           moderators.remove(userId);
           return true;
       }
       return false;
   }
   
   public boolean isModerator(UUID userId) {
       return moderators.contains(userId);
   }
   
   public boolean canModify(UUID userId) {
       return isOwner(userId) || isModerator(userId);
   }
   

   

   

}
