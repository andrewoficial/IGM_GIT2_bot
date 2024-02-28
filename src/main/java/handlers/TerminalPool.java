package handlers;

import services.MyLogger;

import java.util.Scanner;
import java.util.Set;



public class TerminalPool implements Runnable{

    private final Scanner sc = new Scanner(System.in);

    private String input = "";
    @Override
    public void run() {

        while (true) {
            System.out.println("Enter command:");
            input = sc.nextLine();
            if (input == null || input.length() < 2) {
                System.out.println("Wrong input");
            } else if ("/exit".equalsIgnoreCase(input)) {
                MyLogger.myInfo("Quit application");
                sc.close();
                System.exit(0);
            } else if ("/threads".equalsIgnoreCase(input)) {
                MyLogger.myInfo("Show current threads");
                Set<Thread> threads = Thread.getAllStackTraces().keySet();
                System.out.printf("%-15s \t %-15s \t %-15s \t %s\n", "Name", "State", "Priority", "isDaemon");
                for (Thread t : threads) {
                    System.out.printf("%-15s \t %-15s \t %-15d \t %s\n", t.getName(), t.getState(), t.getPriority(), t.isDaemon());
                }
            }
        }

    }
}
