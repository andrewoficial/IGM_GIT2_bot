package handlers;

import services.MyLogger;

import java.io.*;
import java.util.Properties;

public class ConfigFileHelper {

    private static final String DIR = "conf";
    private static final String NAME = "configAcces.properties";

    public static Properties loadOrCreateConfig() {
        File dir = new File(DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, NAME);
        if (!file.exists()) {
            createConfigTemplate(file);
            return new Properties();
        }

        MyLogger.myInfo("Loading config: " + file.getAbsolutePath());
        try (FileInputStream in = new FileInputStream(file)) {
            Properties props = new Properties();
            props.load(in);
            return props;
        } catch (IOException e) {
            MyLogger.myError("Failed to load config: " + e.getMessage());
            return new Properties();
        }
    }

    private static void createConfigTemplate(File file) {
        MyLogger.myWarn("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
        MyLogger.myWarn("!                             W A R N I N G                           !");
        MyLogger.myWarn("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
        MyLogger.myWarn("!                                                                     !");
        MyLogger.myWarn("!   Config file not found! Created a template file at:                 !");
        MyLogger.myWarn("!   " + file.getAbsolutePath() + "          !");
        MyLogger.myWarn("!                                                                     !");
        MyLogger.myWarn("!   Fill in your credentials and restart the application.             !");
        MyLogger.myWarn("!                                                                     !");
        MyLogger.myWarn("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("username=\n");
            writer.write("password=\n");
            writer.write("addressGit=\n");
            writer.write("tgtoken=\n");
            writer.write("tgname=\n");
            writer.write("connection.proxy.telegram.enable=false\n");
            writer.write("connection.proxy.telegram.address=127.0.0.1\n");
            writer.write("connection.proxy.telegram.port=9025\n");
            writer.write("connection.proxy.telegram.login=\n");
            writer.write("connection.proxy.telegram.password=\n");
        } catch (IOException e) {
            MyLogger.myError("Failed to create config template: " + e.getMessage());
        }
    }
}
