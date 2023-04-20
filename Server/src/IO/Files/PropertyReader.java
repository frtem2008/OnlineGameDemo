package IO.Files;

import Exceptions.IllegalConfigFileFormatException;
import Exceptions.PropertyNotFoundException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class PropertyReader {
    private final File configFolder = new File("./Config/");
    private File configFile;
    private String configFileData;

    //initialization
    public PropertyReader(String configFileName) {
        configFile = new File(configFolder, configFileName);
        if (!configFolder.exists())
            configFolder.mkdirs();
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        configFileData = readFile(configFile);
    }

    //reading a file
    private static String readFile(File file) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader read = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = read.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    //getting random string from parsing a label
    public String getRandomFromParse(String file, String parse) throws IllegalConfigFileFormatException {
        String[] split = parse(file, parse).split("\n");
        if (Math.random() <= 0.5)
            Collections.shuffle(Arrays.asList(split));
        Collections.shuffle(Arrays.asList(split));
        if (Math.random() <= 0.5) //for better randomness
            Collections.shuffle(Arrays.asList(split));
        Collections.shuffle(Arrays.asList(split));
        return split[0];
    }

    //parsing from <label> to </label> in file
    public String parse(String file, String label) throws IllegalConfigFileFormatException {
        StringBuilder res = new StringBuilder();
        label = label.trim();

        if (!file.contains("<" + label + ">") ||
                !file.contains("</" + label + ">") ||
                file.indexOf("<" + label + ">") > file.indexOf("</" + label + ">")
        ) {
            throw new IllegalConfigFileFormatException("File \"" + configFolder.getAbsolutePath() + "/config.dat\" has wrong label format");
        } else {
            res.append(file.substring(
                    file.indexOf("<" + label + ">") + ("<" + label + ">").length(),
                    file.indexOf("</" + label + ">")
            ).trim());
        }
        /* Trim each read string */
        String[] split = res.toString().split("\n");
        res = new StringBuilder();
        for (String s : split)
            res.append(s.trim()).append("\n"); //trimming each string in result

        return res.toString().trim();
    }

    //getting cached version of a config file
    public String getConfigFile() {
        return configFileData;
    }

    //getting config file updated every time
    public String updateAndGetConfigFile() {
        configFileData = readFile(new File(configFolder + "/config.dat"));
        return configFileData;
    }

    //getting property=propertyValue in file
    public String getProperty(String file, String property) {
        if (!file.contains(property + "="))
            throw new RuntimeException("Property " + property + " not found");
        String res = file.split(property + "=")[1];
        res = res.substring(0, res.indexOf("\n")).trim();
        return res;
    }
}
