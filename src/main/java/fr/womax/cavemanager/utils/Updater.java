package fr.womax.cavemanager.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.womax.cavemanager.MainApp;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * @author Antonin Ruan
 */
public class Updater {

    public final static String VERSION;
    public final static int VERSION_MAJOR;
    public final static int VERSION_MINOR;
    public final static int VERSION_RELEASE;


    static {

        InputStreamReader reader = new InputStreamReader(MainApp.class.getClassLoader().getResourceAsStream("local.info"));
        JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
        JsonObject version = object.get("version").getAsJsonObject();
        VERSION_MAJOR = version.get("major").getAsInt();
        VERSION_MINOR = version.get("minor").getAsInt();
        VERSION_RELEASE = version.get("release").getAsInt();
        VERSION = String.format("%d.%d.%d", VERSION_MAJOR, VERSION_MINOR, VERSION_RELEASE);

    }

    public static boolean checkUpdate() {

        try {
            URL url = new URL("https://dl.dropboxusercontent.com/s/5gzei6yrm83hopg/remote.info?dl=0");

            InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());

            JsonObject remote = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
            JsonObject version = remote.getAsJsonObject("version");

            int remoteMajor = version.get("major").getAsInt();

            if(remoteMajor > VERSION_MAJOR)
                return true;

            int remoteMinor = version.get("minor").getAsInt();

            if(remoteMinor > VERSION_MINOR)
                return true;

            int remoteRelease = version.get("release").getAsInt();

            if(remoteRelease> VERSION_RELEASE)
                return true;
            else
                return false;

        } catch (IOException e) {
            DialogUtils.sendErrorWindow(e);
        }

        return false;
    }

    public static void update() {

        MainApp.saveFiles();

        Thread thread = new Thread(() -> {

            try {
                File currentJar = new File(MainApp.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                String[] split = currentJar.getAbsolutePath().split("\\.");
                String extenstion = split[split.length - 1];
                split[split.length - 1] = "dl";

                StringBuilder filePath = new StringBuilder();

                for(String s : split) {
                    filePath.append(s + ".");
                }

                File download = new File(filePath.toString());

                URL url = new URL("https://dl.dropboxusercontent.com/s/nq8bt1dndc1pc0z/CaveManager.jar?dl=1");

                long fileSize = getFileSize(url);

                System.out.println("File size=" + fileSize);

                ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());

                FileOutputStream fileOutputStream = new FileOutputStream(currentJar);

                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

                /*System.out.println("delete=" + currentJar.delete());

                System.out.println(download.renameTo(currentJar));*/

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Mise à jour télécharger");
                alert.setHeaderText("La mise à jour a été télécharger");
                alert.setContentText("Le programme va s'arrêter, relancer le pour appliquer la mise à jour");

                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);


                alert.showAndWait();
                System.exit(0);


            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }

        });

        thread.run();


    }

    private static long getFileSize(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            return conn.getContentLengthLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

}
