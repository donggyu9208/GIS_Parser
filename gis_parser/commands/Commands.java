package gis_parser.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import gis_parser.GISData;
import stringFormat.GISDataParser;
import stringFormat.StringFormat;
import struct.BufferPool.BufferPool;
import struct.QuadTree.GISClient.Point;

// Project J4 for CS 3114 Summer I 2017
//
// Programmer:    Dong Gyu Lee
// OS:            Windows 10 Education
// System:        i5-4300U, 8 GB Memory
// Compiler:      Eclipse Neon.3 Release (4.6.3)
// Last modified: June 23, 2017
//
// Commands class serves to execute various different commands
// including: import, debug, what_is, what_is_at, what_is_in, quit
//
// Each command are separated by single tab characters with a 
// sequence of tokens.
//
public class Commands {
    // Commands Field
    private String commandLine;     // raw-data of a command data from script
 
    // Data Field
    private GISData GISData;        // GIS data that consists of Name Index and Coordinate Index
    private BufferPool bufferPool;  // Buffer Pool that assists in performance and find the data
    private File DBFile;            // Database to retrieve data from
   
    private int imported_Locations; // Number of imported locations used for import command
    private int imported_Names;     // Number of imported Names used for import command
    
    //////////////////////////////////////////////////////////////// Commands(GISData GISData, 
    //                                                                          BufferPool bPool, 
    //                                                                          File DBFile, 
    //                                                                          String commandLine,
    //                                                                          CommandsType type,
    //                                                                          int commandNum) 
    // 
    // Constructor for the Commands that parses the command line
    // and connects GISData, bufferPool and DBFile
    //
    // Parameters:
    //          GISData     GIS data to retrieve offsets to the appropriate data
    //          bufferPool  Buffer pool to retrieve raw data
    //          DBFile      Database to retrieve raw data (retrieved through Buffer pool)
    //          commandLine Raw command line
    //          type        Type of command
    //          commandNum  Command Number
    //
    // Pre:       
    //          None
    //
    // Post:    
    //          Commands object with a command line is initiated
    //          command line is parsed into string array separated by spaces
    //          Commands is connected to GISData, BufferPool and DBFile
    //
    public Commands(GISData     GISData, 
                    BufferPool  bufferPool, 
                    File        DBFile, 
                    String      commandLine) 
    {
        commandLine.split("\\s+");
        this.bufferPool = bufferPool;
        this.GISData = GISData;
        this.commandLine = commandLine;
        this.DBFile = DBFile;
    }

    // -------------------------------------- IMPORT COMMAND ------------------------------------ //
    //////////////////////////////////////////////////////////////// execute_import()
    // Imports data from the specified data file to database (DBFile)
    // Unless program is terminated importing multiple times does not create a new database file
    // new imports after the first import will be appended after the last line of database
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          Command Type is IMPORT
    //
    // Post:    
    //          Name Index and Coordinate Index are imported
    //          Number of imported Name and Number of imported Locations are calculated
    //          
    //          
    // Returns:   
    //          Number of Imported Features by Name
    //          Longest probe sequence
    //          Imported Locations 
    //              in String
    //
    // Called by: 
    //          Executor class
    //              execute()
    // Calls:   
    //         GISData class
    //              import_NameIndex(File DBFile)
    //              import_CoordIndex(File DBFile)
    //
    public String execute_import() throws IOException {
        // Import Name Index
        // Count the number before and after the execution and 
        // calculate how much data has been imported for both
        // Name Index and Coordinate Index
        // Only if afterCount - prevCount > 0, data has been imported
        int prevCount = GISData.getNameIndex().getTotalElemCount(), afterCount; // Name Index elem count before execution
        GISData.import_NameIndex(DBFile);
        afterCount = this.GISData.getNameIndex().getTotalElemCount();           // Name index elem count after execution
        
        imported_Names = (afterCount > prevCount) ? afterCount - prevCount : 0; 
        
        // Import Coordinate Index
        // Similar case for importing Coord Index
        prevCount = GISData.getCoordIndex().getElemCount();                     // Coord Index elem count before execution
        GISData.import_CoordIndex(DBFile);
        afterCount = this.GISData.getCoordIndex().getElemCount();               // Coord index elem count after execution
        
        imported_Locations = (afterCount > prevCount) ? afterCount - prevCount : 0;
        
        
        StringBuilder builder = new StringBuilder();    // StringBuilder is used to improve the performance
        builder.append(String.format("%-27s%s\n", "Imported Features by name:", this.imported_Names));
        builder.append(String.format("%-27s%s\n", "Longest probe sequence:", GISData.getNameIndex().getLongestProbe()));
        builder.append(String.format("%-27s%s\n", "Imported Locations:", this.imported_Locations));
        
        return builder.toString();
    }
    
