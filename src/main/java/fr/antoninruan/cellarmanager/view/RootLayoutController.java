package fr.antoninruan.cellarmanager.view;

import fr.antoninruan.cellarmanager.MainApp;
import fr.antoninruan.cellarmanager.model.Bottle;
import fr.antoninruan.cellarmanager.model.Compartment;
import fr.antoninruan.cellarmanager.model.Spot;
import fr.antoninruan.cellarmanager.utils.BottleFilter;
import fr.antoninruan.cellarmanager.utils.DialogUtils;
import fr.antoninruan.cellarmanager.utils.PreferencesManager;
import fr.antoninruan.cellarmanager.utils.Updater;
import fr.antoninruan.cellarmanager.utils.change.Change;
import fr.antoninruan.cellarmanager.utils.github.GitHubAPIService;
import fr.antoninruan.cellarmanager.utils.github.GitHubAccountConnectionInfo;
import fr.antoninruan.cellarmanager.utils.github.exception.GitHubAPIConnectionException;
import fr.antoninruan.cellarmanager.utils.github.exception.LabelNotFoundException;
import fr.antoninruan.cellarmanager.utils.github.exception.RepositoryNotFoundException;
import fr.antoninruan.cellarmanager.utils.github.model.Repository;
import fr.antoninruan.cellarmanager.utils.github.model.issues.Issue;
import fr.antoninruan.cellarmanager.utils.github.model.issues.Label;
import fr.antoninruan.cellarmanager.utils.github.model.release.Release;
import fr.antoninruan.cellarmanager.utils.mobile_sync.MobileSyncManager;
import fr.antoninruan.cellarmanager.utils.report.BugInfo;
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
import javafx.stage.StageStyle;
import javafx.util.Pair;
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
    private Menu folder;
    @FXML
    private MenuItem importM;
    @FXML
    private MenuItem export;
    @FXML
    private Menu syncMenu;
    @FXML
    private MenuItem info;
    @FXML
    private MenuItem settings;
    @FXML
    private MenuItem close;
    @FXML
    private Menu edit;
    @FXML
    private MenuItem bottles;
    @FXML
    private MenuItem manageCompartment;
    @FXML
    private MenuItem deleteCompartment;
    @FXML
    private Menu help;
    @FXML
    private MenuItem checkUpdate;
    @FXML
    private MenuItem sendBugReport;
    @FXML
    private MenuItem sendSuggestion;
    @FXML
    private MenuItem about;

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
    private CheckMenuItem toggleMobileSync;

    private Spot displayedSpot;

    private final ClipboardContent clipboardContent = new ClipboardContent();

    private boolean shiftPressed = false;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void updateLang() {
        folder.setText(PreferencesManager.getLangBundle().getString("folder"));
        importM.setText(PreferencesManager.getLangBundle().getString("import_menu_item"));
        export.setText(PreferencesManager.getLangBundle().getString("export_menu_item"));
        syncMenu.setText(PreferencesManager.getLangBundle().getString("sync_menu"));
        toggleMobileSync.setText(MobileSyncManager.isActivate() ? PreferencesManager.getLangBundle().getString("deactivate")
                : PreferencesManager.getLangBundle().getString("activate"));
        info.setText(PreferencesManager.getLangBundle().getString("info"));
        settings.setText(PreferencesManager.getLangBundle().getString("settings"));
        close.setText(PreferencesManager.getLangBundle().getString("close"));
        edit.setText(PreferencesManager.getLangBundle().getString("edit"));
        cancelMenu.setText(PreferencesManager.getLangBundle().getString("undo"));
        bottles.setText(PreferencesManager.getLangBundle().getString("bottles"));
        manageCompartment.setText(PreferencesManager.getLangBundle().getString("manage_compartments"));
        deleteCompartment.setText(PreferencesManager.getLangBundle().getString("delete_compartment"));
        help.setText(PreferencesManager.getLangBundle().getString("help"));
        checkUpdate.setText(PreferencesManager.getLangBundle().getString("check_update"));
        sendBugReport.setText(PreferencesManager.getLangBundle().getString("send_bug_report"));
        sendSuggestion.setText(PreferencesManager.getLangBundle().getString("send_suggestion"));
        about.setText(PreferencesManager.getLangBundle().getString("about"));
        searchField.setPromptText(PreferencesManager.getLangBundle().getString("search_by"));
        BottleFilter.SearchCriteria selected = criteriaChoiceBox.getValue();
        criteriaChoiceBox.getItems().clear();
        criteriaChoiceBox.getItems().addAll(BottleFilter.SearchCriteria.values());
        criteriaChoiceBox.setValue(selected);
    }

    @FXML
    private void initialize() {

        boolean[] ctrlPressed = {false};

        layout.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.CONTROL) {
                ctrlPressed[0] = true;
            } else if(event.getCode() == KeyCode.SHIFT) {
                shiftPressed = true;
            } else if(event.getCode() == KeyCode.DELETE) {
                if(MainApp.getCompartementDisplayController().getSelectedSpot() != null) {

                    Spot selectedSpot = MainApp.getCompartementDisplayController().getSelectedSpot();
                    selectedSpot.setBottle(null);
                    if(selectedSpot.isHighlighted()) selectedSpot.setHighlighted(false);

                }
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
        criteriaChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(BottleFilter.SearchCriteria object) {
                if (object == null)
                    return "";
                return PreferencesManager.getLangBundle().getString(object.getId());
            }

            @Override
            public BottleFilter.SearchCriteria fromString(String string) {
                return BottleFilter.SearchCriteria.fromId(string);
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

    public void showBottleDetails(Spot spot, boolean closeIfOpen) {

        if(displayedSpot != null && displayedSpot.equals(spot) && descriptionPane.isExpanded() && closeIfOpen) {
            descriptionPane.setExpanded(false);
        } else {
            description.setText(PreferencesManager.getLangBundle().getString("name") + ": " + spot.getBottle().getName() + "\n"+
                    PreferencesManager.getLangBundle().getString("domain") + ": " + spot.getBottle().getDomain() + "\n"+
                    PreferencesManager.getLangBundle().getString("edition") + ": " + spot.getBottle().getEdition() + "\n"+
                    PreferencesManager.getLangBundle().getString("year") + ": " + spot.getBottle().getYear() + "\n"+
                    PreferencesManager.getLangBundle().getString("consumption_year") + ": " + spot.getBottle().getConsumeYear() + "\n" +
                    PreferencesManager.getLangBundle().getString("type") + ": " + spot.getBottle().getType() + "\n"+
                    PreferencesManager.getLangBundle().getString("region") + ": " + spot.getBottle().getRegion() + "\n"+
                    PreferencesManager.getLangBundle().getString("comment") + ": " + spot.getBottle().getComment());

            if(!descriptionPane.isExpanded())
                descriptionPane.setExpanded(true);
        }

        displayedSpot = spot;
    }

    public void handleNewCompartement() {
        mainApp.createCompartment(true);
    }

    public void handleOpenBottleList() {
        DialogUtils.chooseBottle(false);
    }

    public void handleManageCompartment() {
       DialogUtils.manageCompartments();
    }

    public void handleCancel() {
        ObservableList <Change> changeHistory = Change.getChangeHistory();
        Change lastChange = changeHistory.get(changeHistory.size() - 1);
        lastChange.undo();
    }

    public void handleCheckUpdate() {
        Pair<Boolean, Release> result = Updater.checkUpdate();
        if(result.getKey()) {
            DialogUtils.updateAvailable(false, result.getValue());
        } else {
            DialogUtils.noUpdateAvailable();
        }
    }

    public void handleDeleteCompartement() {
        if(MainApp.getCompartements().size() != 1) {
            MainApp.removeCompartement(MainApp.getCompartementDisplayController().getCurrentCompartementDisplayed());
        } else {
            DialogUtils.needAtLeastOneCompartement();
        }

    }

    public void handleToggleMobileSync() {
        MobileSyncManager.toggleState();
        if(MobileSyncManager.isActivate()) {
            DialogUtils.mobileSyncInfo(PreferencesManager.getLangBundle().getString("mobile_sync_info_window_header"));
        }
        toggleMobileSync.setText(MobileSyncManager.isActivate() ? PreferencesManager.getLangBundle().getString("deactivate")
                : PreferencesManager.getLangBundle().getString("activate"));
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

                DialogUtils.successfullySendIssue(PreferencesManager.getLangBundle().getString("bug_report_confirmation_title"),
                        PreferencesManager.getLangBundle().getString("bug_report_confirmation_header"), issue.getHtmlUrl());

            } catch (IOException | ParseException | GitHubAPIConnectionException | RepositoryNotFoundException | LabelNotFoundException e) {
                DialogUtils.sendErrorWindow(e);
            }

            if(disconnectAfter.get())
                GitHubAPIService.removeAuthentication();
        });
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

                DialogUtils.successfullySendIssue(PreferencesManager.getLangBundle().getString("suggestion_send_title"),
                        PreferencesManager.getLangBundle().getString("suggestion_send_header"), issue.getHtmlUrl());

            } catch (IOException | ParseException | GitHubAPIConnectionException | RepositoryNotFoundException | LabelNotFoundException e) {
                DialogUtils.sendErrorWindow(e);
            }

            if(disconnectAfter.get()) {
                GitHubAPIService.removeAuthentication();
            }
        });


    }

    public void handlePreferences() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(PreferencesManager.getLangBundle());
            loader.setLocation(RootLayoutController.class.getClassLoader().getResource("fxml/PreferencesLayout.fxml"));

            VBox root = loader.load();

            MainApp.setPreferencesController(loader.getController());

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(MainApp.LOGO);
            stage.setTitle(PreferencesManager.getLangBundle().getString("settings_window_title"));
            stage.setResizable(false);
            stage.initOwner(MainApp.getPrimaryStage());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);

            MainApp.getPreferencesController().setStage(stage);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
