package fr.antoninruan.cellarmanager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.antoninruan.cellarmanager.model.Bottle;
import fr.antoninruan.cellarmanager.model.Compartment;
import fr.antoninruan.cellarmanager.model.CompartmentInfo;
import fr.antoninruan.cellarmanager.model.Spot;
import fr.antoninruan.cellarmanager.utils.DialogUtils;
import fr.antoninruan.cellarmanager.utils.PreferencesManager;
import fr.antoninruan.cellarmanager.utils.Saver;
import fr.antoninruan.cellarmanager.utils.Updater;
import fr.antoninruan.cellarmanager.utils.github.model.release.Release;
import fr.antoninruan.cellarmanager.utils.mobile_sync.MobileSyncManager;
import fr.antoninruan.cellarmanager.view.CompartementDisplayController;
import fr.antoninruan.cellarmanager.view.PreferencesController;
import fr.antoninruan.cellarmanager.view.RootLayoutController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Antonin RUAN
 */
public class MainApp extends Application {

    //FIXME position du bouton pour ajouter une étagère peu intuitive

    //TODO bind les boutons importer / exporter à des actions.
    //TODO ajouter la possibilité de modifier le nombre de ligne/colonne d'une étagère
    //TODO pouvoir changer l'ordre des étagères.
    //TODO lorsque des bouteilles sont trouvés sur étagère différent de celle courante. Afficher quelque chose qui permettent de le voir
    //TODO ajouter un bind à une appli mobile pour afficher la bouteille séléctionner sur ordinateur sur un téléphone
    //TODO ajouter un plan global de la cave ou l'on puisse placer les étagères

    private static Stage primaryStage;
    private static BorderPane rootLayout;
    private static RootLayoutController controller;

    private static VBox compartementDisplay;
    private static CompartementDisplayController compartementDisplayController;

    private static PreferencesController preferencesController;

    private static File openedFile = null;
    private static File bottleFile = null;
    private static final File preferences = new File("ma_cave.preference");
    public static final DateFormat GITHUB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static JsonObject PREFERENCE_JSON;
    public final static Image LOGO = new Image(MainApp.class.getClassLoader().getResource("img/logo.png").toString());

