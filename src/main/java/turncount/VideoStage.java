package turncount;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lk.vivoxalabs.customstage.CustomStage;
import lk.vivoxalabs.customstage.CustomStageBuilder;
import lk.vivoxalabs.customstage.tools.HorizontalPos;

import java.io.File;
import java.net.URI;
import java.text.DecimalFormat;

public class VideoStage {

    final double INTERVAL_TIME_MS = 1000; //300000;
    final static double DEFAULT_SPEED_CHANGE = 0.5;
    final static int DEFAULT_SEEK_TIME = 3;

    private final Stage stage;
    CustomStage customStage;
    private Scene scene;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private VBox vBox;
    BorderPane bp;
    StackPane stackPane;
    private MenuBar menuBar;
    Label titleLabel;
    Label timeText;
    Label rateText;

    private Double lastChange = 0.0;
    double initX = 0;
    double initY = 0;

    public VideoStage() {
        this.stage = new Stage();
        this.mediaView = new MediaView();

        setupVideoStage();
    }

    // Creates video interface
    public void setupVideoStage() {
        MediaView mediaView = new MediaView();
        menuBar = setupMenuBar();

//        vBox = new VBox(menuBar, mediaView);
//        VBox.setVgrow(mediaView, Priority.ALWAYS);
//        vBox.setOnMousePressed(mouseEvent -> pause());
        timeText = new Label("Hello World!");
        stackPane = new StackPane(mediaView, timeText);
        bp = new BorderPane(stackPane);
        bp.setTop(menuBar);
        //scene = new Scene(bp, 100, 100);
        File f = new File("C:\\Users\\Austin\\Downloads\\trafficam.mp4"); // for testing
        setupMedia(f);
//        stage.setTitle("Playing Video");
//        stage.setX(50);
//        stage.setY(50);
//        //stage.initStyle(StageStyle.UNDECORATED);
//        stage.show();
//        setupKeyHandler();

        // spacebar action does not work with CustomStage
        try {
            customStage = new CustomStageBuilder()
                    .setWindowTitle("Playing Video...", HorizontalPos.RIGHT, HorizontalPos.LEFT)
                    .setTitleColor("rgb(146,199,163)")
                    .setWindowColor("rgb(33,90,109)")
                    .setDimensions(300,355,1920,1080).build();;
            //customStage.initStyle(Stage);
        } catch(Exception e) {
            System.out.println(e);
        }
        customStage.changeScene(bp);
        customStage.show();
        setupKeyHandler();
    }

