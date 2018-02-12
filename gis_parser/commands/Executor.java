package gis_parser.commands;

import java.io.File;
import java.io.IOException;

import gis_parser.GISData;
import struct.BufferPool.*;
import stringFormat.StringFormat;

//Project J4 for CS 3114 Summer I 2017
//
// Programmer:    Dong Gyu Lee
// OS:            Windows 10 Education
// System:        i5-4300U, 8 GB Memory
// Compiler:      Eclipse Neon.3 Release (4.6.3)
// Last modified: June 23, 2017
//
// Overall Commands Controller that governs Commands class
// Executor takes in the command line and executes an appropriate
// method for the command
//
public class Executor {
    // Command Fields
    private String[] commandsData;  // Parsed Command line data
    private String commandLine;     // Raw command line
    private CommandsType type;      // Command type
    private int commandNum;         // Command Number
    
    // Data Field
    private GISData GISData;        // GIS Data that stores Name Index and Coordinate Index
    private BufferPool bufferPool;  // Buffer Pool that retrieves the data from DBFile
    private File DBFile;            // DB file to retrieve data through Buffer Pool
    
    //////////////////////////////////////////////////////////////// Executor(GISData GISData,
    //                                                                        File DBFile,
    //                                                                        String commandLine,
    //                                                                        int commandNum) 
    //
    // This constructor is used to construct the world boundary for the GISData
    // Unnecessary Buffer pool parameter when constructing the boundary is omitted
    //
    // Parameters:
    //          GISData     GIS data to retrieve offsets of the appropriate data
    //          DBFile      Database to retrieve raw data (retrieved through Buffer pool)
    //          commandLine Raw command line
    //          commandNum  Command Number
    //
    // Pre:       
    //          None
    //
    // Post:    
    //          Executor object with a command line is initiated
    //          command line is parsed into string array separated by spaces (tab)
    //          Executor is connected to GISData and DBFile
    //
    public Executor(GISData GISData,
                    File DBFile,
                    String commandLine,
                    int commandNum) 
    {
        this.commandsData = commandLine.split("\\s+");
        this.type         = getCommandType();
        this.GISData      = GISData;
        this.commandNum   = commandNum;
        this.DBFile       = DBFile;
        this.commandLine  = commandLine;
    }
    
    //////////////////////////////////////////////////////////////// Executor(GISData GISData,
    //                                                                        BufferPool bufferPool
    //                                                                        File DBFile,
    //                                                                        String commandLine,
    //                                                                        int commandNum) 
    //
    // This constructor is used to execute rest of the commands excluding world command
    //
    // Parameters:
    //          GISData     GIS data to retrieve offsets to the appropriate data
    //          bufferPool  Buffer pool to retrieve raw data
    //          DBFile      Database to retrieve raw data (retrieved through Buffer pool)
    //          commandLine Raw command line
    //          commandNum  Command Number
    //
    // Pre:       
    //          None
    //
    // Post:    
    //          Executor object with a command line is initiated
    //          command line is parsed into string array separated by spaces (tab)
    //          Executor is connected to GISData, Buffer Pool, and DBFile
    //
    public Executor(GISData GISData,
                    BufferPool bufferPool,
                    File DBFile, 
                    String commandLine, 
                    int commandNum) 
    {
        
        this.commandsData = commandLine.split("\\s+");
        this.type         = getCommandType();
        this.bufferPool   = bufferPool;
        this.GISData      = GISData;
        this.commandNum   = commandNum;
        this.commandLine  = commandLine;
        this.DBFile       = DBFile;
        
    }
    
    //////////////////////////////////////////////////////////////// getGISData()
    // Gets GISData from executor
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          GISData is initialized
    //
    // Post:    
    //          none (GISData is unchanged)
    //          
    //          
    // Returns:   
    //          GISData
    //
    // Called by: 
    //          Controller
    //              execute()
    //
    public GISData getGISData() {
        return this.GISData;
    }
    
    //////////////////////////////////////////////////////////////// getCommandType()
    // Gets Command Type of the command line instruction
    // Command types are: WORLD, IMPORT, DEBUG, AT, IS, IN, QUIT, WRONGTYPE
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
    //          Executor
    //              Constructor
    //              getBoundary()
    //
    //          Controller
    //              executor()
    //
    // Calls:   
    //          CommandType enum
    //
    public CommandsType getCommandType() {
        CommandsType type;
        switch (this.commandsData[0]) {
        case "world":
            type = CommandsType.WORLD;
            break;
        case "import":
            type = CommandsType.IMPORT;
            break;
        case "debug":
            type = CommandsType.DEBUG;
            break;
        case "what_is_at":
            type = CommandsType.AT;
            break;
        case "what_is":
            type = CommandsType.IS;
            break;
        case "what_is_in":
            type = CommandsType.IN;
            break;
        case "quit":
            type = CommandsType.QUIT;
            break;
        default:
            type = CommandsType.WRONGTYPE;
            break;
        }
        return type;
    }
    
