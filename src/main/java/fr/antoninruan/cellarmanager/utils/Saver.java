package fr.antoninruan.cellarmanager.utils;

import fr.antoninruan.cellarmanager.MainApp;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Antonin Ruan
 */
public class Saver {

    private static int changeCount;
    private static final Timer timer = new Timer();

    public static void initSaver() {
        /*timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MainApp.saveFiles();
            }
        }, 0, 10 * 60 * 1000);*/
    }

    public static void doChange() {
        changeCount ++;
        if(changeCount == 10) {
            changeCount = 0;
            MainApp.saveFiles();
            timer.purge();
        } else {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MainApp.saveFiles();
                    changeCount = 0;
                    timer.purge();
                }
            },30L * 1000L);
        }
    }

    public static void cancelTask() {
        timer.purge();
        timer.cancel();
    }

}
