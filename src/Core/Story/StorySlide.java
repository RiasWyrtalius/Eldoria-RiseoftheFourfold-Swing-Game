package Core.Story;

import java.util.List;

public record StorySlide(String imageKey, List<String> lines, Runnable onStart) {
    public StorySlide(String imageKey, List<String> lines) {
        this(imageKey, lines, null);
    }
}