    //////////////////////////////////////////////////////////////// execute()
    // Executes the command, given an command line
    // Logs the command number as wells as the corresponding outputs to 
    // the command
    //
    // Pre:       
    //          Executor is initialized
    //
    // Post:    
    //          Appropriate command is executed
    //          
    // Returns:   
    //          Log output corresponding to the command line
    //          If the command type that is not part of given instruction
    //          Outputs "There is no [WrongType] method"
    //
    // Called by:
    //          Executor
    //              Constructor
    //              getBoundary()
    //
    //          Controller
    //              executor()
    //
    // Calls:   
    //          StringFormat
    //              endString()
    //          
    //          CommandType enum
    //
    public String execute() throws IOException {
        StringBuilder builder = new StringBuilder();
        
        // Logs Command Name and its number
        if (this.type != CommandsType.WORLD) {
            builder.append(String.format("Command %d:  %s\n\n", this.commandNum, this.commandLine));
        }
        
        // Corresponding output to each command
        Commands commands = new Commands(GISData, bufferPool, DBFile, commandLine);
        switch (this.type) {
        case WORLD:
            builder.append(execute_world());
            break;
        case IMPORT:
            builder.append(commands.execute_import());
            break;
        case DEBUG:
            builder.append( commands.execute_debug());
            break;
        case IS:
            builder.append(commands.execute_what_is());
            break;
        case AT:
            builder.append(commands.execute_what_is_at());
            break;
        case IN:
            builder.append(commands.execute_what_is_in());
            break;
        case QUIT:
            builder.append(commands.execute_quit());
            break;
        default:
            builder.append(String.format("There is no [%s] method\n", this.type));
            break;
        }
        
        // At the end of every command, append the separator endString(
        builder.append(StringFormat.endString());
        return builder.toString();
    }
    
    // -------------------------------------- FOR WORLD COMMAND ------------------------------------ //
    //////////////////////////////////////////////////////////////// execute_world()
    // Initializes the world boundary, executed only once in the given script
    //
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          GISData is initialized with its boundary
    //          
    // Returns:   
    //          Latitude and Longitude of the set Boundary
    //
    // Called by: 
    //          Executor
    //              execute()
    // Calls:   
    //          none
    //
    public String execute_world() {
        GISData = new GISData(getBoundary()[0],     // xMin
                              getBoundary()[1],     // xMax
                              getBoundary()[2],     // yMin
                              getBoundary()[3]);    // yMax
        
        StringBuilder builder = new StringBuilder();
        builder.append("\nLatitude/longitude values in index entries are shown as signed integers, "
                        + "in total seconds.\n\n");
        builder.append("World boundaries are set to:\n");
        builder.append(String.format("%20d\n", getBoundary()[3]));  //yMax
        builder.append(String.format("%10d%23d\n", 
                                        getBoundary()[0],           // xMin
                                        getBoundary()[1]));         // xMax
        builder.append(String.format("%20d\n",  getBoundary()[2])); // yMin       
        
        return builder.toString();
    }
    
    //////////////////////////////////////////////////////////////// getBoundary()
    // Helper method for execute_word()
    // Retrieves the commandData and converts DMS format to seconds format
    //
    //
    // Parameters:
    //          none
    //
    // Pre:     
    //          Appropriate DMS format should be stored in the command Data
    //
    // Post:    
    //          DMS format is converted to seconds format and is returned
    //          
    // Returns:   
    //          seconds Longitude and Latitude data in a Long array
    //          If the command type is not world and this command is executed
    //          returns the warning that you cannot set the world boundary
    //          and terminates the program
    //
    // Called by: 
    //          Executor
    //              execute()
    // Calls:   
    //          StringFormat
    //              convertDMStoSec(String DMS)
    //
    private Long[] getBoundary() {
        if (getCommandType() == CommandsType.WORLD) {
            // Convert DMS format to 
            Long[] boundary = {StringFormat.convertDMStoSec(commandsData[1]),    // xLo         
                               StringFormat.convertDMStoSec(commandsData[2]),    // xHi
                               StringFormat.convertDMStoSec(commandsData[3]),    // yLo
                               StringFormat.convertDMStoSec(commandsData[4])};   // yHi
            return boundary;
        }
        else {
            System.out.println("You cannot get boundary for the commands that is not World");
            System.exit(0);
            return null;
        }
    }
}
