package fr.antoninruan.cellarmanager.utils.github.model.commit;

import com.google.gson.JsonObject;
import fr.antoninruan.cellarmanager.utils.github.utils.JsonUtils;

public class File {

    private String sha;
    private String filename;
    private String status;
    private int additions;
    private int deletions;
    private int changes;
    private String blobUrl;
    private String rawUrl;
    private String contentsURl;
    private String patch;

    public File(String sha, String filename, String status, int additions, int deletions, int changes,
                String blobUrl, String rawUrl, String contentsURl, String patch) {
        this.sha = sha;
        this.filename = filename;
        this.status = status;
        this.additions = additions;
        this.deletions = deletions;
        this.changes = changes;
        this.blobUrl = blobUrl;
        this.rawUrl = rawUrl;
        this.contentsURl = contentsURl;
        this.patch = patch;
    }

    public String getSha() {
        return sha;
    }

    public String getFilename() {
        return filename;
    }

    public String getStatus() {
        return status;
    }

    public int getAdditions() {
        return additions;
    }

    public int getDeletions() {
        return deletions;
    }

    public int getChanges() {
        return changes;
    }

    public String getBlobUrl() {
        return blobUrl;
    }

    public String getRawUrl() {
        return rawUrl;
    }

    public String getContentsURl() {
        return contentsURl;
    }

    public String getPatch() {
        return patch;
    }

    public static File fromJson(JsonObject object) {
        String sha = JsonUtils.getAsString(object.get("sha"));
        String filename = JsonUtils.getAsString(object.get("filename"));
        String status = object.get("status").getAsString();
        int additions = JsonUtils.getAsInt(object.get("additions"));
        int deletions = JsonUtils.getAsInt(object.get("deletions"));
        int changes = JsonUtils.getAsInt(object.get("changes"));
        String blobUrl = JsonUtils.getAsString(object.get("blob_url"));
        String rawUrl = JsonUtils.getAsString(object.get("raw_url"));
        String contentsUrl = JsonUtils.getAsString(object.get("contents_url"));
        String patch = JsonUtils.getAsString(object.get("patch"));
        return new File(sha, filename, status, additions, deletions, changes, blobUrl, rawUrl, contentsUrl, patch);
    }

}
