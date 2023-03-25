package Utility;

import java.io.*;

public class FileReading {
    public static void copyInputStreamToFile(InputStream inputStream, File file) {
        final int DEFAULT_BUFFER_SIZE = 8192;

        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
