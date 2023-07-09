package handlers;

import java.io.*;


/**
 * The FIleHandler class is used to interact with files
 *
 * <p>Author: Andrew Kantser</p>
 * <p>Date: 2023-07-09</p>
 *
 */
public class FileHandler {
    /**
     * To check authorization on the site (and correct operation), the site has a picture
     * with a known size, which is available only to this bot.
     * The method receives this picture and checks its size.
     *
     * @param imageBytes Received image file as an array of bytes.
     * @param size Expected image size.
     * @throws ExceptionInInitializerError An initialization exception will be thrown for multiple reasons.
     *  - Unsuccessful interaction with the file system.
     *  - Wrong size image received.
     *
     */
    public void saveAndCheckSize(byte[] imageBytes, long size) throws ExceptionInInitializerError{
        // Получение пути к текущей директории JAR-файла
        String jarPath = PictureGit.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String jarDirPath = new File(jarPath).getParent();
        // Создание папки tmp рядом с JAR-файлом
        File tmpDir = new File(jarDirPath, "tmp");
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }

        File outputFile = new File(tmpDir, "image.jpg");
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(imageBytes);
            System.out.println("Image downloaded and saved to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Can't save test image");
        }

        long imageSize = outputFile.length();
        //System.out.println("Image size: " + imageSize + " bytes");
        if (imageSize < size) {
            throw new ExceptionInInitializerError("Got wrong image");
        }
    }

    /**
     * Gets an array of bytes. Saves to a temporary file. Receives a stream from it. Returns it.
     * It is believed that the work with the file system was checked earlier.
     *
     * @param imageBytes Received image file as an array of bytes.
     * @return InputStream type. This stream contain image file
     */
    public InputStream saveAndReturnStream(byte[] imageBytes) {
        // Получение пути к текущей директории JAR-файла
        String jarPath = Telegram.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String jarDirPath = new File(jarPath).getParent();
        // Создание папки tmp рядом с JAR-файлом
        File tmpDir = new File(jarDirPath, "tmp");
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        File outputFile = new File(tmpDir, "tmp.jpg");
        //File outputFile = new File("src/test/java/Samples/tmp.jpg");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inputStream;
    }

    /**
     * Gets an array of bytes. Saves to a temporary file. Receives a stream from it. Returns it.
     * It is believed that the work with the file system was checked earlier.
     *
     * @param inputStream
     * @throws IOException - Occurs when an error occurs while working with a temporary file
     * @return File type. This File contain file created from inputStream.
     *
     */
    public File createTempFile(InputStream inputStream) throws IOException {
        File tempFile = File.createTempFile("name", null);
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }
}
