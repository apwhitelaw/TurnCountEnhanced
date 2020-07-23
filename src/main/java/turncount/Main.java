package turncount;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.vivoxalabs.customstage.CustomStage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    final static String SITECODE_REGEX = "^[0-9]{8,}";
    private final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private SetupStage popupStageHandler;
    private CountStage          countStageHandler;
    private VideoStage          videoStageHandler;
    private MenuStage           menuStageHandler;
    private FileHandler         fileHandler;

    private CustomStage         countStage;
    private Stage               videoStage;

    public Interval                 currentInterval;
    ArrayList<Interval>             countData;
    public SimpleIntegerProperty    currentBank;
    SimpleIntegerProperty[][]       propertyData;

    private boolean     countStarted;
    private long        timerStart;

    private String username     = "";
    private String siteCode     = "";
    private String startTime    = "";

    @Override
    public void start(final Stage primaryStage) throws Exception {
        initialSetup();
    }

    public void initialSetup() {

        countData = new ArrayList<>();
        currentBank = new SimpleIntegerProperty(0);
        propertyData = new SimpleIntegerProperty[5][16];
        fileHandler = new  FileHandler();
        countStarted = false;

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 16; j++) {
                propertyData[i][j] = new SimpleIntegerProperty(0);
            }
        }
        setupStages();
        setupKeyHandler();
        timerStart = System.nanoTime();
    }

    public void setupStages() {
        setupCountStage();
        setupPopup();
        setupVideoStage();
        setupMenuStage();
    }

    public void setupCountStage() {
        countStageHandler = new CountStage();
        countStage = countStageHandler.getStage();

        countStageHandler.getNextIntervalButton().setOnAction(actionEvent -> incrementInterval());
        countStageHandler.getPrevIntervalButton().setOnAction(actionEvent -> decrementInterval());
        countStageHandler.getDelIntervalButton().setOnAction(actionEvent -> deleteInterval());
        countStageHandler.getMenuButton().setOnAction(actionEvent -> displayMenu());
        countStageHandler.setLabelBinds(propertyData);
    }

    public void setupPopup() {
        popupStageHandler = new SetupStage(countStage);
        popupStageHandler.getStartButton().setOnAction(actionEvent -> {
            if(!popupStageHandler.getUsernameField().getText().isBlank() &&
                    !popupStageHandler.getSiteCodeField().getText().isBlank() &&
                    !popupStageHandler.getStartTimeField().getText().isBlank() &&
                    LocalTime.parse("10:00", TIME_FORMAT) != null) {
                setupNewCount();
            }
        });

        popupStageHandler.getOpenFileButton().setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("C:\\Users\\Austin\\Documents\\Java Projects\\TurnCountEnhanced\\src\\main\\resources"));
            fileChooser.setTitle("Open Count File");
            loadCountData(fileChooser.showOpenDialog(popupStageHandler.getStage()));
        });

        popupStageHandler.getGridPane().requestFocus();
    }

    static double INTERVAL_MILLIS = 300000;
    public void setupVideoStage() {
        videoStageHandler = new VideoStage();
        videoStage = videoStageHandler.getStage();
        videoStageHandler.getMediaPlayer().currentTimeProperty().addListener((observableValue, duration, t1) -> {
            if(countStarted) {
                double prev = duration.toMillis() % INTERVAL_MILLIS;
                double next = t1.toMillis() % INTERVAL_MILLIS;
                if (next < prev) {
                    incrementInterval();
                }
            }
        });
    }

    public void setupMenuStage() {
        menuStageHandler = new MenuStage();
        menuStageHandler.createMenu(countStage);
        menuStageHandler.getNextButton().setOnAction(actionEvent -> {
            incrementInterval();
            menuStageHandler.getStage().close();
        });
        menuStageHandler.getDeleteButton().setOnAction(actionEvent -> {
            deleteInterval();
            menuStageHandler.getStage().close();
        });
        menuStageHandler.getSaveButton().setOnAction(actionEvent -> {
            saveData(fileHandler.getDefaultDirectory());
            menuStageHandler.getStage().close();
        });
        menuStageHandler.positionMenu();

        ChangeListener positionChange = (observableValue, oldVal, newVal) -> menuStageHandler.positionMenu();
        countStageHandler.getStage().xProperty().addListener(positionChange);
        countStageHandler.getStage().yProperty().addListener(positionChange);
    }

    public void completeSetup() {
        popupStageHandler.getStage().close();
        currentInterval = countData.get(0);
        updateIntervalText();
        countStarted = true;
        countStageHandler.getBanksTabPane().requestFocus();
        countStageHandler.getListView().setItems(currentInterval.getButtonFeed());
        System.out.println(countStageHandler.getListView().getItems().size());
        for(String s: countStageHandler.getListView().getItems()) {
            System.out.println(s);
        }
        setConsumeSpacebar();

        //saveData(fileHandler.getDefaultDirectory());

//        countStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
//            if(newValue == true) {
//                countStage.setOpacity(1.0);
//            } else {
//                countStage.setOpacity(0.3);
//            }
//        });
    }

    public void setupNewCount() {
        username = popupStageHandler.getUsernameField().getText();
        siteCode = popupStageHandler.getSiteCodeField().getText();
        startTime = popupStageHandler.getStartTimeField().getText();

        if(siteCodeAlreadyExists(siteCode)) {
            sendAlert(Alert.AlertType.WARNING, "Site Code already exists!",
                    "This site code already exists, are you sure you want to continue?", popupStageHandler.getStage());
        } else {
            LocalTime time = LocalTime.parse(startTime, TIME_FORMAT);
            if(time != null) {
                countData.add(new Interval(time.getHour(), time.getMinute()));
                completeSetup();
            }
        }
    }

    public void loadCountData(File file) {
        try {
            ArrayList<String[]> lines = new ArrayList<>();
            Files.lines(file.toPath()).forEach(value -> {
                lines.add(value.split(","));
            });
            setupIntervals(lines);
            createDataArrays(lines);

            updatePropertyIntegers();
            completeSetup();

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

    public boolean siteCodeAlreadyExists(String siteCodeString) {
        // Allow user to change directory ?
        // DirectoryChooser directoryChooser = new DirectoryChooser()

        File directoryFile = new File("C:\\Users\\Austin\\Documents\\Java Projects\\TurnCountEnhanced\\src\\main\\resources");
        String enteredSiteCode = getSiteCodeFromString(siteCodeString);

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
        currentInterval.setButtonFeed(countStageHandler.getListView().getItems());
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
        updateFeed();
        updateIntervalText();
    }

    // Go back one interval (chronologically) if one exists
    public void decrementInterval() {
        currentInterval.setButtonFeed(countStageHandler.getListView().getItems());
        int index = countData.indexOf(currentInterval);
        if(index > 0) {
            currentInterval = countData.get(index - 1);
        }
        updatePropertyIntegers();
        changeBank(0);
        updateFeed();
        updateIntervalText();
    }

    // "clear interval" is a more accurate term
    public void deleteInterval() {
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 16; j++) {
                currentInterval.setDataValue(i, j, 0);
                propertyData[i][j].set(0);
            }
        }
        countStageHandler.getListView().setItems(FXCollections.observableArrayList("","","","","","","",""));
    }

    // Go to a specific interval
    public void displayMenu() {
        menuStageHandler.getStage().show();
    }

    public void updateIntervalText() {
        countStageHandler.getIntervalText().setText(String.format("%s - %s", currentInterval.startTime.toString(),currentInterval.endTime.toString()));
    }

    // Increment count
    public void increment(int index) {
        currentInterval.increment(currentBank.intValue(), index);
        propertyData[currentBank.intValue()][index].set(currentInterval.getDataValue(currentBank.intValue(), index));
        String moveString = String.format("B%d %s", currentBank.intValue(), Interval.Movement.getFromIndex(index).text);
        countStageHandler.addToFeed(moveString);
        changeBank(0);
    }

    public void changeBank(int newBank) {
        int setBank = (currentBank.intValue() != newBank) ? newBank : 0;
        currentBank.set(setBank);
        countStageHandler.getBanksTabPane().getSelectionModel().select(currentBank.intValue());
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

    public void updateFeed() {
        countStageHandler.updateFeed(currentInterval);
    }

    // Saves (siteCode)_count.csv and (siteCode)_statistics.csv
    public void saveData(String string) {
        Path countPath = Path.of(string + "count.csv");
        Path statsPath = Path.of(string + "stats.csv");

        List<String[]> countStrings = fileHandler.buildCountStrings(username, siteCode, countData);
        List<String[]> statsStrings = fileHandler.buildStatisticsStrings(username, siteCode, countData, timerStart);

        try {
            fileHandler.saveFile(countStrings, countPath);
            fileHandler.saveFile(statsStrings, statsPath);

            sendAlert(Alert.AlertType.INFORMATION, "Saved Successfully!",
                    "Your count file has successfully been saved to the specified folder.", countStage);
        } catch(Exception e) {
            System.err.println(e);
        }
    }

    public void unusedSaveFunction() {

        List<String[]> s = fileHandler.buildCountStrings(username, siteCode, countData);
        Path path = Path.of("src\\main\\resources\\count.csv");
        try {
            fileHandler.writeInts(s, path);
        } catch(Exception e) {
            System.err.println(e);
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
                    case DIGIT1: displayMenu();
                }
            }
        };

        countStage.addEventHandler(KeyEvent.KEY_PRESSED, keyPress);
        //videoStage.addEventHandler(KeyEvent.KEY_PRESSED, keyPress);

    }

    public void setConsumeSpacebar() {
        // Consume spacebar event for all stages (unwanted Button presses)
        // videoStage has own implementation
        EventHandler filter = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent k) {
                if (k.getCode() == KeyCode.SPACE){
                    k.consume();
                }
            }
        };
        popupStageHandler.getStage().addEventFilter(KeyEvent.KEY_PRESSED,filter);
    }

    public static void main(String[] args) {
        launch(args);
    }


}
