import handlers.Network;
import handlers.Telegram;

import handlers.TerminalPool;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.net.*;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.MyLogger;


/**
 * The Application class is the entry point of the application.
 * Threads are created to process web requests and telegram requests.
 *
 * <p>Author: Andrew Kantser</p>
 * <p>Date: 2023-06-25</p>
 * <p>Description: First, an object is created to interact with the telegram API.
 * Then Run in a separate port listener thread. Then receiving commands from the terminal.</p>
 */


public class Application {


    /**
     * The main method is the entry point of the application.
     * It initializes the necessary components and starts the execution.
     *
     * @param args The command-line arguments passed to the application.
     */
    public static void main(String[] args) {

        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            MyLogger.myError("new TelegramBotsApi" + e.getMessage());
            MyLogger.logger2.error(e.getStackTrace());
            throw new RuntimeException(e);
        }

        try {
            telegramBotsApi.registerBot(new Telegram());
        } catch (TelegramApiException e) {
            MyLogger.myError("registerBot" + e.getMessage());
            MyLogger.logger2.error(e.getStackTrace());
            throw new RuntimeException(e);
        }
        MyLogger.myError("run telegram handler...");

        InetSocketAddress address = new InetSocketAddress(2222);
        Network gitHandler = new Network(address);
        Thread thread1 = new Thread(gitHandler);
        thread1.setName("Git Listener");
        thread1.setPriority(5);
        thread1.start();
        MyLogger.myError("run git listener...");

        TerminalPool terminalPool= new TerminalPool();
        Thread thread2 = new Thread(terminalPool);
        thread2.setName("Terminal Listener");
        thread2.setPriority(Thread.MIN_PRIORITY);
        thread2.start();
        MyLogger.myError("run terminal handler...");




    }
}
