package fr.womax.cavemanager.utils.github.model.commit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.womax.cavemanager.utils.github.utils.JsonUtils;

public class Tree {

    private String sha;
    private String url;
    private JsonArray tree;
    private int size;
    private String content;

    Tree(String sha, String url, JsonArray tree) {
        this.sha = sha;
        this.url = url;
        this.tree = tree;
    }

    public String getSha() {
        return sha;
    }

    public String getUrl() {
        return url;
    }

    public JsonArray getTree() {
        return tree;
    }

    public static Tree fromJson(JsonObject object) {
        String sha = JsonUtils.getAsString(object.get("sha"));
        String url = JsonUtils.getAsString(object.get("url"));
        JsonArray tree = object.get("tree").getAsJsonArray();
        return new Tree(sha, url, tree);
    }

}
