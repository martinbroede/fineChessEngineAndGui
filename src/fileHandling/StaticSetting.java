package fileHandling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class StaticSetting {

    private static String generalSettings;

    public static String getSetting(String settingName) {

        String[] args = generalSettings.split("\n");
        for (String arg : args) {
            if (arg.startsWith(settingName)) {
                return arg.replaceAll(settingName + " ", "");
            }
        }
        return "";
    }

    public static void rememberSetting(String newSetting) {

        String[] args = newSetting.split(" ");
        if (args.length < 2) throw new InputMismatchException("TOO FEW ARGUMENTS");
        else {
            int index0 = generalSettings.indexOf(args[0]);
            if (index0 == -1) {
                generalSettings += newSetting + '\n';
                return;
            }
            int index1 = generalSettings.indexOf('\n', index0);
            String sub = generalSettings.substring(index0, index1);
            generalSettings = generalSettings.replaceAll(sub, newSetting);
        }
    }

    public static void storeSettingsInFile() {

        try {
            FileWriter writer = new FileWriter("settings.txt");
            writer.write(generalSettings);
            writer.close();
            System.out.println("STORED SETTINGS");
        } catch (IOException ex) {
            System.err.println("STORE SETTINGS FAILED");
        }
    }

    public static void getStoredSettings() {

        try {
            File settingsFile = new File("settings.txt");

            if (settingsFile.createNewFile()) {
                System.out.println(settingsFile.getName() + " CREATED");
            } else {
                System.out.println("FOUND SETTINGS");
            }

            Scanner settingsIn = new Scanner(settingsFile);
            String settings = "";
            while (settingsIn.hasNext())
                settings += settingsIn.nextLine() + "\n";

            settingsIn.close();

            generalSettings = settings;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchElementException ex) {
            System.out.println("NO SETTINGS STORED YET");
        }
    }
}
