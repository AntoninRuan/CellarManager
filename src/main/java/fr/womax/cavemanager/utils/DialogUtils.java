package fr.womax.cavemanager.utils;

import fr.womax.cavemanager.MainApp;
import fr.womax.cavemanager.model.Bottle;
import fr.womax.cavemanager.model.BottleInfo;
import fr.womax.cavemanager.model.CompartementInfo;
import fr.womax.cavemanager.model.WineType;
import fr.womax.cavemanager.utils.report.BugInfo;
import fr.womax.cavemanager.utils.report.DropboxUtils;
import fr.womax.cavemanager.utils.report.SuggestionInfo;
import fr.womax.cavemanager.view.BottleChooserController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Antonin Ruan
 */
public class DialogUtils {

    public static void sendErrorWindow(Exception e) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(e.getLocalizedMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Message d'erreur:");

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

        ButtonType reportBug = new ButtonType("Envoyer le rapport d'erreur");
        alert.getButtonTypes().add(reportBug);

        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(buttonType -> {
            if(buttonType == reportBug) {
                Optional<BugInfo> result1 = DialogUtils.sendBugReport(textArea.getText());
                result1.ifPresent(bugInfo -> {
                    DropboxUtils.sendBugIssue(e.getMessage(), bugInfo.getDescription(), bugInfo.getDate(), bugInfo.getStackTrace());
                });
            }
        });
    }

