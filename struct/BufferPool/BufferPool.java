package struct.BufferPool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedList;

//Project J4 for CS 3114 Summer I 2017
//
//Programmer:    Dong Gyu Lee
//OS:            Windows 10 Education
//System:        i5-4300U, 8 GB Memory
//Compiler:      Eclipse Neon.3 Release (4.6.3)
//Last modified: June 23, 2017
//
// Buffer pool is presented as a front end for the GIS database file
// to improve search speed. All searches or retrieving a GIS record 
// from the database files are managed through the buffer pool.
//
// The buffer pool for the database file is capable of buffering up to 
// 15 records and uses LRU (Least Recently Used) replacement strategy
// 
// Linked List structure is used to implement buffer pool and since
// the buffer pool is manged through the order in which it is added
// with the linked list structure it is very easy and fast to insert 
// the most recent data to the front and delete the last data.
//
// Buffer pool stores somewhat interpreted raw data, organized in a 
// String array and its offset.
//
public class BufferPool {
    private final static int BUFFER_POOL_SIZE = 15; // Size of the Buffer Pool
    private int              curr_size;             // Current size of the buffer pool
    private RandomAccessFile DBFile;                // Database that buffer pool retrieves data from
    
    private LinkedList<String[]> bufferPool;        // Buffer Pool that stores the parsed data
    
    
    //////////////////////////////////////////////////////////////// BufferPool(File DBFile)
    // Constructor for BufferPool that takes database file as a parameter
    // and initializes the pool
    //
    // Parameters:
    //          DBFile  database file to retrieve data from
    //
    // Pre:       
    //          None
    //
    // Post:    
    //          Buffer pool is initialized and ready to read data from database
    //
    public BufferPool(File DBFile) throws FileNotFoundException {
        this.DBFile     = new RandomAccessFile(DBFile, "r");
        this.bufferPool = new LinkedList<String[]>();
        this.curr_size  = 0;
    }
    

    //////////////////////////////////////////////////////////////// insert(String dataLine, String offset)
    // Insert the data line and offset into buffer pool
    // When inserted both the lines, they are parsed in a string array
    //
    // Parameters:
    //          dataLine    raw data line
    //          offset      offset at which data line is found
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          new data is inserted into buffer pool with its offset        
    //          
    // Returns:   
    //          none
    //
    // Called by: 
    //          BufferPool
    //               getData(String offset)
    //
    public void insert(String dataLine, String offset) {
        String[] dataSet = {offset, dataLine};
        
        // Whenever an data is inserted, remove the last element
        if (curr_size == BUFFER_POOL_SIZE) {
            bufferPool.removeLast();
        }
        else {
            curr_size++;
        }
        
        // Element is always added to the first of the list
        bufferPool.addFirst(dataSet);
    }
    
    //////////////////////////////////////////////////////////////// find(String offset)
    // Finds the offset within the buffer pool
    // If exists returns the corresponding String array
    //
    // Parameters:
    //          offset      offset to be found from buffer pool
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          offset from buffer pool is either found or not found
    //          
    // Returns:   
    //          none
    //
    // Called by: 
    //          BufferPool
    //               getData(String offset)
    //
    public String[] find(String offset) {
        String[] tempData;
        
        // Iterate through the buffer pool to find the offset
        for (int i = 0; i < curr_size; i++) {
            tempData = bufferPool.get(i);
            
            // Offset is stored at the first index of the String array
            if (tempData[0].equals(offset)) {
                return tempData;
            }
        }
        return null;
    }
    
    //////////////////////////////////////////////////////////////// getData(String offset)
    // Retrieves the offset raw data from the buffer pool at the offset
    // Buffer pool first looks into the buffer pool if the corresponding offset exists
    // If it does not exist you retrieve from the database file
    //
    // Parameters:
    //          offset      offset of the data to be retrieved
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          corresponding raw-data of the offset is retrieved
    //          
    // Returns:   
    //          raw-data at offset
    //
    // Called by: 
    //          Commands
    //               execute_what_is_at()
    //               execute_what_is_in()
    //               execute_what_is_in_long()
    //               execute_what_is_in_filter()
    //
    //
    public String getData(String offset) throws NumberFormatException, IOException {
        String[] foundData = find(offset);
        
        String data;
        // If the data looking for is not in Buffer Pool:
        // 1. Retrieve data directly from the database file
        // 2. Insert the retrieved data into Buffer Pool
        // 3. Return found Data
        if (foundData == null) {
            // Only if offset is not found within the buffer pool
            // seek into database
            DBFile.seek(Long.valueOf(offset));
            
            data = DBFile.readLine();
            insert(data, offset);
        } else {
            // Inserts into the front of the list, if found
            bufferPool.remove(foundData);
            bufferPool.addFirst(foundData);
            data = foundData[1];
        }
        return data;
    }
    
    //////////////////////////////////////////////////////////////// getNameIndex(String feature_name, String state_abbr)
    // Finds the name index from buffer pool
    // if found returns the corresponding list of offsets
    //
    // Parameters:
    //          feature_name      feature name to be found
    //          state_abbr        State name to be found
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Either Name index is found from Buffer pool or not
    //          
    // Returns:   
    //          a list of offsets from buffer pool corresponding to
    //          feature name and state name
    //
    public ArrayList<String[]> getNameIndex(String feature_name, String state_abbr) {
        String[] dataSet;
        
        ArrayList<String[]> foundList = new ArrayList<String[]>();
        for (int i = 0; i < curr_size; i++) {
            dataSet = bufferPool.get(i);
            if (dataSet[1].equals(feature_name) &&
                dataSet[3].equals(state_abbr)) {
                foundList.add(dataSet);
            }
        }
        return foundList;
    }
    
    //////////////////////////////////////////////////////////////// toString()
    // Displays the content of buffer pool from Most recently used to Least recently used
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Content of the buffer pool is formatted
    //          
    // Returns:   
    //          Contents of Buffer pool
    //
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MRU\n");
        
        for (int i = 0; i < curr_size; i++) {
            // 21st data holds offset and 22nd data holds data line
            builder.append(String.format("%8s:  %s\n", 
                                        bufferPool.get(i)[0], bufferPool.get(i)[1]));
        }
        
        builder.append("LRU\n");
        return builder.toString();
    }    
}
