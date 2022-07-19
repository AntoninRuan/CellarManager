package fr.antoninruan.cellarmanager.utils;

import fr.antoninruan.cellarmanager.MainApp;
import fr.antoninruan.cellarmanager.model.Bottle;
import fr.antoninruan.cellarmanager.model.BottleInfo;
import fr.antoninruan.cellarmanager.model.CompartmentInfo;
import fr.antoninruan.cellarmanager.model.WineType;
import fr.antoninruan.cellarmanager.utils.github.GitHubAPIService;
import fr.antoninruan.cellarmanager.utils.github.GitHubAccountConnectionInfo;
import fr.antoninruan.cellarmanager.utils.github.exception.GitHubAPIConnectionException;
import fr.antoninruan.cellarmanager.utils.github.exception.LabelNotFoundException;
import fr.antoninruan.cellarmanager.utils.github.exception.RepositoryNotFoundException;
import fr.antoninruan.cellarmanager.utils.github.model.Repository;
import fr.antoninruan.cellarmanager.utils.github.model.issues.Issue;
import fr.antoninruan.cellarmanager.utils.github.model.release.Release;
import fr.antoninruan.cellarmanager.utils.javafx.CustomDialogPane;
import fr.antoninruan.cellarmanager.utils.javafx.CustomSpinnerValueFactory;
import fr.antoninruan.cellarmanager.utils.javafx.SuggestionMenu;
import fr.antoninruan.cellarmanager.utils.mobile_sync.MobileSyncManager;
import fr.antoninruan.cellarmanager.utils.report.BugInfo;
import fr.antoninruan.cellarmanager.utils.report.SuggestionInfo;
import fr.antoninruan.cellarmanager.view.BottleChooserController;
import fr.antoninruan.cellarmanager.view.CompartmentEditController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Antonin Ruan
 */
public class DialogUtils {

    public static void sendErrorWindow(Exception e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setDialogPane(new CustomDialogPane());
            alert.getDialogPane().getStyleClass().add("alert");
            alert.setTitle(PreferencesManager.getLangBundle().getString("error"));
            alert.setHeaderText(e.getLocalizedMessage());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label(PreferencesManager.getLangBundle().getString("error_message"));

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label,0, 0);
            expContent.add(textArea,0 ,1);

            alert.getDialogPane().setExpandableContent(expContent);

