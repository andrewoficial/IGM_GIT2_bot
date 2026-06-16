package handlers;

import org.json.JSONObject;
import org.json.JSONTokener;
import services.MyLogger;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RepoConfig {

    private static final Map<String, String> entries = new LinkedHashMap<>();
    private static String defaultTgId = "1811468195";
    private static boolean loaded = false;

    private static void ensureLoaded() {
        if (!loaded) {
            load();
            loaded = true;
        }
    }

    public static void load() {
        entries.clear();
        File file = new File("conf" + File.separator + "repositories.json");
        if (!file.exists()) {
            MyLogger.myWarn("repositories.json not found, creating template...");
            createTemplate(file);
            return;
        }
        try (FileInputStream in = new FileInputStream(file)) {
            JSONObject json = new JSONObject(new JSONTokener(in));
            if (json.has("default")) {
                defaultTgId = json.getString("default");
            }
            JSONObject repos = json.getJSONObject("entries");
            for (String key : repos.keySet()) {
                entries.put(key, repos.getString(key));
            }
            MyLogger.myInfo("Loaded " + entries.size() + " repositories from " + file.getAbsolutePath());
        } catch (Exception e) {
            MyLogger.myError("Failed to load repositories.json: " + e.getMessage());
        }
    }

    public static RepoInfo find(String repoName) {
        ensureLoaded();
        if (repoName == null) {
            return new RepoInfo("NotDefined", defaultTgId);
        }
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(repoName)) {
                return new RepoInfo(entry.getKey(), entry.getValue());
            }
        }
        return new RepoInfo("NotDefined", defaultTgId);
    }

    private static void createTemplate(File file) {
        try {
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("{\n");
                writer.write("  \"default\": \"1811468195\",\n");
                writer.write("  \"entries\": {\n");
                writer.write("    \"LocalPortal\": \"1811468195\",\n");
                writer.write("    \"IGM_13M\": \"-823582989\",\n");
                writer.write("    \"LongGas\": \"-823582989\",\n");
                writer.write("    \"PolarisMath\": \"-823582989\",\n");
                writer.write("    \"ard_ccm_fee\": \"-823582989\",\n");
                writer.write("    \"Multigassens\": \"-823582989\",\n");
                writer.write("    \"RelayArduinoSalavat\": \"-823582989\",\n");
                writer.write("    \"4CHANNELS\": \"-823582989\",\n");
                writer.write("    \"IGM-Basement\": \"-823582989\",\n");
                writer.write("    \"IPP330\": \"-823582989\",\n");
                writer.write("    \"IGM10M_Tool\": \"-823582989\",\n");
                writer.write("    \"RAK811_Multigassense_Remote_Control\": \"-823582989\",\n");
                writer.write("    \"RAK811_LVS\": \"-823582989\",\n");
                writer.write("    \"PagTool\": \"-823582989\",\n");
                writer.write("    \"IGM12M_IGM13M\": \"-823582989\",\n");
                writer.write("    \"VegaConnect\": \"-823582989\",\n");
                writer.write("    \"VOC\": \"-823582989\",\n");
                writer.write("    \"long_gas_java\": \"-823582989\"\n");
                writer.write("  }\n");
                writer.write("}\n");
            }
            MyLogger.myInfo("Created template: " + file.getAbsolutePath());
            MyLogger.myWarn("Fill repositories.json with your repo mappings and restart.");
        } catch (IOException e) {
            MyLogger.myError("Failed to create repositories.json: " + e.getMessage());
        }
    }

    public static class RepoInfo {
        private final String name;
        private final String tgId;

        public RepoInfo(String name, String tgId) {
            this.name = name;
            this.tgId = tgId;
        }

        public String getName() {
            return name;
        }

        public String getTgId() {
            return tgId;
        }
    }
}
