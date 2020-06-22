package fr.womax.cavemanager.utils;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author Antonin Ruan
 */
public class DropboxUtils {

    private static String ACCESS_TOKEN = "Ncg9OmFPiaAAAAAAAAADLg4g4K8F3ZzDtPzVrYhSRql104TD8D_89AOy-oDmt9i1";
    private static DateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy");

    public static void sendBugIssue(String title, String description, Date date, String exceptionMessage) {

        JsonObject object = new JsonObject();
        object.addProperty("title", title);
        object.addProperty("description", description);
        object.addProperty("date", dateFormat.format(date));
        object.addProperty("exception", exceptionMessage);

        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/cave_manager").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        try (InputStream in = IOUtils.toInputStream(object.toString(), StandardCharsets.UTF_8)) {
            UUID uuid = UUID.randomUUID();
            FileMetadata metadata = client.files().uploadBuilder("/bug-issues/" + uuid.toString() + ".json").uploadAndFinish(in);
            DialogUtils.infoMessage("Rapport de bug envoyé", null, "Votre rapport de bug a bien été envoyé");
        } catch (IOException | DbxException e) {
            DialogUtils.sendErrorWindow(e);
        }

    }

    public static void sendSuggestion(String title, String description, Date date) {
        JsonObject object = new JsonObject();
        object.addProperty("title", title);
        object.addProperty("description", description);
        object.addProperty("date", dateFormat.format(date));

        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/cave_manager").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        try (InputStream in = new ByteArrayInputStream(StandardCharsets.UTF_8.encode(object.toString()).array())){
            UUID uuid = UUID.randomUUID();
            FileMetadata metadata = client.files().uploadBuilder("/suggestion/" + uuid.toString() + ".json").uploadAndFinish(in);
        } catch (IOException | DbxException e) {
            DialogUtils.sendErrorWindow(e);
        }

    }

}
