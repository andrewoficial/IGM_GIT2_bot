package resources;

public interface   TgCommand {
    public default void run(){
        System.out.println("Необрабатываемая команда run (дефолтная реализация в интерфейсе)");
    };

    public default String getTextAnswer(){
        return "Необрабатываемая команда getTextAnswer (дефолтная реализация в интерфейсе)";
    };

    public default String getDescription(){
        return "Необрабатываемая команда getDescription (дефолтная реализация в интерфейсе)";
    };

}
