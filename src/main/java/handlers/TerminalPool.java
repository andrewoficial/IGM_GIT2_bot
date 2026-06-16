package handlers;

import services.MyLogger;

import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class TerminalPool implements Runnable {

    private final Scanner sc = new Scanner(System.in);
    private final long startTime = System.currentTimeMillis();
    private final Map<String, String> commands = new TreeMap<>();

    public TerminalPool() {
        commands.put("/exit", "Shutdown the application");
        commands.put("/help", "Show this help message");
        commands.put("/threads", "List all active threads");
        commands.put("/status", "Show application status (threads, memory, uptime)");
        commands.put("/uptime", "Show application uptime");
        commands.put("/memory", "Show memory usage details");
        commands.put("/gc", "Run garbage collection");
        commands.put("/dump", "Dump stack traces of all threads");
        commands.put("/clear", "Clear the console");
        commands.put("/ping", "Check if terminal is responsive");
        commands.put("/version", "Show application and Java version");
        commands.put("/env", "Show system properties");
    }

    @Override
    public void run() {
        while (true) {
            MyLogger.myInfo("Enter command:");
            String input = sc.nextLine();

            if (input == null) continue;

            String cmd = input.trim().toLowerCase();

            switch (cmd) {
                case "/exit":
                    handleExit();
                    return;
                case "/help":
                    handleHelp();
                    break;
                case "/threads":
                    handleThreads();
                    break;
                case "/status":
                    handleStatus();
                    break;
                case "/uptime":
                    handleUptime();
                    break;
                case "/memory":
                    handleMemory();
                    break;
                case "/gc":
                    handleGc();
                    break;
                case "/dump":
                    handleDump();
                    break;
                case "/clear":
                    handleClear();
                    break;
                case "/ping":
                    handlePing();
                    break;
                case "/version":
                    handleVersion();
                    break;
                case "/env":
                    handleEnv();
                    break;
                default:
                    MyLogger.myWarn("Unknown command. Type /help for available commands");
            }
        }
    }

    private void handleExit() {
        MyLogger.myInfo("Shutting down application...");
        sc.close();
        System.exit(0);
    }

    private void handleHelp() {
        MyLogger.myInfo("Available commands:");
        for (Map.Entry<String, String> entry : commands.entrySet()) {
            MyLogger.myInfo(String.format("  %-12s - %s", entry.getKey(), entry.getValue()));
        }
    }

    private void handleThreads() {
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        MyLogger.myInfo(String.format("Active threads: %d", threads.size()));
        MyLogger.myInfo(String.format("%-20s %-12s %-8s %s", "Name", "State", "Priority", "Daemon"));
        for (Thread t : threads) {
            MyLogger.myInfo(String.format("%-20s %-12s %-8d %s",
                    t.getName(), t.getState(), t.getPriority(), t.isDaemon()));
        }
    }

    private void handleStatus() {
        Runtime rt = Runtime.getRuntime();
        long uptimeMs = System.currentTimeMillis() - startTime;

        MyLogger.myInfo("=== Application Status ===");
        MyLogger.myInfo(String.format("Uptime: %s", formatUptime(uptimeMs)));
        MyLogger.myInfo(String.format("Threads: %d active", Thread.activeCount()));
        MyLogger.myInfo(String.format("Memory: %dM / %dM (used/max)",
                (rt.totalMemory() - rt.freeMemory()) / 1048576,
                rt.maxMemory() / 1048576));
        MyLogger.myInfo(String.format("Processors: %d", rt.availableProcessors()));
        MyLogger.myInfo("==========================");
    }

    private void handleUptime() {
        long uptimeMs = System.currentTimeMillis() - startTime;
        MyLogger.myInfo(String.format("Uptime: %s", formatUptime(uptimeMs)));
    }

    private void handleMemory() {
        Runtime rt = Runtime.getRuntime();
        long used = rt.totalMemory() - rt.freeMemory();
        long total = rt.totalMemory();
        long max = rt.maxMemory();
        long free = rt.freeMemory();

        MyLogger.myInfo("=== Memory ===");
        MyLogger.myInfo(String.format("Used:  %d KB (%d MB)", used / 1024, used / 1048576));
        MyLogger.myInfo(String.format("Free:  %d KB (%d MB)", free / 1024, free / 1048576));
        MyLogger.myInfo(String.format("Total: %d KB (%d MB)", total / 1024, total / 1048576));
        MyLogger.myInfo(String.format("Max:   %d KB (%d MB)", max / 1024, max / 1048576));
        MyLogger.myInfo(String.format("Usage: %.1f%%", (double) used / max * 100));
    }

    private void handleGc() {
        MyLogger.myInfo("Running garbage collection...");
        long before = Runtime.getRuntime().freeMemory();
        System.gc();
        long after = Runtime.getRuntime().freeMemory();
        MyLogger.myInfo(String.format("Freed: %d KB (%d MB)",
                (after - before) / 1024, (after - before) / 1048576));
    }

    private void handleDump() {
        Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
        MyLogger.myInfo(String.format("=== Thread dump (%d threads) ===", stacks.size()));
        for (Map.Entry<Thread, StackTraceElement[]> entry : stacks.entrySet()) {
            Thread t = entry.getKey();
            MyLogger.myInfo(String.format("\"%s\" (state=%s, daemon=%s)",
                    t.getName(), t.getState(), t.isDaemon()));
            for (StackTraceElement el : entry.getValue()) {
                MyLogger.myInfo(String.format("    at %s", el));
            }
            MyLogger.myInfo("");
        }
    }

    private void handleClear() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    private void handlePing() {
        MyLogger.myInfo("pong");
    }

    private void handleVersion() {
        MyLogger.myInfo(String.format("Java: %s (%s)", System.getProperty("java.version"),
                System.getProperty("java.vm.name")));
        MyLogger.myInfo(String.format("OS:   %s %s", System.getProperty("os.name"),
                System.getProperty("os.version")));
        MyLogger.myInfo(String.format("User: %s", System.getProperty("user.name")));
    }

    private void handleEnv() {
        MyLogger.myInfo("=== System Properties ===");
        System.getProperties().forEach((k, v) ->
                MyLogger.myInfo(String.format("  %s = %s", k, v)));
    }

    private String formatUptime(long ms) {
        long days = ms / 86400000;
        long hours = (ms % 86400000) / 3600000;
        long minutes = (ms % 3600000) / 60000;
        long seconds = (ms % 60000) / 1000;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0 || days > 0) sb.append(hours).append("h ");
        if (minutes > 0 || hours > 0 || days > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");
        return sb.toString();
    }
}
