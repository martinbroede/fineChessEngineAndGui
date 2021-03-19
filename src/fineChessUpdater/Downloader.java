package fineChessUpdater;

import gui.DialogMessage;
import gui.DialogText;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import static java.lang.Thread.sleep;

public class Downloader {
    final static String OUTPUT_FILE = "latestRelease.zip";
    final static String DOWNLOAD_RESOURCE = "https://github.com/martinbro2021/fineChessEngineAndGui/archive/main.zip";
    final static int BUFFER_SIZE = 65536;

    public Downloader() {
        download(DOWNLOAD_RESOURCE);
    }

    public Downloader(String downloadUrl) {
        download(downloadUrl);
    }

    public static void main(String[] args) {
        new Downloader(DOWNLOAD_RESOURCE);
    }

    private void download(String downloadUrl) {
        DialogMessage dialog = null;
        DialogText text = null;
        try (BufferedInputStream in = new BufferedInputStream(new URL(downloadUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(OUTPUT_FILE)) {
            byte dataBuffer[] = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, BUFFER_SIZE)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            dialog = new DialogMessage("Download erfolgreich");
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
}