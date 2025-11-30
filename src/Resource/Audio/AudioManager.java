package Resource.Audio;

import Core.Utils.LogManager;

import javax.sound.sampled.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static final AudioManager INSTANCE = new AudioManager();
    private final Map<String, String> audioRegistry = new HashMap<>();

    private Clip currentMusicClip;
    private boolean isMuted = false;
    private float masterVolume = -10.0f;

    private AudioManager() {}

    public static AudioManager getInstance() { return INSTANCE; }

    public void registerSound(String audioID, String filePath) {
        audioRegistry.put(audioID, filePath);
        LogManager.log("Registered Audio: " + audioID);
    }

    /**
     * Plays background music, using Registered ID.
     * @param audioID
     */
    public void playMusic(String audioID) {
        if (isMuted) return;

        String path = audioRegistry.get(audioID);
        if (path == null) {
            LogManager.log("Error: Audio ID not found! " + audioID, Color.red);
            return;
        }

        stopMusic();

        try {
            Clip clip = loadClip(path);
            if (clip == null) return;

            setVolume(clip, masterVolume);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            this.currentMusicClip = clip;
        } catch (Exception e) {
            LogManager.log("Error plying music: " + e.getMessage(), Color.red);
        }
    }

    /**
     * Play sound effect using registered ID
     * @param audioID
     */
    public void playSound(String audioID) {
        if (isMuted) return;

        String path = audioRegistry.get(audioID);
        if (path == null) {
            LogManager.log("Error: SFX ID not found: " + audioID, Color.red);
            return;
        }

        try {
            Clip clip = loadClip(path);
            if (clip == null) return;
            setVolume(clip, masterVolume);

            // bro this listens to close resources
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

            clip.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stopMusic() {
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
            currentMusicClip.close();
            currentMusicClip = null;
        }
    }

    private Clip loadClip(String filePath) throws Exception {
        URL url = getClass().getResource(filePath);
        String resourcePath = filePath;
//        if (!resourcePath.startsWith("/")) {
//            resourcePath = "/" + resourcePath;
//        }
//        LogManager.log("Looking for audio file at: " + resourcePath);

        if (url == null) {
            LogManager.log("Audio file missing: " + filePath, Color.red);
            return null;
        }
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        return clip;
    }

    private void setVolume(Clip clip, float decibels) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(decibels);
        }
    }
}
