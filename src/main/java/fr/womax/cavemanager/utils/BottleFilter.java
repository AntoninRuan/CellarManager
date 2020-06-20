package fr.womax.cavemanager.utils;

import fr.womax.cavemanager.MainApp;
import fr.womax.cavemanager.model.Bottle;
import fr.womax.cavemanager.model.Spot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Antonin Ruan
 */
public class BottleFilter {

    private static boolean searching = false;
    private static String previousSearch = "";
    private static ObservableList<Spot> previousResult = FXCollections.observableArrayList();
    private static String criteria;

    public static String getCriteria() {
        return criteria;
    }

    public static void setCriteria(String criteria) {
        BottleFilter.criteria = criteria;
        if(!previousSearch.isEmpty()) {
            search(previousSearch);
        }
    }

    public static void research() {
        if(!previousSearch.trim().isEmpty())
            search(previousSearch);
    }

    public static void search(String s) {
        if (!searching)
            searching = true;

        //TODO potentiel amélioration pour restreindre les recherches en fonction du résultat précédent

        searchIn(MainApp.getSpots(), s);

        previousSearch = s;
    }

    private static void searchIn(ObservableList<Spot> spots, String s) {
        for (Spot spot : spots) {

            if(!spot.isEmpty()) {
                spot.setHighlighted(false);
            }

            if(!spot.isEmpty()) {
                boolean highlight = false;

                Bottle bottle = spot.getBottle();

                switch (criteria) {
                    case "Nom":
                        if(bottle.getName().toLowerCase().contains(s.toLowerCase()))
                            highlight = true;
                        break;
                    case "Région":
                        if(bottle.getRegion().toLowerCase().contains(s.toLowerCase()))
                            highlight = true;
                        break;
                    case "Type":
                        if (bottle.getType().toString().toLowerCase().contains(s.toLowerCase()));
                            highlight = true;
                        break;
                    case "Édition":
                        if(bottle.getEdition().toLowerCase().contains(s.toLowerCase()))
                            highlight = true;
                        break;
                    case "Domaine":
                        if(bottle.getDomain().toLowerCase().contains(s.toLowerCase()))
                            highlight = true;
                        break;
                    case "Année":
                        if (String.valueOf(bottle.getYear()).toLowerCase().contains(s.toLowerCase()))
                            highlight = true;
                        break;

                    default:
                        break;
                }

                if(!spot.isEmpty())
                    spot.setHighlighted(highlight);
            }

        }
    }

    public static void endSearching() {
        if(searching)
            searching = false;
        previousSearch = "";
        for (Spot spot : MainApp.getSpots()) {
            if(!spot.isEmpty())
                spot.setHighlighted(false);
        }
    }

}