    // --------------------------------------  DEBUG COMMAND ------------------------------------ //
    //////////////////////////////////////////////////////////////// execute_debug()
    // Format of debug command is:
    //      debug<tab>[ quad | hash | pool ]
    //
    //
    // debug quad 
    //      Displays the contents of the hash table
    //      
    // debug hash
    //      Displays the structure of the tree, the relationships between its nodes, and the 
    //      data objects in the leaf nodes.
    //
    // debug pool
    //      Displays the contents of the buffer pool listed from MRU to LRU
    //
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          CommandType is DEBUG
    //          Any of the data structure that has been called by this command previously is initialized 
    //          Otherwise fails to execute debug command
    //
    // Post:    
    //          Displays the contents, relationships and structure for each
    //          data structure     
    //          
    // Returns:   
    //          Details of each data structure 
    //              in String
    //          If debug type is not one of the known type
    //          Error message pops up saying:
    //              "There is no [<Typed Debug Type>] debug type
    //
    // Called by: 
    //          Executor class
    //              execute()
    // Calls:   
    //         GISData class
    //              getCoordIndex()
    //              getNameIndex()
    //
    // 
    public String execute_debug() {
        String debugType = commandLine.split("\\s+")[1];
        String result;
        switch (debugType) {
        case "quad":
            result = GISData.getCoordIndex().toString();
            break;
        case "hash":
            result = GISData.getNameIndex().toString();
            break;
        case "pool":
            result = bufferPool.toString();
            break;            
        default:
            result = "There is no [" + debugType + "] debug type.\n";
            break;
        }
        
        return result;
    }
    
    // -------------------------------------- WHAT_IS_AT COMMAND ------------------------------------ //
    //////////////////////////////////////////////////////////////// execute_what_is_at()
    // The format of what_is_at command is:
    //      what_is_at<tab><Geographic_Coordinate> <Geographic_Coordinate>
    //
    // For every GIS record in the database file that matches the given 
    // <geographic coordinate>, log the offset at which the record was found, 
    // and the feature name, county name, and state abbreviation. 
    //
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          CommandType is AT
    //
    // Post:    
    //          Either the record is found or is not found
    //          and the corresponding output string is executed
    //          
    // Returns:   
    //          Coordinates that are found from the data
    //          If not found, prints out the message nothing is found
    //
    // Called by: 
    //          Executor class
    //              execute()
    // Calls:   
    //          prQuadTree          
    //              find(T elem)
    //          
    //          GISData
    //              getCoordIndex()
    //
    //          BufferPool
    //              getData(String offset)
    //          
    //          GISDataParser
    //              getOffset()
    //              getFeatureName()
    //              getCountyName()
    //              getStateName()
    //
    //          StringFormat
    //              convertDMStoFriendly(String DMS)
    // 
    public String execute_what_is_at() throws NumberFormatException, IOException {
        StringBuilder builder = new StringBuilder();    // StringBuilder is used to improve the performance
        String[] commandData = commandLine.split("\\t");
        
        // Get the DMS Data and convert it to Seconds form
        String DMS_Long = commandData[1];                                       // DMS Latitude
        String DMS_Lat = commandData[2];                                        // DMS Longitude
        Double Long = Double.valueOf(StringFormat.convertDMStoSec(DMS_Long));   // Latitude in seconds
        Double Lat = Double.valueOf(StringFormat.convertDMStoSec(DMS_Lat));     // Longitude in seconds
        
        // Retrieve the desired matched points to the data
        Point matchedPoint = GISData.getCoordIndex().find(new Point(Lat, Long));
        
        // If we have matched points log those matched points
        if (matchedPoint != null) {
            String offset;
            GISDataParser parser;
            
            // Get an offset list from matched points
            ArrayList<Double> offsetList = matchedPoint.getOffsetList();
            int offsetListSize = offsetList.size();
            String matchedData;
            
            // Log matched features found
            builder.append(String.format("   The following features were found at (%s, %s):\n", 
                                            StringFormat.convertDMStoFriendly(DMS_Lat),                     
                                            StringFormat.convertDMStoFriendly(DMS_Long)));
            
            // GISDataParser takes a raw data line and parse the raw data
            ArrayList<GISDataParser> parserList = new ArrayList<GISDataParser>();
            
            // For every offset and its raw data put it into GISDataParser
            for (int i = 0; i < offsetListSize; i++) {
                offset = String.valueOf(offsetList.get(i).longValue());
                
                // Note: * Always retrieving data through Buffer Pool *
                matchedData = bufferPool.getData(offset);
                parserList.add(new GISDataParser(matchedData, offset));
            }
            
            // Sort the parsed data list by its feature name
            Collections.sort(parserList);
            
            // For every parsed data log the offset, feature name, county name, state name
            for (int i = 0; i < parserList.size(); i++) {
                parser = parserList.get(i);
                builder.append(String.format("%8s:  %s  %s  %s\n", 
                                                parser.getOffset(),
                                                parser.getFeatureName(),
                                                parser.getCountyName(),
                                                parser.getStateName()));
            }
            
        }
        else {
            
            // If there is no match, log Nothing was found
            builder.append(String.format("   Nothing was found at (%s, %s)\n", 
                           StringFormat.convertDMStoFriendly(DMS_Lat), 
                           StringFormat.convertDMStoFriendly(DMS_Long)));
        }
        return builder.toString();
    }
    
