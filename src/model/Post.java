package model;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

public abstract class Post extends Content {
    private Set<UUID> likes;

    public Post(UUID userId, String content) {
        super(userId, content);
        this.likes = new HashSet<>();
    }

    public abstract String getPostType();
    public abstract String getDisplayContent();
    
    @Override
    public String getContentType() {
        return "POST_" + getPostType();
    }
    
    @Override
    public String getFormattedContent() {
        return getDisplayContent();
    }
    
    @Override
    public boolean isValid() {
        return getContent() != null && !getContent().trim().isEmpty();
    }
    
    @Override
    public int getContentSize() {
        return getContent() != null ? getContent().length() : 0;
    }

    public UUID getUserId() {
        return getAuthorId();
    }

    public boolean like(UUID userId) {
        return likes.add(userId); 
    }

    public boolean unlike(UUID userId) {
        return likes.remove(userId); 
    }

    public int getLikeCount() {
        return likes.size();
    }

    public Set<UUID> getLikes() {
        return likes;
    }
}