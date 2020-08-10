package fr.antoninruan.cellarmanager.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.antoninruan.cellarmanager.utils.github.GitHubAPIService;
import fr.antoninruan.cellarmanager.utils.github.model.Repository;
import fr.antoninruan.cellarmanager.MainApp;
import fr.antoninruan.cellarmanager.utils.github.exception.GitHubAPIConnectionException;
import fr.antoninruan.cellarmanager.utils.github.exception.RepositoryNotFoundException;
import fr.antoninruan.cellarmanager.utils.github.model.release.Asset;
import fr.antoninruan.cellarmanager.utils.github.model.release.Release;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;

/**
 * @author Antonin Ruan
 */
public class Updater {

    public final static String VERSION;
    public final static int VERSION_MAJOR;
    public final static int VERSION_MINOR;
    public final static int VERSION_RELEASE;

    static {

        InputStreamReader reader = new InputStreamReader(Updater.class.getClassLoader().getResourceAsStream("local.info"));
        JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
        JsonObject version = object.get("version").getAsJsonObject();
        VERSION_MAJOR = version.get("major").getAsInt();
        VERSION_MINOR = version.get("minor").getAsInt();
        VERSION_RELEASE = version.get("release").getAsInt();
        VERSION = String.format("%d.%d.%d", VERSION_MAJOR, VERSION_MINOR, VERSION_RELEASE);

    }

    public static Pair<Boolean, Release> checkUpdate() {

        try {
            Repository repository = GitHubAPIService.getRepository("antoninruan", "cellarmanager");
            Release latest = repository.getLatestRelease();
            String tag = latest.getTagName();
            tag = tag.replace("v", "");
            String[] version = tag.split("\\.");

            if (Integer.parseInt(version[0]) > VERSION_MAJOR) {
                return new Pair <>(true, latest);
            } else if (Integer.parseInt(version[1]) > VERSION_MINOR && Integer.parseInt(version[0]) == VERSION_MAJOR) {
                return new Pair <>(true, latest);
            } else if (Integer.parseInt(version[2]) > VERSION_RELEASE && Integer.parseInt(version[1]) == VERSION_MINOR && Integer.parseInt(version[0]) == VERSION_MAJOR) {
                return new Pair <>(true, latest);
            }

        } catch (UnknownHostException e) {
            DialogUtils.infoMessage("Impossible de se connecter", "Impossible de se connecter à GitHub", "Vérifier votre connection internet avant de recommencer");
        }catch (IOException | RepositoryNotFoundException | ParseException | GitHubAPIConnectionException e) {
            DialogUtils.sendErrorWindow(e);
        }

        return new Pair <>(false, null);
    }

    public static void update() {

        MainApp.saveFiles();
        Saver.cancelTask();

        ProgressBar progressBar = DialogUtils.downloadInfo();

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

                Repository repository = GitHubAPIService.getRepository("antoninruan", "cellarmanager");
                Release release = repository.getLatestRelease();
                String downloadUrl = "";
                long fileSize = 0;
                for(Asset assets : release.getAssets()) {
                    String[] nameSplit = assets.getName().split("\\.");
                    if(nameSplit[nameSplit.length - 1].equalsIgnoreCase("jar")) {
                        downloadUrl = assets.getBrowserDownloadUrl();
                        fileSize = assets.getSize();
                        break;
                    }
                }

                URL url = new URL(downloadUrl);

                ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());

                FileOutputStream fileOutputStream = new FileOutputStream(currentJar);

//                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

                int i = 0;
                long dl;

                do {
                    dl = fileOutputStream.getChannel().transferFrom(readableByteChannel, i , 2048);
                    i += 2048;
                    int finalI = i;
                    long finalFileSize = fileSize;
                    Platform.runLater(() -> progressBar.setProgress((double) finalI / (double) finalFileSize));
                } while (dl != 0);


                Platform.runLater(() ->  {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Mise à jour téléchargée");
                    alert.setHeaderText("La mise à jour a été téléchargée");
                    alert.setContentText("Le programme va s'arrêter, relancer le pour appliquer la mise à jour");

                    ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);


                    alert.showAndWait();
                    System.exit(0);
                });


            } catch (IOException | ParseException | RepositoryNotFoundException | GitHubAPIConnectionException | URISyntaxException e) {
                DialogUtils.sendErrorWindow(e);
            }

        });

        thread.start();


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
