package UI.Components;
import java.awt.Window;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class IconLoader {
    public static void setIcons(Window window) {
        try {
            List<Image> icons = new ArrayList<>();

            String[] paths = {
                    "Assets/Images/Icons/1024x1024.png",
                    "Assets/Images/Icons/512x512.png",
                    "Assets/Images/Icons/256x256.png",
                    "Assets/Images/Icons/128x128.png",
                    "Assets/Images/Icons/64x64.png"
            };

            for (String path : paths) {
                if (new File(path).exists()) {
                    // ImageIcon(String) automatically loads from the file system
                    icons.add(new ImageIcon(path).getImage());
                    System.out.println("Loaded icon: " + path);
                } else {
                    System.err.println("File not found: " + path);
                }
            }

            if (!icons.isEmpty()) {
                window.setIconImages(icons);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
