package fr.antoninruan.cellarmanager.utils.github;

import fr.antoninruan.cellarmanager.utils.Updater;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Test {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static void main(String... args) throws Exception{

        Updater.checkUpdate();

    }

}
