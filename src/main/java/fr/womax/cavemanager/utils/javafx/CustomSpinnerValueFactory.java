package fr.womax.cavemanager.utils.javafx;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.util.converter.IntegerStringConverter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Antonin Ruan
 */
public class CustomSpinnerValueFactory extends SpinnerValueFactory<Integer> {

    private Spinner spinner;
    private int min, max;

    private boolean displayUnauthorizedValue;

    public CustomSpinnerValueFactory(int min, int max) {
        this(min, max, min);
    }

    public CustomSpinnerValueFactory(int min, int max, int initialValue) {
        this.min = min;
        this.max = max;
        setValue(initialValue);
        setConverter(new IntegerStringConverter());

        valueProperty().addListener((observable, oldValue, newValue) -> {

            System.out.println(newValue);

            if(newValue < min) {
                setValue(min);
                unauthorizedValue();
            } else if (newValue > max) {
                setValue(max);
                unauthorizedValue();
            }

        });
    }


    public void setSpinner(Spinner spinner) {
        this.spinner = spinner;
        spinner.setPrefHeight(27.0);
        spinner.setPrefWidth(151.0);
    }

    @Override
    public void decrement(int steps) {
        setValue(getValue() - steps);
    }

    @Override
    public void increment(int steps) {
        setValue(getValue() + steps);
    }

    private void unauthorizedValue() {

        if(!displayUnauthorizedValue) {

            displayUnauthorizedValue = true;

            String style = "";
            style += "-fx-border-color: #ee291b;";
            style += "-fx-border-width: 1px;";
            style += "-fx-border-radius: 2px;";
//            style += "-fx-border-insets: 1px;";

            System.out.println(spinner.getWidth());
            spinner.getEditor().setStyle(style);

            Tooltip tooltip = new Tooltip("La valeur doit être comprise entre " + min + " et " + max);
            Bounds bounds = spinner.localToScreen(spinner.getBoundsInLocal());
            tooltip.show(spinner, bounds.getMaxX(), bounds.getMinY());

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        spinner.getEditor().setStyle("");
                        tooltip.hide();
                        displayUnauthorizedValue = false;
                    });
                }
            }, 1000);

        }

    }

}
