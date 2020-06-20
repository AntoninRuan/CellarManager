package fr.womax.cavemanager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.womax.cavemanager.model.*;
import fr.womax.cavemanager.utils.DialogUtils;
import fr.womax.cavemanager.utils.Updater;
import fr.womax.cavemanager.view.CompartementDisplayController;
import fr.womax.cavemanager.view.RootLayoutController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Antonin RUAN
 */
public class MainApp extends Application {

    //FIXME recherche buggé à travers les pages: n'affiche les recherches que sur la page actuel. (Problèmes de render lors de l'affichage des spots)
    //FIXME recherche buggé lorsqu'une bouteille est ajouter alors qu'une rechercher en encore en cours
    //FIXME je crois qu'il y a un bug lors de la recherche par type: Toutes les bouteilles sont séléctionnés
    //FIXME tenter de corriger le bug avec les spinners de selection. La valeur entrée au clavier n'est pas pris en compte dans le .getValue();

    //TODO ajouter un décompte des bouteilles dans le tableau des bouteilles. (Compter le nombre de chaque type de bouteilles présents dans les slots)
    //TODO ajouter un nom sur les étagères et l'afficher au dessus de la grille
    //TODO ajouter mon nom/prénom au menu à propos

    //TODO ce serait pas de mal passer le projet sur du vcs
    //TODO tester la compilation JavaFX intégré à intellij voir si ça change qq chose au lancement sur une autre machine

    /*TODO Ajouter un menus de paramètre qui permettent
            Changer la taille des cases pour pouvoir mettre plus de ligne/colonnes sur une seule étagère
            Intégrér une gestion multilingue
     */


    private static Stage primaryStage;
    private static BorderPane rootLayout;
    private static RootLayoutController controller;

    private static VBox compartementDisplay;
    private static CompartementDisplayController compartementDisplayController;

    private static File openedFile = null;
    private static File bottleFile = null;
    private static final File preferences = new File("ma_cave.preference");
    private static JsonObject preferenceJson;

    public final static Image LOGO = new Image(MainApp.class.getClassLoader().getResource("img/logo.png").toString());

    private final static ObservableList<Compartement> compartements = FXCollections.observableArrayList();
    private final static ObservableList<Spot> spots = FXCollections.observableArrayList();
    private final static ObservableList<Bottle> bottles = FXCollections.observableArrayList();

    public static void main(String... args) {
        System.setProperty("file.encoding", "UTF-8");

        if(!preferences.exists()) {

            try{
                preferences.createNewFile();
            } catch (IOException e) {
                DialogUtils.sendErrorWindow(e);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(preferences));){
                writer.write("{}");
                writer.flush();
            } catch (IOException e) {
                DialogUtils.sendErrorWindow(e);
            }

        }

        try {
            preferenceJson = JsonParser.parseReader(new FileReader(preferences)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            DialogUtils.sendErrorWindow(e);
        }

        launch(args);


    }

