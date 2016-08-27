import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ErrorFile {
    static final String filePath = "errorlog.txt";
    static final String version = "1.0.2";

    ErrorFile(String errorMessage, String errorType, double[] solution, ArrayList<String> misc) throws IOException {
        if (!this.checkDuplicates(errorMessage)) {
            FileWriter fw = new FileWriter("errorlog.txt", true);
            fw.write(String.valueOf(errorType) + ": " + errorMessage + "\r\n  Output: ");
            int i = 0;
            while (i < solution.length) {
                fw.write(String.valueOf(solution[i]) + " ");
                ++i;
            }
            fw.write("\r\n  Version: 1.0.2\r\n\r\n");
            i = 0;
            while (i < misc.size()) {
                fw.write(String.valueOf(misc.get(i)) + "\r\n");
                ++i;
            }
            fw.write("------------------------------------------------\r\n\r\n");
            fw.close();
        }
    }

    ErrorFile(String errorMessage, String errorType) throws IOException {
        if (!this.checkDuplicates(errorMessage)) {
            FileWriter fw = new FileWriter("errorlog.txt", true);
            fw.write("Error: " + errorMessage + "\r\n" + "  Version: " + "1.0.2" + "\r\n\r\n");
            fw.close();
        }
    }

    private boolean checkDuplicates(String errorMsg) throws FileNotFoundException {
        boolean duplicate = false;
        File errorFile = new File("errorlog.txt");
        if (errorFile.exists()) {
            Scanner read = new Scanner(errorFile);
            while (read.hasNextLine()) {
                if (!read.nextLine().equals(errorMsg)) continue;
                duplicate = true;
                break;
            }
            read.close();
        }
        return duplicate;
    }
}