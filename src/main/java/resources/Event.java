package resources;

import handlers.Telegram;
import org.json.JSONException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.MyLogger;


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
        MyLogger.myError("Try send");
        Telegram tg = new Telegram();
        String marker = "#"+this.repo+"_"+this.number;
        Repositories repository = Repositories.DEFAULT;
        MyLogger.myError(String.valueOf(repository));
        //Проверки
        if(this.body.length()>300){
            this.body = this.body.substring(0, Math.min(250, body.length()));
            this.body += "[the text has been shortened by the Java bridge]";
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

        MyLogger.myError("Found attachments:");
        if(this.attachments != null){
            MyLogger.myError(String.valueOf(attachments.length));
            for (String attachment : attachments) {
                MyLogger.myError(attachment);
            }
        }else{
            MyLogger.myError(" NULL ");
        }


        //Если вложений нету
        if(this.attachments == null || this.attachments.length < 1){
            MyLogger.myError("Sending a simple message");
            try {
                tg.sendText(message, repository.getTgId());
            } catch (TelegramApiException e) {
                //throw new RuntimeException(e);
            }
            return;
        }

        //Если одна картинка
        if(this.attachments.length == 1){
            System.out.println("Sending one picture");
            System.out.println("== picture source:");
            System.out.println(attachments[0]);
            /*
            attachments[0] = attachments[0].replace("![image", "/");
            System.out.println("== picture source fixed:");
            System.out.println(attachments[0]);
            System.out.println("== picture source");
            String address = attachments[0].substring(0, attachments[0].indexOf("!"));
            attachments[0] = attachments[0].substring(attachments[0].indexOf("/attachments/"));
            attachments[0] = address + attachments[0];
            System.out.println("== picture source fixed:");
            System.out.println(attachments[0]);
            */

            try {
                tg.sendPhoto(message, repository.getTgId(), attachments[0]);
            } catch (TelegramApiException e) {
                MyLogger.myError("    Problem with sending one picture" + e.getMessage());
                //throw new RuntimeException(e);
            } catch (IOException | URISyntaxException e) {
                MyLogger.myError("    File problem"+e.getMessage());
                try {
                    String errors = message + " \n [IOException in Java Bridge. Content was not transferred. "+e.getMessage()+" ]";
                    tg.sendText(errors, repository.getTgId());
                    MyLogger.myError(errors);
                } catch (TelegramApiException e1) {
                    MyLogger.myError("        Problem sending notifications"+ e1.getMessage());
                    //throw new RuntimeException(e);
                }
                //throw new RuntimeException(e);
            }
            return;
        }

        if(this.attachments.length > 1) {
            MyLogger.myError("Sending an album " + attachments.length);
            for (int i = 0; i < attachments.length; i++) {
                MyLogger.myError("ATT-" + i + " is " + attachments[i]);
            }
            String str = "";
            for (String attachment : attachments) {
                attachment = attachment.replace("![image", "/");
                str += attachment + ", ";
            }

            try {
                tg.sendAlbum(message, repository.getTgId(), attachments, marker);
            } catch (IOException e) {
                MyLogger.myError("    File problem");
                try {
                    String errors = message + " \n [IOException in Java Bridge. Content was not transferred] \n" +
                            "List of links to pictures: " + str + "\n" +
                            "Error text:" + e.getMessage() + " ]";
                    tg.sendText(errors, repository.getTgId());
                    MyLogger.myError(errors);
                } catch (TelegramApiException e1) {
                    MyLogger.myError("        Problem sending error message");
                    //throw new RuntimeException(e);
                }
                //throw new RuntimeException(e);
            } catch (TelegramApiException e) {
                MyLogger.myError("   Problem with telegram");
                try {
                    String errors = message + " \n [TelegramApiException in Java Bridge. Content was not transferred]. \n" +
                            "List of links to pictures:" + str + "\n" +
                    "Error text:" + e.getMessage() + " ]";

                    tg.sendText(errors,repository.getTgId());
                    MyLogger.myError(errors);
                    //System.out.println(e.getStackTrace());
                } catch (TelegramApiException e1) {
                    System.out.println(" Problem sending error message");
                    MyLogger.myError(" Problem sending error message");
                    //throw new RuntimeException(e);
                } finally {
                    return;
                }

            }
        }
    }


    @Override
    public String toString() {
        return "Event{repo:" + this.repo + "; title:" + this.title+"; attachments:" + this.attachments.length+";}";
    }
}