    // -------------------------------------- WHAT_IS COMMAND ------------------------------------ //
    //////////////////////////////////////////////////////////////// execute_what_is()
    // The format of what_is command is:
    //      what_is<tab><Name_Feature><tab><State_abbr>
    // 
    // For every GIS record in the database file that matches the given 
    // <feature name> and <state abbreviation, return the string of offsets 
    // at which the record is found, and the county name, primary latitude, and primary longitude.
    //
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          CommandType is IS
    //
    // Post:    
    //          Either the record is found or is not found
    //          and the corresponding output string is executed
    //          
    // Returns:   
    //          If found, appropriate offset, County Name, DMS Longitude, DMS Latitude
    //          If not found, message saying there is no records that match
    //
    // Called by: 
    //          Executor class
    //              execute()
    // Calls:   
    //          HashTable          
    //              getValues(String key)
    //          
    //          GISData
    //              getNameIndex()
    //
    //          BufferPool
    //              getData(String offset)
    //          
    //          GISDataParser
    //              getOffset()
    //              getCountyName()
    //              getStateName()
    //
    //          StringFormat
    //              convertDMStoFriendly(String DMS)
    // 
    public String execute_what_is() throws NumberFormatException, IOException {
        StringBuilder builder = new StringBuilder();    // StringBuilder is used to improve the performance
        
        String[] commandData = commandLine.split("\\t");
        
        // Retrieve feature name, state abbr from the command line
        String feature_name = commandData[1];   // Feature Name
        String state_abbr = commandData[2];     // State Abbreviation
        
        // Retrieve offsets from the Name Index
        ArrayList<Long> offsetList = this.GISData.getNameIndex().getValues(feature_name + ":" + state_abbr);
        
        String matchedData;     // Stores the matched data
        
        String offset;          // offset at which matched data is found
        GISDataParser parser;   // raw-data parser
        if (offsetList != null) {
            
            ArrayList<GISDataParser> parserList = new ArrayList<GISDataParser>();
            
            // Parse the raw data and insert into GISDataParser
            for (int i = 0; i < offsetList.size(); i++) {
                offset = offsetList.get(i).toString();
                
                // Note: * Always retrieving data through Buffer Pool *
                matchedData = bufferPool.getData(offset);
                parserList.add(new GISDataParser(matchedData, offset));
            }
            
            // Sort the parsed data by its feature name
            Collections.sort(parserList);
            
            // For every parsed matched data, log its offset, county name, DMS Longitude, DMS Latitude
            for (int i = 0; i < parserList.size(); i++) {
                parser = parserList.get(i);
                builder.append(String.format("%8s:  %s  (%s, %s)\n", 
                        parser.getOffset(),
                        parser.getCountyName(),
                        StringFormat.convertDMStoFriendly(parser.getPrimaryLongDMS()),
                        StringFormat.convertDMStoFriendly(parser.getPrimaryLatDMS())));
            }
        }
        else {
            
            // If no matched data is found, log no records match.
            builder.append(String.format("No records match %s and %s\n", feature_name, state_abbr));
        }
        return builder.toString();
    }
    
