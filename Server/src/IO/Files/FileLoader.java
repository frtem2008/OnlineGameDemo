package IO.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class FileLoader {


    public FileLoader() {

    }


    private static void read(BufferedReader reader, StringBuilder sb) throws IOException {
        String line = reader.readLine();
        while (line != null) {
            sb.append(line).append(System.lineSeparator());
            line = reader.readLine();
        }
    }


    public static String loadFile(String name) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(name))) {
            read(reader, sb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public static String loadFile(File f) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            read(reader, sb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}

