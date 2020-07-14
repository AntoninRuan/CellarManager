package fr.womax.cavemanager.utils.javafx;

import fr.womax.cavemanager.MainApp;
import fr.womax.cavemanager.model.Bottle;
import fr.womax.cavemanager.utils.javafx.MaxSizedContextMenu;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Antonin Ruan
 */
public class SuggestionMenu {

    private static HashMap <Integer, String> names = new HashMap <>();
    private static HashMap <Integer, String> regions = new HashMap <>();
    private static HashMap <Integer, String> edition = new HashMap <>();
    private static HashMap <Integer, String> domain = new HashMap <>();

    public static void addSuggestionMenu(final TextField textField, final List<String> global) {

        final MaxSizedContextMenu nameSuggestion = new MaxSizedContextMenu();
        nameSuggestion.setMaxHeight(145);
        nameSuggestion.setPrefWidth(textField.getPrefWidth());
        textField.textProperty().addListener((observable, oldValue, newValue) -> {

            ObservableList <MenuItem> result = FXCollections.observableArrayList();

            if(newValue.trim().isEmpty()) {
                nameSuggestion.getItems().clear();
                nameSuggestion.hide();
                return;
            }

            for(String s : global) {
                if(s.toLowerCase().startsWith(newValue.toLowerCase())) {
                    MenuItem item = new MenuItem(s);
                    item.setOnAction(event -> {
                        textField.setText(s);
                        textField.positionCaret(s.length());
                    });
                    result.add(item);
                }
            }

            if(result.isEmpty())
                return;

            if(!nameSuggestion.isShowing()) {
                nameSuggestion.getItems().clear();
                nameSuggestion.getItems().add(result.get(0));
                nameSuggestion.show(textField, Side.BOTTOM, 0, 0);
                nameSuggestion.getItems().addAll(result.stream().skip(1).collect(Collectors.toList()));
                return;
            }

            nameSuggestion.getItems().setAll(result);

        });

    }

    public static List<String> getAllBottlesName() {
        if(names.isEmpty() && !MainApp.getBottles().isEmpty()) {

            for(Bottle bottle : MainApp.getBottles().values()) {
                names.put(bottle.getId(), bottle.getName());
            }

            MainApp.getBottles().addListener((MapChangeListener <? super Integer, ? super Bottle>) change -> {
                if(change.wasAdded()) {
                    names.put(change.getValueAdded().getId(), change.getValueAdded().getName());
                }
                if(change.wasRemoved()) {
                    names.remove(change.getValueRemoved().getId());
                }
            });

        }

        return names.values().stream().distinct().collect(Collectors.toList());
    }

    public static List<String> getAllBottleRegions() {
        if(regions.isEmpty() && !MainApp.getBottles().isEmpty()) {

            for(Bottle bottle : MainApp.getBottles().values()) {
                regions.put(bottle.getId(), bottle.getRegion());
            }

            MainApp.getBottles().addListener((MapChangeListener <? super Integer, ? super Bottle>) change -> {
                if(change.wasAdded()) {
                    regions.put(change.getValueAdded().getId(), change.getValueAdded().getRegion());
                }
                if(change.wasRemoved()) {
                    regions.remove(change.getValueRemoved().getId());
                }
            });

        }

        return regions.values().stream().distinct().collect(Collectors.toList());
    }

    public static List<String> getAllBottlesEdition() {
        if(edition.isEmpty() && !MainApp.getBottles().isEmpty()) {

            for(Bottle bottle : MainApp.getBottles().values()) {
                edition.put(bottle.getId(), bottle.getEdition());
            }

            MainApp.getBottles().addListener((MapChangeListener <? super Integer, ? super Bottle>) change -> {
                if(change.wasAdded()) {
                    edition.put(change.getValueAdded().getId(), change.getValueAdded().getEdition());
                }
                if(change.wasRemoved()) {
                    edition.remove(change.getValueRemoved().getId());
                }
            });

        }

        return edition.values().stream().distinct().collect(Collectors.toList());
    }

    public static List<String> getAllBottlesDomain() {
        if(domain.isEmpty() && !MainApp.getBottles().isEmpty()) {

            for(Bottle bottle : MainApp.getBottles().values()) {
                domain.put(bottle.getId(), bottle.getDomain());
            }

            MainApp.getBottles().addListener((MapChangeListener <? super Integer, ? super Bottle>) change -> {
                if(change.wasAdded()) {
                    domain.put(change.getValueAdded().getId(), change.getValueAdded().getDomain());
                }
                if(change.wasRemoved()) {
                    domain.remove(change.getValueRemoved().getId());
                }
            });

        }

        return domain.values().stream().distinct().collect(Collectors.toList());
    }

}
