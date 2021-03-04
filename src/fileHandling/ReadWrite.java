package fileHandling;

import java.awt.*;
import java.io.*;

public class ReadWrite {
    private static Font ttfBase = null;
    private static Font myFont = null;
    private static InputStream myStream = null;

    static public void writeToFile(String path, Object obj) {
        try (ObjectOutputStream write = new ObjectOutputStream(new FileOutputStream(path))) {
            write.writeObject(obj);
            System.out.println("OBJECT HAS BEEN WRITTEN TO FILE " + path);
        } catch (FileNotFoundException nse) {
            nse.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public Object readFromFile(String path) {
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Object obj = objectIn.readObject();
            System.out.println("OBJECT HAS BEEN LOADED FROM FILE " + path);
            objectIn.close();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static public Font createFontFromFile(String name, int size) {
        String FONT_PATH = "fonts/" + name + ".ttf";
        try {
            myStream = new BufferedInputStream(
                    new FileInputStream(FONT_PATH));
            ttfBase = Font.createFont(Font.TRUETYPE_FONT, myStream);
            myFont = ttfBase.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("LOADING FONT FAILED.");
            return null;
        }
        return myFont;
    }
}