    public static Optional<BugInfo> sendBugReport(String stackTrace) {
        Dialog<BugInfo> dialog = new Dialog <>();
        dialog.setTitle("Reporter un bug");
        dialog.setHeaderText("Veuillez décrire le bug que vous rencontrez");

        final ButtonType validationButtonType = new ButtonType("Envoyer", ButtonBar.ButtonData.OK_DONE);
        final ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(validationButtonType, cancelButtonType);

        GridPane gridPane = new GridPane();

        TextArea description = new TextArea();
        description.setPromptText("Description");
        description.setWrapText(true);

        gridPane.add(description, 0, 0);

        dialog.getDialogPane().setContent(gridPane);
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

        dialog.setTitle("Suggérer une idée");
        dialog.setHeaderText("Veuillez décrire votre idée");

        final ButtonType validationButtonType = new ButtonType("Envoyer", ButtonBar.ButtonData.OK_DONE);
        final ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(validationButtonType, cancelButtonType);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField title = new TextField();
        title.setPromptText("Nom de l'idée");

        TextArea description = new TextArea();
        description.setPromptText("Description de l'idée");

        gridPane.add(title, 0, 0);
        gridPane.add(description, 0, 1);

        dialog.getDialogPane().setContent(gridPane);

        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        dialog.setResultConverter(param -> {
            if(param == validationButtonType) {
                return new SuggestionInfo(title.getText(), description.getText(), new Date());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static Optional<CompartementInfo> createNewCompartement(boolean cancelable) {
        Dialog <CompartementInfo> dialog = new Dialog <>();
        dialog.setTitle("Nouvelle Etagère");
        dialog.setHeaderText("Entrez les informations de cette nouvelle étagère");

        final ButtonType validationButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(validationButtonType, cancelButtonType);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Nom");

        final Spinner <Integer> raw = new Spinner <>(1, 10, 1, 1);
        raw.setEditable(true);
        raw.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                raw.increment(0);
            }
        });
        final Spinner<Integer> column = new Spinner <>(1, 10, 1, 1);
        column.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                column.increment(0);
            }
        });
        column.setEditable(true);

        ToggleGroup group = new ToggleGroup();
        final RadioButton before = new RadioButton("Remplacer Actuel");
        before.setToggleGroup(group);
        RadioButton after = new RadioButton("Mettre après");
        after.setSelected(true);
        after.setToggleGroup(group);

        gridPane.add(new Label("Nom"), 0, 0);
        gridPane.add(name, 1, 0);
        gridPane.add(new Label("Ligne"), 0, 1);
        gridPane.add(raw, 1, 1);
        gridPane.add(new Label("Colonne"), 0, 2);
        gridPane.add(column, 1, 2);
        gridPane.add(before, 0, 3);
        gridPane.add(after, 1, 3);

        Node cancel = dialog.getDialogPane().lookupButton(cancelButtonType);
        cancel.setDisable(!cancelable);

        dialog.getDialogPane().setContent(gridPane);
        ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == validationButtonType) {
                return new CompartementInfo(name.getText() == null ? "Etagère" : name.getText(), raw.getValue(), column.getValue(), before.isSelected());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static Optional<Bottle> chooseBottle(boolean cancelable) {
        Optional<Bottle> result = Optional.ofNullable(null);
        try {
            FXMLLoader loader = new FXMLLoader();
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
        dialog.setTitle("Ajouter une bouteille");
        dialog.setHeaderText("Entrez les informations de la bouteille");

        final ButtonType validationButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
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
        name.setPromptText("Nom");
        TextField region = new TextField();
        region.setPromptText("Région");
        TextField edition = new TextField();
        edition.setPromptText("Édition (ou Cuvée)");
        TextField domain = new TextField();
        domain.setPromptText("Domaine");
        TextField comment = new TextField();
        comment.setPromptText("Commentaire");
        Spinner<Integer> yearSpinner = new Spinner <>(1950, 3000, year, 1);
        yearSpinner.setEditable(true);
        Spinner<Integer> consumeYearSpinner = new Spinner <>(1950, 3000, consumeYear, 1);
        consumeYearSpinner.setEditable(true);
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

        gridPane.add(new Label("Nom:"), 0, 0);
        gridPane.add(new Label("Région:"), 0, 1);
        gridPane.add(new Label("Édition:"), 0, 2);
        gridPane.add(new Label("Domaine:"), 0, 3);
        gridPane.add(new Label("Commentaire:"), 0,4);
        gridPane.add(new Label("Année:"), 0, 5);
        gridPane.add(new Label("Année de consommation:"), 0, 6);
        gridPane.add(new Label("Type:"), 0, 7);

        gridPane.add(name, 1, 0);
        gridPane.add(region, 1, 1);
        gridPane.add(edition, 1, 2);
        gridPane.add(domain, 1, 3);
        gridPane.add(comment, 1, 4);
        gridPane.add(yearSpinner, 1, 5);
        gridPane.add(consumeYearSpinner, 1, 6);
        gridPane.add(wineType, 1, 7);

        dialog.getDialogPane().setContent(gridPane);
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

    public static void updateAvailable(boolean neverAskButton) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Mise à jour disponible");
        alert.setHeaderText("Une nouvelle mise à jour est disponible");
        alert.setContentText("Voulez vous la faire?");

        ButtonType ok = new ButtonType("Oui");
        ButtonType no = new ButtonType("Non");
        ButtonType neverAsk = new ButtonType("Ne plus me demander");

        alert.getButtonTypes().setAll(ok, no);
        if(neverAskButton)
            alert.getButtonTypes().add(neverAsk);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        Optional<ButtonType> result = alert.showAndWait();

        result.ifPresent(buttonType -> {

            if(buttonType == ok) {
                Updater.update();
            } else if (buttonType == neverAsk) {
                MainApp.getPreferenceJson().addProperty("check_update", false);
            }

        });

    }

    public static void noUpdateAvailable() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pas de mise à jour disponible");
        alert.setHeaderText(null);
        alert.setContentText("Aucune mise à jour n'a été trouvée");

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        alert.showAndWait();
    }

    public static void needAtLeastOneCompartement() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Une étagère obligatoire");
        alert.setHeaderText("Vous devez au moins avoir une étagère");
        alert.setContentText(null);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(MainApp.LOGO);

        alert.showAndWait();
    }

    public static void bottlePresentInCave() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Bouteille présente dans la cave");
        alert.setHeaderText("Cette bouteille est présente dans votre cave");
        alert.setContentText("Veuillez l'enlever de tous les emplacements si vous souhaitez vraiment la supprimer");

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        alert.showAndWait();
    }

    public static void about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("A propos");
        alert.setHeaderText(null);
        alert.setContentText("Développé par Antonin Ruan \n" +
                "Design par Théo Lasnier \n" +
                "Version: " + Updater.VERSION);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        alert.showAndWait();
    }

    public static void infoMessage(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        alert.showAndWait();
    }

    public static ProgressBar downloadInfo() {

        Stage stage = new Stage();
        stage.setTitle("Téléchargement en cours");
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

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(Double.MAX_VALUE);

        final boolean[] displayed = {false};

        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.doubleValue() >= 1 && !displayed[0]) {
                ButtonBar buttonBar = new ButtonBar();

                Button ok = new Button("Oui");
                ok.setOnAction(event -> {
                    System.exit(0);
                });

                buttonBar.getButtons().setAll(ok);

                Label label = new Label("Le programme va s'arrêter, relancer le pour appliquer la mise à jour");
                label.setWrapText(true);

                vBox.getChildren().setAll(label, buttonBar);
                displayed[0] = true;
                stage.setOnCloseRequest(event -> {
                    Platform.exit();
                });
            }
        });

        vBox.getChildren().addAll(progressBar, new Label("Téléchargement en cours"));

        Scene scene = new Scene(vBox);
        stage.setScene(scene);

        stage.show();

        return progressBar;
    }

}
