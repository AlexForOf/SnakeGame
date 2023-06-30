package Functional;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

//1 - body horizontal, 2 - body vertical, 3 - head left, 4 - head up, 5 - head right, 6 - head down,
//7 - body topleft, 8 - body topright, 9 - body bottomleft, 10 - body bottomright, 11 - tail left, 12 - tail up, 13 - tail right, 14 - tail down,
//15 - apple

public class ImagesLoader {
    public ImageIcon[] loadImages() throws IOException {
        String[] filePaths = {"body_horizontal.png", "body_vertical.png", "head_left.png", "head_up.png", "head_right.png",
        "head_down.png", "body_topleft.png", "body_topright.png", "body_bottomleft.png", "body_bottomright.png", "tail_left.png",
        "tail_up.png", "tail_right.png" , "tail_down.png", "apple.png"};

        ImageIcon[] images = new ImageIcon[15];

        for (int i = 0; i < filePaths.length; i++) {
            BufferedImage bufferedImage = ImageIO.read(getClass().getResource("../Textures/" + filePaths[i]));
            images[i] = new ImageIcon(bufferedImage);
        }
        return images;
    }
}
