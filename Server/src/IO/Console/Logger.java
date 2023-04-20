package IO.Console;



import java.util.HashSet;
import java.util.Set;


record PrintColor(String name, OutputColor color) {
    
    @Override
    public String toString() {
        return color.toString();
    }
}


public class Logger {
    
    private static Logger instance = null;
    

    private final Set<PrintColor> colors;
    
    private boolean useColoredText;
    
    private OutputColor current;

    
    private Logger() {
        this.colors = new HashSet<>();
        this.current = OutputColor.RESET;
        addPrintColor("Default", OutputColor.RESET);
    }

    
    public static synchronized Logger getInstance() {
        if (instance == null)
            instance = new Logger();
        return instance;
    }


    
    public void enableColoredText() {
        useColoredText = true;
    }

    
    public void disableColoredText() {
        useColoredText = false;
    }

    
    public void setOutputColor(OutputColor color) {
        current = color;
    }

    
    public void setOutputColor(String name) {
        current = getColorByName(name).color();
    }

    
    public void setDefaultOutputColor() {
        current = getColorByName("Default").color();
    }


    
    public void addPrintColor(String name, OutputColor color) {
        colors.add(new PrintColor(name, color));
    }

    
    private String getResetString() {
        return OutputColor.RESET.toString();
    }

    
    private PrintColor getColorByName(String name) {
        for (PrintColor color : colors)
            if (color.name().equalsIgnoreCase(name))
                return color;
        return new PrintColor("default", OutputColor.RESET);
    }

    
    public void print(String toPrint) {
        print(toPrint, current);
    }

    
    public void print(String toPrint, String colorName) {
        print(toPrint, getColorByName(colorName).color());
    }

    
    public void println(String toPrint, String colorName) {
        System.out.println();
        print(toPrint, getColorByName(colorName).color());
    }

    
    public void print(String toPrint, OutputColor color) {
        toPrint = "[" + Thread.currentThread().getName() + "]" + toPrint;
        if (useColoredText)
            System.out.println(color.toString() + toPrint + getResetString());
        else
            System.out.println(toPrint);
    }
}