    public void start(Stage primaryStage) {
        MainApp.primaryStage = primaryStage;
        MainApp.primaryStage.setTitle("Cave");

        initRootLayout();
        //Récupération du fichier de sauvegarde

        try {
            if(preferenceJson.get("save_file") != null) {
                openFile(new File(preferenceJson.get("save_file").getAsString()));
            } else {
                openedFile = null;
                do {
                    try {
                        openedFile = noSaveFile();
                    } catch (URISyntaxException e) {
                        DialogUtils.sendErrorWindow(e);
                    }
                } while (openedFile == null);

                if(!openedFile.exists()) {
                    openedFile.createNewFile();
                    BufferedWriter writer = new BufferedWriter(new FileWriter(openedFile));
                    writer.write("{}");
                    writer.flush();
                    writer.close();
                    createNewCompartements(false);
                }
                preferenceJson.addProperty("save_file", openedFile.getAbsolutePath());
            }

        }  catch (IOException e) {
            DialogUtils.sendErrorWindow(e);
        }

        //Récupération du fichier contenant l'ensemble des bouteilles

        if(preferenceJson.get("bottle_file") != null) {
            registerBottle(new File(preferenceJson.get("bottle_file").getAsString()));
        } else {
            File file = new File(openedFile.getParent() + File.separator + "bottle_file.mcv");
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    DialogUtils.sendErrorWindow(e);
                }
                bottleFile = file;
                try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("{}");
                    writer.flush();
                } catch (IOException e) {
                    DialogUtils.sendErrorWindow(e);
                }
            } else {
                registerBottle(file);
            }
            preferenceJson.addProperty("bottle_file", file.getAbsolutePath());
        }

        initCompartementDisplayLayout();

        if(preferenceJson.get("check_update") == null) {
            preferenceJson.addProperty("check_update", true);
        }

        if(preferenceJson.get("check_update").getAsBoolean()) {
            boolean newUpdate = Updater.checkUpdate();
        }

    }

    public static void saveFiles() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(preferences));){
            writer.write(preferenceJson.toString());
            writer.flush();
        } catch (IOException e) {
            DialogUtils.sendErrorWindow(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(bottleFile))){
            JsonObject bottlesJson = new JsonObject();
            if(bottles == null || bottles.isEmpty()) {
                writer.write("{}");
            } else {
                for(Bottle bottle : bottles) {
                    bottlesJson.add(String.valueOf(bottle.getId()), bottle.toJson());
                }
                writer.write(bottlesJson.toString());
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(openedFile))){
            JsonObject jsonObject = new JsonObject();
            if(compartements == null || compartements.isEmpty()) {
                writer.write("{}");
            } else {
                for(Compartement compartement : MainApp.compartements) {
                    jsonObject.add(String.valueOf(compartement.getId()), compartement.toJson());
                }
                writer.write(jsonObject.toString());
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        saveFiles();
    }

    private void initRootLayout() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("fxml/RootLayout.fxml"));
            rootLayout = loader.load();

            controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.getIcons().add(LOGO);
            primaryStage.show();


        } catch (Exception e) {
            DialogUtils.sendErrorWindow(e);
        }

    }

    public void initCompartementDisplayLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("fxml/CompartementDisplayLayout.fxml"));

            VBox vBox = loader.load();
            rootLayout.setCenter(vBox);

            compartementDisplayController = loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File noSaveFile() throws URISyntaxException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aucun fichier de sauvegarde détécté");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez choisir le fichier pour sauvegarder votre cave");

        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(LOGO);

        alert.showAndWait();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choissisez un fichier pour sauvegarder votre cave");
        File currentJar = new File(MainApp.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        System.out.println(currentJar.getAbsolutePath());
        fileChooser.setInitialDirectory(currentJar.getParentFile());
        fileChooser.setInitialFileName("ma_cave");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichier MaCave", "*.mcv")
        );
        return fileChooser.showSaveDialog(primaryStage);
    }

    private void registerBottle(File file) {
        try {
            bottleFile = file;
            JsonObject bottles = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
            int i = 0;
            while (bottles.get(String.valueOf(i)) != null) {
                JsonObject bottle = bottles.get(String.valueOf(i)).getAsJsonObject();
                BottleInfo bottleInfo = BottleInfo.fromJson(bottle);
                bottleInfo.createBottle();
                i ++;
            }
        } catch (FileNotFoundException e) {
            DialogUtils.sendErrorWindow(e);
        }
    }

    public void openFile(File file) {
        try {
            openedFile = file;
            JsonObject object = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();

            int i = 0;
            while (object.get(String.valueOf(i)) != null) {
                Compartement compartement = Compartement.fromJson(object.get(String.valueOf(i)).getAsJsonObject());
                compartements.add(compartement);
                i ++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createNewCompartements(boolean cancelable) {

        Optional<CompartementInfo> result = DialogUtils.createNewCompartement(cancelable);

        result.ifPresent(compartementInfo -> {
            compartementInfo.createCompartement(this);
        });

    }

    public static ObservableList <Compartement> getCompartements() {
        return compartements;
    }

    public static ObservableList <Spot> getSpots() {
        return spots;
    }

    public static ObservableList<Bottle> getBottles() {
        return bottles;
    }

    public static RootLayoutController getController() {
        return controller;
    }

    public static CompartementDisplayController getCompartementDisplayController() {
        return compartementDisplayController;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static BorderPane getRootLayout() {
        return rootLayout;
    }

    public static JsonObject getPreferenceJson() {
        return preferenceJson;
    }

    public static List<Spot> hasBottle(Bottle bottle) {
        List<Spot> spots = new ArrayList <>();

        for(Spot spot : spots) {
            if (spot.getBottle().equals(bottle)) {

                spots.add(spot);

            }
        }
        return spots;
    }

    public static int nextBottleId() {
        if(bottles != null ) {
            return bottles.size();
        } else
            return 0;
    }

    public static int nextCompartementId() {
        if(compartements != null ) {
            return compartements.size();
        } else
            return 0;
    }

}
