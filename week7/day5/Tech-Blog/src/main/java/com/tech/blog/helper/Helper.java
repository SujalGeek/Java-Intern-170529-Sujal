package com.tech.blog.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Helper {

    // Delete file by path
    public static boolean deleteFile(String path) {
        boolean f = false;
        try {
            File file = new File(path);
            if (file.exists()) {
                f = file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    // Save file from InputStream
    public static boolean saveFile(InputStream is, String path) {
        boolean f = false;
        try (FileOutputStream fos = new FileOutputStream(path)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            f = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }
}
