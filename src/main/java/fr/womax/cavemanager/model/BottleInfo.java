package fr.womax.cavemanager.model;

import com.google.gson.JsonObject;
import fr.womax.cavemanager.MainApp;

/**
 * @author Antonin Ruan
 */
public class BottleInfo {

    private String name;
    private String region;
    private String domain;
    private String edition;
    private String comment;
    private int year;
    private WineType type;

    public BottleInfo(String name, String region, String domain, String edition, String comment, int year, WineType type) {
        this.name = name;
        this.region = region;
        this.domain = domain;
        this.edition = edition;
        this.comment = comment;
        this.year = year;
        this.type = type;
    }

    public Bottle createBottle() {
        Bottle bottle = new Bottle(name, region, domain, edition, comment, year, type);
        MainApp.getBottles().add(bottle);
        return bottle;
    }

    public void modifyBottle(Bottle bottle) {
        bottle.setName(name);
        bottle.setRegion(region);
        bottle.setDomain(domain);
        bottle.setEdition(edition);
        bottle.setComment(comment);
        bottle.setYear(String.valueOf(year));
        bottle.setType(type);
    }

    public static BottleInfo fromJson(JsonObject object) {
        return new BottleInfo(object.get("name").getAsString(), object.get("region").getAsString(), object.get("domain").getAsString(),
                object.get("edition").getAsString(), object.get("comment").getAsString(), object.get("year").getAsInt(),
                WineType.valueOf(object.get("type").getAsString()));
    }
}
