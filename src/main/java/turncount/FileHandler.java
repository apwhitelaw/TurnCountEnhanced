package turncount;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static final String DEFAULT_DIRECTORY = "src\\main\\resources\\";

    // strings to output to _count.csv
    public List<String[]> buildCountStrings(String username, String siteCode, ArrayList<Interval> countData) {
        List<String[]> outputStrings = new ArrayList<>();
        outputStrings.add(new String[]{"Job number", siteCode});
        outputStrings.add(new String[]{"Employee ID", username, "Interval size", "5"});
        outputStrings.add(new String[]{"Time", "SB Right", "SB Thru", "SB Left", "SB Ped", "WB Right",
                                        "WB Thru", "WB Left", "WB Ped", "NB Right", "NB Thru", "NB Left",
                                        "NB Ped", "EB Right", "EB Thru" , "EB Left", "EB Ped"});

        List<List<String[]>> bankList = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            bankList.add(new ArrayList<>());
            bankList.get(i).add(new String[]{"Bank number", String.valueOf(i)});
        }

        for(Interval interval : countData) {
            int[][] data = interval.getData();
            for(int j = 0; j < 5; j++) {

                int[] bank = data[j];
                String[] singleBank = new String[17];
                singleBank[0] = interval.startTime.toString();
                for(int i = 0; i < bank.length; i++) {
                    singleBank[i+1] = String.valueOf(bank[i]);
                }
                bankList.get(j).add(singleBank);
            }
        }
        for(List<String[]> bank : bankList) {
            for(String[] line : bank) {
                outputStrings.add(line);
            }
        }
        return outputStrings;
    }

    public List<String[]> buildStatisticsStrings(String username, String siteCode, ArrayList<Interval> countData, long timerStart) {

        List<String[]> outputStrings = new ArrayList<>();
        outputStrings.add(new String[]{"Job number", siteCode});
        outputStrings.add(new String[]{""});
        outputStrings.add(new String[]{"Employee", username});
        outputStrings.add(new String[]{""});
        int secondsUsed = getSecondsUsed(timerStart);
        outputStrings.add(new String[]{"Time Used (seconds)", String.valueOf(secondsUsed)});
        outputStrings.add(new String[]{""});
        int totalCars = calcTotalCars(countData);
        outputStrings.add(new String[]{"Total number of cars", String.valueOf(totalCars)});
        outputStrings.add(new String[]{""});
        int hour = (secondsUsed / 60) / 60;
        int minute = (secondsUsed / 60) % 60;
        int second = secondsUsed % 60;
        LocalTime timeUsed = LocalTime.of(hour, minute, second);
        outputStrings.add(new String[]{"Time Used (hh:mm:ss)", timeUsed.toString()});
        outputStrings.add(new String[]{""});
        double countRealTimeHours = hour + (minute/60);
        double carsPerHour = totalCars / countRealTimeHours;;
        if(Double.isNaN(carsPerHour)) {
            carsPerHour = 0;
        }
        outputStrings.add(new String[]{"Cars per hour", String.valueOf(carsPerHour)});
        outputStrings.add(new String[]{""});
        LocalTime firstIntervalStartTime = countData.get(0).getStartTime();
        LocalTime lastIntervalStartTime = countData.get(countData.size() - 1).getStartTime();
        outputStrings.add(new String[]{"First interval", firstIntervalStartTime.toString()});
        outputStrings.add(new String[]{""});
        outputStrings.add(new String[]{"Last interval", lastIntervalStartTime.toString()});
        outputStrings.add(new String[]{""});
        int[] counts = calcMovementTotals();
        String[] countString = new String[17];
        countString[0] = "Counts";
        for(int i=0;i<countString.length-1;i++) { countString[i+1] = String.valueOf(counts[i]); }
        outputStrings.add(countString);

        return outputStrings;
    }

    private int getSecondsUsed(long timerStart) {
        long currentTime = System.nanoTime();
        long length = currentTime - timerStart;
        return (int) (length / 1000000000);
    }

    private int calcTotalCars(ArrayList<Interval> countData) {
        int total = 0;
        for(Interval interval : countData) {
            int[][] data = interval.getData();
            for(int i = 0; i < 5; i++) {
                for(int j = 0; j < 16; j++) {
                    total += data[i][j];
                }
            }
        }
        return total;
    }

    private int[] calcMovementTotals() {
        return new int[16];
    }

    public boolean saveFile(List<String[]> stringArray, Path path) throws Exception {
        return writeAll(stringArray, path);
    }

    public boolean writeAll(List<String[]> stringArray, Path path) throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter(path.toString(), false),
                CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
        writer.writeAll(stringArray);

        writer.close();
        System.out.println("Saved File");
        return true;
    }

    public boolean writeInts(List<String[]> stringArray, Path path) throws Exception {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter("src\\main\\resources\\count.csv", false),
                    CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            writer.writeAll(stringArray);

            writer.close();
            System.out.println("saved");
            return true;
        } catch(Exception e) {

        }
        return false;
    }

    public boolean writeOneByOne(List<String[]> stringArray, Path path) throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter(path.toString()));
        for(String[] array : stringArray) {
            writer.writeNext(array);
        }

        writer.close();
        System.out.println("saved");
        return true;
    }

    public List<String[]> readEntireFile(String pathString) throws Exception {
        System.out.println(pathString);
        Reader reader = Files.newBufferedReader(Paths.get(
                ClassLoader.getSystemResource(pathString).toURI()));
        return readAll(reader);
    }

    public List<String[]> readEntireFile(File file) throws Exception {
        Reader reader = Files.newBufferedReader(file.toPath());
        return readAll(reader);
    }

    public List<String[]> readAll(Reader reader) throws Exception {
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> list = new ArrayList<>();
        try {
            list = csvReader.readAll();
            reader.close();
            csvReader.close();
        } catch(Exception e) {
            //System.err.println(e);
            e.printStackTrace();
        }

        return list;
    }

    public static String getDefaultDirectory() {
        return DEFAULT_DIRECTORY;
    }
}
