import handlers.Network;
import handlers.Telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.net.*;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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
    private static final Logger logger2 = LogManager.getLogger(Application.class);

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
            logger2.error("new TelegramBotsApi" + e.getMessage());
            logger2.error(e.getStackTrace());
            throw new RuntimeException(e);
        }

        try {
            telegramBotsApi.registerBot(new Telegram());
        } catch (TelegramApiException e) {
            logger2.error("registerBot" + e.getMessage());
            logger2.error(e.getStackTrace());
            throw new RuntimeException(e);
        }
        logger2.info("run telegram handler...");

        InetSocketAddress address = new InetSocketAddress(2222);
        Network gitHandler = new Network(address);
        Thread thread1 = new Thread(gitHandler);
        thread1.start();

        Scanner sc = new Scanner(System.in);
        String input = "";
        while (true) {
            System.out.println("Enter command:");
            input = sc.nextLine();
            if (input == null || input.length() < 2) {
                System.out.println("Wrong input");
            } else if ("/exit".equalsIgnoreCase(input)) {
                System.out.println("Quit application");
                sc.close();
                logger2.info("quit application");
                System.exit(0);
            }
        }
    }
}
