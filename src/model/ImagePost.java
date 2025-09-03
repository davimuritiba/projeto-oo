package model;

import java.util.UUID;

public class ImagePost extends Post {
    private String imageUrl;
    private String description;

    public ImagePost(UUID userId, String imageUrl, String description) {
        super(userId, imageUrl + " | " + description);
        this.imageUrl = imageUrl;
        this.description = description;
    }

    @Override
    public String getContent() {
        return imageUrl + " | " + description;
    }

    @Override
    public String getPostType() {
        return "IMAGE";
    }

    @Override
    public String getDisplayContent() {
        return "🖼️ " + imageUrl + "\n📝 " + description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
