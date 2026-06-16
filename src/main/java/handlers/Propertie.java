package handlers;

import java.util.Properties;
import services.MyLogger;

/**
 * Holds configuration values. File I/O is handled by ConfigFileHelper.
 *
 * <p>Author: Andrew Kantser</p>
 * <p>Date: 2023-07-01</p>
 *
 */
public class Propertie {
    private String gitLogin;
    private String gitPass;
    private String gitAdress;
    private String tgToken;
    private String tgName;
    private boolean proxyTelegramEnable;
    private String proxyTelegramAddress;
    private int proxyTelegramPort;
    private String proxyTelegramLogin;
    private String proxyTelegramPassword;

    public Propertie(){
        this(ConfigFileHelper.loadOrCreateConfig());
    }

    public Propertie(Properties props){
        String username = props.getProperty("username");
        String password = props.getProperty("password");
        String addressGit = props.getProperty("addressGit");
        String tgToken = props.getProperty("tgtoken");
        String tgName = props.getProperty("tgname");
        MyLogger.myDebug("username: " + username);
        MyLogger.myDebug("password: " + (password != null ? password.substring(0, Math.min(3, password.length())) : null));
        MyLogger.myDebug("addressGit: " + addressGit);
        MyLogger.myDebug("tgToken: " + (tgToken != null ? tgToken.substring(0, Math.min(3, tgToken.length())) : null));
        MyLogger.myDebug("tgName: " + tgName);

        String proxyEnable = props.getProperty("connection.proxy.telegram.enable", "false");
        this.proxyTelegramEnable = "true".equalsIgnoreCase(proxyEnable);
        this.proxyTelegramAddress = props.getProperty("connection.proxy.telegram.address", "127.0.0.1");
        String proxyPort = props.getProperty("connection.proxy.telegram.port", "9025");
        this.proxyTelegramPort = Integer.parseInt(proxyPort);
        this.proxyTelegramLogin = props.getProperty("connection.proxy.telegram.login", "");
        this.proxyTelegramPassword = props.getProperty("connection.proxy.telegram.password", "");

        this.gitLogin = username;
        this.gitPass = password;
        this.gitAdress = addressGit;
        this.tgToken = tgToken;
        this.tgName = tgName;
    }

    public String getTgToken() {
        return tgToken;
    }

    public String getTgName() {
        return tgName;
    }

    public String getGitLogin() {
        return gitLogin;
    }

    public String getGitPass() {
        return gitPass;
    }

    public String getGitAdress() {
        return gitAdress;
    }

    public boolean isProxyTelegramEnable() {
        return proxyTelegramEnable;
    }

    public String getProxyTelegramAddress() {
        return proxyTelegramAddress;
    }

    public int getProxyTelegramPort() {
        return proxyTelegramPort;
    }

    public String getProxyTelegramLogin() {
        return proxyTelegramLogin;
    }

    public String getProxyTelegramPassword() {
        return proxyTelegramPassword;
    }
}
