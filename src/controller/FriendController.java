package controller;

import java.util.UUID;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import model.FriendRequest;

public class FriendController {
    private final Map<UUID, Set<UUID>> friendsByUser;
    private FriendRequestController friendRequestController;
    
    public FriendController(FriendRequestController friendRequestController) {
        this.friendsByUser = new HashMap<>();
        this.friendRequestController = friendRequestController;
    }
    
    public FriendController() {
        this.friendsByUser = new HashMap<>();

    }

        private Set<UUID> ensure(Map<UUID, Set<UUID>> map, UUID key)
            {
                return map.computeIfAbsent(key, k -> new HashSet<UUID>());
            }

        public boolean sendFriendRequest(UUID fromUserId, UUID toUserId) {
            if (fromUserId == null || toUserId == null) return false;
            if (fromUserId.equals(toUserId)) return false;
            if (areFriends(fromUserId, toUserId)) return false;

            if (friendRequestController != null) {
                return friendRequestController.sendFriendRequest(fromUserId, toUserId);
            }

            return false;
        }

        public boolean acceptRequest(UUID recipientId, UUID requesterId) {
            if (recipientId == null || requesterId == null) return false;

            if (friendRequestController != null) {

                List<FriendRequest> pendingRequests = friendRequestController.getPendingRequestsReceived(recipientId);
                for (FriendRequest request : pendingRequests) {
                    if (request.getSenderId().equals(requesterId)) {
                        if (friendRequestController.acceptRequest(request.getId())) {
                            ensure(friendsByUser, recipientId).add(requesterId);
                            ensure(friendsByUser, requesterId).add(recipientId);
                            return true;
                        }
                    }
                }
                return false;
            }

            return false;
        }

        public boolean declineRequest(UUID recipientId, UUID requesterId) {
            if (recipientId == null || requesterId == null) return false;

            if (friendRequestController != null) {

                List<FriendRequest> pendingRequests = friendRequestController.getPendingRequestsReceived(recipientId);
                for (FriendRequest request : pendingRequests) {
                    if (request.getSenderId().equals(requesterId)) {
                        return friendRequestController.rejectRequest(request.getId());
                    }
                }
                return false;
            }

            return false;
        }

        public boolean removeFriend(UUID userId, UUID friendId)
            {
                if (userId == null || friendId == null) return false;
                boolean a = ensure(friendsByUser, userId).remove(friendId);
                boolean b = ensure(friendsByUser, friendId).remove(userId);
                return a || b;
            }

        public boolean areFriends(UUID a, UUID b)
            {
                return ensure(friendsByUser, a).contains(b) && ensure(friendsByUser, b).contains(a);
            }

        public Set<UUID> getFriends(UUID userId)
            {
                return Collections.unmodifiableSet(ensure(friendsByUser, userId));
            }

        public Set<UUID> getPendingReceived(UUID recipientId) {
            if (friendRequestController != null) {
                List<FriendRequest> pendingRequests = friendRequestController.getPendingRequestsReceived(recipientId);
                Set<UUID> senderIds = new HashSet<>();
                for (FriendRequest request : pendingRequests) {
                    senderIds.add(request.getSenderId());
                }
                return Collections.unmodifiableSet(senderIds);
            }
            return Collections.emptySet();
        }

        public Set<UUID> getPendingSent(UUID senderId) {
            if (friendRequestController != null) {
                List<FriendRequest> sentRequests = friendRequestController.getRequestsSent(senderId);
                Set<UUID> receiverIds = new HashSet<>();
                for (FriendRequest request : sentRequests) {
                    if (request.getStatus() == FriendRequest.RequestStatus.PENDING) {
                        receiverIds.add(request.getReceiverId());
                    }
                }
                return Collections.unmodifiableSet(receiverIds);
            }
            return Collections.emptySet();
        }
    }


