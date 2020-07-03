package fr.womax.cavemanager.view;

import fr.womax.cavemanager.MainApp;
import fr.womax.cavemanager.model.Bottle;
import fr.womax.cavemanager.model.Compartement;
import fr.womax.cavemanager.model.Spot;
import fr.womax.cavemanager.utils.BottleFilter;
import fr.womax.cavemanager.utils.DialogUtils;
import fr.womax.cavemanager.utils.Updater;
import fr.womax.cavemanager.utils.report.BugInfo;
import fr.womax.cavemanager.utils.report.DropboxUtils;
import fr.womax.cavemanager.utils.report.SuggestionInfo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

import java.util.Date;
import java.util.Optional;

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
    private ChoiceBox<BottleFilter.SearchCriteria> criteriaChoiceBox;

    @FXML
    private CheckMenuItem checkUpdate;

    private Spot displayedSpot;

    private final ClipboardContent clipboardContent = new ClipboardContent();

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {

        boolean[] ctrlPressed = {false};

        layout.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.CONTROL) {
                ctrlPressed[0] = true;
            }
            if(ctrlPressed[0]) {
                if(event.getCode() == KeyCode.V) {

                    if(clipboardContent.hasString()) {
                        if(MainApp.getCompartementDisplayController().getSelectedSpot() != null) {
                            MainApp.getCompartementDisplayController().getSelectedSpot().setBottle(MainApp.getBottles().get(Integer.valueOf(clipboardContent.getString())));
                            BottleFilter.researchInSpot();
                        }
                    }

                } else if (event.getCode() == KeyCode.C) {
                    if(MainApp.getCompartementDisplayController().getSelectedSpot() == null)
                        return;

                    Bottle selectedBottle = MainApp.getCompartementDisplayController().getSelectedSpot().getBottle();
                    if(selectedBottle != null) {
                        clipboardContent.putString(String.valueOf(selectedBottle.getId()));
                    }
                }
            }
        });

        layout.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.CONTROL) {
                ctrlPressed[0] = false;
            }
        });

        criteriaChoiceBox.getItems().setAll(BottleFilter.SearchCriteria.values());
        criteriaChoiceBox.setValue(BottleFilter.SearchCriteria.NAME);
        criteriaChoiceBox.setConverter(new StringConverter <BottleFilter.SearchCriteria>() {
            @Override
            public String toString(BottleFilter.SearchCriteria object) {
                return object.getName();
            }

            @Override
            public BottleFilter.SearchCriteria fromString(String string) {
                return BottleFilter.SearchCriteria.fromName(string);
            }
        });
        BottleFilter.setCriteria(BottleFilter.SearchCriteria.NAME);
        criteriaChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            BottleFilter.setCriteria(newValue);
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue.trim().isEmpty()) {
                BottleFilter.endSearching();
            } else
                BottleFilter.searchInSpots(newValue);

        });
        checkUpdate.setSelected(MainApp.PREFERENCE_JSON.get("check_update").getAsBoolean());
        checkUpdate.selectedProperty().addListener((observable, oldValue, newValue) -> {
            MainApp.PREFERENCE_JSON.addProperty("check_update", newValue);
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

    public void handleReportBug() {
        Optional<BugInfo> result = DialogUtils.sendBugReport(null);
        result.ifPresent(bugInfo -> {
            DropboxUtils.sendBugIssue(bugInfo.getTitle(), bugInfo.getDescription(), new Date(), bugInfo.getStackTrace());
        });
    }

    public void handleSuggestIdea() {
        Optional<SuggestionInfo> result = DialogUtils.sendSuggestion();
        result.ifPresent(suggestionInfo -> {
            DropboxUtils.sendSuggestion(suggestionInfo.getTitle(), suggestionInfo.getDescription(), suggestionInfo.getDate());
        });
    }

}
