package handlers;

import handlers.PictureGit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import resources.TgCommands;

import java.io.*;
import java.net.*;
import java.util.*;

public class Telegram extends TelegramLongPollingBot {
    Propertie prop = new Propertie();
    final private String BOT_TOKEN = prop.getTgToken();
    final private String BOT_NAME = prop.getTgName();

    FileHandler fh = new FileHandler();

    ExchangeRate exRate =  new ExchangeRate();

    String urlLoginString = prop.getGitAdress();
    String user_name = prop.getGitLogin();
    String password = prop.getGitPass();
    PictureGit dw = new PictureGit(urlLoginString, user_name, password);

    public Telegram(){
        if(!dw.checkConnection()){
            System.out.println("Ошибка проверки соединения для получения картинок");
            throw new ExceptionInInitializerError("Ошибка проверки соединения для получения картинок");
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try{
            if(update.hasMessage() && update.getMessage().hasText())
            {
                Message inMess = update.getMessage();
                String chatId = inMess.getChatId().toString();
                parseMessage(inMess.getText(), chatId);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void parseMessage(String textMsg, String chatId) throws TelegramApiException {
        String response = "";
        TgCommands command = null;

        for (TgCommands tgCommand : TgCommands.values()) {
            if (textMsg.equals(tgCommand.getCommand())) {
                command = tgCommand;
                break;
            }
        }

        if (command != null) {
            switch (command) {
                case start, help:
                    response = command.getTextAnswer();
                    sendText(response, chatId);
                    break;

                case timg:
                    try {
                        sendPhoto("ttt", chatId, null);
                    } catch (IOException | URISyntaxException e) {
                        sendText("При отправке изображения произошла ошибка "+ e.getMessage(), chatId);
                        //throw new RuntimeException(e);
                    }
                    break;

                case talb:
                    try {
                        sendTestAlbum(chatId, "Test album");
                    } catch (TelegramApiException e) {
                        System.out.println(e.getMessage());
                        sendText("При отправке альбома произошла ошибка "+ e.getMessage(), chatId);
                        //throw new RuntimeException(e);
                    } catch (IOException e){
                        System.out.println(e.getMessage());
                        sendText("При отправке альбома произошла ошибка "+ e.getMessage(), chatId);
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                        sendText("При отправке альбома произошла ошибка "+ e.getMessage(), chatId);
                    }
                    break;

                case tgrur:
                    try {
                        sendTgRubCourse(chatId);
                    } catch (TelegramApiException e) {
                        String error = "При отправке курса валют произошла ошибка (TelegramApiException) "+ e.getMessage() + "\n" +
                                Arrays.toString(e.getStackTrace());
                        System.out.println(error);
                        sendText(error, chatId);
                    } catch (IOException e){
                        String error = "При отправке курса валют произошла ошибка (IOException) "+ e.getMessage() + "\n" +
                                Arrays.toString(e.getStackTrace());
                        System.out.println(error);
                        sendText(error, chatId);
                    } catch (Exception e){
                        String error = "При отправке курса валют произошла ошибка (Exception) "+ e.getMessage() + "\n" +
                                Arrays.toString(e.getStackTrace());
                        System.out.println(error);
                        sendText(error, chatId);
                    }
            }
        }
    }

    public void sendText(String text, String chatId) throws TelegramApiException {
        SendMessage outMess = new SendMessage();
        text = text + " chat id is " + chatId;
        outMess.setChatId(chatId);
        outMess.setText(text);
        execute(outMess);
    }

    public void sendPhoto(String text, String chatId, String path) throws TelegramApiException, IOException, URISyntaxException {
            SendPhoto outPhoto = new SendPhoto();
            outPhoto.setChatId(chatId);

            if(path == null){
                InputFile image = new InputFile();
                InputStream stream =new URL("https://i.pinimg.com/originals/19/9a/9c/199a9c3fdf578a7713ae87589404fdee.jpg").openStream();
                image.setMedia(stream, "Gon and Kill");
                outPhoto.setCaption("Gon and Kill");

                outPhoto.setPhoto(image);
            }
            else{
                InputStream inputStream = dw.getImageStream(path);
                InputFile image = new InputFile();
                image.setMedia(inputStream, "From java");
                outPhoto.setCaption(text);
                outPhoto.setPhoto(image);
            }
        execute(outPhoto);
    }

    public void sendAlbum(String text, String chatId, String[] path, String marker) throws IOException, TelegramApiException {
        if(path.length > 10){
            sendText(text + " [Java-мост разделил сообщение из-за большого количества картинок]", chatId);
            text = marker;
        }

        for(int count = 0; count < path.length; count += 10) {
            List<InputMedia> inputMediaList = new ArrayList<>();
            int limitAlbumPart = Math.min(path.length, count+10);

            List<File> files = new ArrayList<>();
            for (int i = count; i < limitAlbumPart; i++) {
                InputStream stream = dw.getImageStream(path[i]);
                if(stream == null){
                    text += "\n [Ошибка получения файла "+path[i]+"]";
                    continue;
                }
                files.add(fh.createTempFile(stream));
            }

            for (int i = 0; i < files.size(); i++) {
                InputMedia inpMedia = createInputMedia(files.get(i));
                if (i == 0) {
                    inpMedia.setCaption(text);
                }
                inputMediaList.add(inpMedia);
            }
            if(files.size() > 2 && count > path.length - 1){
                sendText(text + "\n [Произошла ошибка, изображения не отправлены.]", chatId);
                for (File file : files) {
                    file.delete();
                }

            }else{
                SendMediaGroup sendMediaGroup = new SendMediaGroup();
                sendMediaGroup.setChatId(chatId);
                sendMediaGroup.setMedias(inputMediaList);
                execute(sendMediaGroup);

                for (File file : files) {
                    file.delete();
                }
            }

        }
    }

    private InputMediaPhoto createInputMedia(File imageFile) throws IOException {
        InputMediaPhoto inputMedia = new InputMediaPhoto();
        InputStream inputStream = new FileInputStream(imageFile);
        inputMedia.setMedia(inputStream, imageFile.getName());
        return inputMedia;
    }


    public void sendTestAlbum(String chatId, String description) throws TelegramApiException, IOException {
        SendMediaGroup mediaGroup = new SendMediaGroup();
        ArrayList <InputMedia> medias = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            // Получение пути к текущей директории JAR-файла
            String jarPath = Telegram.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String jarDirPath = new File(jarPath).getParent();
            // Создание папки tmp рядом с JAR-файлом
            File tmpDir = new File(jarDirPath, "tmp");
            if (!tmpDir.exists()) {
                tmpDir.mkdir();
            }
            File outputFile = new File(tmpDir, "Killua.jpg");
            InputStream stream = new FileInputStream(outputFile);
            InputMediaPhoto im = new InputMediaPhoto();
            im.setMedia(stream, description);
            if(i == 0){
                im.setCaption(description);
            }
            medias.add(im);
        }
        mediaGroup.setMedias(medias);
        mediaGroup.setChatId(chatId);
        mediaGroup.validate();
        execute(mediaGroup);
    }

    public void sendTgRubCourse(String chatId) throws TelegramApiException, IOException {
        SendMessage outMess = new SendMessage();
        outMess.setText(exRate.getTgRubCourse());
        outMess.setChatId(chatId);
        execute(outMess);
    }

}
