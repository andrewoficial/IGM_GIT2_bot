package handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * The ExchangeRate class is used to get exchange rate Russian rouble (RUB) and Kazakhstan tenge (tg)
 * This class does not use JSON extensions. It was created a very long time ago,
 * with the aim of training how to work with strings.
 *
 * <p>Author: Andrew Kantser</p>
 * <p>Date: 2023-02-02</p>
 *
 */
public class ExchangeRate {

    /**
     * To check authorization on the site (and correct operation), the site has a picture
     * with a known size, which is available only to this bot.
     * The method receives this picture and checks its size.
     *
     * @return String type. String contain marked-up text with current rates or errors.
     */
    public String getTgRubCourse()  {
        String text = "";

        try {
            String rssData = fetchRSS("http://www.cbr.ru/rss/RssCurrency");
            String date = rssData.split("UniDbQuery.To=")[1];
            date = date.substring(0, 10);
            String result = rssData.split("тенге")[1];
            result = result.substring(0, 9);
            result = result.replaceAll("[^A-Za-z0-9, '.', '-']", "");
            result = result.replace(',', '.');
            double rate = Double.parseDouble(result);
            rate = 100.0/rate;
            double scale = Math.pow(10, 2);
            double roundRate = Math.ceil(rate * scale) / scale;
            text += "Received RSS data RU ("+date+"):\n RUB-TG(100):" + result + " \n TG-RUB:" + roundRate;
        } catch (IOException e) {
            String error = "Возникла ошибка при парсинге ответа www.cbr.ru/rss/RssCurrency (IOException) "+ e.getMessage() + "\n" +
                    Arrays.toString(e.getStackTrace());
            System.out.println(error);
            text += error + "\n";
        } catch (ArrayIndexOutOfBoundsException e) {
            String error = "Возникла ошибка при парсинге ответа www.cbr.ru/rss/RssCurrency (ArrayIndexOutOfBoundsException) "+ e.getMessage() + "\n" +
                    Arrays.toString(e.getStackTrace());
            String rssData = null;
            try {
                rssData = fetchRSS("http://www.cbr.ru/rss/RssCurrency");
            } catch (IOException ex) {
                error = "Возникла ошибка при запросе  к www.cbr.ru/rss/RssCurrency (Exception) "+ e.getMessage() + "\n" +
                        Arrays.toString(e.getStackTrace());
                System.out.println(error);
                text += error + "\n";
            }
            rssData = rssData.substring(0, 350);
            error += rssData;
            System.out.println(error);
            text += error + "\n";
        } catch (Exception e) {
            String error = "Возникла ошибка при парсинге ответа www.cbr.ru/rss/RssCurrency (Exception) "+ e.getMessage() + "\n" +
                    Arrays.toString(e.getStackTrace());
            System.out.println(error);
            text += error + "\n";
        }finally {
            text +="\n";
        }

        try {
            String rssData = fetchRSS("https://nationalbank.kz/rss/rates_all.xml");
            String date = rssData.split("<pubDate>")[1];
            date = date.substring(0, 10);
            String result = rssData.split("RUB")[1];
            result = result.split("<description>")[1];
            result = result.substring(0, 6);
            result = result.replaceAll("[^A-Za-z0-9, '.']", "");
            double rate = Double.parseDouble(result);
            rate = 100.0/rate;
            double scale = Math.pow(10, 2);
            double roundRate = Math.ceil(rate * scale) / scale;
            text += "Received XML data KZ ("+date+"):\n RUB-TG(100):" + roundRate + " \n TG-RUB:" + result;

        } catch (IOException e) {
            String error = "Возникла ошибка при парсинге ответа https://nationalbank.kz/rss/rates_all.xml (IOException) " + e.getMessage() + "\n" +
                    Arrays.toString(e.getStackTrace());
            System.out.println(error);
            text += error + "\n";
        } catch (ArrayIndexOutOfBoundsException e) {
            String error = "Возникла ошибка при парсинге ответа https://nationalbank.kz/rss/rates_all.xml (ArrayIndexOutOfBoundsException) " + e.getMessage() + "\n" +
                    Arrays.toString(e.getStackTrace());
            System.out.println(error);
            text += error + "\n";
        }catch (Exception e){
            String error = "Возникла ошибка при парсинге ответа https://nationalbank.kz/rss/rates_all.xml (Exception) " + e.getMessage() + "\n" +
                    Arrays.toString(e.getStackTrace());
            System.out.println(error);
            text += error + "\n";
        }finally {
            text +="\n";
        }

        try {
            String rssData = fetchRSS("https://mig.kz/api/v1/gadget/html");
            String result = rssData.split(">EUR<")[1];
            result = result.split("<tr>")[1];
            result = result.split(">KGS<")[0];
            String buy = result.split("td class=\"buy delta-")[1];
            buy = buy.split(">")[1];
            buy = buy.substring(0, 5);
            buy = buy.replaceAll("[^A-Za-z0-9, '.']", "");
            buy = buy.substring(0, 3);
            buy = buy.replace(',', '.');
            double bRate = Double.parseDouble(buy);
            bRate = 100.0/bRate;
            double bScale = Math.pow(10, 2);
            bRate = Math.ceil(bRate * bScale) / bScale;

            //System.out.println(bRate);
            String sell  = result.split("td class=\"sell delta-")[1];
            sell = sell.split(">")[1];
            sell  = sell .substring(0, 5);
            sell  = sell .replaceAll("[^A-Za-z0-9, '.']", "");
            sell  = sell .substring(0, 3);
            sell = sell.replace(',', '.');
            double sRate = Double.parseDouble(sell);
            sRate = 100.0/sRate;
            double sScale = Math.pow(10, 2);
            sRate = Math.ceil(sRate * sScale) / sScale;
            text += "Parsed  data MIG.KZ (buy/sell):\n RUB-TG(100):" + bRate + "/" + sRate +" \n TG-RUB:" + buy+"/"+ sell;
        } catch (IOException e) {
            String error = "Возникла ошибка при парсинге ответа https://mig.kz/api/v1/gadget/html (IOException) " + e.getMessage() + "\n" +
                    Arrays.toString(e.getStackTrace());
            System.out.println(error);
            text += error + "\n";
        } catch (ArrayIndexOutOfBoundsException e) {
            String error = "Возникла ошибка при парсинге ответа https://mig.kz/api/v1/gadget/html (ArrayIndexOutOfBoundsException) " + e.getMessage() + "\n" +
                    Arrays.toString(e.getStackTrace());
            System.out.println(error);
            text += error + "\n";
        }catch (Exception e) {
            String error = "Возникла ошибка при парсинге ответа https://mig.kz/api/v1/gadget/html (Exception) " + e.getMessage() + "\n" +
                    Arrays.toString(e.getStackTrace());
            System.out.println(error);
            text += error + "\n";
        }finally {
            text +="\n";
        }
        return text;
    }



    /**
     * Executes a request at the specified address and returns the response as a string.
     *
     * @param url - String type with address.
     * @return String type. String contain server answer.
     * @throws  IOException - Throws an exception if an address access error occurs.
     */
    private String fetchRSS(String url) throws IOException {
        URL rssURL = new URL(url);
        URLConnection connection = rssURL.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));
        StringBuilder rssData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            rssData.append(line);
        }
        reader.close();
        return rssData.toString();
    }
}
