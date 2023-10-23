package handlers;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

/**
 * The class responsible for getting the settings from the file
 * (what not to place in the code and on the git-hub)
 *
 * <p>Author: Andrew Kantser</p>
 * <p>Date: 2023-07-01</p>
 *
 */
public class Propertie {
    private String gitLogin;
    private String gitPass;
    private String gitAdress;
    private String tgToken;
    private String tgName;

    public Propertie(){
        this.gitPass = null;
        this.gitLogin = null;

        Properties properties = new Properties();
        InputStream  inputStream = null;

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream  inputStream2 = classLoader.getResourceAsStream("configAcces.properties");
            if (inputStream2 != null) {

                inputStream = inputStream2;
            } else {
                System.out.println("Resource configAcces.properties not found");
                inputStream = new FileInputStream("src/main/resources/configAcces.properties");
            }





            properties.load(inputStream);

            // Получение значений логина и пароля из файла
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            String addressGit = properties.getProperty("adresGit");
            String tgToken = properties.getProperty("tgtoken");
            String tgName = properties.getProperty("tgname");

            this.gitLogin = username;
            this.gitPass = password;
            this.gitAdress = addressGit;
            this.tgToken = tgToken;
            this.tgName = tgName;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Закрытие потока
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getTgToken() {
        return tgToken;
    }

    public String getTgName() {
        return tgName;
    }

    public String getGitLogin() {
        return gitLogin;
    }

    public String getGitPass() {
        return gitPass;
    }

    public String getGitAdress() {
        return gitAdress;
    }
}

