package de.tum.bgu.msm.properties;

import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class PropertiesUtil {


    private PropertiesUtil(){
    }

    private static String format = "%-60s%s";
    private static String formatDefault = "%-80s%s";
    private static Map<Integer, String> propertiesInUse = new HashMap<>();
    private static Logger logger = Logger.getLogger(PropertiesUtil.class);


    public static int getIntProperty(ResourceBundle bundle, String key, int defaultValue) {
        try {
            bundle.getString(key);
            return getIntProperty(bundle, key);
        } catch (MissingResourceException e) {
            logger.info("Using default value of " + defaultValue + " for property " + key);
            printOutProperty(key, defaultValue, true);
            return defaultValue;
        }
    }

    public static int getIntProperty(ResourceBundle bundle, String key) {
        try {
            Integer returnValue = Integer.parseInt(bundle.getString(key).replace(" ",""));
            printOutProperty(key, returnValue, false);
            return returnValue;
        } catch (NumberFormatException e){
            throw new RuntimeException("Cannot convert property " + key + " to an integer");
        } catch (MissingResourceException e){
            throw new RuntimeException("The property " + key + " is not defined and has no default value");
        }
    }

    public static double getDoubleProperty(ResourceBundle bundle, String key, double defaultValue) {
        try {
            bundle.getString(key);
            return getDoubleProperty(bundle, key);
        } catch (MissingResourceException e) {
            logger.info("Using default value of " + defaultValue + " for property " + key);
            printOutProperty(key, defaultValue, true);
            return defaultValue;
        }
    }

    public static double getDoubleProperty(ResourceBundle bundle, String key) {
        try {
            Double returnValue = Double.parseDouble(bundle.getString(key));
            printOutProperty(key, returnValue, false);
            return returnValue;
        } catch (NumberFormatException e){
            throw new RuntimeException("Cannot convert property " + key + " to a double");
        } catch (MissingResourceException e){
            throw new RuntimeException("The property " + key + " is not defined and has no default value");
        }
    }

    public static boolean getBooleanProperty(ResourceBundle bundle, String key, boolean defaultValue) {
        try {
            bundle.getString(key);
            return getBooleanProperty(bundle, key);
        } catch (MissingResourceException e) {
            logger.info("Using default value of " + defaultValue + " for property " + key);
            printOutProperty(key, defaultValue, true);
            return defaultValue;
        }
    }

    public static boolean getBooleanProperty(ResourceBundle bundle, String key) {
        try {
            Boolean returnValue = Boolean.parseBoolean(bundle.getString(key));
            printOutProperty(key, returnValue, false);
            return returnValue;
        } catch (NumberFormatException e){
            throw new RuntimeException("Cannot convert property " + key + " to a boolean");
        } catch (MissingResourceException e){
            throw new RuntimeException("The property " + key + " is not defined and has no default value");
        }
    }

    public static String getStringProperty(ResourceBundle bundle, String key, String defaultValue) {
        try {
            bundle.getString(key);
            return getStringProperty(bundle, key);
        } catch (MissingResourceException e) {
            logger.info("Using default value of " + defaultValue + " for property " + key);
            printOutProperty(key, defaultValue, true);
            return defaultValue;
        }
    }

    public static String getStringProperty(ResourceBundle bundle, String key) {
        try {
            printOutProperty(key, bundle.getString(key), false);
            return bundle.getString(key);
        } catch (MissingResourceException e){
            throw new RuntimeException("The property " + key + " is not defined and has no default value");
        }

}


    public static int[] getIntPropertyArray(ResourceBundle bundle, String key){
        try{
            String propertyAsString = bundle.getString(key);
            int[] returnValue = Arrays.stream(propertyAsString.replace(" ","").split(",")).mapToInt(Integer::parseInt).toArray();
            printOutArrayProperty(key, Arrays.stream(returnValue).boxed().toArray(), false);
            return returnValue;
        } catch (MissingResourceException e){
            throw new RuntimeException("The property " + key + " is not defined and has no default value");
        } catch (NumberFormatException e){
            throw new RuntimeException("Cannot convert the property " + key + " to an int[]");
        }

    }

    public static int[] getIntPropertyArray(ResourceBundle bundle, String key, int[] defaultValue) {
        try {
            bundle.getString(key);
            return getIntPropertyArray(bundle, key);
        } catch (MissingResourceException e) {
            logger.info("Using default value of " + Arrays.toString(defaultValue) + " for property " + key);
            printOutArrayProperty(key, Arrays.stream(defaultValue).boxed().toArray(), true);
            return defaultValue;
        }
    }

    public static double[] getDoublePropertyArray(ResourceBundle bundle, String key){
        try{
            String propertyAsString = bundle.getString(key);
            double[] returnValue = Arrays.stream(propertyAsString.split(",")).mapToDouble(Double::parseDouble).toArray();
            printOutArrayProperty(key, Arrays.stream(returnValue).boxed().toArray(), false);
            return returnValue;
        } catch (MissingResourceException e){
            throw new RuntimeException("The property " + key + " is not defined and has no default value");
        } catch (NumberFormatException e){
            throw new RuntimeException("Cannot convert the property " + key + " to an int[]");
        }

    }

    public static double[] getDoublePropertyArray(ResourceBundle bundle, String key, double[] defaultValue) {
        try {
            bundle.getString(key);
            return getDoublePropertyArray(bundle, key);
        } catch (MissingResourceException e) {
            logger.info("Using default value of " + Arrays.toString(defaultValue) + " for property " + key);
            printOutArrayProperty(key, Arrays.stream(defaultValue).boxed().toArray(), true);
            return defaultValue;
        }
    }

    public static String[] getStringPropertyArray(ResourceBundle bundle, String key){
        try{
            String propertyAsString = bundle.getString(key);
            String[] returnValue = propertyAsString.split(",");
            printOutArrayProperty(key, returnValue, false);
            return returnValue;
        } catch (MissingResourceException e){
            throw new RuntimeException("The property " + key + " is not defined and has no default value");
        }

    }

    public static String[] getStringPropertyArray(ResourceBundle bundle, String key, String[] defaultValue) {
        try {
            bundle.getString(key);
            return getStringPropertyArray(bundle, key);
        } catch (MissingResourceException e) {
            logger.info("Using default value of " + Arrays.toString(defaultValue) + " for property " + key);
            printOutArrayProperty(key, defaultValue, true);
            return defaultValue;
        }


    }

    private static <E> void printOutProperty(String key, E property, boolean isDefault) {
        if (isDefault){
            printOutLine(String.format(formatDefault, key, " = " + property.toString()));
        } else {
            printOutLine(String.format(format, key, " = " + property.toString()));
        }

    }

    private static <E> void  printOutArrayProperty(String key, E[] property, boolean isDefault) {
        StringBuilder value = new StringBuilder();
        for(int i =0; i< property.length; i++){
            value.append(property[i].toString());
            if (i != property.length -1){
                value.append(",");
            }
        }
        if (isDefault){
            printOutLine(String.format(formatDefault, key, " = " + value.toString()));
        } else {
            printOutLine(String.format(format, key, " = " + value.toString()));
        }


    }

    private static void printOutLine(String line){
        propertiesInUse.put(propertiesInUse.size()+1, line);

    }

    public static void printOutModuleTitle(String line){
        printOutLine("");
        printOutLine("#" + line);

    }

    public static void printOutPropertiesOfThisRun(String folder){
        try {
            PrintWriter pw = new PrintWriter(new File(folder + "/siloProperties.properties"));
            pw.println("#Properties for SILO");
            pw.println(String.format("%-60s %-20s %s","#Legend", "set by user", "default"));
            propertiesInUse.values().forEach(row -> pw.println(row));
            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }





}