package fr.antoninruan.cellarmanager.utils;

import fr.antoninruan.cellarmanager.MainApp;
import fr.antoninruan.cellarmanager.model.Bottle;
import fr.antoninruan.cellarmanager.model.Spot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Locale;

/**
 * @author Antonin Ruan
 */
public class BottleFilter {

    private static boolean searching = false;
    private static String previousSearch = "";
    private static boolean previousSearchInSpots = true;
    private static ObservableList<Spot> previousResult = FXCollections.observableArrayList();
    private static SearchCriteria criteria;

    public static SearchCriteria getCriteria() {
        return criteria;
    }

    public static void setCriteria(SearchCriteria criteria) {
        BottleFilter.criteria = criteria;
        if(!previousSearch.isEmpty() && previousSearchInSpots) {
            searchInSpots(previousSearch);
        } else if(!previousSearch.isEmpty()) {
            searchInBottles(previousSearch);
        }
    }

    public static void researchInSpot() {
        if(!previousSearch.trim().isEmpty())
            searchInSpots(previousSearch);
    }

    public static void searchInSpots(String s) {
        if (!searching)
            searching = true;

        //TODO potentiel amélioration pour restreindre les recherches en fonction du résultat précédent

        searchInSpots(MainApp.getSpots(), s);

        previousSearch = s;
    }

    private static void searchInSpots(ObservableList<Spot> spots, String s) {
        for (Spot spot : spots) {

            if(!spot.isEmpty()) {
                spot.setHighlighted(false);
            }

            if(!spot.isEmpty()) {
                boolean highlight = false;

                Bottle bottle = spot.getBottle();

                switch (criteria) {
                    case NAME:
                        if(bottle.getName().toLowerCase().contains(s.toLowerCase()))
                            highlight = true;
                        break;
                    case REGION:
                        if(bottle.getRegion().toLowerCase().contains(s.toLowerCase()))
                            highlight = true;
                        break;
                    case TYPE:
                        if (bottle.getType().toString().toLowerCase().contains(s.toLowerCase()))
                            highlight = true;
                        break;
                    case EDITION:
                        if(bottle.getEdition().toLowerCase().contains(s.toLowerCase()))
                            highlight = true;
                        break;
                    case DOMAIN:
                        if(bottle.getDomain().toLowerCase().contains(s.toLowerCase()))
                            highlight = true;
                        break;
                    case YEAR:
                        if (String.valueOf(bottle.getYear()).toLowerCase().contains(s.toLowerCase()))
                            highlight = true;
                        break;
                    case APOGEE:
                        if(String.valueOf(bottle.getConsumeYear()).toLowerCase().contains(s.toLowerCase()))
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

    public static ObservableList<Bottle> searchInBottles(String s) {
        ObservableList<Bottle> result = FXCollections.observableArrayList();
        for(Bottle bottle : MainApp.getBottles().values()) {

            switch (criteria) {
                case NAME:
                    if(bottle.getName().toLowerCase().contains(s.toLowerCase()))
                        result.add(bottle);
                    break;
                case TYPE:
                    if(bottle.getType().toString().toLowerCase().contains(s.toLowerCase()))
                        result.add(bottle);
                    break;
                case YEAR:
                    if(Integer.toString(bottle.getYear()).toLowerCase().contains(s.toLowerCase()))
                        result.add(bottle);
                    break;
                case APOGEE:
                    if(Integer.toString(bottle.getConsumeYear()).toLowerCase().contains(s.toLowerCase()))
                        result.add(bottle);
                case DOMAIN:
                    if(bottle.getDomain().toLowerCase().contains(s.toLowerCase()))
                        result.add(bottle);
                    break;
                case REGION:
                    if(bottle.getRegion().toLowerCase().contains(s.toLowerCase()))
                        result.add(bottle);
                    break;
                case EDITION:
                    if(bottle.getEdition().toLowerCase().contains(s.toLowerCase()))
                        result.add(bottle);
                    break;
            }

        }
        return result;
    }

    public static ObservableList<Bottle> researchInBottles() {
        if(!previousSearch.trim().isEmpty())
            return searchInBottles(previousSearch);
        return null;
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

    public enum SearchCriteria {

        NAME("Nom", "Name"),
        REGION("Région", "Region"),
        TYPE("Type", "Type"),
        EDITION("Edition", "Edition"),
        DOMAIN("Domaine", "Domain"),
        YEAR("Année", "Year"),
        APOGEE("Apogée", "Year of Consumption");

        String fr;
        String en;

        SearchCriteria(String fr, String en) {
            this.fr = fr;
            this.en = en;
        }

        public String getNameIn(Locale locale) {
            if (locale == Locale.FRENCH) {
                return fr;
            } else if (locale == Locale.ENGLISH) {
                return en;
            } else {
                return fr;
            }
        }

        public static SearchCriteria fromNameIn(String name, Locale locale) {
            for(SearchCriteria criteria : SearchCriteria.values()) {
                if (criteria.getNameIn(locale).equals(name)) {
                    return criteria;
                }
            }
            return null;
        }

    }

}
