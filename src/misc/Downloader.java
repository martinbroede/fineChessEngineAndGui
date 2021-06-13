package misc;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import static java.lang.Thread.sleep;
import static misc.Properties.resourceBundle;

public class Downloader {

    private final static String OUTPUT_FILE = "latestRelease.zip";
    private final static String DOWNLOAD_RESOURCE = "https://github.com/martinbro2021/fineChessEngineAndGui/archive/main.zip";
    private final static int BUFFER_SIZE = 1024;

    private Downloader() {
    }

    public static void download() {
        download(DOWNLOAD_RESOURCE, OUTPUT_FILE);
    }

    public static void download(String downloadUrl, String outputFile) {

        try (BufferedInputStream in = new BufferedInputStream(new URL(downloadUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            byte[] dataBuffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, BUFFER_SIZE)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            System.out.println("DOWNLOAD SUCCESSFUL");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                sleep(1000);
            } catch (InterruptedException ignored) {
            }
            System.out.println("DOWNLOADER GOODBYE");
        }
    }

    public static String getHeadLineFromURL(String resourceURL) {

        String line = "";
        try {
            URL url = new URL(resourceURL);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            line = bufferedReader.readLine();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, resourceBundle.getString("no.internet"));
            ex.printStackTrace();
        }
        return line;
    }

    public static String getStringFromURL(String resourceURL) {

        StringBuilder textFromURL = new StringBuilder();
        try {
            URL url = new URL(resourceURL);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                textFromURL.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, resourceBundle.getString("no.internet"));
            ex.printStackTrace();
        }
        return textFromURL.toString();
    }
}