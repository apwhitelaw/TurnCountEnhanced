package turncount;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    final static String SITECODE_REGEX = "^[0-9]{8,}";

    Stage mainStage;
    Layout layout = new Layout();
    public Interval currentInterval;
    ArrayList<Interval> countData = new ArrayList<>();
    public SimpleIntegerProperty currentBank = new SimpleIntegerProperty(0);
    SimpleIntegerProperty[][] propertyData = new SimpleIntegerProperty[5][16];
    FileHandler fileHandler = new  FileHandler();

    private String username = "";
    private String siteCode = "";
    private String startTime = "";

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
//            List<String[]> s = fileHandler.buildStrings(username, siteCode, countData);
            Path path = Path.of("src\\main\\resources\\count.csv");
//            try {
//                fileHandler.writeInts(s, path);
//            } catch(Exception e) {
//                System.err.println(e);
//            }
            saveData(path);
        });

        SetupPopup popup = new SetupPopup();
        popup.setupPopup(primaryStage);
        popup.getStartButton().setOnAction(actionEvent -> {
            if(!popup.getUsernameField().getText().isBlank() &&
                    !popup.getSiteCodeField().getText().isBlank() &&
                    !popup.getStartTimeField().getText().isBlank()) {
                username = popup.getUsernameField().getText();
                siteCode = popup.getSiteCodeField().getText();
                startTime = popup.getStartTimeField().getText();

                if(siteCodeAlreadyExists(siteCode)) {
                    displayAlert(popup.getStage());
                } else {
                    LocalTime time = LocalTime.parse(startTime);
                    if(time != null) {
                        popup.getStage().close();
                        currentInterval = new Interval(time.getHour(), time.getMinute());
                        countData.add(currentInterval);
                        updateTitle();
                    }
                }

            }
        });

        popup.getOpenFileButton().setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("C:\\Users\\Austin\\Documents\\Java Projects\\TurnCountEnhanced\\src\\main\\resources"));
            fileChooser.setTitle("Open Count File");
            loadData(fileChooser.showOpenDialog(popup.getStage()));
        });

    }

    public void displayAlert(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.initOwner(stage);
        alert.show();
    }

    public boolean siteCodeAlreadyExists(String string) {
        // Allow user to change directory ?
        // DirectoryChooser directoryChooser = new DirectoryChooser()

        File directoryFile = new File("C:\\Users\\Austin\\Documents\\Java Projects\\TurnCountEnhanced\\src\\main\\resources");
        String enteredSiteCode = getSiteCodeFromString(siteCode);

        if(directoryFile.isDirectory()) {
            File[] fileList = directoryFile.listFiles();
            for(File file: fileList) {
                if(file.isFile()) {
                    String[] splitString = file.getName().split("\\.");
                    String fileSiteCode = getSiteCodeFromString(splitString[0]);
                    if(fileSiteCode.equals(enteredSiteCode)) {
                        System.out.println("Warning! Site code already exists!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getSiteCodeFromString(String string) {
        Pattern pattern = Pattern.compile(SITECODE_REGEX);
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()){
            System.out.println(matcher.group(0));
            return matcher.group(0);
        } else {
            return string;
        }
    }

    // Next interval, new or existing (chronologically)
    public void incrementInterval() {
        int index = countData.indexOf(currentInterval);

        // if not last index, get interval from arraylist
        // else create new interval and add it to arraylist
        if(index < (countData.size() - 1)) {
            currentInterval = countData.get(index + 1);
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
            countData.add(currentInterval);
        }
        updatePropertyIntegers();
        changeBank(0);
        updateTitle();
    }

    // Go back one interval (chronologically) if one exists
    public void decrementInterval() {
        int index = countData.indexOf(currentInterval);
        System.out.println(countData.size());
        if(index > 0) {
            currentInterval = countData.get(index - 1);
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

    public void saveData(Path path) {
        List<String[]> countStrings = fileHandler.buildStrings(username, siteCode, countData);
        try {
            fileHandler.saveFile(countStrings, path);
        } catch(Exception e) {
            System.err.println(e);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(mainStage);
        alert.show();
    }

    public void loadData(File file) {
        try {
            Files.lines(file.toPath()).forEach(value -> {
                System.out.println(value);
            });
        } catch (Exception e) {
            System.err.println(e);
        }
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
