package gis_parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import gis_parser.GISData;
import gis_parser.commands.Executor;
import gis_parser.commands.CommandsType;
import stringFormat.StringFormat;

import struct.BufferPool.*;

// Project J4 for CS 3114 Summer I 2017
//
// Programmer:    Dong Gyu Lee
// OS:            Windows 10 Education
// System:        i5-4300U, 8 GB Memory
// Compiler:      Eclipse Neon.3 Release (4.6.3)
// Last modified: June 23, 2017
//
// This is an overall controller that validates 
// the command line arguments and manages the initialization 
// of the various system components.
// 
// The controller retrieves commands from the script file and make necessary 
// calls to other components to carry out the commands.
//
// The controller hands off execution to a command processor 
// that manages retrieving commands from the script file, and making 
// the necessary calls to other components in order to carry out those commands.
//
// The only complete GIS records that are stored in the memory at any time are:
// 1. Those that have just been retrieved to satisfy the current search
// 2. Individual GIS records created while importing data
// 3. GIS records stored in the buffer pool.
//
// Any other records should not be stored in the memory.
//
public class Controller {
    private GISData GISData;            // GIS Data that stores Name Index and Coordinate Index
    
    // Buffer pool is presented as a front end for the GIS database file
    // to improve search speed
    private BufferPool bufferPool;      // Buffer Pool that assists in performance and find the data
    private File DBFile;                // GIS data that consists of Name Index and Coordinate Index
    private File ScriptFile;            // Script file that consists of 
    private File LogFile;               // Log file to write the resulting output
    
    private int commandNum;             // keep the command number in the controller

    private boolean imported;           // Indicator if the file has been imported
                                        // Used for when imported twice in the system
    
    //////////////////////////////////////////////////////////////// Controller(File DBFile, File LogFile, File ScriptFile)
    // 
    // Parameters:
    //          DBFile      Database to retrieve raw data (retrieved through Buffer pool)
    //          LogFile     Writes the Log output to the command Data
    //          ScriptFile  Script file from which you retrieve the commands data
    //
    // Pre:       
    //          None
    //
    // Post:    
    //          Controller is initialized
    //
    public Controller(File DBFile, File LogFile, File ScriptFile) throws IOException {
        // Database and Log is initialized delete any of the words that was initially written in the LogFile and DBFile
        PrintWriter DBWriter;
        PrintWriter LogWriter;
        DBWriter = new PrintWriter(DBFile);
        DBWriter.print("");            
        LogWriter = new PrintWriter(LogFile);
        LogWriter.print("");
        
        this.commandNum = 1;    // Command number starts with 1
        this.imported = false;  // When Controller is initialized it is yet to import the data
        
        // DBFile, LogFile, ScriptFile and Buffer Pool are initialized
        this.DBFile = DBFile;
        this.LogFile = LogFile;
        this.ScriptFile = ScriptFile;
        this.bufferPool = new BufferPool(DBFile);
        
        DBWriter.close();
        LogWriter.close();
    }
    
    //////////////////////////////////////////////////////////////// execute()
    // Executes the command line and outputs in the LogFile
    // execute() skips any line that startswith ';' character or if it is a blank line
    // execute write out all the comments that start with ';' to the log file as well
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          LogFile is created
    //
    // Post:    
    //          LogFile is written with the appropriate output to the given script   
    //          
    // Returns:   
    //          none
    //
    // Called by: 
    //          Controller
    //              initialComment()
    //              init_DBFile(File importFile, File DBFile)
    //
    //          Executor
    //              execute()
    //              getCommandType()
    //
    public void execute() throws IOException {
        BufferedReader scriptReader = new BufferedReader(new FileReader(ScriptFile));
        BufferedWriter LogWriter = new BufferedWriter(new FileWriter(this.LogFile));
        
        Executor executor;  // Executor that executes each command line
        String line;        // line of script retrieved from the script file
        String output;      // resulting output string after reading from the script file
        
        // Reads until the end of the script file
        while ((line = scriptReader.readLine()) != null) {
            
            // Lines beginning with a semicolon character or blank lines
            // are skipped and ignored and written to the LogFile
            if (line.startsWith(";") || line.trim().isEmpty()) {
                LogWriter.write(line + "\n");
            }
            
            // For every command in the Script file
            else {
                
                // Executor is initialized for every new command line
                executor = new Executor(this.GISData, 
                                           this.DBFile, 
                                           line, 
                                           this.commandNum);
                
                // Case for World command
                if (executor.getCommandType() == CommandsType.WORLD) {
                    
                    // Writes the world command line 
                    LogWriter.write(line + "\n");
                    
                    // Writes the introduction to the Log file
                    output = initialComments();
                    LogWriter.write(output);
                    
                    // Writes the resulting World Boundary after initialization
                    output = executor.execute();
                    LogWriter.write(output);
                    
                    // GISData is updated with the World boundary
                    GISData = executor.getGISData();
                }
                
                // Case for Import Command
                else if (executor.getCommandType() == CommandsType.IMPORT) {                   
                    String importFileName = line.split("\\s+")[1];
                    
                    // If initially not imported
                    if (!imported) {
                        File importFile = new File(importFileName);
                        
                        // If import file does not exist
                        if (!importFile.exists()) {
                            
                            // If the import file does not exist, print out saying Import File does not exist
                            // and terminate the program
                            LogWriter.write(String.format("Import File [%s] does not exist", importFileName));                           
                            System.exit(0);
                        }
                        
                        // Initialize the DB File 
                        this.init_DBFile(importFile, DBFile);
                        imported = true;    // imported is set to true for the next import command
                    }
                    
                    // If previously have been imported
                    else {
                        // Just append to the existing DB file
                        append_DBFile(new File(importFileName), DBFile);
                    }
                    
                    // Writes the the import output: 
                    // Imported Features by name, Longest probe sequence, Imported Locations
                    output = executor.execute();
                    LogWriter.write(output);
                    
                    // After importing the data, GISData is updated
                    GISData = executor.getGISData();
                    
                    // commandNum is incremented for every command 
                    commandNum++;
                }
                
                // Rest of executing commands
                else {
                    // Again Executor is initialized for rest of each command line
                    executor = new Executor(this.GISData, 
                                               this.bufferPool,
                                               this.DBFile, 
                                               line, 
                                               this.commandNum);
                    
                    
                    // Writes the appropriate output for each command
                    output = executor.execute();
                    LogWriter.write(output);
                             
                    // commandNum is incremented for each command
                    commandNum++;
                }
            }
        }
        
        // Always close the File writer after its use due to memory issues
        LogWriter.close();
        scriptReader.close();
    }
    
