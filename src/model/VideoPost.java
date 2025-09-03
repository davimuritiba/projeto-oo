package model;

import java.util.UUID;

public class VideoPost extends Post {
    private String videoUrl;
    private String description;
    private int duration;

    public VideoPost(UUID userId, String videoUrl, String description, int duration) {
        super(userId, videoUrl + " | " + description + " | " + formatDurationStatic(duration));
        this.videoUrl = videoUrl;
        this.description = description;
        this.duration = duration;
    }
    
    private static String formatDurationStatic(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    @Override
    public String getContent() {
        return videoUrl + " | " + description + " | " + formatDuration(duration);
    }

    @Override
    public String getPostType() {
        return "VIDEO";
    }

    @Override
    public String getDisplayContent() {
        return "🎥 " + videoUrl + "\n📝 " + description + "\n⏱️ " + formatDuration(duration);
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