            ButtonType reportBug = new ButtonType(PreferencesManager.getLangBundle().getString("send_error_report"));
            alert.getButtonTypes().add(reportBug);

            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(buttonType -> {
                if(buttonType == reportBug) {
                    AtomicBoolean disconnectAfter = new AtomicBoolean(false);
                    if(!GitHubAPIService.isAuthenticated()) {
                        if(PreferencesManager.isNeverConnectToGitHub()) {
                            GitHubAPIService.authenticateAsGuestUser();
                        } else {
                            Optional<GitHubAccountConnectionInfo> result1 = DialogUtils.loginToGitHub();
                            result1.ifPresent(connectionInfo -> {
                                disconnectAfter.set(!connectionInfo.isStayConnected());
                                GitHubAPIService.setAuthentication(connectionInfo.getUsername(), connectionInfo.getPassword());
                                if(connectionInfo.getUsername().equals("") && connectionInfo.getPassword().equals("")) {
                                    GitHubAPIService.authenticateAsGuestUser();
                                    disconnectAfter.set(true);
                                }
                            });
                            if(!result1.isPresent())
                                return;
                        }
                    }

                    Optional<BugInfo> result1 = DialogUtils.sendBugReport(textArea.getText());
                    result1.ifPresent(bugInfo -> {
                        try {
                            Repository repository = GitHubAPIService.getRepository("antoninruan", "cellarmanager");
                            fr.antoninruan.cellarmanager.utils.github.model.issues.Label bug = repository.getLabel("bug");
                            Issue issue = repository.createIssue(bugInfo.getTitle(), "Description:" + bugInfo.getDescription() +
                                    (bugInfo.getStackTrace() == null ? "" : "\nStacktrace:" + bugInfo.getStackTrace()), new fr.antoninruan.cellarmanager.utils.github.model.issues.Label[]{bug});

                            DialogUtils.successfullySendIssue(PreferencesManager.getLangBundle().getString("bug_report_confirmation_title"),
                                    PreferencesManager.getLangBundle().getString("bug_report_confirmation_header"), issue.getHtmlUrl());

                        } catch (IOException | ParseException | GitHubAPIConnectionException | RepositoryNotFoundException | LabelNotFoundException e1) {
                            DialogUtils.sendErrorWindow(e);
                        }

                        if(disconnectAfter.get())
                            GitHubAPIService.removeAuthentication();
                    });

                }
            });
        });
    }

    public static File noSaveFile() throws URISyntaxException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(PreferencesManager.getLangBundle().getString("no_save_file_window_title"));
        alert.setHeaderText(null);
        alert.setContentText(PreferencesManager.getLangBundle().getString("no_save_file_window_content"));

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        alert.showAndWait();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(PreferencesManager.getLangBundle().getString("no_save_file_file_chooser_title"));
        File currentJar = new File(MainApp.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        fileChooser.setInitialDirectory(currentJar.getParentFile());
        fileChooser.setInitialFileName(PreferencesManager.getLangBundle().getString("initial_save_file_name"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(PreferencesManager.getLangBundle().getString("ma_cave_file_type"), "*.mcv")
        );
        return fileChooser.showSaveDialog(MainApp.getPrimaryStage());
    }

    public static Optional<BugInfo> sendBugReport(String stackTrace) {
        Dialog<BugInfo> dialog = new Dialog <>();
        dialog.setTitle(PreferencesManager.getLangBundle().getString("bug_report_window_title"));
        dialog.setHeaderText(PreferencesManager.getLangBundle().getString("bug_report_confirmation_header"));

        final ButtonType validationButtonType = new ButtonType(PreferencesManager.getLangBundle().getString("send"), ButtonBar.ButtonData.OK_DONE);
        final ButtonType cancelButtonType = new ButtonType(PreferencesManager.getLangBundle().getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(validationButtonType, cancelButtonType);

        GridPane gridPane = new GridPane();

        TextArea description = new TextArea();
        description.setPromptText(PreferencesManager.getLangBundle().getString("description"));
        description.setWrapText(true);

        gridPane.add(description, 0, 0);

        dialog.getDialogPane().setContent(gridPane);

        dialog.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        dialog.getDialogPane().getStyleClass().add("myDialog");
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        dialog.setResultConverter(param -> {
            if(param == validationButtonType) {
                return new BugInfo(null, description.getText(), stackTrace, new Date());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static Optional<SuggestionInfo> sendSuggestion() {
        Dialog<SuggestionInfo> dialog = new Dialog<>();

        dialog.setTitle(PreferencesManager.getLangBundle().getString("suggest_idea_window_title"));
        dialog.setHeaderText(PreferencesManager.getLangBundle().getString("suggest_idea_window_header"));

        final ButtonType validationButtonType = new ButtonType(PreferencesManager.getLangBundle().getString("send"), ButtonBar.ButtonData.OK_DONE);
        final ButtonType cancelButtonType = new ButtonType(PreferencesManager.getLangBundle().getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(validationButtonType, cancelButtonType);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField title = new TextField();
        title.setPromptText(PreferencesManager.getLangBundle().getString("title"));

        TextArea description = new TextArea();
        description.setPromptText(PreferencesManager.getLangBundle().getString("description"));

        gridPane.add(title, 0, 0);
        gridPane.add(description, 0, 1);

        dialog.getDialogPane().setContent(gridPane);

        dialog.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        dialog.getDialogPane().getStyleClass().add("myDialog");
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        dialog.setResultConverter(param -> {
            if(param == validationButtonType) {
                return new SuggestionInfo(title.getText(), description.getText(), new Date());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static Optional<CompartmentInfo> createNewCompartement(boolean cancelable) {
        Dialog <CompartmentInfo> dialog = new Dialog <>();
        dialog.setTitle(PreferencesManager.getLangBundle().getString("new_compartment_title"));
        dialog.setHeaderText(PreferencesManager.getLangBundle().getString("new_compartment_header"));

        final ButtonType validationButtonType = new ButtonType(PreferencesManager.getLangBundle().getString("ok"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(PreferencesManager.getLangBundle().getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(validationButtonType, cancelButtonType);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText(PreferencesManager.getLangBundle().getString("new_compartment_name"));

        final Spinner <Integer> row = new Spinner <>(new CustomSpinnerValueFactory(1, 40, 1));
        row.setEditable(true);
        row.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                row.increment(0);
            }
        });
        ((CustomSpinnerValueFactory) row.getValueFactory()).setSpinner(row);
        final Spinner<Integer> column = new Spinner <>(new CustomSpinnerValueFactory(1, 10, 1));
        ((CustomSpinnerValueFactory) column.getValueFactory()).setSpinner(column);
        column.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                column.increment(0);
            }
        });
        column.setEditable(true);

        ToggleGroup group = new ToggleGroup();
        final RadioButton before = new RadioButton(PreferencesManager.getLangBundle().getString("new_compartment_replace_current"));
        before.setToggleGroup(group);
        RadioButton after = new RadioButton(PreferencesManager.getLangBundle().getString("new_compartment_put_after"));
        after.setSelected(true);
        after.setToggleGroup(group);

        gridPane.add(new Label(PreferencesManager.getLangBundle().getString("new_compartment_name")), 0, 0);
        gridPane.add(name, 1, 0);
        gridPane.add(new Label(PreferencesManager.getLangBundle().getString("row")), 0, 1);
        gridPane.add(row, 1, 1);
        gridPane.add(new Label(PreferencesManager.getLangBundle().getString("column")), 0, 2);
        gridPane.add(column, 1, 2);
        gridPane.add(before, 0, 3);
        gridPane.add(after, 1, 3);

        Node cancel = dialog.getDialogPane().lookupButton(cancelButtonType);
        cancel.setDisable(!cancelable);

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        dialog.getDialogPane().getStyleClass().add("myDialog");
        ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == validationButtonType) {
                return new CompartmentInfo(name.getText() == null ? PreferencesManager.getLangBundle().getString("compartment") : name.getText(),
                        row.getValue(), column.getValue(), before.isSelected());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static void manageCompartments() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(PreferencesManager.getLangBundle());
            loader.setLocation(DialogUtils.class.getClassLoader().getResource("fxml/CompartmentEditLayout.fxml"));

            VBox vBox = loader.load();

            CompartmentEditController controller = loader.getController();

            Scene scene = new Scene(vBox);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(MainApp.getPrimaryStage());
            stage.getIcons().add(MainApp.LOGO);
            stage.setTitle(PreferencesManager.getLangBundle().getString("manage_compartments_window_title"));

            controller.setStage(stage);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Optional<Bottle> chooseBottle(boolean cancelable) {
        Optional<Bottle> result = Optional.ofNullable(null);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(PreferencesManager.getLangBundle());
            loader.setLocation(DialogUtils.class.getClassLoader().getResource("fxml/BottleChooserLayout.fxml"));

            AnchorPane pane = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Choisir une bouteille");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(MainApp.getPrimaryStage());
            Scene scene = new Scene(pane);
            dialogStage.setScene(scene);
            dialogStage.getIcons().add(MainApp.LOGO);
            dialogStage.setResizable(false);

            BottleChooserController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCancelable(cancelable);

            dialogStage.showAndWait();

            if(controller.isOkClicked()) {
                result = Optional.ofNullable(controller.getSelected());
            }

        } catch (IOException e) {
            DialogUtils.sendErrorWindow(e);
        }
        return result;
    }

    public static Optional<BottleInfo> addNewBottle(Bottle bottle) {
        Dialog <BottleInfo> dialog = new Dialog <>();
        dialog.setTitle(PreferencesManager.getLangBundle().getString("new_bottle_window_title"));
        dialog.setHeaderText(PreferencesManager.getLangBundle().getString("new_bottle_window_header"));

        final ButtonType validationButtonType = new ButtonType(PreferencesManager.getLangBundle().getString("ok"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(PreferencesManager.getLangBundle().getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(validationButtonType, cancelButtonType);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        int year = 1980;
        if (bottle != null) {
            year = bottle.getYear();
        }

        int consumeYear = year;
        if(bottle != null)
            consumeYear = bottle.getConsumeYear();

        TextField name = new TextField();
        name.setPromptText(PreferencesManager.getLangBundle().getString("name"));

        TextField region = new TextField();
        region.setPromptText(PreferencesManager.getLangBundle().getString("region"));

        TextField edition = new TextField();
        edition.setPromptText(PreferencesManager.getLangBundle().getString("edition"));

        TextField domain = new TextField();
        domain.setPromptText(PreferencesManager.getLangBundle().getString("domain"));

        TextField comment = new TextField();
        comment.setPromptText(PreferencesManager.getLangBundle().getString("comment"));
        Spinner<Integer> yearSpinner = new Spinner <>(1950, 3000, year, 1);
        yearSpinner.setEditable(true);
        yearSpinner.getStyleClass().add("spinner");
        Spinner<Integer> consumeYearSpinner = new Spinner <>(1950, 3000, consumeYear, 1);
        consumeYearSpinner.setEditable(true);
        consumeYearSpinner.getStyleClass().add("spinner");
        yearSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue)
                yearSpinner.increment(0);
            ((SpinnerValueFactory.IntegerSpinnerValueFactory) consumeYearSpinner.getValueFactory()).setMin(yearSpinner.getValue());
        });
        consumeYearSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue)
                consumeYearSpinner.increment(0);
        });
        ChoiceBox<WineType> wineType = new ChoiceBox <>();
        wineType.setItems(FXCollections.observableArrayList(WineType.values()));
        wineType.setValue(WineType.ROUGE);

        if (bottle != null) {

            name.setText(bottle.getName());
            region.setText(bottle.getRegion());
            edition.setText(bottle.getEdition());
            domain.setText(bottle.getDomain());
            comment.setText(bottle.getComment());
            wineType.setValue(bottle.getType());

        }

        SuggestionMenu.addSuggestionMenu(name, SuggestionMenu.getAllBottlesName());
        SuggestionMenu.addSuggestionMenu(region, SuggestionMenu.getAllBottleRegions());
        SuggestionMenu.addSuggestionMenu(edition, SuggestionMenu.getAllBottlesEdition());
        SuggestionMenu.addSuggestionMenu(domain, SuggestionMenu.getAllBottlesDomain());

        gridPane.add(new Label(PreferencesManager.getLangBundle().getString("name") + ":"), 0, 0);
        gridPane.add(new Label(PreferencesManager.getLangBundle().getString("region") + ":"), 0, 1);
        gridPane.add(new Label(PreferencesManager.getLangBundle().getString("edition") + ":"), 0, 2);
        gridPane.add(new Label(PreferencesManager.getLangBundle().getString("domain") + ":"), 0, 3);
        gridPane.add(new Label(PreferencesManager.getLangBundle().getString("comment") + ":"), 0,4);
        gridPane.add(new Label(PreferencesManager.getLangBundle().getString("year") + ":"), 0, 5);
        gridPane.add(new Label(PreferencesManager.getLangBundle().getString("consumption_year") + ":"), 0, 6);
        gridPane.add(new Label(PreferencesManager.getLangBundle().getString("type") + ":"), 0, 7);

        gridPane.add(name, 1, 0);
        gridPane.add(region, 1, 1);
        gridPane.add(edition, 1, 2);
        gridPane.add(domain, 1, 3);
        gridPane.add(comment, 1, 4);
        gridPane.add(yearSpinner, 1, 5);
        gridPane.add(consumeYearSpinner, 1, 6);
        gridPane.add(wineType, 1, 7);

        dialog.getDialogPane().setContent(gridPane);

        dialog.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        dialog.getDialogPane().getStyleClass().add("myDialog");
        ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == validationButtonType) {
                BottleInfo bottleInfo = new BottleInfo(name.getText(), region.getText(), domain.getText(),
                        edition.getText(), comment.getText(), yearSpinner.getValue(), consumeYearSpinner.getValue(), wineType.getValue());
                return bottleInfo;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static void updateAvailable(boolean neverAskButton, Release release) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setDialogPane(new CustomDialogPane());
        alert.getDialogPane().getStyleClass().add("alert");
        alert.setTitle(PreferencesManager.getLangBundle().getString("update_available_window_title"));
        alert.setHeaderText(PreferencesManager.getLangBundle().getString("update_available_window_header"));
        alert.setContentText(PreferencesManager.getLangBundle().getString("update_available_window_content"));
        alert.getDialogPane().setPrefWidth(700.0);

        ButtonType ok = new ButtonType(PreferencesManager.getLangBundle().getString("yes"));
        ButtonType no = new ButtonType(PreferencesManager.getLangBundle().getString("no"));
        ButtonType neverAsk = new ButtonType(PreferencesManager.getLangBundle().getString("neverAsk"));

        alert.getButtonTypes().setAll(ok, no);
        if(neverAskButton)
            alert.getButtonTypes().add(neverAsk);


        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setFillHeight(true);

        Label label = new Label(PreferencesManager.getLangBundle().getString("update_available_complete_changelog"));

        Hyperlink hyperlink = createHyperLink(PreferencesManager.getLangBundle().getString("here"),
                "https://github.com/AntoninRuan/CellarManager/tree/master#changelog", alert);

        hBox.getChildren().setAll(label, hyperlink);

        GridPane expContent = new GridPane();
        expContent.setVgap(5);
        expContent.setMaxWidth(Double.MAX_VALUE);

        try {
            WebView changelog = new WebView();
            changelog.setZoom(.75);
            changelog.setPrefWidth(550.0);
            changelog.setPrefHeight(250.0);

            changelog.getEngine().loadContent(MarkdownParser.parseMarkdown(release.getBody()));
            changelog.getEngine().setUserStyleSheetLocation(DialogUtils.class.getClassLoader().getResource("style/markdown.css").toString());

            GridPane.setVgrow(changelog, Priority.ALWAYS);
            GridPane.setHgrow(changelog, Priority.ALWAYS);
            expContent.add(changelog,0 ,0);
        } catch (UnsatisfiedLinkError error) {
            expContent.add(new Label(PreferencesManager.getLangBundle().getString("update_available_window_webview_non_supported")), 0, 0);
        }


        expContent.add(hBox, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        alert.getDialogPane().getStyleClass().add("myDialog");
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        Optional<ButtonType> result = alert.showAndWait();

        result.ifPresent(buttonType -> {

            if(buttonType == ok) {
                Updater.update(release);
            } else if (buttonType == neverAsk) {
                PreferencesManager.setCheckUpdateAtStart(false);
            }

        });

    }

    public static void noUpdateAvailable() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(PreferencesManager.getLangBundle().getString("no_update_available_window_title"));
        alert.setHeaderText(null);
        alert.setContentText(PreferencesManager.getLangBundle().getString("no_update_available_window_content"));

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);
        alert.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        alert.getDialogPane().getStyleClass().add("myDialog");

        alert.showAndWait();
    }

    public static void needAtLeastOneCompartement() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(PreferencesManager.getLangBundle().getString("need_at_least_one_compartment_window_title"));
        alert.setHeaderText(PreferencesManager.getLangBundle().getString("need_at_least_one_compartment_window_header"));
        alert.setContentText(null);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(MainApp.LOGO);
        alert.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        alert.getDialogPane().getStyleClass().add("myDialog");

        alert.showAndWait();
    }

    public static void bottlePresentInCellar() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(PreferencesManager.getLangBundle().getString("bottle_present_in_cellar_window_title"));
        alert.setHeaderText(PreferencesManager.getLangBundle().getString("bottle_present_in_cellar_window_header"));
        alert.setContentText(PreferencesManager.getLangBundle().getString("bottle_present_in_cellar_window_content"));

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);
        alert.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        alert.getDialogPane().getStyleClass().add("myDialog");

        alert.showAndWait();
    }

    public static void about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(PreferencesManager.getLangBundle().getString("about_window_title"));
        alert.setHeaderText(null);

        VBox vBox = new VBox();
        vBox.setSpacing(3);
        vBox.setPrefWidth(424.0);

        vBox.getChildren().addAll(new Label(PreferencesManager.getLangBundle().getString("about_window_dev")),
                new Label(PreferencesManager.getLangBundle().getString("about_window_design")),
                new Label(PreferencesManager.getLangBundle().getString("about_window_version") + Updater.VERSION));

        Hyperlink changelog = createHyperLink(PreferencesManager.getLangBundle().getString("about_window_changelog"),
                "https://github.com/AntoninRuan/CellarManager/tree/master#changelog" + (PreferencesManager.getLang().equals(Locale.FRENCH) ? "" : "-1"), alert);


        vBox.getChildren().add(changelog);

        vBox.getChildren().addAll(new Label(""),
                new Label(PreferencesManager.getLangBundle().getString("about_window_license")),
                new Label(PreferencesManager.getLangBundle().getString("about_window_copyright")));

        alert.getDialogPane().setContent(vBox);
        alert.getDialogPane().getButtonTypes().clear();

//        alert.setWidth(424.0);
        alert.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        alert.getDialogPane().getStyleClass().add("myDialog");
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);
        alert.getDialogPane().getScene().getWindow().setOnCloseRequest(t -> ((Stage) alert.getDialogPane().getScene().getWindow()).close());

        alert.showAndWait();
    }

    public static void mobileSyncInfo(String header) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(PreferencesManager.getLangBundle().getString("mobile_sync_info_window_title"));
        alert.setHeaderText(header);
        alert.setContentText(String.format(PreferencesManager.getLangBundle().getString("mobile_sync_info_window_content"),
                MobileSyncManager.LINK_CODE, MobileSyncManager.VERSION));

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);
        alert.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        alert.getDialogPane().getStyleClass().add("myDialog");

        alert.showAndWait();

    }

    public static void infoMessage(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);
        alert.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        alert.getDialogPane().getStyleClass().add("myDialog");

        alert.showAndWait();
    }

    public static void successfullySendIssue(String title, String header, String issueLink) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);

        VBox vBox = new VBox();
        vBox.setFillWidth(true);

        HBox hBox = new HBox();

        Label label = new Label(PreferencesManager.getLangBundle().getString("successfully_send_issue_window_content"));
        Hyperlink issue = createHyperLink(PreferencesManager.getLangBundle().getString("here"), issueLink, alert);
        issue.setTranslateY(-1);

        hBox.getChildren().setAll(label, issue);
        vBox.getChildren().setAll(hBox);

        alert.getDialogPane().setContent(vBox);

        alert.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        alert.getDialogPane().getStyleClass().add("myDialog");
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        alert.showAndWait();
    }

    public static Optional<GitHubAccountConnectionInfo> loginToGitHub() {
        Dialog<GitHubAccountConnectionInfo> dialog = new Dialog<>();
        dialog.setTitle(PreferencesManager.getLangBundle().getString("login_to_github_window_title"));
        dialog.setHeaderText(PreferencesManager.getLangBundle().getString("login_to_github_window_header"));

        ButtonType loginButtonType = new ButtonType(PreferencesManager.getLangBundle().getString("login"), ButtonBar.ButtonData.OK_DONE);
        ButtonType continueWithoutAccountButtonType = new ButtonType(PreferencesManager.getLangBundle().getString("continue_without_account"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(PreferencesManager.getLangBundle().getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, continueWithoutAccountButtonType, cancel);

        ImageView view = new ImageView(new Image(DialogUtils.class.getClassLoader().getResource("img/github_icon.png").toString()));
        view.setPreserveRatio(true);
        dialog.setGraphic(view);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText(PreferencesManager.getLangBundle().getString("username"));
        PasswordField password = new PasswordField();
        password.setPromptText(PreferencesManager.getLangBundle().getString("password"));
        CheckBox stayConnect = new CheckBox();

        grid.add(new Label(PreferencesManager.getLangBundle().getString("username") + ":"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label(PreferencesManager.getLangBundle().getString("password") + ":"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(new Label(PreferencesManager.getLangBundle().getString("stay_connected")), 0, 2);
        grid.add(stayConnect, 1, 2);
        grid.add(new Label(PreferencesManager.getLangBundle().getString("stay_connected_alert_1")), 0 ,3, 2 ,1);
        grid.add(new Label(PreferencesManager.getLangBundle().getString("stay_connected_alert_2")), 0, 4,2, 1);

        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || password.getText().trim().isEmpty());
        });
        password.textProperty().addListener((observableValue, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || username.getText().trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        dialog.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        dialog.getDialogPane().getStyleClass().add("myDialog");

        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        Platform.runLater(username::requestFocus);

        dialog.setResultConverter(param -> {
            if (param == loginButtonType) {
                return new GitHubAccountConnectionInfo(username.getText(), password.getText(), stayConnect.isSelected());
            } else if (param == continueWithoutAccountButtonType) {
                return new GitHubAccountConnectionInfo("", "", false);
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static void networkConnectionError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de connection");
        alert.setHeaderText(null);
        alert.setContentText("Une erreur de connection s'est produite, veuillez vérifiez votre connection et réessayer");

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);
        alert.getDialogPane().getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/dialog.css").toString());
        alert.getDialogPane().getStyleClass().add("myDialog");

        alert.showAndWait();
    }

    public static Pair<ProgressBar, Label> downloadInfo() {

        Stage stage = new Stage();
        stage.setTitle(PreferencesManager.getLangBundle().getString("download_info_window_title"));
        stage.initOwner(MainApp.getPrimaryStage());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(MainApp.LOGO);
        stage.initStyle(StageStyle.UNIFIED);
        stage.setResizable(false);
        stage.setOnCloseRequest(Event::consume);

        VBox vBox = new VBox();
        vBox.setSpacing(5d);
        vBox.setPrefSize(375, 50);
        vBox.setPadding(new Insets(5));
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.CENTER);

        vBox.getStyleClass().add("vbox");
        vBox.getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/download-progress.css").toString());

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(Double.MAX_VALUE);

        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.doubleValue() >= 1) {
                stage.close();
            }
        });

        Label label = new Label(PreferencesManager.getLangBundle().getString("download_starting"));
        vBox.getChildren().addAll(progressBar, label);

        // Set the max status
        int maxStatus = 12;
        // Create the Property that holds the current status count
        IntegerProperty statusCountProperty = new SimpleIntegerProperty(1);
        // Create the timeline that loops the statusCount till the maxStatus
        Timeline timelineBar = new Timeline(
                new KeyFrame(
                        // Set this value for the speed of the animation
                        Duration.millis(1000),
                        new KeyValue(statusCountProperty, maxStatus)
                )
        );
        // The animation should be infinite
        timelineBar.setCycleCount(Timeline.INDEFINITE);
        timelineBar.play();
        // Add a listener to the statusproperty
        statusCountProperty.addListener((ov, statusOld, statusNewNumber) -> {
            int statusNew = statusNewNumber.intValue();
            // Remove old status pseudo from progress-bar
            progressBar.pseudoClassStateChanged(PseudoClass.getPseudoClass("status" + statusOld.intValue()), false);
            // Add current status pseudo from progress-bar
            progressBar.pseudoClassStateChanged(PseudoClass.getPseudoClass("status" + statusNew), true);
        });

        Scene scene = new Scene(vBox);
        stage.setScene(scene);

        stage.show();

        return new Pair <>(progressBar, label);
    }

    private static Hyperlink createHyperLink(String text, String link, Dialog alert) {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setPadding(new Insets(0));
        hyperlink.setOnAction(event -> {
            final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                Thread thread = new Thread(() -> {
                    try {
                        Desktop.getDesktop().browse(new URL(link).toURI());
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            } else {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(link), null);
                Tooltip tooltip = new Tooltip("Le lien a bien été copié");
                tooltip.show(alert.getDialogPane().getScene().getWindow(), MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y - 30);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(tooltip::hide);
                        timer.cancel();
                    }
                }, 750);
            }
            hyperlink.setVisited(false);
        });
        return hyperlink;
    }

}
