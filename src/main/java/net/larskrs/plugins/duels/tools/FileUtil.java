package net.larskrs.plugins.duels.tools;

import java.io.*;

public final class FileUtil {
    public static void copy(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdir();
            }

            String[] files = source.list();
            if (files == null) return;
            for (String file : files) {
                File newSource = new File(source, file);
                File newDestination = new File(destination, file);
                copy(newSource, newDestination);
            }
        } else {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];
            int lenght;
            // copy the file content in bytes
            while ((lenght = in.read(buffer)) > 0) {
                out.write(buffer, 0, lenght);
            }

            in.close();
            out.close();

        }
    }
    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return;
            for (File child : files) {
                delete(child);
            }
        }

        file.delete();
    }
}
