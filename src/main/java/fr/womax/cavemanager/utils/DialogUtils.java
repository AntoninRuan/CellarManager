package fr.womax.cavemanager.utils;

import fr.womax.cavemanager.MainApp;
import fr.womax.cavemanager.model.Bottle;
import fr.womax.cavemanager.model.BottleInfo;
import fr.womax.cavemanager.model.CompartementInfo;
import fr.womax.cavemanager.model.WineType;
import fr.womax.cavemanager.view.BottleChooserController;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

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

        alert.showAndWait();
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
        final Spinner<Integer> column = new Spinner <>(1, 10, 1, 1);

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
                return new CompartementInfo(name.getText(), raw.getValue(), column.getValue(), before.isSelected());
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
            e.printStackTrace();
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
        ChoiceBox<WineType> wineType = new ChoiceBox <>();
        wineType.setItems(FXCollections.observableArrayList(WineType.values()));

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
        gridPane.add(new Label("Type:"), 0, 6);

        gridPane.add(name, 1, 0);
        gridPane.add(region, 1, 1);
        gridPane.add(edition, 1, 2);
        gridPane.add(domain, 1, 3);
        gridPane.add(comment, 1, 4);
        gridPane.add(yearSpinner, 1, 5);
        gridPane.add(wineType, 1, 6);

        dialog.getDialogPane().setContent(gridPane);
        ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.LOGO);

        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == validationButtonType) {
                BottleInfo bottleInfo = new BottleInfo(name.getText(), region.getText(), edition.getText(),
                        domain.getText(), comment.getText(), yearSpinner.getValue(), wineType.getValue());
                return bottleInfo;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static void updateAvailable() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Mise à jour disponible");
        alert.setHeaderText("Une nouvelle mise à jour est disponible");
        alert.setContentText("Voulez vous la faire?");

        ButtonType ok = new ButtonType("Oui");
        ButtonType no = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType neverAsk = new ButtonType("Ne plus me demander");

        alert.getButtonTypes().setAll(ok, no, neverAsk);

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
        alert.setContentText("Aucune mise à jour n'a été trouvé");

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

}