    // -------------------------------------- FOR WHAT_IS_IN COMMAND ------------------------------------ //
    //////////////////////////////////////////////////////////////// execute_what_is_in()
    // The format of what_is command is:
    //      what_is_in<tab><geographic coordinate><tab><half-height><tab><half-width>
    // 
    // For every GIS record in the database file whose coordinates fall within the
    // specified boundary region with the specific height and width centered at the
    // given geographic coordinate, this method logs the offset, feature name, 
    // state name, primary latitude, primary longitude.
    //
    // There exits an optional modifier, which modifies the search result and its log output
    //
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          CommandType is IN
    //
    // Post:    
    //          Either the record is found or is not found
    //          and the corresponding output string is executed
    //          
    // Returns:   
    //          If found, appropriate offset, Feature Name, State Name, Primary Latitude, Primary Longitude are logged
    //          If not found, message saying there is no records that is in the boundary
    //
    // Called by: 
    //          Executor class
    //              execute()
    // Calls:   
    //          Commands
    //              execute_what_is_in_long()
    //              execute_what_is_in_filter()
    //          
    //          GISData
    //              getCoordIndex()
    //
    //          BufferPool
    //              getData(String offset)
    //
    //          Point
    //              getOffsetList()
    //          
    //          StringFormat
    //              convertDMStoSec(String DMS)
    //
    //          prQuadTree
    //              find(T elem)
    //
    //          GISDataParser
    //              getOffset()
    //              getFeatureName()
    //              getStateName()
    //
    public String execute_what_is_in() throws NumberFormatException, IOException {
        String[] commandData = commandLine.split("\\t");
        
        // There is 2 different types of switch
        // 1: -long switch 
        // 2: -filter switch
        // 
        // For each switch case execute the corresponding command
        if (commandData[1].equals("-long")) {
            return execute_what_is_in_long();
        }
        else if (commandData[1].equals("-filter")) {
            return execute_what_is_in_filter();
        }
        
        // If switch is not assigned, execute what_is_in command
        else {
            
            StringBuilder builder = new StringBuilder();    // StringBuilder is used to improve the performance
            String DMS_Lat = commandData[1];                // DMS Latitude
            String DMS_Long = commandData[2];               // DMS Longitude
            String half_height = commandData[3];            // half of height that is added or subtracted from the coordinate
            String half_width = commandData[4];             // half of width that is added or subtracted from the coordinate
            
            Double x = Double.valueOf(StringFormat.convertDMStoSec(DMS_Long));  // x coordinate 
            Double y = Double.valueOf(StringFormat.convertDMStoSec(DMS_Lat));   // y coordinate 
            
            Double x_add = Double.valueOf(half_width);                          // added or subtracted from x coordinate
            Double y_add = Double.valueOf(half_height);                         // added or subtracted from y coordinate
            
            // Retrieve Points in the specified Boundary
            ArrayList<Point> pointList = GISData.getCoordIndex().find(x - x_add, x + x_add, y - y_add, y + y_add);
            
            // Case for if there is not points found in the specified range
            if (pointList != null) {
                
                int offsetListSize;             // size of offsets that lies within the range
                ArrayList<Double> offsetList;   // list of offsets
                String offset;                  // offset of the matched data
                String matchedData;             // raw-matched data
                GISDataParser parser;           // raw-data parser
                
                // List of parsed raw-data
                ArrayList<GISDataParser> parserList = new ArrayList<GISDataParser>();
                
                // For every points, there can exist more than one offsets
                // Thus we need to import every one of those offsets
                for (int i = 0; i < pointList.size(); i++) {
                    
                    // Get the offset list from each coordinate point
                    offsetList = pointList.get(i).getOffsetList();
                    offsetListSize = offsetList.size();             
                    
                    // Retrieve raw-data at each offsets that are retrieved
                    for (int j = 0; j < offsetListSize; j++) {
                        offset = String.valueOf(offsetList.get(j).longValue());
                        
                        // Note: * Always Retrieve data through buffer pool
                        matchedData = bufferPool.getData(offset);
                        
                        // parse the retrieved raw-data and added into the raw-data parser 
                        parserList.add(new GISDataParser(matchedData, offset));
                    }
                }
                
                // If the resulting size of the parser list is 0,
                // There is no data that are found within the range
                int parserListSize = parserList.size();
                if (parserListSize > 0) {
                    
                    // Combination of StringBuilder and String format improves its performance of computing string data
                    builder.append(String.format("   The following %s features were found in (%s +/- %s, %s +/- %s)\n",
                                                    parserList.size(),
                                                    StringFormat.convertDMStoFriendly(DMS_Long),
                                                    half_width,
                                                    StringFormat.convertDMStoFriendly(DMS_Lat),
                                                    half_height));
                    
                    // Sorts data line by the feature name
                    Collections.sort(parserList);
                    
                    // Log the sorted data
                    for (int i = 0; i < parserListSize; i++) {
                      parser = parserList.get(i);
                      
                      // Combination of StringBuilder and String format improves its performance of computing string data
                      builder.append(String.format("%8s: %s  %s  (%s, %s)\n", 
                                                      parser.getOffset(), 
                                                      parser.getFeatureName(),
                                                      parser.getStateName(),
                                                      StringFormat.convertDMStoFriendly(parser.getPrimaryLongDMS()),
                                                      StringFormat.convertDMStoFriendly(parser.getPrimaryLatDMS())));
                    }
                    return builder.toString();
                }
                                
            }
            
            // If nothing is found log that there is no element found within the range
            builder.append(String.format("   Nothing was found in (%s +/- %s, %s +/- %s)\n", 
                                            StringFormat.convertDMStoFriendly(DMS_Long),
                                            half_width,
                                            StringFormat.convertDMStoFriendly(DMS_Lat),
                                            half_height));
            return builder.toString();
        }
    }
    
