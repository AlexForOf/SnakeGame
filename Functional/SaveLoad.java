package Functional;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SaveLoad {
    private static final String path = "scores.bin";

    public static void saveScore(List<Score> scores){
        try {
            FileOutputStream saver = new FileOutputStream(path);
            for (int i = 0; i < scores.size(); i++) {
                saver.write(scores.get(i).getPlayer().length());
                char[] playerName = scores.get(i).getPlayer().toCharArray();
                for (int j = 0; j < playerName.length; j++) {
                    saver.write(playerName[j]);
                    saver.write(0);
                }
                int value = scores.get(i).getScore();
                byte[] bytes = new byte[4];
                bytes[0] = (byte) (value >>> 24);
                bytes[1] = (byte) (value >>> 16);
                bytes[2] = (byte) (value >>> 8);
                bytes[3] = (byte) value;

                saver.write(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<Score> loadScores(){
        List<Score> loadedScores = new ArrayList<>();
        try {
            FileInputStream loader = new FileInputStream(path);
            while (loader.available() > 0){
                int length = loader.read();
                StringBuilder name = new StringBuilder();
                for (int i = 0; i < length * 2; i++) {
                    if (i % 2 == 0){
                        name.append((char) loader.read());
                    }else{
                        loader.read();
                    }
                }
                byte[] bytes = new byte[4];
                int bytesRead = loader.read(bytes);
                int value = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
                loadedScores.add(new Score(name.toString(), value));
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }

        return loadedScores;
    }
}
