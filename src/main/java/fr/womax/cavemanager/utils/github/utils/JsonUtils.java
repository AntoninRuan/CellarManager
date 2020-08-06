package fr.womax.cavemanager.utils.github.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import fr.womax.cavemanager.utils.github.Test;

import java.text.ParseException;
import java.util.Date;

public class JsonUtils {

    public static String getAsString(JsonElement element) {
        return element instanceof JsonNull ? "" : element.getAsString();
    }

    public static boolean getAsBoolean(JsonElement element) {
        return !(element instanceof JsonNull) && element.getAsBoolean();
    }

    public static int getAsInt(JsonElement element) {
        return element instanceof JsonNull ? 0 : element.getAsInt();
    }

    public static Date getAsDate(JsonElement element) throws ParseException {
        return element instanceof JsonNull ? null : Test.DATE_FORMAT.parse(element.getAsString());
    }

}
