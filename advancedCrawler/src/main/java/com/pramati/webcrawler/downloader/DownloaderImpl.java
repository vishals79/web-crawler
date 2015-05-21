package com.pramati.webcrawler.downloader;

import org.jsoup.nodes.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipFile;

public class DownloaderImpl implements Downloader {

    private static int emailCount = 1;

    public void downloadEmail(String path, Document doc) {
        File file = null;
        FileWriter fileWriter;
        BufferedWriter bufferedWriter = null;


        try {
            if (doc != null) {
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                file = new File(path + "email" + DownloaderImpl.emailCount);
                DownloaderImpl.emailCount++;
                if (!file.exists()) {
                    file.createNewFile();
                }

                fileWriter = new FileWriter(file.getAbsoluteFile());
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(doc.text());
            }
        } catch (IOException e) {
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
