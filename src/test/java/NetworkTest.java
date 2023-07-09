import handlers.Network;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import resources.Event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class NetworkTest {

    private Network network;

    @BeforeEach
    public void setUp() {
        // Инициализация объекта Network перед каждым тестом
        network = new Network(null);
    }



    @Test
    public void testValidDataReaction_01() {
        //Открытие задачи

        String filePath = "src/test/java/Samples/sample_01.txt";
        String content = "";
        try {
            // Чтение содержимого файла в строку
            content = readFileToString(filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + filePath + "  " + e.getMessage());
        }
        Event result = null;
        try {
            result = network.dataReaction(content);
        } catch (IOException e) {
            System.out.println(content);
            //throw new RuntimeException(e);
        }
        Assertions.assertNotNull(result); // убедиться, что результат не равен null
        Assertions.assertEquals("LocalPortal", result.getRepo());
        Assertions.assertEquals("Комментарий к открытию", result.getBody());
        Assertions.assertEquals("Открытие новой задачи", result.getTitle());
        Assertions.assertEquals("work.andrey.igm@yandex.ru", result.getLogin());
        Assertions.assertEquals("72", result.getNumber());
    }

    @Test
    public void testValidDataReaction_02() {
        //Комментарий к задаче

        String filePath = "src/test/java/Samples/sample_02.txt";
        String content = "";
        try {
            // Чтение содержимого файла в строку
            content = readFileToString(filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + filePath + "  " + e.getMessage());
        }
        Event result = null;
        try {
            result = network.dataReaction(content);
        } catch (IOException e) {
            System.out.println(content);
            //throw new RuntimeException(e);
        }
        Assertions.assertNotNull(result); // убедиться, что результат не равен null
        Assertions.assertEquals("LocalPortal", result.getRepo());
        Assertions.assertEquals("Добавление комментариев 4", result.getBody());
        Assertions.assertEquals("Открытие новой задачи", result.getTitle());
        Assertions.assertEquals("work.andrey.igm@yandex.ru", result.getLogin());
        Assertions.assertEquals("72", result.getNumber());
    }

    @Test
    public void testValidDataReaction_03() {
        //Комментарий к задаче с картинкой с яндекс-диска

        String filePath = "src/test/java/Samples/sample_03.txt";
        String content = "";
        try {
            // Чтение содержимого файла в строку
            content = readFileToString(filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + filePath + "  " + e.getMessage());
        }
        Event result = null;
        try {
            result = network.dataReaction(content);
        } catch (IOException e) {
            System.out.println(content);
            //throw new RuntimeException(e);
        }
        Assertions.assertNotNull(result); // убедиться, что результат не равен null
        Assertions.assertEquals("LocalPortal", result.getRepo());
        Assertions.assertEquals("Комент с картинкой из яндекс-диска ", result.getBody());
        Assertions.assertEquals("Открытие новой задачи", result.getTitle());
        Assertions.assertEquals("work.andrey.igm@yandex.ru", result.getLogin());
        Assertions.assertEquals("72", result.getNumber());
        Assertions.assertEquals("http://192.168.1.162:3000/attachments/54504a44-931a-414c-85d4-a5473b183cac", result.getAttachments()[0]);
    }

    @Test
    public void testValidDataReaction_04() {
        //Комментарий к задаче с картинкой с ножниц

        String filePath = "src/test/java/Samples/sample_04.txt";
        String content = "";
        try {
            // Чтение содержимого файла в строку
            content = readFileToString(filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + filePath + "  " + e.getMessage());
        }
        Event result = null;
        try {
            result = network.dataReaction(content);
        } catch (IOException e) {
            System.out.println(content);
            //throw new RuntimeException(e);
        }
        Assertions.assertNotNull(result); // убедиться, что результат не равен null
        Assertions.assertEquals("LocalPortal", result.getRepo());
        Assertions.assertEquals("Добавление комментаия с няшным киллуа но в тексте JPG (ножницы)  Хоба    Хоба 2  ", result.getBody());
        Assertions.assertEquals("Открытие новой задачи", result.getTitle());
        Assertions.assertEquals("work.andrey.igm@yandex.ru", result.getLogin());
        Assertions.assertEquals("72", result.getNumber());
        Assertions.assertEquals("http://192.168.1.162:3000/attachments/60e32876-b794-4645-b020-2d0dd1c0bf5a", result.getAttachments()[0]);
        Assertions.assertEquals("http://192.168.1.162:3000/attachments/f551a29b-f485-43a2-bbdb-a0b735a4359b", result.getAttachments()[1]);
    }

    @Test
    public void testValidDataReaction_05() {
        //Комментарий с 15 картинками в тексте (посылка была сломана - починил)

        String filePath = "src/test/java/Samples/sample_05.txt";
        String content = "";
        try {
            // Чтение содержимого файла в строку
            content = readFileToString(filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + filePath + "  " + e.getMessage());
        }
        Event result = null;
        try {
            result = network.dataReaction(content);
        } catch (IOException e) {
            System.out.println(content);
            //throw new RuntimeException(e);
        }
        Assertions.assertNotNull(result); // убедиться, что результат не равен null
        Assertions.assertEquals("LocalPortal", result.getRepo());
        Assertions.assertEquals("1   2   3   4   5   6   7   8   9    10   11   12   13   14   15  Конец сообщения", result.getBody());
        Assertions.assertEquals("Открытие новой задачи", result.getTitle());
        Assertions.assertEquals("work.andrey.igm@yandex.ru", result.getLogin());
        Assertions.assertEquals("72", result.getNumber());
        Assertions.assertEquals("http://192.168.1.162:3000/attachments/a6a0fdf3-7c8b-4844-90b1-65f88070f4ed", result.getAttachments()[14]);
    }

    @Test
    public void testValidDataReaction_06() {
        //Комментарий к закрытой задаче

        String filePath = "src/test/java/Samples/sample_06.txt";
        String content = "";
        try {
            // Чтение содержимого файла в строку
            content = readFileToString(filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + filePath + "  " + e.getMessage());
        }
        Event result = null;
        try {
            result = network.dataReaction(content);
        } catch (IOException e) {
            System.out.println(content);
            //throw new RuntimeException(e);
        }
        Assertions.assertNotNull(result); // убедиться, что результат не равен null
        Assertions.assertEquals("LocalPortal", result.getRepo());
        Assertions.assertEquals("Комментарий у открытию закрытой задачи", result.getBody());
        Assertions.assertEquals("Открытие новой задачи", result.getTitle());
        Assertions.assertEquals("work.andrey.igm@yandex.ru", result.getLogin());
        Assertions.assertEquals("72", result.getNumber());
    }

    @Test
    public void testValidDataReaction_07() {
        //Комментарий картинка как вложение

        String filePath = "src/test/java/Samples/sample_07.txt";
        String content = "";
        try {
            // Чтение содержимого файла в строку
            content = readFileToString(filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + filePath + "  " + e.getMessage());
        }
        Event result = null;
        try {
            result = network.dataReaction(content);
        } catch (IOException e) {
            System.out.println(content);
            //throw new RuntimeException(e);
        }
        Assertions.assertNotNull(result); // убедиться, что результат не равен null
        Assertions.assertEquals("LocalPortal", result.getRepo());
        Assertions.assertEquals("Добавление комментаия с няшным киллуа", result.getBody());
        Assertions.assertEquals("Открытие новой задачи", result.getTitle());
        Assertions.assertEquals("work.andrey.igm@yandex.ru", result.getLogin());
        Assertions.assertEquals("72", result.getNumber());
    }

    @Test
    public void testValidDataReaction_08() {
        //Закрытие без комментария

        String filePath = "src/test/java/Samples/sample_08.txt";
        String content = "";
        try {
            // Чтение содержимого файла в строку
            content = readFileToString(filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + filePath + "  " + e.getMessage());
        }
        Event result = null;
        try {
            result = network.dataReaction(content);
        } catch (IOException e) {
            System.out.println(content);
            //throw new RuntimeException(e);
        }
        Assertions.assertNotNull(result); // убедиться, что результат не равен null
        Assertions.assertEquals("LocalPortal", result.getRepo());
        Assertions.assertEquals("", result.getBody());
        Assertions.assertEquals("Открытие новой задачи", result.getTitle());
        Assertions.assertEquals("work.andrey.igm@yandex.ru", result.getLogin());
        Assertions.assertEquals("72", result.getNumber());
    }

    @Test
    public void testValidDataReaction_09() {
        //Закрытие с комментарием

        String filePath = "src/test/java/Samples/sample_09.txt";
        String content = "";
        try {
            // Чтение содержимого файла в строку
            content = readFileToString(filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + filePath + "  " + e.getMessage());
        }
        Event result = null;
        try {
            result = network.dataReaction(content);
        } catch (IOException e) {
            System.out.println(content);
            //throw new RuntimeException(e);
        }
        Assertions.assertNotNull(result); // убедиться, что результат не равен null
        Assertions.assertEquals("LocalPortal", result.getRepo());
        Assertions.assertEquals("Комментарий при закрытии задачи", result.getBody());
        Assertions.assertEquals("Открытие новой задачи", result.getTitle());
        Assertions.assertEquals("work.andrey.igm@yandex.ru", result.getLogin());
        Assertions.assertEquals("72", result.getNumber());
    }

    @Test
    public void testValidDataReaction_10() {
        //Переоткрытие без комментария

        String filePath = "src/test/java/Samples/sample_10.txt";
        String content = "";
        try {
            // Чтение содержимого файла в строку
            content = readFileToString(filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + filePath + "  " + e.getMessage());
        }
        Event result = null;
        try {
            result = network.dataReaction(content);
        } catch (IOException e) {
            System.out.println(content);
            //throw new RuntimeException(e);
        }
        Assertions.assertNotNull(result); // убедиться, что результат не равен null
        Assertions.assertEquals("LocalPortal", result.getRepo());
        Assertions.assertEquals("", result.getBody());
        Assertions.assertEquals("Открытие новой задачи", result.getTitle());
        Assertions.assertEquals("work.andrey.igm@yandex.ru", result.getLogin());
        Assertions.assertEquals("72", result.getNumber());
    }


    @Test
    public void testInvalidDataReaction_14() {
        // Проверка на сломанную строку (без "{")

        String input = "some data but not json";
        Event result = new Event("", "", "", "", "", "",null);
        try {
            result = network.dataReaction(input);
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }
        Assertions.assertNull(result);
    }


    private String readFileToString(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }
}