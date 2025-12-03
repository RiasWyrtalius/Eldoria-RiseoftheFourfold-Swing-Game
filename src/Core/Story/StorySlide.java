package Core.Story;

import java.util.List;

public record StorySlide(String imageKey, List<String> lines, Runnable onRun) {}
