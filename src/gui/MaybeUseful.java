package gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MaybeUseful {
    public static void main(String[] args) {
        //printFonts();
        try{getNames();}catch (Exception e){e.printStackTrace();}
    }

    public static void printFonts() {
        String[] fonts
                = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String s : fonts) System.out.println(s);
    }

    public static void getNames() throws IOException {
        //Creating a File object for directory
        File directoryPath = new File("C:\\Users\\admin\\kurs1618\\IntelliJ\\MartinChessV1.0\\fonts");
        //List of all files and directories
        String contents[] = directoryPath.list();
        System.out.println("List of files and directories in the specified directory:");
        for (int i = 0; i < contents.length; i++) {
            System.out.println(contents[i]);
        }
    }

}
