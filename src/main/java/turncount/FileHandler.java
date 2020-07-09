package turncount;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    Path filePath = Path.of("C:\\Users\\Austin\\Documents\\Java Projects\\TurnCountEnhanced\\src\\15248303 Austin");

    public void saveFile() {
        //CSVWriter writer = new CSVWriter(new FileWriter(filePath.toString()));
    }

    public List<String[]> readAll(Reader reader) throws Exception {
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> list = new ArrayList<>();
        try {
            list = csvReader.readAll();
            reader.close();
            csvReader.close();
        } catch(Exception e) {
            System.err.println(e);
        }

        return list;
    }

}
