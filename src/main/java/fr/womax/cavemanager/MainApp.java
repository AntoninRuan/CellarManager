package fr.womax.cavemanager;

import com.google.gson.JsonElement;
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
import javafx.collections.ObservableMap;
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
import java.util.Map;
import java.util.Optional;

/**
 * @author Antonin RUAN
 */
public class MainApp extends Application {

    //FIXME tenter de corriger le bug avec les spinners de selection. La valeur entrée au clavier n'est pas pris en compte dans le .getValue();
    //FIXME fix temporaire -> l'édition au clavier est désactivé

    //TODO changer la couleur affiché de la bouteille selon son type
    //TODO ajouter la possibilité de modifier le nombre de ligne/colonne d'une étagère
    //TODO ajouter mon nom/prénom au menu à propos

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

    private final static ObservableMap<Integer, Compartement> compartements = FXCollections.observableHashMap();
    private final static ObservableList<Spot> spots = FXCollections.observableArrayList();
    private final static ObservableMap <Integer, Bottle> bottles = FXCollections.observableHashMap();
    private static int lastBottleId;
    private static int lastCompartementId;

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
            if(newUpdate) {
                DialogUtils.updateAvailable(true);
            }
        }

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
                for(Bottle bottle : bottles.values()) {
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
                for(Compartement compartement : MainApp.compartements.values()) {
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
            for(Map.Entry<String, JsonElement> entry : bottles.entrySet()) {
                int id = Integer.valueOf(entry.getKey());
                Bottle bottle = Bottle.fromJson(id, entry.getValue().getAsJsonObject());
                MainApp.bottles.put(id, bottle);
                if(MainApp.lastBottleId < id)
                    MainApp.lastBottleId = id;
            }
        } catch (FileNotFoundException e) {
            DialogUtils.sendErrorWindow(e);
        }
    }

    public void openFile(File file) {
        try {
            openedFile = file;
            JsonObject object = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();

            for(Map.Entry<String, JsonElement> entry : object.entrySet()) {

                int id = Integer.valueOf(entry.getKey());
                Compartement compartement = Compartement.fromJson(entry.getValue().getAsJsonObject());
                MainApp.compartements.put(id, compartement);
                if(MainApp.lastCompartementId < id)
                    MainApp.lastCompartementId = id;

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createNewCompartements(boolean cancelable) {

        Optional<CompartementInfo> result = DialogUtils.createNewCompartement(cancelable);

        result.ifPresent(CompartementInfo::createCompartement);

    }

    public static ObservableMap <Integer, Compartement> getCompartements() {
        return compartements;
    }

    public static ObservableList <Spot> getSpots() {
        return spots;
    }

    public static ObservableMap<Integer, Bottle> getBottles() {
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

        for(Spot spot : MainApp.spots) {
            if (!spot.isEmpty()) {
                if (spot.getBottle().getId() == bottle.getId()) {
                    spots.add(spot);
                }
            }
        }
        return spots;
    }

    public static int nextBottleId() {
        return lastBottleId ++;
    }

    public static int nextCompartementId() {
        return lastCompartementId ++;
    }

}
