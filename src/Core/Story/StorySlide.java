package Core.Story;

import java.util.List;

public record StorySlide(String imageKey, List<String> lines, Runnable onStart, Runnable onEnd) {
    public StorySlide(String imageKey, List<String> lines) {
        this(imageKey, lines, null, null);
    }
}
