package stringFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

// Project J1 for CS 3114 Summer I 2017
//
// Programmer:    Dong Gyu Lee
// OS:            Windows 10 Education
// System:        i5-4300U, 8 GB Memory
// Compiler:      Eclipse Neon.3 Release (4.6.3)
// Last modified: May 30, 2017
//
// Purpose
// StringFormat class is used specifically for formatting the output results
//
public class StringFormat {
    
    //////////////////////////////////////////////////////////////// addSpaces(String string, int number)
    // Adds number of space characters in a string used for formatting purposes
    //
    // Parameters:
    //          string given string
    //          number number of spaces added
    //
    // Pre:       
    //          None
    //
    // Post:    
    //          String output with added spaces in front
    //          
    // Returns:   
    //          New string with spaces of a given number of spaces in front and a given string
    //
    // Called by: 
    //          prQuadTree
    //              toString()
    //
    public static String addSpaces(String string, int number) {
        StringBuilder builder = new StringBuilder();
        
        for (int i = 0; i < number; i++) {
            builder.append(" ");
        }
        
        builder.append(string);
        
        return builder.toString();
    }
   
    //////////////////////////////////////////////////////////////// convertDirection(String direction)
    // Converts the symbolic direction form 'E, W, S, N' to full-word form:
    // East, West, South, North. 
    //
    // Parameters:
    //          direction Possible directions are E, W, S, N
    //                    each letter corresponds to East, West, South, North, respectively.
    //
    // Pre:       
    //          None
    //
    // Post:   
    //          Creates a string converting from directions in short-form to full-form
    //
    // Returns:   
    //          String direction in full-word form
    //
    // Called by: 
    //          convertDMStoFriendly(String DMS)
    //
    private static String convertDirection(String direction) {
        String result = ""; 
        
        switch (direction) {
            case "E":
                result = "East";
                break;
            case "W":
                result = "West";
                break;
            case "S":
                result = "South";
                break;
            case "N":
                result = "North";
                break;
            default: 
                result = "Not a valid direction";
        }
        
        return result;
    }
    
    ////////////////////////////////////////////////////////////////convertDMStoFriendly(String DMS)
    // Takes the Primary Longitude or Latitude of DMS format and converts it to human-friendly format
    //
    // Parameters:
    //          DMS DMS formatted location coordinate
    //
    // Pre:       
    //          String DMS_LONG must be in the correct form straight from GIS data
    //          i.e. 1090303W
    //          or it Unknown 
    //          Otherwise returns an error
    //
    // Post:   
    //          Creates a formatted string with a given DMS to human friendly version string data
    //          If DMS is given as Unknown, return Unknown
    //
    // Returns:   
    //          Converted DMS format
    //
    // Called by: 
    //          Commands
    //              execute_what_is_at()
    //              execute_what_is_in()
    //              execute_what_is_in_long()
    //              execute_what_is_in_filter()
    //
    public static String convertDMStoFriendly(String DMS) {
        if (!DMS.equals("Unknown")) {
            
            int DMSLength = DMS.length();
            
            String direction = convertDirection(DMS.substring(DMSLength - 1, DMSLength));
            
            int sec       = Integer.valueOf(DMS.substring(DMSLength - 3, DMSLength - 1));
            int min       = Integer.valueOf(DMS.substring(DMSLength - 5, DMSLength - 3));
            int degree    = Integer.valueOf(DMS.substring(0, DMSLength - 5));
            
            return degree + "d " + min + "m " + sec + "s " + direction;
        }
        return DMS;
    }
    
    //////////////////////////////////////////////////////////////// endString()
    // String that is appended at the end of every command
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:   
    //          At the end of command log, endString is printed
    //
    // Returns:   
    //          Converted DMS format
    //
    // Called by: 
    //          Executor
    //              execute()
    //          
    //          Controller
    //              initialComments()
    //
    public static String endString() {
        return "--------------------------------------------------------------------------------\n";
    }
    
    //////////////////////////////////////////////////////////////// getTime()
    // Gets the current time of execution in the form
    // Fri Mar 24 21:25:28 EDT 2017
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:   
    //          current time of execution is printed
    //
    // Returns:   
    //          current time of execution
    //
    // Called by: 
    //          Executor
    //              execute_quit()
    //          
    //          Controller
    //              initialComments()
    //
    public static String getTime() {
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date dateobj  = new Date();
        
        return df.format(dateobj);
    }
    
    //////////////////////////////////////////////////////////////// convertDMStoSec(String DMS)
    // Converts DMS location data format to Seconds location data
    //
    // Parameters:
    //          DMS DMS formatted coordinate
    //
    // Pre:       
    //          none
    //
    // Post:   
    //          converted seconds coordinate is created
    //
    // Returns:   
    //          converted seconds coordinate data
    //
    // Called by: 
    //          Commands
    //              execute_what_is_at()
    //              execute_what_is_in()
    //              execute_what_is_in_long()
    //              execute_what_is_in_filter()
    //          
    //          Executor
    //              getBoundary()
    //
    //          GISData
    //              import_CoordIndex(File DBFile)
    //
    public static long convertDMStoSec(String DMS) {
        if (!DMS.equals("Unknown")) {
            int DMSLength = DMS.length() - 1;
            
            char direction = DMS.charAt(DMSLength);
            
            int seconds = Integer.valueOf(DMS.substring(DMSLength - 2, DMSLength));
            int minutes = Integer.valueOf(DMS.substring(DMSLength - 4, DMSLength -2)) * 60;
            int degrees = Integer.valueOf(DMS.substring(0, DMSLength - 4)) * 3600;
            
            int sign;
            
            if (direction == 'N' || direction == 'E') {
                sign = 1;
            }
            else {
                sign = -1;
            }
            
            return (seconds + minutes + degrees) * sign;
        }
        return 0;
    }
}
