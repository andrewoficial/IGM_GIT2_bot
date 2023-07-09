package resources;
/**
 * Перечисление команд со стороны Telegram
 */
public enum TgCommands implements TgCommand{

    start("/start",
            "Команда инициализации бота") {
        @Override
        public void run() {
            System.out.println("Я бот, написанный на Java  Канцером Андреем для компании ЭМИ-Прибор. " +
                    "\n Я умею асинхронно оповещать о новых событиях на локальном репозитории компании и отвечать на некоторые команды. " +
                    "\n Их список доступен по команде /help");
        }

        @Override
        public String getTextAnswer() {
            return "Я бот, написанный на Java  Канцером Андреем для компании ЭМИ-Прибор. " +
                    "\n Я умею асинхронно оповещать о новых событиях на локальном репозитории компании и отвечать на некоторые команды. " +
                    "\n Их список доступен по команде /help";
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