    // -------------------------------------- WHAT_IS_IN -LONG COMMAND ------------------------------------ //
    ////////////////////////////////////////////////////////////////execute_what_is_in_long()
    // The format of what_is command is:
    //     what_is_in<tab><geographic coordinate><tab><-long> <half-height><tab><half-width>
    // 
    // This is a method for optional modifier -long which displays the long listing of the relevant
    // records. The switch is located at the first token of the what_is_in commend (right after tab)
    // If the switch is present for every matched data we log more detailed description of each parsed data
    // Every important non-empty-fields are printed in the format.
    //
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          CommandType is IN
    //
    // Post:    
    //          Either the record is found or is not found
    //          and the corresponding output string is executed
    //          
    // Returns:   
    //          If found, appropriate detailed relevant list of data line.
    //          If not found, message saying there is no records that is in the boundary
    //
    // Called by: 
    //          Commands
    //              what_is_in()
    // Calls:   
    //          Commands
    //          
    //          BufferPool
    //              getData(String offset)
    //
    //          StringFormat
    //              convertDMStoSec(String DMS)
    //              convertDMStoFriendly(String DMS)
    //
    //          prQuadTree
    //              find(T elem)
    //
    //          GISDataParser
    //              getFeatureID() 
    //              getFeatureName()
    //              getFeatureClass()
    //              getStateName()
    //              getCountyName()
    //              getElevationFt()
    //              getDateCreated()
    //              getDateEdited()
    //
    private String execute_what_is_in_long() throws NumberFormatException, IOException {
        StringBuilder builder = new StringBuilder();        // StringBuilder is used to improve the performance
        String[] commandData = commandLine.split("\\t");
        String DMS_Lat = commandData[2];                    // DMS Latitude
        String DMS_Long = commandData[3];                   // DMS Longitude
        String half_height = commandData[4];                // half height that is added or subtracted
        String half_width = commandData[5];                 // half width that is added or subtracted
        
        Double x = Double.valueOf(StringFormat.convertDMStoSec(DMS_Long));  // x coordinate
        Double y = Double.valueOf(StringFormat.convertDMStoSec(DMS_Lat));   // y coordinate
        Double x_add = Double.valueOf(half_width);                          // added or subtracted from x coordinate
        Double y_add = Double.valueOf(half_height);                         // added or subtracted from y coordinate
        
        // Retrieve Points in the specified Boundary
        ArrayList<Point> pointList = GISData.getCoordIndex().find(x - x_add, x + x_add, y - y_add, y + y_add);
        
        // Case for if there is no points found in the specified range
        if (pointList != null) {
            
            int offsetListSize;             // size of offsets that lies within the range
            ArrayList<Double> offsetList;   // list of offsets
            String offset;                  // offset of the matched data
            String matchedData;             // raw-matched data
            GISDataParser parser;           // raw-data parser
            
            // List of parsed raw-data
            ArrayList<GISDataParser> parserList = new ArrayList<GISDataParser>();
            
            // For every points, there can exist more than one offsets
            // Thus we need to import every one of those offsets
            for (int i = 0; i < pointList.size(); i++) {
                // Get the offset list from each coordinate point
                offsetList = pointList.get(i).getOffsetList();
                offsetListSize = offsetList.size();
                
                // Retrieve raw-data at each offsets that are retrieved
                for (int j = 0; j < offsetListSize; j++) {
                    offset = String.valueOf(offsetList.get(j).longValue());
                    // Note: * Always Retrieve data through buffer pool
                    matchedData = bufferPool.getData(offset);
                    // parse the retrieved raw-data and added into the raw-data parser 
                    parserList.add(new GISDataParser(matchedData, offset));
                }
            }
            int parserListSize = parserList.size();
            if (parserListSize > 0) {
            
                // Combination of StringBuilder and String format improves its performance of computing string data
            builder.append(String.format("   The following %s features were found in (%s +/- %s, %s +/- %s)\n",
                                            parserListSize,
                                            StringFormat.convertDMStoFriendly(DMS_Long),
                                            half_width,
                                            StringFormat.convertDMStoFriendly(DMS_Lat),
                                            half_height));
            
            // Sorts data line the its feature name
            Collections.sort(parserList);
            
                for (int i = 0; i < parserListSize; i++) {
                    parser = parserList.get(i);
                   
                    // Combination of StringBuilder and String format improves its performance of computing string data
                    builder.append(String.format("  %-13s: %s\n", "Feature ID",     parser.getFeatureID()));
                    builder.append(String.format("  %-13s: %s\n", "Feature Name",   parser.getFeatureName()));
                    builder.append(String.format("  %-13s: %s\n", "Feature Cat",    parser.getFeatureClass()));
                    builder.append(String.format("  %-13s: %s\n", "State",          parser.getStateName()));
                    builder.append(String.format("  %-13s: %s\n", "County",         parser.getCountyName()));
                    builder.append(String.format("  %-13s: %s\n", "Longitude",      StringFormat.convertDMStoFriendly(parser.getPrimaryLongDMS())));
                    builder.append(String.format("  %-13s: %s\n", "Latitude",       StringFormat.convertDMStoFriendly(parser.getPrimaryLatDMS())));
                    builder.append(String.format("  %-13s: %s\n", "Elev in ft",     parser.getElevationFt()));
                    builder.append(String.format("  %-13s: %s\n", "USGS Quad",      parser.getMapName()));
                    builder.append(String.format("  %-13s: %s\n", "Date created", parser.getDateCreated()));
                    if (parser.getDateEdited() != null) {
                        builder.append(String.format("  %-13s: %s\n", "Date mod", parser.getDateEdited()));
                    }
                    builder.append('\n');
                }
                return builder.toString();
            }
        }
        
        builder.append(String.format("   Nothing was found in (%s +/- %s, %s +/- %s)\n", 
                StringFormat.convertDMStoFriendly(DMS_Long),
                half_width,
                StringFormat.convertDMStoFriendly(DMS_Lat),
                half_height));
        return builder.toString();
    }
    