    //////////////////////////////////////////////////////////////// append_DBFile(File importFile, File dbFile, String FileName)
    // This method is used for appending the new import data to the existing database file
    //
    // Parameters:
    //          importFile  importing file 
    //          dbFile      database file
    //
    // Pre:       
    //          DBFile is initialized
    //
    // Post:    
    //          DBFile is appended with the new data
    //          
    // Returns:   
    //          none
    //
    // Called by: 
    //          Controller
    //              execute()
    //
    private void append_DBFile(File importFile, File dbFile) throws IOException {
        // Write DB file
        FileWriter dbWriter = new FileWriter(dbFile, true);
        BufferedWriter bw = new BufferedWriter(dbWriter);
        PrintWriter print = new PrintWriter(bw);
        
        // Read import file
        BufferedReader importFileReader = new BufferedReader(new FileReader(importFile));
        
        // This is read twice here to skip the first description line for the data
        String line  = importFileReader.readLine();
        
        // Read and write to the database until the end of import file
        while ((line = importFileReader.readLine()) != null) {
            print.print(line + "\n");
        }
        
        // Always close after reading or writing the file to prevent memory problem
        print.close();
        importFileReader.close();
    }
    
    //////////////////////////////////////////////////////////////// init_DBFile(File importFile, File dbFile)
    // Initializes the database file when importing data for the first time
    //
    // Parameters:
    //          importFile  importing file 
    //          dbFile      database file
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          DBFile is initialized with imported data
    //          
    // Returns:   
    //          none
    //
    // Called by: 
    //          Controller
    //              execute()
    //
    private void init_DBFile(File importFile, File dbFile) throws IOException {
        InputStream importStream = new FileInputStream(importFile);
        OutputStream dbStream = new FileOutputStream(dbFile);
        
        // Provide enough space to read a line
        byte[] buffer = new byte[1024];
        int line;
        
        // Read the import file and write to the DBFile
        while ((line = importStream.read(buffer))> 0) {
            dbStream.write(buffer, 0, line);
        }
        
        // Always close writer and reader after it is used to prevent any memory problem
        importStream.close();
        dbStream.close();
    }
    
    //////////////////////////////////////////////////////////////// init_DBFile(File importFile, File dbFile)
    // Used to provide initial introduction to the Log File when world command is first called
    // provides the name of the database file, script file, log file, and the start time it is written
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          DBFile is initialized with imported data
    //          
    // Returns:   
    //          none
    //
    // Called by: 
    //          Controller
    //              execute()
    //
    private String initialComments() {
        
        StringBuilder builder = new StringBuilder();
        
        builder.append("\nGIS Program\n\n");
        builder.append(String.format("%-11s %s\n","dbFile:" ,DBFile.getName()));
        builder.append(String.format("%-11s %s\n", "script:", ScriptFile.getName()));
        builder.append(String.format("%-11s %s\n", "log:", LogFile.getName()));
        builder.append(String.format("%-11s %s\n", "Start time:", StringFormat.getTime()));
        builder.append("Quadtree children are printed in the order SW  SE  NE  NW\n");
        builder.append(StringFormat.endString());
        
        return builder.toString();
    }
}