    private final static ObservableMap<Integer, Compartment> compartements = FXCollections.observableHashMap();
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
                writer.write("{\"check_update\": true}");
                writer.flush();
            } catch (IOException e) {
                DialogUtils.sendErrorWindow(e);
            }

        }

        try {
            PreferencesManager.loadPreferences(JsonParser.parseReader(new FileReader(preferences)).getAsJsonObject());
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

        boolean newFile = true;

        try {
            if(PreferencesManager.getSaveFilePath() != null) {
                newFile = false;
                openedFile = new File(PreferencesManager.getSaveFilePath());
            } else {
                openedFile = null;
                try {
                    openedFile = DialogUtils.noSaveFile();
                } catch (URISyntaxException e) {
                    DialogUtils.sendErrorWindow(e);
                }

                if(openedFile == null) {
                    Platform.exit();
                }

                if(!openedFile.exists()) {
                    openedFile.createNewFile();
                    try(BufferedWriter writer = new BufferedWriter(new FileWriter(openedFile))) {
                        writer.write("{}");
                        writer.flush();
                    }
                    createCompartment(false);
                } else {
                    newFile = false;
                }
                PreferencesManager.setSaveFilePath(openedFile.getAbsolutePath());
            }

        }  catch (IOException e) {
            DialogUtils.sendErrorWindow(e);
        }

        //Récupération du fichier contenant l'ensemble des bouteilles

        if(PreferencesManager.getBottleFilePath() != null) {
            registerBottle(new File(PreferencesManager.getBottleFilePath()));
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
            PreferencesManager.setBottleFilePath(file.getAbsolutePath());
        }

        initCompartementDisplayLayout();

        if(!newFile) {
            openFile(openedFile);
        }

        MainApp.primaryStage.setTitle(String.format(PreferencesManager.getLangBundle().getString("main_window_title"), openedFile.getName()));

        Thread checkUpdate = new Thread(() -> {
            if(PreferencesManager.doCheckUpdateAtStart()) {
                Pair<Boolean, Release> result = Updater.checkUpdate();
                if(result.getKey()) {
                    Platform.runLater(() -> DialogUtils.updateAvailable(true, result.getValue()));
                }
            }
        });

        checkUpdate.start();

    }

    public static void saveFiles() {
        PreferencesManager.savePreferences(preferences);
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
            DialogUtils.sendErrorWindow(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(openedFile))){
            JsonObject jsonObject = new JsonObject();
            if(compartements == null || compartements.isEmpty()) {
                writer.write("{}");
            } else {
                for(Compartment compartment : MainApp.compartements.values()) {
                    jsonObject.add(String.valueOf(compartment.getId()), compartment.toJson());
                }
                writer.write(jsonObject.toString());
            }
            writer.flush();
        } catch (IOException e) {
            DialogUtils.sendErrorWindow(e);
        }
    }

    public void stop() {
        if(MobileSyncManager.isActivate()) {
            MobileSyncManager.toggleState();
        }
        if(bottleFile != null && openedFile != null)
            saveFiles();
        Saver.cancelTask();
    }

    private void initRootLayout() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(PreferencesManager.getLangBundle());
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
            loader.setResources(PreferencesManager.getLangBundle());
            loader.setLocation(MainApp.class.getClassLoader().getResource("fxml/CompartementDisplayLayout.fxml"));

            VBox vBox = loader.load();

            Node right = rootLayout.getRight();
            rootLayout.setRight(null);
            rootLayout.setCenter(vBox);
            rootLayout.setRight(right);

            compartementDisplayController = loader.getController();

        } catch (IOException e) {
            DialogUtils.sendErrorWindow(e);
        }
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
                Compartment compartment = Compartment.fromJson(entry.getValue().getAsJsonObject(), id);
                MainApp.compartements.put(id, compartment);
                if(MainApp.lastCompartementId < id)
                    MainApp.lastCompartementId = id;

            }

            lastCompartementId ++;

        } catch (FileNotFoundException e) {
            DialogUtils.sendErrorWindow(e);
        }
    }

    public void createCompartment(boolean cancelable) {

        Optional<CompartmentInfo> result = DialogUtils.createNewCompartement(cancelable);

        result.ifPresent(CompartmentInfo::createCompartment);

    }

    public static Compartment removeCompartement(int id) {
        Compartment compartment = MainApp.getCompartement(id);
        for(Spot[] spotColumn : compartment.getSpots()) {
            for(Spot spot : spotColumn) {
                MainApp.getSpots().remove(spot);
            }
        }
        MainApp.getCompartements().remove(compartment.getId());
        for(Compartment c : MainApp.getCompartements().values()) {
            if(c.getIndex() > compartment.getIndex()) {
                c.setIndex(c.getIndex() - 1);
            }
        }
        Saver.doChange();
        MainApp.getCompartementDisplayController().setCurrentCompartementDisplayed(compartment.getIndex());
        return compartment;
    }

    public static File getOpenedFile() {
        return openedFile;
    }

    public static File getBottleFile() {
        return bottleFile;
    }

    public static ObservableMap <Integer, Compartment> getCompartements() {
        return compartements;
    }

    public static Compartment getCompartement(int index) {
        for(Compartment compartment : compartements.values()) {
            if(compartment.getIndex() == index) {
                return compartment;
            }
        }
        return null;
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

    public static PreferencesController getPreferencesController() {
        return preferencesController;
    }

    public static void setPreferencesController(PreferencesController preferencesController) {
        MainApp.preferencesController = preferencesController;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static BorderPane getRootLayout() {
        return rootLayout;
    }

    public static JsonObject getPreferenceJson() {
        return PREFERENCE_JSON;
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
        return ++lastBottleId;
    }

    public static int nextCompartementId() {
        return lastCompartementId ++;
    }

}