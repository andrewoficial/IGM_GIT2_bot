package resources;

import handlers.Telegram;
import org.json.JSONException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Event {

    private String repo;

    private String action;

    private String number;

    private String login;

    private String body;

    private String title;

    private String [] attachments;

    public Event(String repo, String number, String action, String login, String body, String title, String [] attachments) {
        this.repo = repo;
        this.action = action;
        this.login = login;
        this.body = body;
        this.attachments = attachments;
        this.title = title;
        this.number = number;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String [] getAttachments() {
        return attachments;
    }

    public void setAttachments(String [] attachments) {
        this.attachments = attachments;
    }

    public String getTitle() {
        return title;
    }
    public String getNumber() {
        return number;
    }

    public void sendToTelegram(){
        System.out.println("Try send");
        Telegram tg = new Telegram();
        String marker = "#"+this.repo+"_"+this.number;
        Repositories repository = Repositories.DEFAULT;
        System.out.println(repository);
        //Проверки
        if(this.body.length()>300){
            this.body = this.body.substring(0, Math.min(250, body.length()));
            this.body += "[текст был сокращен Java-мостом]";
        }

        for (Repositories value : Repositories.values()) {
            if(this.repo.equalsIgnoreCase(value.getRepoName())){
                repository = value;
            }
        }

        System.out.println(body);
        //Формирование заголовка
        String message = "В репозитории " + repository.getRepoName() + " \n";
        System.out.println();
        if("opened".equalsIgnoreCase(action)){
            message += "Открыта новая задача: " + this.title + " \n";
            if(body != null && body != "")
                message += "Описание: " + this.body + " \n";
            message += "Тег: " + marker + " \n";
        }
        else if("created".equalsIgnoreCase(action)){
            message += "К задаче: " + this.title + " \n";
            if(body != null && body != "")
                message += "Добавлен комментарий: " + this.body + " \n";
            message += "Тег: " + marker + " \n";
        }
        else if("closed".equalsIgnoreCase(action)){
            message += "Закрыта задача: " + this.title + " \n";
            if(body != null && body != "")
                message += "Описание: " + this.body + " \n";
            message += "Тег: " + marker + " \n";
        }
        else if("reopened".equalsIgnoreCase(action)){
            message += "Задача: " + this.title + " была открыта снова \n";
            if(body != null && body != "")
                message += "Добавлен комментарий: " + this.body + " \n";
            message += "Тег: " + marker + " \n";
        }
        else{
            return;
        }

        //Если вложений нету
        if(this.attachments == null || this.attachments.length < 1){
            System.out.println("Отправка простого сообщения");
            try {
                tg.sendText(message, repository.getTgId());
            } catch (TelegramApiException e) {
                //throw new RuntimeException(e);
            }
            return;
        }

        //Если одна картинка
        if(this.attachments.length == 1){
            System.out.println("Отправка одной картинки");
            System.out.println(attachments[0]);
            try {
                tg.sendPhoto(message, repository.getTgId(), attachments[0]);
            } catch (TelegramApiException e) {
                System.out.println("    Проблема с отправкой одной картинки" + e.getMessage());
                //throw new RuntimeException(e);
            } catch (IOException | URISyntaxException e) {
                System.out.println("    Проблема с файлом"+e.getMessage());
                try {
                    tg.sendText(message + " \n [IOException в Java-мосте. Контент не был передан. "+e.getMessage()+" ]", repository.getTgId());
                } catch (TelegramApiException e1) {
                    System.out.println("        Проблема отправкой уведомления"+ e1.getMessage());
                    //throw new RuntimeException(e);
                }
                //throw new RuntimeException(e);
            }
            return;
        }


        System.out.println("Отправка альбома " + attachments.length);
        String str = "";
        for (String attachment : attachments) {
            str += attachment + ", ";
        }

        try {
            tg.sendAlbum(message, repository.getTgId(), attachments, marker);
        } catch (IOException e) {
            System.out.println("    Проблема с файлом");
            try {
                tg.sendText(message + " \n [IOException в Java-мосте. Контент не был передан. \n" +
                        "Список ссылок на картинки: "+str + "\n" +
                        "Текст ошибки:"+e.getMessage()+" ]", repository.getTgId());
            } catch (TelegramApiException e1) {
                System.out.println("        Проблема отправкой сообщения об ошибке");
                //throw new RuntimeException(e);
            }
            //throw new RuntimeException(e);
        } catch (TelegramApiException e) {
            System.out.println("    Проблема с телеграммом");
            try {

                tg.sendText(message + " \n [TelegramApiException в Java-мосте. Контент не был передан. \n" +
                        "Список ссылок на картинки: "+str + "\n" +
                        "Текст ошибки:"+e.getMessage()+" ]", repository.getTgId());
                //System.out.println(e.getStackTrace());
            } catch (TelegramApiException e1) {
                System.out.println("        Проблема отправкой сообщения об ошибке");
                //throw new RuntimeException(e);
            }

        }
    }


    @Override
    public String toString() {
        return "Event{repo:" + this.repo + "; title:" + this.title+"; attachments:" + this.attachments.length+";}";
    }
}
