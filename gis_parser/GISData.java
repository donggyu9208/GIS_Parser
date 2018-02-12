package gis_parser;

import struct.HashTable.*;
import struct.QuadTree.*;
import struct.QuadTree.GISClient.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import stringFormat.StringFormat;

// Project J4 for CS 3114 Summer I 2017
//
// Programmer:    Dong Gyu Lee
// OS:            Windows 10 Education
// System:        i5-4300U, 8 GB Memory
// Compiler:      Eclipse Neon.3 Release (4.6.3)
// Last modified: June 23, 2017
//
// GIS Data holds two different index:
//  1. Name Index
//  2. Coordinate Index
// 
// Holds the parsed raw data to its offset 
// Name Index stores both feature name and its state name and the corresponding offset
// Coordinate Index stores the coordinate of each location and its offset
//
public class GISData {
    // DB File
    private RandomAccessFile DBFile;        // DBFile to retrieve data and store in the GISData
    
    // Data Structure
    private HashTable NameIndex;            // Name Index with the Hash Table data structure
    private prQuadTree<Point> CoordIndex;   // Coordinate Index with the prQaudTree data structure stores Point elments
    
    // Private Fields 
    private int name_offset;                // offset that keeps track of importing data to Name Index
    private int coord_offset;               // offset that keeps track of importing data to Coordinate Index
    
    private boolean name_imported;          // Checks if name index has already been imported
    private boolean coord_imported;         // Checks if coordinate index has already been imported
    
    //////////////////////////////////////////////////////////////// GISData(long xLo, long xHi, long yLo, long yHi)
    // Construct the world boundary for the GISData
    // Initially offsets start at 0
    // No data is imported
    // Name Index and Coord Index are initialized
    //
    // Parameters:
    //          xLo     Minimum x-boundary
    //          xHi     Maximum x-boundary
    //          yLo     Minimum y-boundary
    //          yHi     Maximum y-boundary
    //
    // Pre:       
    //          None
    //
    // Post:    
    //          GISData is initialized with Name Index and Coordinate Index
    //
    public GISData(long xLo, long xHi, long yLo, long yHi) {
        // Offsets start at 0
        this.name_offset = 0;
        this.coord_offset = 0;
        
        // Data has not been imported
        this.coord_imported = false;
        this.name_imported = false;
        
        // Name Index and Coordinate Index are initialized
        this.CoordIndex = new prQuadTree<Point>(xLo, xHi, yLo, yHi);
        this.NameIndex = new HashTable();
    }
    
    //////////////////////////////////////////////////////////////// getCoordIndex()
    // Gets the Coordinate Index from GISData
    //
    // Pre:       
    //          Coordinate Index is initialized
    //
    // Post:    
    //          GISData is unchanged
    //          
    // Returns:   
    //          Coordinate Index
    //
    // Called by:
    //          Commands
    //              execute_import()
    //              execute_debug()
    //              execute_what_is_at()
    //              execute_what_is_in()
    //              execute_what_is_in_long()
    //              execute_what_is_in_filter()
    //
    // Calls:   
    //          none
    //
    public prQuadTree<Point> getCoordIndex() {
        return this.CoordIndex;
    }
    
    //////////////////////////////////////////////////////////////// getNameIndex()
    // Gets the Name Index from GISData
    //
    // Pre:       
    //          Name Index is initialized
    //
    // Post:    
    //          GISData is unchanged
    //          
    // Returns:   
    //          Name Index
    //
    // Called by:
    //          Commands
    //              execute_import()
    //              execute_debug()
    //              execute_what_is()
    //
    // Calls:   
    //          none
    //
    public HashTable getNameIndex() {
        return this.NameIndex;
    }
    
