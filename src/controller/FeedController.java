package controller;

import model.Post;
import model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Comparator;

public class FeedController {
    private PostController postController;
    private FriendController friendController;
    private UserController userController;
    
    public FeedController(PostController postController, FriendController friendController, UserController userController) {
        this.postController = postController;
        this.friendController = friendController;
        this.userController = userController;
    }

    public List<Post> getFriendsFeed(UUID userId) {
        if (userId == null) return new ArrayList<>();

        List<UUID> friends = new ArrayList<>(friendController.getFriends(userId));

        if (friends.isEmpty()) {
            return new ArrayList<>();
        }

        List<Post> friendsPosts = new ArrayList<>();
        for (UUID friendId : friends) {
            friendsPosts.addAll(postController.getPostsByUser(friendId));
        }

        friendsPosts.sort(Comparator.comparing(Post::getCreatedAt).reversed());
        
        return friendsPosts;
    }

    public List<Post> getFriendsFeed(UUID userId, int limit) {
        List<Post> allPosts = getFriendsFeed(userId);
        if (allPosts.size() <= limit) {
            return allPosts;
        }
        return allPosts.subList(0, limit);
    }

    public List<Post> getFriendsFeedByType(UUID userId, String postType) {
        List<Post> friendsPosts = getFriendsFeed(userId);
        
        return friendsPosts.stream()
            .filter(post -> post.getPostType().equalsIgnoreCase(postType))
            .collect(Collectors.toList());
    }

    public List<Post> getFriendsFeedMostLiked(UUID userId) {
        List<Post> friendsPosts = getFriendsFeed(userId);

        friendsPosts.sort(Comparator.comparing(Post::getLikeCount).reversed());
        
        return friendsPosts;
    }

    public List<Post> getFriendsFeedFromFriend(UUID userId, UUID friendId) {
        if (userId == null || friendId == null) return new ArrayList<>();

        if (!friendController.areFriends(userId, friendId)) {
            return new ArrayList<>();
        }

        List<Post> friendPosts = postController.getPostsByUser(friendId);

        friendPosts.sort(Comparator.comparing(Post::getCreatedAt).reversed());
        
        return friendPosts;
    }

    public List<Post> getFriendsFeedByDateRange(UUID userId, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        List<Post> friendsPosts = getFriendsFeed(userId);
        
        return friendsPosts.stream()
            .filter(post -> post.getCreatedAt().isAfter(startDate) && post.getCreatedAt().isBefore(endDate))
            .collect(Collectors.toList());
    }

    public List<Post> getFriendsFeedToday(UUID userId) {
        java.time.LocalDateTime startOfDay = java.time.LocalDateTime.now().toLocalDate().atStartOfDay();
        java.time.LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        return getFriendsFeedByDateRange(userId, startOfDay, endOfDay);
    }

    public List<Post> getFriendsFeedThisWeek(UUID userId) {
        java.time.LocalDateTime startOfWeek = java.time.LocalDateTime.now()
            .with(java.time.DayOfWeek.MONDAY)
            .toLocalDate()
            .atStartOfDay();
        java.time.LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);
        
        return getFriendsFeedByDateRange(userId, startOfWeek, endOfWeek);
    }

    public String getFeedStats(UUID userId) {
        List<Post> friendsPosts = getFriendsFeed(userId);
        List<UUID> friends = new ArrayList<>(friendController.getFriends(userId));
        
        if (friends.isEmpty()) {
            return "Você ainda não tem amigos. Adicione alguns amigos para ver posts no seu feed!";
        }
        
        if (friendsPosts.isEmpty()) {
            return String.format("Você tem %d amigos, mas nenhum deles postou ainda.", friends.size());
        }

        long textPosts = friendsPosts.stream().filter(p -> p.getPostType().equals("TEXT")).count();
        long imagePosts = friendsPosts.stream().filter(p -> p.getPostType().equals("IMAGE")).count();
        long videoPosts = friendsPosts.stream().filter(p -> p.getPostType().equals("VIDEO")).count();

        int totalLikes = friendsPosts.stream().mapToInt(Post::getLikeCount).sum();

        Post mostLikedPost = friendsPosts.stream()
            .max(Comparator.comparing(Post::getLikeCount))
            .orElse(null);
        
        String mostLikedInfo = "";
        if (mostLikedPost != null && mostLikedPost.getLikeCount() > 0) {
            User author = userController.getUserById(mostLikedPost.getUserId());
            String authorName = author != null ? author.getName() : "Usuário desconhecido";
            mostLikedInfo = String.format("\nPost mais curtido: %s (%d curtidas)", authorName, mostLikedPost.getLikeCount());
        }
        
        return String.format(
            "📊 Estatísticas do Feed:\n" +
            "👥 Amigos: %d\n" +
            "📝 Total de posts: %d\n" +
            "📄 Posts de texto: %d\n" +
            "🖼️ Posts de imagem: %d\n" +
            "🎥 Posts de vídeo: %d\n" +
            "❤️ Total de curtidas: %d%s",
            friends.size(), friendsPosts.size(), textPosts, imagePosts, videoPosts, totalLikes, mostLikedInfo
        );
    }

    public List<FeedPost> getFriendsFeedWithAuthorInfo(UUID userId) {
        List<Post> friendsPosts = getFriendsFeed(userId);
        List<FeedPost> feedPosts = new ArrayList<>();
        
        for (Post post : friendsPosts) {
            User author = userController.getUserById(post.getUserId());
            String authorName = author != null ? author.getName() : "Usuário desconhecido";
            feedPosts.add(new FeedPost(post, authorName));
        }
        
        return feedPosts;
    }

    public static class FeedPost {
        private Post post;
        private String authorName;
        
        public FeedPost(Post post, String authorName) {
            this.post = post;
            this.authorName = authorName;
        }
        
        public Post getPost() {
            return post;
        }
        
        public String getAuthorName() {
            return authorName;
        }
        
        public String getDisplayText() {
            return String.format("[%s] %s: %s", 
                post.getFormattedDate(), 
                authorName, 
                post.getDisplayContent()
            );
        }
    }
}
