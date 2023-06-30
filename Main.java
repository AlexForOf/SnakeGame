import Functional.ImagesLoader;
import Gameplay.Gameplay;
import Visual.Visual;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Gameplay gameplay = new Gameplay();
        ImagesLoader textureLoader = new ImagesLoader();
        ImageIcon[] textures = new ImageIcon[15];
        try {
            textures = textureLoader.loadImages();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Visual visual = new Visual(gameplay, textures);

        gameplay.addMovementEventListener(visual);
        gameplay.addEatenFoodEventListener(visual.controlsPanel);
        gameplay.addCollisionEventListener(visual);
        gameplay.addLevelUpEventListener(visual.controlsPanel);

        visual.addDirectionChangedEventListener(gameplay);
        visual.addRestartEventListener(gameplay);

        Thread thread = new Thread(gameplay);
        thread.start();
    }
}
