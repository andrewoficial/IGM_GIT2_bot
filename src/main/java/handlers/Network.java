package handlers;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import resources.Event;

/**
 * The Network class is used to interact with files
 * the class implements a primitive web server.
 * When creating an object, address:port is specified as a string parameter.
 * Once created, the class is best run as a thread. (Overridden run method)
 * At startup, an object is created from the internal nested private class NioThread
 * Inside this class, the run method is also overridden.
 * First, the init method is call. (It opens a port)
 * Then a cycle is start with a constant polling of data from the port and their processing.
 * Processing consists in an attempt to create a JSON object from the received data, on the basis of which an
 * object of the Event data type will be created for further transmission to telegrams.
 *  ! The program will not send a response to the request until it reads the message (JSON), converts it to an Event, sends it to telegram.
 * In some places the code is redundant, this is due to the fact that it is taken from the educational project.
 * <p>Author: Andrew Kantser</p>
 * <p>Date: 2023-06-10</p>
 *
 */
public class Network implements Runnable{
    private static final Logger logger2 = LogManager.getLogger(Network.class);
    private InetSocketAddress address;

    /**
     * Make object and set address.
     *
     * @param address InetSocketAddress data type i.e. 127.0.0.1:22222
     *
     */
    public Network(InetSocketAddress address) {
        this.address = address;
    }

    /**
     * Make Override run() for creating new object NioThread.
     *
     */
    public void run() {
        new NioThread().start();
    }


    /**
     * A class that constantly checks for data on a port.
     * If they are present, it sends a response with a status of 200.
     * Contains a collection of connections and a buffer.
     * If there is a connection request, adds it to the connection collection,
     * and then processes the connections previously in the collection (queue)
     * In some places the code is redundant, this is due to the fact that it is taken from the educational project.
     * <p>Author: Andrew Kantser</p>
     * <p>Date: 2023-06-14</p>
     *
     */
    private class NioThread extends Thread {
        private ServerSocketChannel serverChannel;
        private HashSet<SocketChannel> channels = new HashSet<>();

        private static final int BUF_SIZE = 15024;
        private volatile ByteBuffer byteBuffer;
        private Selector selector;

        /**
         * Performs initialization. Buffer location. Opening of the port, its monopolization.
         *
         * @throws IOException An IO exception will be thrown if something wrong with port or address
         *
         */
        private void init() throws IOException {
            byteBuffer = ByteBuffer.allocate(BUF_SIZE);
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(address);
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger2.info("  \nserver configured successfully"+"\n"+
                    "   BUF_SIZE:" + BUF_SIZE + "\n"+
                    "   address:" + address + "\n"+
                    "   summary info:" + serverChannel);
        }

        /**
         * Iterates over the connections in the map, checking their status. If this is a connection request, then it connects and puts it in the queue, if it is a data receipt, then it receives the data.
         *
         * IOException - Can be thrown if a network layer error occurs
         * ClassNotFoundException - may be thrown if the received data is not a JSON object (or it is corrupted)
         *
         */
        private void chooseEventReaction() throws IOException, ClassNotFoundException {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = keys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey selectionKey = keyIterator.next();
                if (selectionKey.isAcceptable()) {
                    SocketChannel channel = serverChannel.accept();
                    channels.add(channel);
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                    System.out.println("Client connected ");
                } else if (selectionKey.isReadable()) { // true, если событие OP_READ - в канале есть данные для чтения
                    SocketChannel currentChannel = (SocketChannel)selectionKey.channel();
                    int read = currentChannel.read(byteBuffer);
                    if (read == -1) continue;
                    String fromChannel = new String(byteBuffer.array(), 0, byteBuffer.limit(), StandardCharsets.UTF_8);
                    byteBuffer.compact(); // Сброс позиции и перемещение оставшихся данных в начало буфера ...
                    Event event = dataReaction(fromChannel);
                    if(event != null)
                        event.sendToTelegram();

                    System.out.println();
                    JSONObject responseJson = new JSONObject();
                    responseJson.put("status", 200);
                    responseJson.put("message", "Java answer...");

                    String response = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + responseJson.toString().length() + "\r\n" +
                            "Connection: close\r\n\r\n" + responseJson.toString();

                    ByteBuffer buffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
                    currentChannel.write(buffer);
                    logger2.info("Send close answer...");
                    byteBuffer = ByteBuffer.allocate(BUF_SIZE);
                    currentChannel.finishConnect();
                    selectionKey.cancel();
                    serverChannel.accept();
                }
                keyIterator.remove();
            }
        }

