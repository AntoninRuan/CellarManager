package fr.antoninruan.cellarmanager.view;

import fr.antoninruan.cellarmanager.model.Compartement;
import fr.antoninruan.cellarmanager.model.Spot;
import fr.antoninruan.cellarmanager.utils.BottleFilter;
import fr.antoninruan.cellarmanager.utils.DialogUtils;
import fr.antoninruan.cellarmanager.utils.PreferencesManager;
import fr.antoninruan.cellarmanager.utils.Updater;
import fr.antoninruan.cellarmanager.utils.github.GitHubAPIService;
import fr.antoninruan.cellarmanager.utils.github.model.Repository;
import fr.antoninruan.cellarmanager.utils.mobile_sync.MobileSyncManager;
import fr.antoninruan.cellarmanager.utils.report.BugInfo;
import fr.antoninruan.cellarmanager.MainApp;
import fr.antoninruan.cellarmanager.model.Bottle;
import fr.antoninruan.cellarmanager.utils.change.Change;
import fr.antoninruan.cellarmanager.utils.github.GitHubAccountConnectionInfo;
import fr.antoninruan.cellarmanager.utils.github.exception.GitHubAPIConnectionException;
import fr.antoninruan.cellarmanager.utils.github.exception.LabelNotFoundException;
import fr.antoninruan.cellarmanager.utils.github.exception.RepositoryNotFoundException;
import fr.antoninruan.cellarmanager.utils.github.model.issues.Issue;
import fr.antoninruan.cellarmanager.utils.github.model.issues.Label;
import fr.antoninruan.cellarmanager.utils.report.SuggestionInfo;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private MenuItem cancelMenu;

    @FXML
    private MenuItem toggleMobileSync;

    private Spot displayedSpot;

    private final ClipboardContent clipboardContent = new ClipboardContent();

    private boolean shiftPressed = false;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {

        boolean[] ctrlPressed = {false};

        layout.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.CONTROL) {
                ctrlPressed[0] = true;
            } else if(event.getCode() == KeyCode.SHIFT) {
                shiftPressed = true;
            }
            if(ctrlPressed[0]) {
                if(event.getCode() == KeyCode.V) {

                    if(clipboardContent.hasString()) {
                        if(MainApp.getCompartementDisplayController().getSelectedSpot() != null) {
                            Spot selectedSpot = MainApp.getCompartementDisplayController().getSelectedSpot();
                            if(selectedSpot.isEmpty())
                                new Change(Change.ChangeType.SPOT_FILLED, selectedSpot, selectedSpot, null);
                            else
                                new Change(Change.ChangeType.BOTTLE_CHANGED, selectedSpot, selectedSpot, selectedSpot.getBottle());
                            selectedSpot.setBottle(MainApp.getBottles().get(Integer.valueOf(clipboardContent.getString())));
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
            } else if (event.getCode() == KeyCode.SHIFT) {
                shiftPressed = false;
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

        cancelMenu.setDisable(Change.getChangeHistory().size() == 0);

        Change.getChangeHistory().addListener((ListChangeListener <? super Change>) c -> {
            cancelMenu.setDisable(Change.getChangeHistory().size() == 0);
        });
    }

    public boolean isShiftPressed() {
        return shiftPressed;
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

    public void handleCancel() {
        ObservableList <Change> changeHistory = Change.getChangeHistory();
        Change lastChange = changeHistory.get(changeHistory.size() - 1);
        lastChange.undo();
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
            Compartement compartement = MainApp.getCompartement(MainApp.getCompartementDisplayController().getCurrentCompartementDisplayed());
            for(Spot[] spotColumn : compartement.getSpots()) {
                for(Spot spot : spotColumn) {
                    MainApp.getSpots().remove(spot);
                }
            }
            MainApp.getCompartements().remove(compartement.getId());
            for(Compartement c : MainApp.getCompartements().values()) {
                if(c.getIndex() > compartement.getIndex()) {
                    c.setIndex(c.getIndex() - 1);
                }
            }
            MainApp.getCompartementDisplayController().setCurrentCompartementDisplayed(compartement.getIndex());
        } else {
            DialogUtils.needAtLeastOneCompartement();
        }

    }

    public void handleToggleMobileSync() {
        MobileSyncManager.toggleState();
        if(MobileSyncManager.isActivate()) {
            DialogUtils.mobileSyncInfo("Synchronisation Mobile activée, voici les informations pour vous connecter");
        }
        toggleMobileSync.setText(MobileSyncManager.isActivate() ? "Désactiver" : "Activer");
    }

    public void handleMobileSyncInfo() {
        DialogUtils.mobileSyncInfo(null);
    }

    public void handleClose() {
        MainApp.saveFiles();
        MainApp.getPrimaryStage().close();
    }

    public void handleAbout() {
        DialogUtils.about();
    }

    public void handleReportBug() {
        AtomicBoolean disconnectAfter = new AtomicBoolean(false);
        if(!GitHubAPIService.isAuthenticated()) {
            if(PreferencesManager.isNeverConnectToGitHub()) {
                GitHubAPIService.authenticateAsGuestUser();
            } else {
                Optional<GitHubAccountConnectionInfo> result = DialogUtils.loginToGitHub();
                result.ifPresent(connectionInfo -> {
                    disconnectAfter.set(!connectionInfo.isStayConnected());
                    GitHubAPIService.setAuthentication(connectionInfo.getUsername(), connectionInfo.getPassword());
                    if(connectionInfo.getUsername().equals("") && connectionInfo.getPassword().equals("")) {
                        GitHubAPIService.authenticateAsGuestUser();
                        disconnectAfter.set(true);
                    }
                });
                if(!result.isPresent())
                    return;
            }
        }

        Optional<BugInfo> result = DialogUtils.sendBugReport(null);
        result.ifPresent(bugInfo -> {
            try {
                Repository repository = GitHubAPIService.getRepository("antoninruan", "cellarmanager");
                Label bug = repository.getLabel("bug");
                Issue issue = repository.createIssue(bugInfo.getTitle(), "Description:" + bugInfo.getDescription() +
                        (bugInfo.getStackTrace() == null ? "" : "\nStacktrace:" + bugInfo.getStackTrace()), new Label[]{bug});

                DialogUtils.infoMessage("Report de bug effectué", "Le bug a bien été reporté", "Vous pouvez suivre l'évolution du rapport ici:\n" +
                        issue.getHtmlUrl());


            } catch (IOException | ParseException | GitHubAPIConnectionException | RepositoryNotFoundException | LabelNotFoundException e) {
                DialogUtils.sendErrorWindow(e);
            }

        });

        if(disconnectAfter.get())
            GitHubAPIService.removeAuthentication();

    }

    public void handleSuggestIdea() {
        AtomicBoolean disconnectAfter = new AtomicBoolean(false);

        if(!GitHubAPIService.isAuthenticated()) {
            if(PreferencesManager.isNeverConnectToGitHub()) {
                GitHubAPIService.authenticateAsGuestUser();
            } else {
                Optional<GitHubAccountConnectionInfo> result = DialogUtils.loginToGitHub();
                result.ifPresent(connectionInfo -> {
                    disconnectAfter.set(!connectionInfo.isStayConnected());

                    GitHubAPIService.setAuthentication(connectionInfo.getUsername(), connectionInfo.getPassword());
                    if(connectionInfo.getUsername().equals("") && connectionInfo.getPassword().equals("")) {
                        GitHubAPIService.authenticateAsGuestUser();
                        disconnectAfter.set(true);
                    }
                });
                if(!result.isPresent())
                    return;
            }
        }

        Optional<SuggestionInfo> result = DialogUtils.sendSuggestion();
        result.ifPresent(suggestionInfo -> {
            try {
                Repository repository = GitHubAPIService.getRepository("antoninruan", "cellarmanager");
                Label suggest = repository.getLabel("enhancement");

                Issue issue = repository.createIssue(suggestionInfo.getTitle(), suggestionInfo.getDescription(), new Label[]{suggest});

                DialogUtils.infoMessage("Suggestion envoyé", "La suggestion a bien été envoyé", "Vous pouvez suivre l'évolution de cette suggestion ici\n" +
                        issue.getHtmlUrl());


            } catch (IOException | ParseException | GitHubAPIConnectionException | RepositoryNotFoundException | LabelNotFoundException e) {
                DialogUtils.sendErrorWindow(e);
            }
        });

        if(disconnectAfter.get()) {
            GitHubAPIService.removeAuthentication();
        }

    }

    public void handlePreferences() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RootLayoutController.class.getClassLoader().getResource("fxml/PreferencesLayout.fxml"));

            VBox root = loader.load();

            PreferencesController controller = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(MainApp.LOGO);
            stage.setTitle("Ma Cave - Paramètres");
            stage.setResizable(false);
            stage.initOwner(MainApp.getPrimaryStage());
            stage.initModality(Modality.APPLICATION_MODAL);

            controller.setStage(stage);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
