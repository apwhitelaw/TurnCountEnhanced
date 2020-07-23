package turncount;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalTime;

public class Interval {

    LocalTime               startTime;
    LocalTime               endTime;
    private int[][]         data;
    ObservableList<String>  buttonFeed;

    public enum Movement {
        SBR(0, "SB Right"),
        SBT(1,"SB Thru"),
        SBL(2,"SB Left"),
        SBP(3,"SB Ped"),
        WBR(4,"WB Right"),
        WBT(5,"WB Thru"),
        WBL(6,"WB Left"),
        WBP(7, "WB Ped"),
        NBR(8,"NB Right"),
        NBT(9,"NB Thru"),
        NBL(10,"NB Left"),
        NBP(11,"NB Ped"),
        EBR(12,"EB Right"),
        EBT(13,"EB Thru"),
        EBL(14,"EB Left"),
        EBP(15,"EB Ped");

        public final int index;
        public final String text;

        private Movement(int index, String text) {
            this.index = index;
            this.text = text;
        }

        public static Movement getFromIndex(int index) {
            for(Movement m : values()) {
                if(index == m.index) {
                    return m;
                }
            }
            return null;
        }
    }

    public Interval(LocalTime time, int[][] data) {
        this(time);
        this.data = data;
        this.buttonFeed = FXCollections.observableArrayList("","","","","","","","");
    }

    public Interval(int hour, int minute) {
        this(LocalTime.of(hour, minute));
    }

    public Interval(LocalTime time) {
        startTime = time;
        data = new int[5][16];
        endTime = startTime.plusMinutes(5);
        this.buttonFeed = FXCollections.observableArrayList("","","","","","","","");
    }

    public void increment(int bank, int index) {
        data[bank][index] += 1;;
    }

    public ObservableList<String> getButtonFeed() {
        return buttonFeed;
    }

    public void setButtonFeed(ObservableList<String> buttonFeed) {
        this.buttonFeed = buttonFeed;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public int[][] getData() {
        return data;
    }

    public void setData(int[][] data) {
        this.data = data;
    }

    public int getDataValue(int bank, int index) {
        return this.data[bank][index];
    }

    public void setDataValue(int bank, int index, int value) {
        this.data[bank][index] = value;
    }

}
