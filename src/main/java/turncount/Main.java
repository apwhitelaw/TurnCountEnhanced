package turncount;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    Stage mainStage;
    Layout layout = new Layout();
    public Interval currentInterval;
    ArrayList<Interval> intervalData = new ArrayList<>();
    public SimpleIntegerProperty currentBank = new SimpleIntegerProperty(0);
    SimpleIntegerProperty[][] propertyData = new SimpleIntegerProperty[5][16];

    String username = "";
    String siteCode = "";
    String startTime = "";

    @Override
    public void start(final Stage primaryStage) throws Exception {
        mainStage = primaryStage;

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 16; j++) {
                propertyData[i][j] = new SimpleIntegerProperty(0);
            }
        }

        layout.createSceneAndSetupStage(primaryStage);
        setupKeyHandler(layout.mainScene);
        layout.setLabelBinds(propertyData);

        // Setup button actions
        layout.nextIntervalButton.setOnAction(actionEvent -> {
            incrementInterval();
            System.out.println("current interval " + currentInterval.startTime);
        });
        layout.prevIntervalButton.setOnAction(actionEvent -> {
            decrementInterval();
            System.out.println("current interval " + currentInterval.startTime);
        });
        layout.delIntervalButton.setOnAction(actionEvent -> {
            deleteInterval();
        });
        layout.goToIntervalButton.setOnAction(actionEvent -> {
            //popup.setupPopup(primaryStage);
        });

        SetupPopup popup = new SetupPopup();
        popup.setupPopup(primaryStage);
        popup.startButton.setOnAction(actionEvent -> {
            if(!popup.usernameField.getText().isBlank() && !popup.siteCodeField.getText().isBlank() && !popup.startTimeField.getText().isBlank()) {
                popup.stage.close();
                username = popup.usernameField.getText();
                siteCode = popup.siteCodeField.getText();
                startTime = popup.startTimeField.getText();

                LocalTime time = LocalTime.parse(startTime);
                currentInterval = new Interval(time.getHour(), time.getMinute());
                intervalData.add(currentInterval);
                updateTitle();
            }
        });

        FileHandler fileHandler = new FileHandler();
        Reader reader = Files.newBufferedReader(Paths.get(ClassLoader.getSystemResource("test.csv").toURI()));
        List<String[]> list = fileHandler.readAll(reader);
        for(String[] strings: list) {
            for(String s : strings) {
                System.out.println(s);
            }
        }

    }

    // Next interval, new or existing (chronologically)
    public void incrementInterval() {
        int index = intervalData.indexOf(currentInterval);

        // if not last index, get interval from arraylist
        // else create new interval and add it to arraylist
        if(index < (intervalData.size() - 1)) {
            currentInterval = intervalData.get(index + 1);
        } else {
            int hour = currentInterval.startTime.getHour();
            int minute = currentInterval.startTime.getMinute();
            if (minute == 55) {
                hour++;
                minute = 0;
            } else {
                minute += 5;
            }
            currentInterval = new Interval(hour, minute);
            intervalData.add(currentInterval);
        }
        updatePropertyIntegers();
        changeBank(0);
        updateTitle();
    }

    // Go back one interval (chronologically) if one exists
    public void decrementInterval() {
        int index = intervalData.indexOf(currentInterval);
        System.out.println(intervalData.size());
        if(index > 0) {
            currentInterval = intervalData.get(index - 1);
        }
        updatePropertyIntegers();
        changeBank(0);
        updateTitle();
    }

    // Updates all SimplePropertyInteger 's to values of currentInterval
    // Used for interval change
    public void updatePropertyIntegers() {
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 16; j++) {
                propertyData[i][j].set(currentInterval.getData()[i][j]);
            }

        }
    }

    // "clear interval" is a more accurate term
    public void deleteInterval() {
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 16; j++) {
                currentInterval.setDataValue(i, j, 0);
                propertyData[i][j].set(0);
            }
        }
    }

    public void increment(int index) {
        currentInterval.increment(currentBank.intValue(), index);
        propertyData[currentBank.intValue()][index].set(currentInterval.getDataValue(currentBank.intValue(), index));
        changeBank(0);
    }

    public void changeBank(int newBank) {
        int setBank = (currentBank.intValue() != newBank) ? newBank : 0;
        currentBank.set(setBank);
        layout.tabPane.getSelectionModel().select(currentBank.intValue());
    }

    public void saveData() {
        // intervals now added to array upon creation

        // TODO: WRITE TO FILE (CSV)
    }

    public void setupKeyHandler(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case DIGIT3: increment(0); break;
                case DIGIT4: increment(1); break;
                case DIGIT5: increment(2); break;
                case DIGIT6: increment(3); break;
                case E: increment(4); break;
                case R: increment(5); break;
                case T: increment(6); break;
                case Y: increment(7); break;
                case D: increment(8); break;
                case F: increment(9); break;
                case G: increment(10); break;
                case H: increment(11); break;
                case C: increment(12); break;
                case V: increment(13); break;
                case B: increment(14); break;
                case N: increment(15); break;
                case DIGIT8: changeBank(1); break;
                case I: changeBank(2); break;
                case K: changeBank(3); break;
                case COMMA: changeBank(4); break;
            }
        });
    }

    public void updateTitle() {
        mainStage.setTitle(String.format("TurnCountEnhanced - (%s - %s)", currentInterval.startTime.toString(),currentInterval.endTime.toString()));
    }

    public static void main(String[] args) {
        launch(args);
    }


}