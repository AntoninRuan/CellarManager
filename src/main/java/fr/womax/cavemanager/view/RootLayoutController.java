package fr.womax.cavemanager.view;

import fr.womax.cavemanager.MainApp;
import fr.womax.cavemanager.model.Bottle;
import fr.womax.cavemanager.model.Compartement;
import fr.womax.cavemanager.model.Spot;
import fr.womax.cavemanager.utils.BottleFilter;
import fr.womax.cavemanager.utils.DialogUtils;
import fr.womax.cavemanager.utils.Updater;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

import java.util.Optional;

/**
 * @author Antonin Ruan
 */
public class RootLayoutController {

    private MainApp mainApp;

    @FXML
    private BorderPane layout;

    @FXML
    private MenuItem versionMenuItem;

    @FXML
    private TitledPane descriptionPane;

    @FXML
    private TextArea description;

    @FXML
    private TextField searchField;

    @FXML
    private ChoiceBox<String> criteriaChoiceBox;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        versionMenuItem.setText("Version: " + Updater.VERSION);
        String name = "Nom";
        String region = "Région";
        String type = "Type";
        String edition = "Édition";
        String domain = "Domaine";
        String year = "Année";
        criteriaChoiceBox.getItems().addAll(name, region, type, edition, domain, year);
        criteriaChoiceBox.setValue(name);
        BottleFilter.setCriteria(name);
        criteriaChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            BottleFilter.setCriteria(newValue);
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue.trim().isEmpty()) {
                BottleFilter.endSearching();
            } else
                BottleFilter.search(newValue);

        });
    }

    public void showBottleDetails(Spot spot) {

        description.setText("Nom: " + spot.getBottle().getName() + "\n"+
                "Domaine: " + spot.getBottle().getDomain() + "\n"+
                "Edition: " + spot.getBottle().getEdition() + "\n"+
                "Année: " + spot.getBottle().getYear() + "\n"+
                "Type: " + spot.getBottle().getType() + "\n"+
                "Région: " + spot.getBottle().getRegion() + "\n"+
                "Commentaire: " + spot.getBottle().getComment() + "\n");

        if(!descriptionPane.isExpanded()) {
            descriptionPane.setExpanded(true);
        }
    }

    public void handleNewCompartement() {
        mainApp.createNewCompartements(true);
    }

    public void handleOpenBottleList() {
        DialogUtils.chooseBottle(false);
    }

    public void handleCheckUpdate() {
        boolean newUpdate = Updater.checkUpdate();
        if(newUpdate) {
            DialogUtils.updateAvailable();
        } else {
            DialogUtils.noUpdateAvailable();
        }
    }

    public void handleDeleteCompartement() {
        if(MainApp.getCompartements().size() != 1) {
            Compartement compartement = MainApp.getCompartements().get(MainApp.getCompartementDisplayController().getCurrentCompartementDisplayed());
            for(Spot[] spotColumn : compartement.getSpots()) {
                for(Spot spot : spotColumn) {
                    MainApp.getSpots().remove(spot);
                }
            }
            MainApp.getCompartements().remove(compartement.getId());
        } else {
            DialogUtils.needAtLeastOneCompartement();
        }

    }

    public void handleClose() {
        MainApp.saveFiles();
        MainApp.getPrimaryStage().close();
    }


}
