package fr.antoninruan.cellarmanager.utils;

import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class PreferencesManager {

    private static Locale lang;
    private static int doubleClickDelay;
    private static boolean neverConnectToGitHub;
    private static boolean checkUpdateAtStart;

    private static String saveFilePath;
    private static String bottleFilePath;

    public static Locale getLang() {
        return lang;
    }

    public static void setLang(Locale lang) {
        if(lang != PreferencesManager.lang) {
            PreferencesManager.lang = lang;
        }
    }

    public static int getDoubleClickDelay() {
        return doubleClickDelay;
    }

    public static void setDoubleClickDelay(int doubleClickDelay) {
        PreferencesManager.doubleClickDelay = doubleClickDelay;
    }

    public static boolean isNeverConnectToGitHub() {
        return neverConnectToGitHub;
    }

    public static void setNeverConnectToGitHub(boolean neverConnectToGitHub) {
        PreferencesManager.neverConnectToGitHub = neverConnectToGitHub;
    }

    public static boolean doCheckUpdateAtStart() {
        return checkUpdateAtStart;
    }

    public static void setCheckUpdateAtStart(boolean checkUpdateAtStart) {
        PreferencesManager.checkUpdateAtStart = checkUpdateAtStart;
    }

    public static String getSaveFilePath() {
        return saveFilePath;
    }

    public static void setSaveFilePath(String saveFilePath) {
        PreferencesManager.saveFilePath = saveFilePath;
    }

    public static String getBottleFilePath() {
        return bottleFilePath;
    }

    public static void setBottleFilePath(String bottleFilePath) {
        PreferencesManager.bottleFilePath = bottleFilePath;
    }

    public static void loadPreferences(JsonObject object) {
        checkUpdateAtStart = object.has("check_update") ? JsonUtils.getAsBoolean(object.get("check_update")) : true;
        lang = object.has("lang") ? Locale.forLanguageTag(JsonUtils.getAsString(object.get("lang"))) : Locale.FRENCH;
        doubleClickDelay = object.has("double_click_delay") ? JsonUtils.getAsInt(object.get("double_click_delay")): 500;
        neverConnectToGitHub = object.has("never_connect_to_github") && JsonUtils.getAsBoolean(object.get("never_connect_to_github"));

        saveFilePath = object.has("save_file") ? JsonUtils.getAsString(object.get("save_file")) : null;
        bottleFilePath = object.has("bottle_file") ? JsonUtils.getAsString(object.get("bottle_file")) : null;
    }

    public static void savePreferences(File file) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            JsonObject object = new JsonObject();
            object.addProperty("check_update", checkUpdateAtStart);
            object.addProperty("lang", lang.toLanguageTag());
            object.addProperty("double_click_delay", doubleClickDelay);
            object.addProperty("never_connect_to_github", neverConnectToGitHub);
            object.addProperty("save_file", saveFilePath);
            object.addProperty("bottle_file", bottleFilePath);

            writer.write(object.toString());
            writer.flush();
        } catch (IOException e) {
            DialogUtils.sendErrorWindow(e);
        }

    }
}
