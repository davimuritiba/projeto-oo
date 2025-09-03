package controller;

import model.Message;
import model.User;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import model.Privacy;

public class MessageController {
    private List<Message> messages;
    private FriendController friendController;
    private UserController userController;
    private NotificationController notificationController;

    public MessageController(FriendController friendController, UserController userController) {
        this.messages = new ArrayList<>();
        this.friendController = friendController;
        this.userController = userController;
        this.notificationController = null; // Será definido posteriormente
    }
    
    public void setNotificationController(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    public Message sendMessage(UUID senderId, UUID receiverId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("O conteúdo da mensagem não pode estar vazio");
        }

        if (!friendController.areFriends(senderId, receiverId)) {
            throw new IllegalArgumentException("Você só pode enviar mensagens para amigos");
        }

        User sender = userController.getUserById(senderId);
        User receiver = userController.getUserById(receiverId);
        
        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        
        Message message = new Message(senderId, receiverId, content.trim());
        messages.add(message);

        if (notificationController != null) {
            notificationController.createMessageNotification(receiverId, sender.getName(), message.getId());
        }
        
        return message;
    }

    public List<Message> getMessages(UUID userId) {
        return messages.stream()
                .filter(msg -> msg.getReceiverId().equals(userId) || msg.getSenderId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Message> getMessagesSent(UUID userId) {
        return messages.stream()
                .filter(msg -> msg.getSenderId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Message> getMessagesReceived(UUID userId) {
        return messages.stream()
                .filter(msg -> msg.getReceiverId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Message> getConversation(UUID user1Id, UUID user2Id) {
        return messages.stream()
                .filter(msg -> 
                    (msg.getSenderId().equals(user1Id) && msg.getReceiverId().equals(user2Id)) ||
                    (msg.getSenderId().equals(user2Id) && msg.getReceiverId().equals(user1Id))
                )
                .collect(Collectors.toList());
    }

    public boolean markMessageAsRead(UUID messageId) {
        for (Message msg : messages) {
            if (msg.getId().equals(messageId)) {
                msg.markAsRead();
                return true;
            }
        }
        return false;
    }

    public void markConversationAsRead(UUID user1Id, UUID user2Id) {
        messages.stream()
                .filter(msg -> 
                    msg.getReceiverId().equals(user1Id) && 
                    msg.getSenderId().equals(user2Id) && 
                    !msg.isRead()
                )
                .forEach(Message::markAsRead);
    }

    public boolean deleteMessage(UUID messageId, UUID userId) {
        return messages.removeIf(msg -> 
            msg.getId().equals(messageId) && msg.getSenderId().equals(userId)
        );
    }

    public int getUnreadMessageCount(UUID userId) {
        return (int) messages.stream()
                .filter(msg -> msg.getReceiverId().equals(userId) && !msg.isRead())
                .count();
    }

    public List<Message> getAllMessages() {
        return new ArrayList<>(messages);
    }

    public void clearAllMessages() {
        messages.clear();
    }
}
