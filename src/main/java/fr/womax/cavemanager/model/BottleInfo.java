package fr.womax.cavemanager.model;

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
    private int consumeYear;
    private WineType type;

    public BottleInfo(String name, String region, String domain, String edition, String comment, int year, int consumeYear, WineType type) {
        this.name = name;
        this.region = region;
        this.domain = domain;
        this.edition = edition;
        this.comment = comment;
        this.year = year;
        this.consumeYear = consumeYear;
        this.type = type;
    }

    public Bottle createBottle() {
        Bottle bottle = new Bottle(name, region, domain, edition, comment, year, consumeYear, type);
        MainApp.getBottles().put(bottle.getId(), bottle);
        return bottle;
    }

    public void modifyBottle(Bottle bottle) {
        bottle.setName(name);
        bottle.setRegion(region);
        bottle.setDomain(domain);
        bottle.setEdition(edition);
        bottle.setComment(comment);
        bottle.setYear(year);
        bottle.setConsumeYear(consumeYear);
        bottle.setType(type);
    }

}
