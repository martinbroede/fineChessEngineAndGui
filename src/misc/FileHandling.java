package misc;

import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class FileHandling {

    static public void writeToFile(String path, Object obj) throws IOException {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path))) {
            os.writeObject(obj);
        }
    }

    static public Object readFromFile(String path) throws IOException, ClassNotFoundException {

        FileInputStream fileIn = new FileInputStream(path);
        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        Object obj = objectIn.readObject();
        objectIn.close();
        return obj;
    }

    static public Font createFontFromFile(String name, int size) throws IOException, FontFormatException {

        String FONT_PATH = "fonts/" + name + ".ttf";
        Font myFont;
        InputStream myStream = new BufferedInputStream(new FileInputStream(FONT_PATH));
        Font ttfBase = Font.createFont(Font.TRUETYPE_FONT, myStream);
        myFont = ttfBase.deriveFont(Font.PLAIN, size);
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
            System.out.println("FILE " + filename + " NOT FOUND");
        }
        return text.toString();
    }
}
