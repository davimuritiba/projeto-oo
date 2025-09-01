package controller;

import java.util.UUID;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;

public class FriendController 
    {
        private final Map<UUID, Set<UUID>> friendsByUser;
        private final Map<UUID, Set<UUID>> pendingByRecipient;
        private final Map<UUID, Set<UUID>> pendingBySender;

        public FriendController()
            {
                this.friendsByUser = new HashMap<>();
                this.pendingByRecipient = new HashMap<>();
                this.pendingBySender = new HashMap<>();
            }

        private Set<UUID> ensure(Map<UUID, Set<UUID>> map, UUID key)
            {
                return map.computeIfAbsent(key, k -> new HashSet<UUID>());
            }

        public boolean sendFriendRequest(UUID fromUserId, UUID toUserId)
            {
                if (fromUserId == null || toUserId == null) return false;
                if (fromUserId.equals(toUserId)) return false;
                if (areFriends(fromUserId, toUserId)) return false;

                Set<UUID> sent = ensure(pendingBySender, fromUserId);
                Set<UUID> received = ensure(pendingByRecipient, toUserId);

                if (sent.contains(toUserId) || received.contains(fromUserId)) return false;

                sent.add(toUserId);
                received.add(fromUserId);
                return true;
            }

        public boolean acceptRequest(UUID recipientId, UUID requesterId)
            {
                if (recipientId == null || requesterId == null) return false;
                Set<UUID> recPend = ensure(pendingByRecipient, recipientId);
                Set<UUID> reqSent = ensure(pendingBySender, requesterId);
                if (!recPend.remove(requesterId)) return false;
                reqSent.remove(recipientId);

                ensure(friendsByUser, recipientId).add(requesterId);
                ensure(friendsByUser, requesterId).add(recipientId);
                return true;
            }

        public boolean declineRequest(UUID recipientId, UUID requesterId)
            {
                if (recipientId == null || requesterId == null) return false;
                Set<UUID> recPend = ensure(pendingByRecipient, recipientId);
                Set<UUID> reqSent = ensure(pendingBySender, requesterId);
                boolean removed = recPend.remove(requesterId);
                reqSent.remove(recipientId);
                return removed;
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

        public Set<UUID> getPendingReceived(UUID recipientId)
            {
                return Collections.unmodifiableSet(ensure(pendingByRecipient, recipientId));
            }

        public Set<UUID> getPendingSent(UUID senderId)
            {
                return Collections.unmodifiableSet(ensure(pendingBySender, senderId));
            }
    }


