package fr.womax.cavemanager.view;

import fr.womax.cavemanager.MainApp;
import fr.womax.cavemanager.model.Compartement;
import fr.womax.cavemanager.model.Spot;
import fr.womax.cavemanager.utils.BottleFilter;
import fr.womax.cavemanager.utils.DialogUtils;
import fr.womax.cavemanager.utils.Updater;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

/**
 * @author Antonin Ruan
 */
public class RootLayoutController {

    private MainApp mainApp;

    @FXML
    private BorderPane layout;

    @FXML
    private TitledPane descriptionPane;

    @FXML
    private TextArea description;

    @FXML
    private TextField searchField;

    @FXML
    private ChoiceBox<String> criteriaChoiceBox;

    private Spot displayedSpot;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
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

        if(displayedSpot != null && displayedSpot.equals(spot) && descriptionPane.isExpanded()) {
            descriptionPane.setExpanded(false);
        } else {
            description.setText("Nom: " + spot.getBottle().getName() + "\n"+
                    "Domaine: " + spot.getBottle().getDomain() + "\n"+
                    "Edition: " + spot.getBottle().getEdition() + "\n"+
                    "Année: " + spot.getBottle().getYear() + "\n"+
                    "Année de consommation: " + spot.getBottle().getConsumeYear() + "\n" +
                    "Type: " + spot.getBottle().getType() + "\n"+
                    "Région: " + spot.getBottle().getRegion() + "\n"+
                    "Commentaire: " + spot.getBottle().getComment() + "\n");

            if(!descriptionPane.isExpanded()) {
                descriptionPane.setExpanded(true);
            }
        }

        displayedSpot = spot;
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
            DialogUtils.updateAvailable(false);
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
            MainApp.getCompartements().remove(MainApp.getCompartementDisplayController().getCurrentCompartementDisplayed());
        } else {
            DialogUtils.needAtLeastOneCompartement();
        }

    }

    public void handleClose() {
        MainApp.saveFiles();
        MainApp.getPrimaryStage().close();
    }

    public void handleAbout() {
        DialogUtils.about();
    }

}