        @Override
        public void run() {
            boolean initialized = false;
            try {
                init();
                initialized = true;
            } catch (IOException e) {
                System.out.println("Initialization error (probably cant open port)");
                initialized = false;
            }
            while (initialized) {
                try {
                    chooseEventReaction();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * dataReaction method. From the received JSON object, an object of type Event is created.
     * (Which has methods for sending to telegrams)
     * All this happens while the client is waiting for a response (not in a separate thread),
     * this is done so that in the future it is planned to change the status of the response depending
     * on the errors that have occurred. (A large load is not planned for the application)
     *
     *  @param fromChannel String type date. Contain received data from post-request.
     *  @return Event object or null
     *
     *
     */
    public Event dataReaction(String fromChannel)  {
        if(fromChannel == null){
            return null;
        }
        //logger2.info("run dataReaction");
        //logger2.info(fromChannel);
        fromChannel = fromChannel.trim();
        if(fromChannel.split("\\{",2).length < 2){
            logger2.error("Symbol  {  not found");
            System.out.println("Symbol  {  not found");
            return null;
        }
        fromChannel = fromChannel.split("\\{",2)[1];
        fromChannel = "{" + fromChannel;

        JSONObject jsonObject = null;
        if(fromChannel.length() > 25) {
            try {
                jsonObject = new JSONObject(fromChannel);
            } catch (JSONException e) {
                logger2.error("An error occurred while trying to convert a string from the client to a JSON object");
                System.out.println("An error occurred while trying to convert a string from the client to a JSON object");
                //throw new RuntimeException(e);
            }
        }
        if(jsonObject == null) {
            logger2.error("Can`t read JSON");
            logger2.warn(fromChannel);
            System.out.println("Can`t read JSON");
            return null;
        }

        String action;
        try{
            action = jsonObject.getString("action");
        } catch (JSONException e) {
            logger2.warn("Parameter 'action' not found (wrong JSON message?)");
            System.out.println("Parameter 'action' not found (wrong JSON message?)");
            return null;
        }

        String repo;
        String body;
        String login;
        String title;
        String number = null;
        if("opened".equalsIgnoreCase(action)){
            try{
                repo = jsonObject.getJSONObject("repository").getString("name");//Different place
            } catch (JSONException e) {
                System.out.println("There was an error searching for the repository name parameter.when " + action);
                logger2.warn("There was an error searching for the repository name parameter.when " + action);
                return null;
            }
            try{
                number = (jsonObject.getInt("number")) + "";
            } catch (JSONException e) {
                System.out.println("There was an error searching for the task number parameter. when " + action);
                logger2.warn("There was an error searching for the task number parameter. when " + action);
                return null;
            }
            try{
                body = jsonObject.getJSONObject("issue").getString("body");
            } catch (JSONException e) {
                body = "";
                System.out.println("Ошибка при поиске параметра текста комментария репозитория. when " + action);
                logger2.info("Ошибка при поиске параметра текста комментария репозитория. when " + action);
            }

            try{
                title = jsonObject.getJSONObject("issue").getString("title");
            } catch (JSONException e) {
                title = "";
                System.out.println("There was an error searching for the header parameter. when " + action);
                logger2.info("There was an error searching for the header parameter. when " + action);
            }

            try{
                login = jsonObject.getJSONObject("sender").getString("email");
            } catch (JSONException e) {
                login = "dunno";
                System.out.println("There was an error searching for the sender name parameter. when " + action);
                logger2.warn("There was an error searching for the sender name parameter. when " + action);
            }
        }
        else if("created".equalsIgnoreCase(action)){
            try{
                repo = jsonObject.getJSONObject("repository").getString("name");
            } catch (JSONException e) {
                System.out.println("Error when searching for the repository name parameter. when " + action);
                logger2.warn("Error when searching for the repository name parameter. when " + action);
                return null;
            }

            try{
                number = (jsonObject.getJSONObject("issue").getInt("number")) + "";
            } catch (JSONException e) {
                System.out.println("There was an error searching for the task number parameter. when " + action);
                logger2.warn("There was an error searching for the task number parameter. when " + action);
                return null;
            }

            try{
                body = jsonObject.getJSONObject("comment").getString("body");
            } catch (JSONException e) {
                body = "";
                System.out.println("There was an error searching for the comment text parameter. when " + action);
                logger2.info("There was an error searching for the comment text parameter. when " + action);
            }

            try{
                title = jsonObject.getJSONObject("issue").getString("title");
            } catch (JSONException e) {
                title = "";
                System.out.println("There was an error searching for the header parameter. when " + action);
                logger2.info("There was an error searching for the header parameter. when " + action);
            }

            try{
                login = jsonObject.getJSONObject("sender").getString("email");
            } catch (JSONException e) {
                login = "dunno";
                System.out.println("There was an error searching for the sender name parameter. when " + action);
                logger2.warn("There was an error searching for the sender name parameter. when " + action);
            }
        }
        else if("closed".equalsIgnoreCase(action)){
            try{
                repo = jsonObject.getJSONObject("repository").getString("name");
            } catch (JSONException e) {
                System.out.println("Error when searching for the repository name parameter. when " + action);
                logger2.warn("Error when searching for the repository name parameter. when " + action);
                return null;
            }

            try{
                number = (jsonObject.getJSONObject("issue").getInt("number")) + "";
            } catch (JSONException e) {
                System.out.println("There was an error searching for the task number parameter. when " + action);
                logger2.warn("There was an error searching for the task number parameter. when " + action);
                return null;
            }

            try{
                body = jsonObject.getJSONObject("comment").getString("body");
            } catch (JSONException e) {
                body = "";
                System.out.println("There was an error searching for the comment text parameter. when " + action);
                logger2.info("There was an error searching for the comment text parameter. when " + action);
            }

            try{
                title = jsonObject.getJSONObject("issue").getString("title");
            } catch (JSONException e) {
                title = "";
                System.out.println("There was an error searching for the header parameter. when " + action);
                logger2.info("There was an error searching for the header parameter. when " + action);
            }

            try{
                login = jsonObject.getJSONObject("sender").getString("email");
            } catch (JSONException e) {
                login = "dunno";
                System.out.println("There was an error searching for the sender name parameter. when " + action);
                logger2.warn("There was an error searching for the sender name parameter. when " + action);
            }
        }
        else if("reopened".equalsIgnoreCase(action)){
            try{
                repo = jsonObject.getJSONObject("repository").getString("name");
            } catch (JSONException e) {
                System.out.println("Error when searching for the repository name parameter. when " + action);
                logger2.warn("Error when searching for the repository name parameter. when " + action);
                return null;
            }

            try{
                number = (jsonObject.getJSONObject("issue").getInt("number")) + "";
            } catch (JSONException e) {
                System.out.println("There was an error searching for the task number parameter. when " + action);
                logger2.warn("There was an error searching for the task number parameter. when " + action);
                return null;
            }

            try{
                body = jsonObject.getJSONObject("comment").getString("body");
            } catch (JSONException e) {
                body = "";
                System.out.println("There was an error searching for the comment text parameter. when " + action);
                logger2.info("There was an error searching for the comment text parameter. when " + action);
            }

            try{
                title = jsonObject.getJSONObject("issue").getString("title");
            } catch (JSONException e) {
                title = "";
                System.out.println("There was an error searching for the header parameter. when " + action);
                logger2.info("There was an error searching for the header parameter. when " + action);
            }

            try{
                login = jsonObject.getJSONObject("sender").getString("email");
            } catch (JSONException e) {
                login = "dunno";
                System.out.println("There was an error searching for the sender name parameter. when " + action);
                logger2.warn("There was an error searching for the sender name parameter. when " + action);
            }
        }
        else{
            logger2.error("Error determining when event type");
            System.out.println("Error determining when event type");
            return null;
        }


        logger2.info(repo);
        logger2.info(body);
        logger2.info(login);
        while(true){
            if(! body.contains("\r\n")){
                break;
            }else{
                body = body.replace("\r\n", " ");
            }
        }


        String [] attachments = body.split("изображение");
        ArrayList <String> links = new ArrayList<>();
        if(attachments.length > 0){
            for(int i=0; i<attachments.length; i++){
                if(attachments [i].contains("/")){
                    attachments [i] = attachments[i].split("\\)")[0];
                    attachments [i] = attachments[i].replace("]", "");
                    attachments [i] = attachments[i].replace("(", "");
                    links.add(attachments [i]);
                }
            }
        }
        attachments = new String[links.size()];
        int i = 0;
        for (String link : links) {
            attachments [i++] = "http://192.168.1.162:3000" + link;
        }

        for (String link : links) {
            body = body.replace("![изображение]("+link+")", "");
        }
        Event event = new Event(repo, number, action, login, body, title, attachments);
        System.out.println(event);
        return event;
    }
}
