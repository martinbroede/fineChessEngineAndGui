package fileHandling;

import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class ReadWrite {

    static public void writeToFile(String path, Object obj) {

        try (ObjectOutputStream write = new ObjectOutputStream(new FileOutputStream(path))) {
            write.writeObject(obj);
            System.out.println("OBJECT HAS BEEN WRITTEN TO FILE " + path);
        } catch (IOException ex) {
            ex.printStackTrace();
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
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    static public Font createFontFromFile(String name, int size) {

        String FONT_PATH = "fonts/" + name + ".ttf";
        Font myFont;
        try {
            InputStream myStream = new BufferedInputStream(
                    new FileInputStream(FONT_PATH));
            Font ttfBase = Font.createFont(Font.TRUETYPE_FONT, myStream);
            myFont = ttfBase.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("LOADING FONT FAILED.");
            return null;
        }
        return myFont;
    }

    static public String getStringFromFile(String filename) {

        StringBuilder text = new StringBuilder();
        try {
            FileInputStream stream = new FileInputStream(filename);
            Scanner scanner = new Scanner(stream);
            while (scanner.hasNext()) {
                text.append(scanner.nextLine()).append('\n');
            }
        } catch (FileNotFoundException ex) {
            System.err.println("FILE " + filename + " NOT FOUND");
        }
        return text.toString();
    }
}