    // -------------------------------------- WHAT_IS_IN -FILTER COMMAND ------------------------------------ //
    ////////////////////////////////////////////////////////////////execute_what_is_in_filter()
    // The format of what_is command is:
    //     what_is_in<tab><geographic coordinate><tab><-filter><tab><filter type><half-height><tab><half-width>
    // 
    // This is a method for optional modifier -filter which only displays the elements that falls under filter
    // first token after what_is_in command is given as the switch and the second token is given as the filter modifier
    // When filter switch is present, the corresponding elements that both lies under the specified range and 
    // the filter type are selected and logged.
    // 
    // Filter types of elements are determined from the method getFilterType(String className)
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          CommandType is IN
    //
    // Post:    
    //          Either the record is found or is not found
    //          and the corresponding output string is executed
    //          
    // Returns:   
    //          If found, appropriate offset, Feature Name, State Name, Primary Latitude, Primary Longitude are logged
    //          If not found, message saying there is no records that is in the boundary
    //
    // Called by: 
    //          Commands
    //              what_is_in()
    // Calls:   
    //          GISData
    //              getCoordIndex()
    //          
    //          StringFormat
    //              convertDMStoFriendly(String DMS)
    //
    //          prQuadTree
    //              find(T elem)
    //
    //          GISDataParser
    //              getFeatureName()
    //              getPrimaryLongDMS()
    //              getPrimaryLatDMS()
    //              getOffset()
    //
    private String execute_what_is_in_filter() throws NumberFormatException, IOException {
        StringBuilder builder = new StringBuilder();        // StringBuilder is used to improve the performance
        String[] commandData = commandLine.split("\\t");
        FilterType filterType = convertStringtoFilterType(commandData[2]);  // Filter type
        String DMS_Lat = commandData[3];                                    // DMS Latitude
        String DMS_Long = commandData[4];                                   // DMS Longitude
        String half_height = commandData[5];                                // half height that is added or subtracted
        String half_width = commandData[6];                                 // half width that is added or subtracted
        
        Double x = Double.valueOf(StringFormat.convertDMStoSec(DMS_Long));  // x coordinate
        Double y = Double.valueOf(StringFormat.convertDMStoSec(DMS_Lat));   // y coordinate
        Double x_add = Double.valueOf(half_width);                          // added or subtracted from x coordinate
        Double y_add = Double.valueOf(half_height);                         // added or subtracted from y coordinate
        
        // Retrieve Points in the specified Boundary
        ArrayList<Point> pointList = GISData.getCoordIndex().find(x - x_add, x + x_add, y - y_add, y + y_add);
        
        // Case for if there is no points found in the specified range
        if (pointList != null) {
            
            int offsetListSize;             // size of offsets that lies within the range
            ArrayList<Double> offsetList;   // list of offsets
            String offset;                  // offset of the matched data
            String matchedData;             // raw-matched data
            GISDataParser parser;           // raw-data parser
            
            // List of parsed raw-data
            ArrayList<GISDataParser> parserList = new ArrayList<GISDataParser>();
            
            // For every points, there can exist more than one offsets
            // Thus we need to import every one of those offsets
            for (int i = 0; i < pointList.size(); i++) {
                
                // Get the offset list from each coordinate point
                offsetList = pointList.get(i).getOffsetList();
                offsetListSize = offsetList.size();
                
                
                for (int j = 0; j < offsetListSize; j++) {
                    offset = String.valueOf(offsetList.get(j).longValue());
                    
                    // Note: * Always Retrieve data through buffer pool
                    matchedData = bufferPool.getData(offset);
                    
                    // parse the retrieved raw-data and added into the raw-data parser 
                    parserList.add(new GISDataParser(matchedData, offset));
                    
                }
            }
            
            // Sorts data line the its feature name
            Collections.sort(parserList);
            StringBuilder matchedBuilder = new StringBuilder(); // StringBuilder is used to improve the performance
            int matchedSize = 0;
            
            // Here we are determining which of the matched data falls under the correct filter type
            for (int i = 0; i < parserList.size(); i++) {
                parser = parserList.get(i);
                if (getFilterType(parser.getFeatureClass()) == filterType) {
                    parser = parserList.get(i);
                        
                        // Combination of StringBuilder and String format improves its performance of computing string data
                        matchedBuilder.append(String.format("%8s: %s  %s  (%s, %s)\n", 
                                                                parser.getOffset(), 
                                                                parser.getFeatureName(),
                                                                parser.getStateName(),
                                                                StringFormat.convertDMStoFriendly(parser.getPrimaryLongDMS()),
                                                                StringFormat.convertDMStoFriendly(parser.getPrimaryLatDMS())));
                    matchedSize++;
                }
            }
            
            // Print how many elements are matched
            if (matchedSize > 0) {
                
                // Combination of StringBuilder and String format improves its performance of computing string data
                builder.append(String.format("   The following features matching your criteria were found in (%s +/- %s, %s +/- %s)\n\n",
                                                StringFormat.convertDMStoFriendly(DMS_Long),
                                                half_width,
                                                StringFormat.convertDMStoFriendly(DMS_Lat),
                                                half_height));
                builder.append(matchedBuilder.toString());
                builder.append(String.format("\nThere were %d features of type water.\n", matchedSize));
                
                return builder.toString();
            }
        }
        
        // If not elements lies under the specified region and match the filter type
        // Log nothing was found in the specified range
        builder.append(String.format("   Nothing was found in (%s +/- %s, %s +/- %s)\n", 
                StringFormat.convertDMStoFriendly(DMS_Long),
                half_width,
                StringFormat.convertDMStoFriendly(DMS_Lat),
                half_height));
        return builder.toString();
    }
    
