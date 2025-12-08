package UI.Components;
import java.awt.Window;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class IconLoader {
    public static void setIcons(Window window) {
        try {
            List<Image> icons = new ArrayList<>();

            String[] paths = {
                    "/Assets/Images/Icons/1024x1024.png",
                    "/Assets/Images/Icons/512x512.png",
                    "/Assets/Images/Icons/256x256.png",
                    "/Assets/Images/Icons/128x128.png",
                    "/Assets/Images/Icons/64x64.png"
            };

            for (String path : paths) {
                URL imgURL = IconLoader.class.getResource(path);

                if (imgURL != null) {
                    // ImageIcon accepts URL directly
                    icons.add(new ImageIcon(imgURL).getImage());
                    System.out.println("Loaded icon: " + path);
                } else {
                    System.err.println("Icon resource not found: " + path);
                }
            }

            if (!icons.isEmpty()) {
                window.setIconImages(icons);
            }

        } catch (Exception e) {
            System.err.println("Error setting window icons: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
