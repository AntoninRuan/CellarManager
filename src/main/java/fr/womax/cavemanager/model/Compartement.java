package fr.womax.cavemanager.model;

import com.google.gson.JsonObject;
import fr.womax.cavemanager.MainApp;

/**
 * @author Antonin Ruan
 */
public class Compartement {

    private int id;

    private int row, column;
    private Spot[][] spots;

    Compartement(int row, int column) {
        this.id = MainApp.nextCompartementId();
        this.row = row;
        this.column = column;
        this.spots = new Spot[row][column];
        fillEmpty();
    }

    Compartement(int raw, int column, Spot[][] spots) {
        this.id = MainApp.nextCompartementId();
        this.row = raw;
        this.column = column;
        this.spots = spots;
    }

    public int getId() {
        return id;
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

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Spot[][] getSpots() {
        return spots;
    }

    public static Compartement fromJson(JsonObject jsonObject) {
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

       return new Compartement(row, column, spots);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
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
}