    //////////////////////////////////////////////////////////////// getFilterType(String className)
    // This is a helper method for execute_what_is_in_filter.
    // Given a class name of the data, determines the corresponding
    // filter type either structure, water, pop, or no type
    //
    //
    // Parameters:
    //          className class name for the data line
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          filter type for the class name is determined
    //          
    // Returns:   
    //          enum FilterType for the corresponding class name
    //
    // Called by: 
    //          Commands
    //              what_is_in_filter()
    // Calls:   
    //          FilterType enum
    //
    private FilterType getFilterType(String className) {
        
        // Specifies which classes corresponds to which filter type
        String[] StructureType = {"Airpot", "Bridge", "Building", "Church", "Dam",
                                  "Hospital", "Levee", "Park", "Post Office", "School",
                                  "Tower", "Tunnel"};
        String[] WaterType = {"Arroyo", "Bay", "Bend", "Canal", "Channel", "Falls",
                              "Glacier", "Gut", "Harbor", "Lake", "Rapids", "Reservoir",
                              "Sea", "Spring", "Stream", "Swamp", "Well"};
        String[] PopType = {"Populated Place"};
        
        // if else statement in the order of smallest values contained type 
        // to the biggest which can provide performance improvement
        if (Arrays.asList(PopType).contains(className)) {
            return FilterType.POP;
        }
        else if (Arrays.asList(WaterType).contains(className)) {
            return FilterType.WATER;
        }
        else if (Arrays.asList(StructureType).contains(className)) {
            return FilterType.STRUCTURE;
        }
        else {
            return FilterType.NOTYPE;
        }
    }
    
