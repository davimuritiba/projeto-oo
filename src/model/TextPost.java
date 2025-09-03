package model;

import java.util.UUID;

public class TextPost extends Post {
    private String textContent;

    public TextPost(UUID userId, String textContent) {
        super(userId, textContent);
        this.textContent = textContent;
    }

    @Override
    public String getContent() {
        return textContent;
    }

    @Override
    public String getPostType() {
        return "TEXT";
    }

    @Override
    public String getDisplayContent() {
        return "[TEXTO] " + textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getTextContent() {
        return textContent;
    }
}
