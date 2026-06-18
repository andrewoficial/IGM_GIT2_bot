import handlers.Network;
import handlers.Propertie;
import handlers.Telegram;
import handlers.TerminalPool;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import services.MyLogger;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        Propertie config = new Propertie();
        setupTelegramProxy(config);

        InetSocketAddress address = new InetSocketAddress(2222);
        Network gitHandler = new Network(address);
        Thread thread1 = new Thread(gitHandler);
        thread1.setName("Git Listener");
        thread1.setPriority(5);
        thread1.start();
        MyLogger.myInfo("run git listener...");

        TerminalPool terminalPool = new TerminalPool();
        Thread thread2 = new Thread(terminalPool);
        thread2.setName("Terminal Listener");
        thread2.setPriority(Thread.MIN_PRIORITY);
        thread2.start();
        MyLogger.myInfo("run terminal handler...");

        startTelegramBot();
    }

    private static void setupTelegramProxy(Propertie config) {
        if (!config.isProxyTelegramEnable()) {
            MyLogger.myInfo("Telegram proxy: disabled");
            return;
        }
        String host = config.getProxyTelegramAddress();
        int port = config.getProxyTelegramPort();
        String login = config.getProxyTelegramLogin();
        String password = config.getProxyTelegramPassword();

        if (!login.isEmpty()) {
            System.setProperty("java.net.socks.username", login);
            System.setProperty("java.net.socks.password", password);
        }

        ProxySelector defaultSelector = ProxySelector.getDefault();
        ProxySelector telegramProxy = new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                if (uri != null && uri.getHost() != null && uri.getHost().contains("api.telegram.org")) {
                    return List.of(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, port)));
                }
                if (defaultSelector != null) {
                    return defaultSelector.select(uri);
                }
                return List.of(Proxy.NO_PROXY);
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException e) {
                if (defaultSelector != null) {
                    defaultSelector.connectFailed(uri, sa, e);
                }
            }
        };
        ProxySelector.setDefault(telegramProxy);
        MyLogger.myInfo("Telegram proxy: SOCKS5 " + host + ":" + port);
    }

    private static void startTelegramBot() {
        Thread tgThread = new Thread(() -> {
            while (true) {
                try {
                    TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
                    BotSession session = telegramBotsApi.registerBot(new Telegram());
                    MyLogger.myInfo("Telegram bot registered, monitoring session...");

                    while (session.isRunning()) {
                        Thread.sleep(5000);
                    }
                    MyLogger.myWarn("Telegram session stopped, reconnecting...");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (Exception e) {
                    MyLogger.myError("Telegram error (" + e.getMessage() + "), retrying in 30s...");
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        });
        tgThread.setName("Telegram-Bot");
        tgThread.setDaemon(true);
        tgThread.start();
    }
}
