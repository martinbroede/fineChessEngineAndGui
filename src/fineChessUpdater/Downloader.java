package fineChessUpdater;

import gui.dialogs.DialogMessage;
import gui.dialogs.DialogText;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import static java.lang.Thread.sleep;

public class Downloader {
    final static String OUTPUT_FILE = "latestRelease.zip";
    final static String DOWNLOAD_RESOURCE = "https://github.com/martinbro2021/fineChessEngineAndGui/archive/main.zip";
    final static int BUFFER_SIZE = 65536;

    public Downloader() {
        download(DOWNLOAD_RESOURCE, OUTPUT_FILE);
    }

    public Downloader(String downloadUrl, String outputFile) {
        download(downloadUrl, outputFile);
    }

    public static void main(String[] args) {
        //new Downloader(DOWNLOAD_RESOURCE, OUTPUT_FILE);
        String output = getStringFromURL("https://raw.githubusercontent.com/martinbro2021/fineChessEngineAndGui/main/version.txt");
        System.out.println(output);
    }

    private void download(String downloadUrl, String outputFile) {
        DialogMessage dialog = null;
        DialogText text = null;
        try (BufferedInputStream in = new BufferedInputStream(new URL(downloadUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            byte dataBuffer[] = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, BUFFER_SIZE)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            dialog = new DialogMessage("Download " + outputFile + " erfolgreich");
        } catch (IOException ex) {
            text = new DialogText(ex.getMessage(), new Point(300, 300));
            text.setVisible(true);
        } finally {
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (dialog != null) dialog.dispose();
            if (text != null) text.dispose();
            System.out.println("DOWNLOADER GOODBYE");
        }
    }

    private static String getStringFromURL(String ressourceURL)
    {
        StringBuilder textFromURL = new StringBuilder();
        try
        {
            URL url = new URL(ressourceURL);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                textFromURL.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return textFromURL.toString();
    }
}