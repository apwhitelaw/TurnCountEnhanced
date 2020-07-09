package turncount;

import java.time.LocalTime;

public class Interval {

    LocalTime startTime;
    LocalTime endTime;
    private int[][] data = new int[5][16];

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

    public Interval(int hour, int minute) {
        startTime = LocalTime.of(hour, minute);
        if(startTime.getMinute() == 55) {
            endTime = startTime.plusHours(1).minusMinutes(55);
        } else {
            endTime = startTime.plusMinutes(5);
        }

    }

    public void increment(int bank, int index) {
        data[bank][index] += 1;;
    }

    public int[][] getData() {
        return data;
    }

    public int getDataValue(int bank, int index) {
        return this.data[bank][index];
    }

    public void setDataValue(int bank, int index, int value) {
        this.data[bank][index] = value;
    }

}
