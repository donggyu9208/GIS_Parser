// On my honor:
//
// - I have not discussed the Java language code in my program with
// anyone other than my instructor or the teaching assistants
// assigned to this course.
//
// - I have not used Java language code obtained from another student,
// or any other unauthorized source, including the Internet, either
// modified or unmodified.
//
// - If any Java language code or documentation used in my program
// was obtained from another source, such as a text book or course
// notes, that has been clearly noted with a proper citation in
// the comments of my program.
//
// - I have not designed this program in such a way as to defeat or
// interfere with the normal operation of the supplied grading code.
//
// Dong Gyu Lee

import java.io.File;
import java.io.IOException;
import gis_parser.Controller;

// Project J4 for CS 3114 Summer I 2017
//
// Programmer:    Dong Gyu Lee
// OS:            Windows 10 Education
// System:        i5-4300U, 8 GB Memory
// Compiler:      Eclipse Neon.3 Release (4.6.3)
// Last modified: June 23, 2017
//
// This is the main class for GIS project.
// The program takes the name of three files
// <database file name> <command script file name> <log file name> in this order
//
// Database file is created as an empty file and if the specified database file already exists
// existing file is rewritten over with the new data.
//
// Similarly, log file is rewritten every time the program is run, and if the log file already exists
// it is rewritten over with the new data
//
// If the command script file is not found the program writes an error message to the console and exit.
//
//
// This program can by the command 
// java GIS <database file name> <command script file name> <log file name>
// after compiling the java file
//
public class GIS {
    public static void main(String[] args) throws IOException {
        
        String DBFileName     = args[0];
        String ScriptFileName = args[1];
        String LogFileName    = args[2];

       
        File DBFile  = new File(DBFileName);
        File LogFile = new File(LogFileName);
        
        // If DB file and Log file does not exist
        // Create one so that we do not encounter an error 
        // for unable to find the file
        if (!DBFile.exists()) {
            DBFile.createNewFile();          
        }
        
        if (!LogFile.exists()) {
            LogFile.createNewFile();
        }
        
        File ScriptFile = new File(ScriptFileName);
        // When Script file does not exist we cannot really proceed further to
        // test the program. So we exit immediately and prints out 
        // "There is not [Script File] to run the program
        if (!ScriptFile.exists()) {
            System.out.println("There is no [Script File] to run the program");
            System.exit(0);
        }
        
        // Controller class takes all three DB file, Log file, Script file and 
        // execute commands accordingly.
        Controller control = new Controller(DBFile, LogFile, ScriptFile);
        control.execute();
    }

}
