package resources;

import services.MyLogger;

public interface   TgCommand {
    public default void run(){
        MyLogger.myInfo("Необрабатываемая команда run (дефолтная реализация в интерфейсе)");
    };

    public default String getTextAnswer(){
        return "Необрабатываемая команда getTextAnswer (дефолтная реализация в интерфейсе)";
    };

    public default String getDescription(){
        return "Необрабатываемая команда getDescription (дефолтная реализация в интерфейсе)";
    };

}
