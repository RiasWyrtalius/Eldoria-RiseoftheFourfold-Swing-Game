package Core.GameFlow;

import Core.Utils.LogFormat;
import Core.Utils.LogManager;

import java.io.*;

public class SaveManager {
    private static final String SAVE_FILE_PATH = "elordia_gay.dat";

    private SaveManager() {}

    public static void saveGame(GameState data) {
        try (
                FileOutputStream fileOut = new FileOutputStream(SAVE_FILE_PATH);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);){
            objectOut.writeObject(data);
            LogManager.log("Game Saved Successfully!", LogFormat.SYSTEM);
        } catch (IOException e) {
            LogManager.log("ERROR: Failed to save game! - " + e.getMessage(), LogFormat.SYSTEM_ERROR);
        }
    }

    /**
     * Reads and deserializes the GameState object from a file.
     * @return The loaded GameState object, or null if loading fails or no file exists.
     */
    public static GameState loadGame() {
        File saveFile = new File(SAVE_FILE_PATH);

        // Check if the file even exists
        if (!saveFile.exists()) {
            LogManager.log("No save file found at: " + SAVE_FILE_PATH, LogFormat.SYSTEM);
            return null;
        }

        try (
                FileInputStream fileIn = new FileInputStream(saveFile);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)
        ) {
            // Read the object from the stream
            GameState loadedState = (GameState) objectIn.readObject();
            LogManager.log("Save Game Loaded Successfully!", LogFormat.SYSTEM);
            return loadedState;
        } catch (IOException | ClassNotFoundException e) {
            LogManager.log("ERROR: Failed to load save file! It might be corrupted. - " + e.getMessage(), LogFormat.SYSTEM_ERROR);
            return null;
        }
    }

    /**
     * A simple check to see if a save file is present.
     * Used by the MainMenu to enable the "Continue" button.
     */
    public static boolean hasSaveFile() {
        return new File(SAVE_FILE_PATH).exists();
    }


}
