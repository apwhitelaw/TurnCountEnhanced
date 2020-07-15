package turncount;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.vivoxalabs.customstage.CustomStage;
import lk.vivoxalabs.customstage.CustomStageBuilder;

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

    Stage countStage; // 'Stage countStage' and 'CountStage layout' is confusing, will fix later
    Stage videoStage;
    SetupPopup popup;
    CountStage countStageHandler;
    VideoStage videoStageHandler;
    public Interval currentInterval;
    ArrayList<Interval> countData = new ArrayList<>();
    public SimpleIntegerProperty currentBank = new SimpleIntegerProperty(0);
    SimpleIntegerProperty[][] propertyData = new SimpleIntegerProperty[5][16];
    FileHandler fileHandler = new  FileHandler();
    boolean countStarted = false;

    private String username = "";
    private String siteCode = "";
    private String startTime = "";

    @Override
    public void start(final Stage primaryStage) throws Exception {
        countStage = primaryStage;

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 16; j++) {
                propertyData[i][j] = new SimpleIntegerProperty(0);
            }
        }

        countStageHandler = new CountStage(countStage);

//        CustomStageBuilder builder = new CustomStageBuilder();
//        builder = builder.setWindowTitle("New test window");
//        builder = builder.setTitleColor("white");
//        builder = builder.setWindowColor("blue");
//        CustomStage customStage = builder.build();
//        customStage.show();

        // Setup button actions
        countStageHandler.nextIntervalButton.setOnAction(actionEvent -> {
            incrementInterval();
        });
        countStageHandler.prevIntervalButton.setOnAction(actionEvent -> {
            decrementInterval();
        });
        countStageHandler.delIntervalButton.setOnAction(actionEvent -> {
            deleteInterval();
        });
        countStageHandler.goToIntervalButton.setOnAction(actionEvent -> {
            goToInterval();

////            List<String[]> s = fileHandler.buildStrings(username, siteCode, countData);
//            Path path = Path.of("src\\main\\resources\\count.csv");
////            try {
////                fileHandler.writeInts(s, path);
////            } catch(Exception e) {
////                System.err.println(e);
////            }
//            saveData(path);
        });

        popup = new SetupPopup();
        popup.setupPopup(primaryStage);
        popup.getStartButton().setOnAction(actionEvent -> {
            if(!popup.getUsernameField().getText().isBlank() &&
                    !popup.getSiteCodeField().getText().isBlank() &&
                    !popup.getStartTimeField().getText().isBlank()) {
                username = popup.getUsernameField().getText();
                siteCode = popup.getSiteCodeField().getText();
                startTime = popup.getStartTimeField().getText();

                if(siteCodeAlreadyExists(siteCode)) {
                    sendAlert(Alert.AlertType.WARNING, "Site Code already exists!",
                            "This site code already exists, are you sure you want to continue?", popup.getStage());
                } else {
                    LocalTime time = LocalTime.parse(startTime);
                    if(time != null) {
                        popup.getStage().close();
                        currentInterval = new Interval(time.getHour(), time.getMinute());
                        countData.add(currentInterval);
                        updateTitle();
                        countStarted = true;
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

        videoStageHandler = new VideoStage();
        videoStageHandler.setupVideoStage();
        videoStage = videoStageHandler.getStage();

        setupKeyHandler();
        countStageHandler.setLabelBinds(propertyData);
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
        if(index > 0) {
            currentInterval = countData.get(index - 1);
        }
        updatePropertyIntegers();
        changeBank(0);
        updateTitle();
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

    // Go to a specific interval
    public void goToInterval() {
        Stage stage = new Stage();
        Label label = new Label("Go to time:");
        //label.setAlignment(Pos.CENTER);
        HBox hBox = new HBox(label);
        hBox.setAlignment(Pos.CENTER);
        TextField timeField = new TextField("Enter time");
        Button goButton = new Button("Go");
        goButton.setMinWidth(33);
        HBox.setHgrow(goButton, Priority.NEVER);
        goButton.setOnAction(actionEvent -> stage.close());
        Button cancelButton = new Button("Cancel");
        HBox hBox2 = new HBox(timeField, goButton);
        HBox.setHgrow(timeField, Priority.ALWAYS);
        hBox2.setAlignment(Pos.CENTER);
        hBox2.setSpacing(5);
        VBox vBox = new VBox(hBox, hBox2);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10));
        vBox.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        vBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        Scene timeFieldScene = new Scene(vBox, 200, 60);
        stage.setScene(timeFieldScene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(countStage);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
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

    public void increment(int index) {
        currentInterval.increment(currentBank.intValue(), index);
        propertyData[currentBank.intValue()][index].set(currentInterval.getDataValue(currentBank.intValue(), index));
        changeBank(0);
    }

    public void changeBank(int newBank) {
        int setBank = (currentBank.intValue() != newBank) ? newBank : 0;
        currentBank.set(setBank);
        countStageHandler.tabPane.getSelectionModel().select(currentBank.intValue());
    }

    public void saveData(Path path) {
        List<String[]> countStrings = fileHandler.buildStrings(username, siteCode, countData);
        try {
            fileHandler.saveFile(countStrings, path);
        } catch(Exception e) {
            System.err.println(e);
        }

        sendAlert(Alert.AlertType.INFORMATION, "Saved Successfully!",
                "Your count file has successfully been saved to the specified folder.",
                countStage);
    }

    public void loadData(File file) {

        try {
            ArrayList<String[]> lines = new ArrayList<>();
            Files.lines(file.toPath()).forEach(value -> {
                lines.add(value.split(","));
            });
            setupIntervals(lines);
            createDataArrays(lines);

            popup.getStage().close();
            currentInterval = countData.get(0);
            updatePropertyIntegers();
            updateTitle();
            countStarted = true;

        } catch (Exception e) {
            System.err.println(e);
            sendAlert(Alert.AlertType.ERROR, "Load Failed!", "Failed to load the selected file.", countStage);
        }
    }

    // Creates blank intervals that will be populated with loaded data
    public void setupIntervals(ArrayList<String[]> lines) {
        boolean isTime = true;
        int count = 0;
        while(isTime) {
            String[] line = lines.get(4 + count); // first line of count data
            try {
                LocalTime time = LocalTime.parse(line[0]);
                count += 1;
                Interval interval = new Interval(time);
                countData.add(interval);
            } catch(Exception e) {
                isTime = false;
            }
        }
    }

    // Create data arrays and populate each Interval object
    public void createDataArrays(ArrayList<String[]> lines) {
        int count = countData.size();
        for (int i = 0; i < count; i++) {
            int[][] data = new int[5][16];
            for(int x = 0; x < 5; x++) {
                int index = (4 + x) + (count * x);
                String[] line = lines.get(index + i);
                for (int j = 1; j <= 16; j++) {
                    data[x][j - 1] = Integer.parseInt(line[j]);
                }
            }
            countData.get(i).setData(data);
        }
    }

    // Basic alert
    public void sendAlert(Alert.AlertType alertType, String header, String context, Stage owner) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(header);
        // alert.setHeaderText(null); for no header
        alert.setContentText(context);
        alert.initOwner(owner);
        alert.show();
    }

    public void setupKeyHandler() {
        EventHandler<KeyEvent> keyPress = keyEvent -> {
            if(countStarted) {
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
            }
        };

        countStage.addEventHandler(KeyEvent.KEY_PRESSED, keyPress);
        videoStage.addEventHandler(KeyEvent.KEY_PRESSED, keyPress);
    }

    public void updateTitle() {
        countStage.setTitle(String.format("TurnCountEnhanced - (%s - %s)", currentInterval.startTime.toString(),currentInterval.endTime.toString()));
    }

    public static void main(String[] args) {
        launch(args);
    }


}