    //////////////////////////////////////////////////////////////// import_CoordIndex(File DBFile)
    // Imports from DBFile and updates the Coordinate Index
    //
    // Pre:       
    //          Coordinate Index is initialized
    //
    // Post:    
    //          Coordinate Index is updated with the database
    //          
    // Returns:   
    //          none
    //
    // Called by:
    //          Commands
    //              execute_import()
    //
    // Calls:   
    //          none
    //
    public void import_CoordIndex(File DBFile) throws IOException {
        this.DBFile = new RandomAccessFile(DBFile, "r");
        
        String[] dataParsed;   // Parsed data into each word separated by '|' 
        String   dataLine;     // Raw-data line
        
        // if not previously imported you skip the first line other wise you read first the line
        // Because first line is just the description of the database file
        // It does not need to be repeated when imported for the second time
        if (!coord_imported) {
            dataLine = this.DBFile.readLine();
            
            // Get the data Bytes of the first line + 1 is the offset of the beginning of the next line
            this.coord_offset += dataLine.getBytes(Charset.defaultCharset()).length + 1;
            
            // once first imported, coord_imported is true
            coord_imported = true;
        }
        
        // if previously has been imported, we do not read the data base all the way from the beginning
        // Re-reading the database over from the beginning slows down the process
        // Therefore we keep track of the offset of the last time is it imported and continue
        // from the last line of the last offset. 
        // This will improve the performance by not needing to read all the way from the beginning
        else {
            this.DBFile.seek(this.coord_offset);
        }
        
        // Read Until the end of the file
        while ((dataLine = this.DBFile.readLine()) != null) {
            dataParsed = dataLine.split("\\|");
            
            Point point;
            // Case for if Unknown is found in the DMS data file
            // We set the coordinate as 0
            if (dataParsed[8].equals("Unknown") || dataParsed[9].equals("Unknown")) {
                point = new Point(0, 0, coord_offset);
            }
            // Otherwise we just import the coordinates and update Coordinate Index
            else {
                point = new Point(StringFormat.convertDMStoSec(dataParsed[8]),     // DMS Long
                                    StringFormat.convertDMStoSec(dataParsed[7]),   // DMS Lat
                                    coord_offset);                              // Offset
            }
            
            // Every point retrieved from the database, insert into coordinate index
            this.CoordIndex.insert(point);
            
            // offset is incremented to the next line end the end of each line
            this.coord_offset += dataLine.getBytes(Charset.defaultCharset()).length + 1;
        }
    }
    
    //////////////////////////////////////////////////////////////// import_NameIndex(File DBFile)
    // Imports from DBFile and updates the Name Index
    //
    // Pre:       
    //          Name Index is initialized
    //
    // Post:    
    //          Name Index is updated with the database
    //          
    // Returns:   
    //          none
    //
    // Called by:
    //          Commands
    //              execute_import()
    //
    // Calls:   
    //          none
    //
    public void import_NameIndex(File DBFile) throws IOException {
        // Import_NameIndex operation is very similar to import_CoordIndex
        this.DBFile = new RandomAccessFile(DBFile, "r");
        
        String[] dataParsed;   // Parsed data into each word separated by '|' 
        String   dataLine;     // Raw-data line
        
        // if not previously imported you skip the first line other wise you read first the line
        // Because first line is just the description of the database file
        // It does not need to be repeated when imported for the second time
        if (!name_imported) {
            dataLine = this.DBFile.readLine();
            
            // Get the data Bytes of the first line + 1 is the offset of the beginning of the next line
            name_offset += dataLine.getBytes(Charset.defaultCharset()).length + 1;
            
            // once first imported, name_imported is true
            name_imported = true;
        }
        else {
            // if previously has been imported, we do not read the data base all the way from the beginning
            // Re-reading the database over from the beginning slows down the process
            // Therefore we keep track of the offset of the last time is it imported and continue
            // from the last line of the last offset. 
            // This will improve the performance by not needing to read all the way from the beginning
            this.DBFile.seek(this.name_offset);
        }
        
        // Read Until the end of the file
        while ((dataLine = this.DBFile.readLine()) != null) {
            dataParsed = dataLine.split("\\|");
            
            // We only parse Feature Name and State Name into Name Index: 
            // dataParsed[1] == FEATURE_NAME
            // dataParsed[2] == STATE_NAME
            this.NameIndex.insert(dataParsed[1] + ":" + dataParsed[3], name_offset); // FEATURE_NAME
            
            // offset is incremented to the next line end the end of each line
            name_offset += dataLine.getBytes(Charset.defaultCharset()).length + 1;
        }
    }
}
