package controller;

import model.GroupMessage;
import model.Group;
import model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GroupChatController {
    private List<GroupMessage> messages;
    private GroupController groupController;
    private UserController userController;
    
    public GroupChatController(GroupController groupController, UserController userController) {
        this.messages = new ArrayList<>();
        this.groupController = groupController;
        this.userController = userController;
    }

    public GroupMessage sendMessage(UUID groupId, UUID senderId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Conteúdo da mensagem não pode estar vazio");
        }
        
        if (groupId == null) {
            throw new IllegalArgumentException("ID do grupo não pode ser nulo");
        }
        
        if (senderId == null) {
            throw new IllegalArgumentException("ID do remetente não pode ser nulo");
        }

        Group group = groupController.getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Grupo não encontrado");
        }

        if (!groupController.isMember(groupId, senderId)) {
            throw new IllegalArgumentException("Usuário não é membro deste grupo");
        }
        
        GroupMessage message = new GroupMessage(groupId, senderId, content.trim());
        messages.add(message);
        return message;
    }

    public List<GroupMessage> getGroupMessages(UUID groupId) {
        if (groupId == null) return new ArrayList<>();
        
        return messages.stream()
            .filter(msg -> msg.getGroupId().equals(groupId))
            .sorted((m1, m2) -> m1.getSentAt().compareTo(m2.getSentAt()))
            .collect(Collectors.toList());
    }

    public List<GroupMessage> getGroupMessages(UUID groupId, int limit) {
        List<GroupMessage> groupMessages = getGroupMessages(groupId);
        if (groupMessages.size() <= limit) {
            return groupMessages;
        }
        return groupMessages.subList(groupMessages.size() - limit, groupMessages.size());
    }

    public List<GroupMessage> getGroupMessagesByUser(UUID groupId, UUID userId) {
        if (groupId == null || userId == null) return new ArrayList<>();
        
        return messages.stream()
            .filter(msg -> msg.getGroupId().equals(groupId) && msg.getSenderId().equals(userId))
            .sorted((m1, m2) -> m1.getSentAt().compareTo(m2.getSentAt()))
            .collect(Collectors.toList());
    }

    public boolean deleteMessage(UUID messageId, UUID userId) {
        if (messageId == null || userId == null) return false;
        
        for (int i = 0; i < messages.size(); i++) {
            GroupMessage message = messages.get(i);
            if (message.getId().equals(messageId)) {

                if (message.getSenderId().equals(userId) || 
                    groupController.canModify(message.getGroupId(), userId)) {
                    messages.remove(i);
                    return true;
                }
                return false; // Sem permissão
            }
        }
        return false; // Mensagem não encontrada
    }

    public boolean editMessage(UUID messageId, UUID userId, String newContent) {
        if (messageId == null || userId == null || newContent == null || newContent.trim().isEmpty()) {
            return false;
        }
        
        for (GroupMessage message : messages) {
            if (message.getId().equals(messageId)) {

                if (message.getSenderId().equals(userId)) {

                    messages.remove(message);
                    GroupMessage newMessage = new GroupMessage(message.getGroupId(), userId, newContent.trim());
                    newMessage.setId(messageId); // Manter o mesmo ID
                    messages.add(newMessage);
                    return true;
                }
                return false; // Sem permissão
            }
        }
        return false; // Mensagem não encontrada
    }

    public String getGroupChatStats(UUID groupId) {
        if (groupId == null) return "Grupo não especificado";
        
        List<GroupMessage> groupMessages = getGroupMessages(groupId);
        if (groupMessages.isEmpty()) {
            return "Nenhuma mensagem neste grupo";
        }
        
        long totalMessages = groupMessages.size();
        long uniqueUsers = groupMessages.stream()
            .map(GroupMessage::getSenderId)
            .distinct()
            .count();
        
        GroupMessage firstMessage = groupMessages.get(0);
        GroupMessage lastMessage = groupMessages.get(groupMessages.size() - 1);
        
        return String.format("Total de mensagens: %d\nUsuários ativos: %d\nPrimeira mensagem: %s\nÚltima mensagem: %s",
            totalMessages, uniqueUsers, firstMessage.getFormattedDate(), lastMessage.getFormattedDate());
    }

    public boolean clearGroupChat(UUID groupId, UUID userId) {
        if (groupId == null || userId == null) return false;
        
        if (!groupController.canModify(groupId, userId)) {
            return false; // Sem permissão
        }
        
        messages.removeIf(msg -> msg.getGroupId().equals(groupId));
        return true;
    }

    public List<GroupMessage> searchMessages(UUID groupId, String searchTerm) {
        if (groupId == null || searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String term = searchTerm.toLowerCase().trim();
        return messages.stream()
            .filter(msg -> msg.getGroupId().equals(groupId) && 
                          msg.getContent().toLowerCase().contains(term))
            .sorted((m1, m2) -> m1.getSentAt().compareTo(m2.getSentAt()))
            .collect(Collectors.toList());
    }

    public int getTotalMessages() {
        return messages.size();
    }

    public int getGroupMessageCount(UUID groupId) {
        if (groupId == null) return 0;
        
        return (int) messages.stream()
            .filter(msg -> msg.getGroupId().equals(groupId))
            .count();
    }

    public void clearAllMessages() {
        messages.clear();
    }
}
