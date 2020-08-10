package fr.antoninruan.cellarmanager.model;

import com.google.gson.JsonObject;
import fr.antoninruan.cellarmanager.MainApp;

/**
 * @author Antonin Ruan
 */
public class Compartment {

    private int id;

    private int index;
    private String name;
    private int row, column;
    private Spot[][] spots;

    Compartment(int id, String name, int row, int column) {
        this.id = id;
        this.row = row;
        this.column = column;
        this.spots = new Spot[row][column];
        fillEmpty();
    }

    Compartment(String name, int row, int column, int index) {
        this.name = name.trim().isEmpty() ? "Etagère" : name;
        this.id = MainApp.nextCompartementId();
        this.row = row;
        this.column = column;
        this.spots = new Spot[row][column];
        this.index = index;
        fillEmpty();
    }

    Compartment(int id, String name, int raw, int column, Spot[][] spots, int index) {
        this.name = name;
        this.id = id;
        this.row = raw;
        this.column = column;
        this.spots = spots;
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private void fillEmpty() {
        for(int i = 0; i < row; i ++) {

            for(int j = 0; j < column; j++) {
                int id = i * 100 + j;
                Spot spot = new Spot(i, j);
                spots[i][j] = spot;
            }

        }
    }

    public String getName() {
        return name;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Spot[][] getSpots() {
        return spots;
    }

    public static Compartment fromJson(JsonObject jsonObject, int ids) {
        String name = jsonObject.get("name").getAsString();
        int index = jsonObject.has("index") ? jsonObject.get("index").getAsInt() : ids;
        int row = jsonObject.get("raw_number").getAsInt();
        int column = jsonObject.get("column_number").getAsInt();
        JsonObject spotsJson = jsonObject.get("spots").getAsJsonObject();
        Spot[][] spots = new Spot[row][column];

        for(int i = 0; i < row; i ++) {

            for(int j = 0; j < column; j++) {
                int id = (i * 100) + j;
                Spot spot = Spot.fromJson(spotsJson.get(String.valueOf(id)).getAsJsonObject());
                spots[i][j] = spot;
            }

       }

       return new Compartment(ids, name, row, column, spots, index);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("index", index);
        jsonObject.addProperty("raw_number", row);
        jsonObject.addProperty("column_number", column);

        JsonObject spots = new JsonObject();
        for(int i = 0; i < row; i ++) {

            for (int j = 0; j < column; j ++) {

                Spot spot = this.spots[i][j];
                spots.add(String.valueOf(spot.getId()), spot.toJson());

            }

        }
        jsonObject.add("spots", spots);
        return jsonObject;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public void setName(String name) {
        this.name = name;
    }
}
