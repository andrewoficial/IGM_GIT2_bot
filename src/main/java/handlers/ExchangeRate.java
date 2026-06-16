package handlers;

import services.MyLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExchangeRate {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(-?\\d+[,\\.]?\\d*)");

    public String getTgRubCourse()  {
        String text = "";

        text += parseCbr();
        text += parseNationalBankKz();
        text += parseMigKz();
        return text;
    }

    private String parseCbr() {
        try {
            String rssData = fetchRSS("http://www.cbr.ru/rss/RssCurrency");
            String date = extractBetween(rssData, "UniDbQuery.To=", 10);
            String raw = rssData.split("тенге")[1];
            String value = extractNumber(raw);
            if (value.isEmpty()) {
                return "CBR: rate not found\n\n";
            }
            value = value.replace(',', '.');
            double rate = Double.parseDouble(value);
            double inverted = Math.ceil(100.0 / rate * 100.0) / 100.0;
            return "Received RSS data RU (" + date + "):\n RUB-TG(100):" + value + " \n TG-RUB:" + inverted + "\n\n";
        } catch (Exception e) {
            String error = "CBR parse error: " + e.getMessage();
            MyLogger.myError(error);
            return error + "\n\n";
        }
    }

    private String parseNationalBankKz() {
        try {
            String rssData = fetchRSS("https://nationalbank.kz/rss/rates_all.xml");
            String date = extractBetween(rssData, "<pubDate>", 10);
            String afterRuble = rssData.split("RUB")[1];
            String raw = afterRuble.split("<description>")[1];
            String value = extractNumber(raw);
            if (value.isEmpty()) {
                return "KZ: rate not found\n\n";
            }
            double rate = Double.parseDouble(value);
            double inverted = Math.ceil(100.0 / rate * 100.0) / 100.0;
            return "Received XML data KZ (" + date + "):\n RUB-TG(100):" + inverted + " \n TG-RUB:" + value + "\n\n";
        } catch (Exception e) {
            String error = "KZ parse error: " + e.getMessage();
            MyLogger.myError(error);
            return error + "\n\n";
        }
    }

    private String parseMigKz() {
        try {
            String html = fetchRSS("https://mig.kz/api/v1/gadget/html");
            String eurBlock = html.split(">EUR<")[1];
            eurBlock = eurBlock.split("<tr>")[1];
            eurBlock = eurBlock.split(">KGS<")[0];

            String buyRaw = eurBlock.split("td class=\"buy delta-")[1];
            buyRaw = buyRaw.split(">")[1];
            String buyStr = extractNumber(buyRaw).replace(',', '.');
            if (buyStr.length() > 3) buyStr = buyStr.substring(0, 3);

            String sellRaw = eurBlock.split("td class=\"sell delta-")[1];
            sellRaw = sellRaw.split(">")[1];
            String sellStr = extractNumber(sellRaw).replace(',', '.');
            if (sellStr.length() > 3) sellStr = sellStr.substring(0, 3);

            if (buyStr.isEmpty() || sellStr.isEmpty()) {
                return "MIG.KZ: rate not found\n\n";
            }

            double bRate = Double.parseDouble(buyStr);
            double sRate = Double.parseDouble(sellStr);
            double bInverted = Math.ceil(100.0 / bRate * 100.0) / 100.0;
            double sInverted = Math.ceil(100.0 / sRate * 100.0) / 100.0;
            return "Parsed data MIG.KZ (buy/sell):\n RUB-TG(100):" + bInverted + "/" + sInverted + " \n TG-RUB:" + buyStr + "/" + sellStr + "\n\n";
        } catch (Exception e) {
            String error = "MIG.KZ parse error: " + e.getMessage();
            MyLogger.myError(error);
            return error + "\n\n";
        }
    }

    private String extractNumber(String text) {
        Matcher m = NUMBER_PATTERN.matcher(text);
        return m.find() ? m.group(1) : "";
    }

    private String extractBetween(String text, String after, int maxLen) {
        String part = text.split(after)[1];
        return part.substring(0, Math.min(maxLen, part.length()));
    }

    private String fetchRSS(String url) throws IOException {
        URL rssURL = new URL(url);
        URLConnection connection = rssURL.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        StringBuilder rssData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            rssData.append(line);
        }
        reader.close();
        return rssData.toString();
    }
}
