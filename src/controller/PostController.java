package controller;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Set;
import model.Post;
import model.TextPost;
import model.ImagePost;
import model.VideoPost;
import model.User;

public class PostController {
    List<Post> posts;
    private NotificationController notificationController;
    private UserController userController;

    public PostController() {
        posts = new ArrayList<>();
        this.notificationController = null; // Será definido posteriormente
        this.userController = null; // Será definido posteriormente
    }
    
    public void setNotificationController(NotificationController notificationController) {
        this.notificationController = notificationController;
    }
    
    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    public Post createTextPost(UUID userId, String textContent) {
        Post post = new TextPost(userId, textContent);
        posts.add(post);
        return post;
    }

    public Post createImagePost(UUID userId, String imageUrl, String description) {
        Post post = new ImagePost(userId, imageUrl, description);
        posts.add(post);
        return post;
    }

    public Post createVideoPost(UUID userId, String videoUrl, String description, int duration) {
        Post post = new VideoPost(userId, videoUrl, description, duration);
        posts.add(post);
        return post;
    }

    public Post createPost(UUID userId, String content, String postType) {
        Post post;
        switch (postType.toUpperCase()) {
            case "TEXT":
                post = new TextPost(userId, content);
                break;
            case "IMAGE":

                post = new ImagePost(userId, content, "Imagem compartilhada");
                break;
            case "VIDEO":

                post = new VideoPost(userId, content, "Vídeo compartilhado", 0);
                break;
            default:
                post = new TextPost(userId, content);
        }
        posts.add(post);
        return post;
    }

    public boolean editPost(UUID postId, String newContent, String newPostType) {
        Post post = findPostById(postId);
        if (post == null) return false;

        posts.remove(post);

        Post newPost;
        switch (newPostType.toUpperCase()) {
            case "TEXT":
                newPost = new TextPost(post.getUserId(), newContent);
                break;
            case "IMAGE":
                newPost = new ImagePost(post.getUserId(), newContent, "Imagem editada");
                break;
            case "VIDEO":
                newPost = new VideoPost(post.getUserId(), newContent, "Vídeo editado", 0);
                break;
            default:
                newPost = new TextPost(post.getUserId(), newContent);
        }

        for (UUID like : post.getLikes()) {
            newPost.like(like);
        }

        posts.add(newPost);
        return true;
    }

    private Post findPostById(UUID postId) {
        for (Post p : posts) {
            if (p.getId().equals(postId)) {
                return p;
            }
        }
        return null;
    }

    public boolean deletePost(UUID postId) {
        return posts.removeIf(post -> post.getId().equals(postId));
    }

    public List<Post> getPostsByUser(UUID userId) {
        List<Post> userPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post.getUserId().equals(userId)) {
                userPosts.add(post);
            }
        }
        return userPosts;
    }

    public Post getPostById(UUID postId) {
        return findPostById(postId);
    }

    public boolean likePost(UUID postId, UUID userId) {
        Post post = findPostById(postId);
        if (post == null) return false;
        
        boolean liked = post.like(userId);

        if (liked && notificationController != null && userController != null && 
            !post.getUserId().equals(userId)) {
            User liker = userController.getUserById(userId);
            String likerName = liker != null ? liker.getName() : "Usuário Desconhecido";
            notificationController.createPostLikeNotification(post.getUserId(), likerName, postId);
        }
        
        return liked;
    }

    public boolean unlikePost(UUID postId, UUID userId) {
        Post post = findPostById(postId);
        if (post == null) return false;
        return post.unlike(userId);
    }

    public List<Post> getAllPosts() {
        return new ArrayList<>(posts);
    }

    public List<TextPost> getTextPosts() {
        List<TextPost> textPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post instanceof TextPost) {
                textPosts.add((TextPost) post);
            }
        }
        return textPosts;
    }

    public List<ImagePost> getImagePosts() {
        List<ImagePost> imagePosts = new ArrayList<>();
        for (Post post : posts) {
            if (post instanceof ImagePost) {
                imagePosts.add((ImagePost) post);
            }
        }
        return imagePosts;
    }

    public List<VideoPost> getVideoPosts() {
        List<VideoPost> videoPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post instanceof VideoPost) {
                videoPosts.add((VideoPost) post);
            }
        }
        return videoPosts;
    }
}