    public void setupElapsedTimeListener(boolean countStarted) {
        MediaPlayer mp = mediaPlayer;
        mp.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
            if(countStarted) {
                System.out.println(newDuration);
                double nextInterval = lastChange + INTERVAL_TIME_MS;
                if (oldDuration.toMillis() < nextInterval && newDuration.toMillis() > nextInterval) {
                    // incrementInterval();  cant use it here...alternative?
                    lastChange += INTERVAL_TIME_MS;
                }
            }
        });
    }

    // Create Media object from File
    private Media loadMedia(File file) {
        URI uri = file.toURI();
        return new Media(uri.toString());
    }

    // Setup MediaPlayer from Media
    private void setupMedia(File file) {
        Media media = loadMedia(file);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        timeText = new Label("Time");
        timeText.setFont(new Font(20));
        timeText.setBackground(Helper.createBackground(Color.RED));
        rateText = new Label("Rate");
        rateText.setFont(new Font(20));
        rateText.setBackground(Helper.createBackground(Color.RED));
        VBox videoInfo = new VBox(timeText, rateText);
        stackPane = new StackPane(mediaView, videoInfo);
        stackPane.setOnMouseClicked(mouseEvent -> pause());
        bp = new BorderPane(stackPane);
        bp.setTop(menuBar);
//        mediaView.fitWidthProperty().bind(bp.widthProperty());
//        mediaView.fitHeightProperty().bind(bp.heightProperty());

        //mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnReady(() -> {
//            Scene newScene = new Scene(bp, media.getWidth(), media.getHeight());
//            newScene.setFill(Color.DARKSLATEGRAY);
//            stage.setScene(newScene);
//            stage.widthProperty().addListener((observableValue, number, t1) -> mediaView.setFitWidth(t1.doubleValue()));
//            stage.heightProperty().addListener((observableValue, number, t1) -> mediaView.setFitHeight(t1.doubleValue()));

            customStage.changeScene(bp);
            customStage.setWidth(media.getWidth());
            customStage.setHeight(media.getHeight() + 30 + 25); // stage window (30) menubar (25)
//            mediaView.fitWidthProperty().bind(customStage.widthProperty());
//            mediaView.fitHeightProperty().bind(customStage.heightProperty());
            customStage.widthProperty().addListener((observableValue, number, t1) -> mediaView.setFitWidth(t1.doubleValue()));
            customStage.heightProperty().addListener((observableValue, number, t1) -> mediaView.setFitHeight(t1.doubleValue() + 55));
            mediaPlayer.currentTimeProperty().addListener((observableValue, oldVal, newVal) -> {
                double milli = newVal.toMillis();
                double hours = Math.floor(milli / 3600000);
                double minutes = Math.floor((milli % 3600000) / 60000);
                double seconds = ((milli % 3600000) % 60000) / 1000;
                DecimalFormat df = new DecimalFormat("00.##");
                String secondsString = df.format(seconds);
                timeText.setText(String.valueOf(String.format("%02.0f:%02.0f:%s", hours, minutes, secondsString)));
            });

            mediaPlayer.rateProperty().addListener((observableValue, oldVal, newVal) -> {
                if(newVal.doubleValue() <= 8.0) {
                    rateText.setText(String.valueOf(newVal.doubleValue()));
                }
            });

            menuBar.setOnMousePressed(mouseEvent -> {
                initX = mouseEvent.getSceneX();
                initY = mouseEvent.getSceneY();
            });
            menuBar.setOnMouseDragged(mouseEvent -> {
                stage.setX(mouseEvent.getScreenX() - initX);
                stage.setY(mouseEvent.getScreenY() - initY);
            });
        });
    }

    private MenuBar setupMenuBar() {
        MenuItem open = new MenuItem("Open Video");
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        open.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("C:\\Users\\Austin\\Downloads"));
            fileChooser.setTitle("Open Video");
            setupMedia(fileChooser.showOpenDialog(stage));
        });
        MenuItem quit = new MenuItem("Quit");
        quit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(open, quit);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu);

        return menuBar;
    }

    // Keyboard media controls
    public void setupKeyHandler() {
        // Consume spacebar press to eliminate issue with CustomStage
        customStage.addEventFilter(KeyEvent.KEY_PRESSED, k -> {
            if (k.getCode() == KeyCode.SPACE){
                k.consume();
                pause();
            }
        });

        customStage.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            switch(keyEvent.getCode()) {
                case LEFT: seek(-DEFAULT_SEEK_TIME); break;
                case RIGHT: seek(DEFAULT_SEEK_TIME); break;
                case SPACE: pause(); break;
                case OPEN_BRACKET: changeSpeed(-DEFAULT_SPEED_CHANGE); break;
                case CLOSE_BRACKET: changeSpeed(DEFAULT_SPEED_CHANGE); break;
            }
        });
    }

    // Seek by amount of seconds. Pass negative to seek backward
    public void seek(int seconds) {
        if(mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaPlayer.play();
            double time = mediaPlayer.getCurrentTime().toMillis();
            Duration dur = new Duration(time + (seconds * 1000));
            mediaPlayer.seek(dur);
            mediaPlayer.pause();
        }
        if(mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            double time = mediaPlayer.getCurrentTime().toMillis();
            Duration dur = new Duration(time + (seconds * 1000));
            mediaPlayer.seek(dur);
        }
    }

    // Toggles pause
    public void pause() {
        MediaPlayer.Status status = mediaPlayer.getStatus();
        if(status == MediaPlayer.Status.PLAYING)
            mediaPlayer.pause();
        else
            mediaPlayer.play();
    }

    // Changes playback speed
    public void changeSpeed(double speedChange) {
        mediaPlayer.setRate(mediaPlayer.getRate() + speedChange);
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Stage getStage() {
        return stage;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public double getINTERVAL_TIME_MS() {
        return INTERVAL_TIME_MS;
    }

    public Double getLastChange() {
        return lastChange;
    }

    public void setLastChange(Double lastChange) {
        this.lastChange = lastChange;
    }
}
