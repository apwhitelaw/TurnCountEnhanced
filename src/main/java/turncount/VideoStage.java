package turncount;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URI;

public class VideoStage {

    final double INTERVAL_TIME_MS = 1000; //300000;
    final static double DEFAULT_SPEED_CHANGE = 0.5;
    final static int DEFAULT_SEEK_TIME = 3;

    private final Stage stage;
    private Scene scene;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private VBox vBox;
    private MenuBar menuBar;

    private Double lastChange = 0.0;

    public VideoStage() {
        this.stage = new Stage();
        this.mediaView = new MediaView();

        setupVideoStage();
    }

    // Creates video interface
    public void setupVideoStage() {
        MediaView mediaView = new MediaView();
        menuBar = setupMenuBar();
        vBox = new VBox(menuBar, mediaView);
        vBox.setOnMousePressed(mouseEvent -> pause());
        scene = new Scene(vBox, 100, 100);
        File f = new File("C:\\Users\\Austin\\Downloads\\small.mp4"); // for testing
        setupMedia(f);
        stage.setTitle("Playing Video");
        stage.setX(50);
        stage.setY(50);
        stage.show();
        setupKeyHandler();


//        videoStage.widthProperty().addListener((obs, oldVal, newVal) -> {
//            if(newVal.doubleValue() > mediaView.getFitWidth()) {
//                videoStage.setWidth(oldVal.doubleValue());
//            }
//        });
//        videoStage.heightProperty().addListener((obs, oldVal, newVal) -> {
//            if(newVal.doubleValue() > mediaView.getFitHeight()) {
//                videoStage.setHeight(oldVal.doubleValue());
//            }
//        });

        DoubleProperty mediaViewWidth = mediaView.fitWidthProperty();
        DoubleProperty mediaViewHeight = mediaView.fitHeightProperty();
        mediaViewWidth.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        mediaViewHeight.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(true);
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
        vBox = new VBox(menuBar, mediaView);
        vBox.setOnMousePressed(mouseEvent -> pause());
        //mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnReady(() -> {
            Scene newScene = new Scene(vBox, media.getWidth(), media.getHeight() + menuBar.getHeight());
            newScene.setFill(Color.DARKSLATEGRAY);
            stage.setScene(newScene);
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
        stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
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
        }
        double time = mediaPlayer.getCurrentTime().toMillis();
        Duration dur = new Duration(time + (seconds * 1000));
        mediaPlayer.seek(dur);
        mediaPlayer.pause();
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
