package controller;

import model.FriendRequest;
import model.User;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class FriendRequestController {
    private List<FriendRequest> requests;
    private UserController userController;
    private NotificationController notificationController;
    
    public FriendRequestController(UserController userController) {
        this.requests = new ArrayList<>();
        this.userController = userController;
        this.notificationController = null; // Será definido posteriormente
    }
    
    public void setNotificationController(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    public boolean sendFriendRequest(UUID senderId, UUID receiverId) {

        if (hasPendingRequest(senderId, receiverId) || hasPendingRequest(receiverId, senderId)) {
            return false;
        }

        if (areAlreadyFriends(senderId, receiverId)) {
            return false;
        }

        if (senderId.equals(receiverId)) {
            return false;
        }

        FriendRequest request = new FriendRequest(senderId, receiverId);
        requests.add(request);

        if (notificationController != null) {
            User sender = userController.getUserById(senderId);
            String senderName = sender != null ? sender.getName() : "Usuário Desconhecido";
            notificationController.createFriendRequestNotification(receiverId, senderName);
        }
        
        return true;
    }

    public boolean acceptRequest(UUID requestId) {
        FriendRequest request = getRequestById(requestId);
        if (request != null && request.getStatus() == FriendRequest.RequestStatus.PENDING) {
            request.setStatus(FriendRequest.RequestStatus.ACCEPTED);
            return true;
        }
        return false;
    }

    public boolean rejectRequest(UUID requestId) {
        FriendRequest request = getRequestById(requestId);
        if (request != null && request.getStatus() == FriendRequest.RequestStatus.PENDING) {
            request.setStatus(FriendRequest.RequestStatus.REJECTED);
            return true;
        }
        return false;
    }

    public List<FriendRequest> getPendingRequestsReceived(UUID userId) {
        return requests.stream()
            .filter(r -> r.getReceiverId().equals(userId) && 
                        r.getStatus() == FriendRequest.RequestStatus.PENDING)
            .collect(Collectors.toList());
    }

    public List<FriendRequest> getRequestsSent(UUID userId) {
        return requests.stream()
            .filter(r -> r.getSenderId().equals(userId))
            .collect(Collectors.toList());
    }

    public List<FriendRequest> getAllRequestsForUser(UUID userId) {
        return requests.stream()
            .filter(r -> r.getSenderId().equals(userId) || r.getReceiverId().equals(userId))
            .collect(Collectors.toList());
    }

    public boolean hasPendingRequest(UUID userId1, UUID userId2) {
        return requests.stream()
            .anyMatch(r -> r.getSenderId().equals(userId1) && 
                          r.getReceiverId().equals(userId2) && 
                          r.getStatus() == FriendRequest.RequestStatus.PENDING);
    }

    public boolean areAlreadyFriends(UUID userId1, UUID userId2) {
        boolean request1Accepted = requests.stream()
            .anyMatch(r -> r.getSenderId().equals(userId1) && 
                          r.getReceiverId().equals(userId2) && 
                          r.getStatus() == FriendRequest.RequestStatus.ACCEPTED);
        
        boolean request2Accepted = requests.stream()
            .anyMatch(r -> r.getSenderId().equals(userId2) && 
                          r.getReceiverId().equals(userId1) && 
                          r.getStatus() == FriendRequest.RequestStatus.ACCEPTED);
        
        return request1Accepted || request2Accepted;
    }

    private FriendRequest getRequestById(UUID requestId) {
        return requests.stream()
            .filter(r -> r.getId().equals(requestId))
            .findFirst()
            .orElse(null);
    }

    public int getPendingRequestsCount(UUID userId) {
        return (int) requests.stream()
            .filter(r -> r.getReceiverId().equals(userId) && 
                        r.getStatus() == FriendRequest.RequestStatus.PENDING)
            .count();
    }

    public void cleanupOldRejectedRequests() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        requests.removeIf(r -> r.getStatus() == FriendRequest.RequestStatus.REJECTED && 
                               r.getTimestamp().isBefore(thirtyDaysAgo));
    }

    public List<FriendRequest> getAllRequests() {
        return new ArrayList<>(requests);
    }
}