    ////////////////////////////////////////////////////////////////getFilterType(String className)
    // Given a string filter class name, convert it to filter type enum
    // By using a converted enum filter type instead of using String filter type
    // We can improve its performance by without need to go through using equals(String ) method
    //
    //
    // Parameters:
    //          filterType filter type of the data line (String)
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          filter type is determined
    //          
    // Returns:   
    //          enum FilterType for the corresponding Stiring fliter type
    //
    // Called by: 
    //          Commands
    //              what_is_in_filter()
    // Calls:   
    //          FilterType enum
    //
    private FilterType convertStringtoFilterType(String filterType) {
        switch(filterType) {
        case "structure":
            return FilterType.STRUCTURE;
        case "water":
            return FilterType.WATER;
        case "pop":
            return FilterType.POP;
        default:
            return FilterType.NOTYPE;
        }
    }
    // -------------------------------------- FOR QUIT COMMAND ------------------------------------ //
    //////////////////////////////////////////////////////////////// execute_quit()
    // Terminates the program execution
    //
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Ouput: "Termninating execution of commands"
    //                 Time at the execution
    //          
    // Returns:   
    //          quit log including the message terminating the program
    //          and the quit time
    //
    // Called by: 
    //          Executor
    //              execute()
    //
    public String execute_quit () {
        StringBuilder builder = new StringBuilder();                
        builder.append("Terminating execution of commands.\n");
        builder.append(String.format("End time: %s\n", StringFormat.getTime()));
        return builder.toString();
    }
}
