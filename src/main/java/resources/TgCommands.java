package resources;
/**
 * Перечисление команд со стороны Telegram
 */
public enum TgCommands implements TgCommand{

    start("/start",
            "Команда инициализации бота") {

        String answer = "I am a bot written in Java by Andrei Kantser for the EMI-Pribor company. " +
                "\n I can asynchronously notify about new events on the company's local repository and respond to some commands. " +
                "\n Their list is available with the /help command";
        @Override
        public void run() {
            System.out.println(answer);
        }

        @Override
        public String getTextAnswer() {
            return answer;
        }
        public String getCommand(){
            return this.command;
        }
    },
    help("/help",
            "Команда вывода подсказки пользователю"){
        @Override
        public void run() {
            System.out.println("Выполняется команда help");
        }

        @Override
        public String getTextAnswer() {
            String answ = "";
            answ += "Список доступных команд и их описания \n";
            for (TgCommands tgCommand : TgCommands.values()) {
                answ += "Команда " + tgCommand.getCommand() + " " + tgCommand.getDescription() + "\n";
            }
            return answ;
        }
        public String getCommand(){
            return this.command;
        }
    },
    timg("/timg",
            "Тестовая отправка изображения"){
        @Override
        public void run() {
            System.out.println("Выполняется команда help");
        }

        @Override
        public String getTextAnswer() {
            String answ = "";
            answ += "Список доступных команд и их описания \n";
            for (TgCommands tgCommand : TgCommands.values()) {
                answ += "Команда " + tgCommand.getCommand() + " " + tgCommand.getDescription() + "\n";
            }
            return answ;
        }
        public String getCommand(){
            return this.command;
        }
    },
    talb("/talb",
            "Тестовая отправка альбома"){
        @Override
        public void run() {
            System.out.println("Выполняется команда talb");
        }

        @Override
        public String getTextAnswer() {
            return null;
        }
        public String getCommand(){
            return this.command;
        }
    },
    tgrur("/tgrur",
            "Курс валюты тенге к рублю"){
        @Override
        public void run() {
            System.out.println("Выполняется команда rgrur");
        }

        @Override
        public String getTextAnswer() {
            return null;
        }
        public String getCommand(){
            return this.command;
        }
    },
    stop("string1", "Des1");

    String command = "";
    String description = "";
    private TgCommands (String command, String description){
        this.command = command;
        this.description = description;
    }

    public String getCommand(){
        return this.command;
    }

    public String getDescription(){
        return this.description;
    }


    @Override
    public String toString() {
        return "[enum TgCommands {" + command + "}]";
    }

}
