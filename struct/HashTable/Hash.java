package struct.HashTable;

import java.util.ArrayList;
//Project J4 for CS 3114 Summer I 2017
//
//Programmer:    Dong Gyu Lee
//OS:            Windows 10 Education
//System:        i5-4300U, 8 GB Memory
//Compiler:      Eclipse Neon.3 Release (4.6.3)
//Last modified: June 23, 2017
//
// Interface for Hash Table
// Provides important methods to be implemented
//
public interface Hash {
    
    //////////////////////////////////////////////////////////////// getValues()
    // Gets a list of values from the element
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Element is unchanged     
    //          
    // Returns:   
    //          arraylist of values of the element
    //
    // Called by: 
    //          HashTable
    //              getValues(String key)
    //              remove(String key)
    //
    // Calls:   
    //         none
    //
    public ArrayList<Long> getValues(String key);
    
    //////////////////////////////////////////////////////////////// findKey(String key)
    // Find the key within the Hash Table and changes the field variable key_index when
    // this method is called.
    // key_index is set to the index where either value is found or not found in the table
    //
    // Parameters:
    //          key key of the element values to be found from Hash Table
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Element to the key is either found or not found
    //          key_index is updated
    //          
    // Returns:   
    //          If element is found in the Hash Table returns true
    //          If not found returns false
    //
    //
    public boolean findKey(String key);
    
    //////////////////////////////////////////////////////////////// insert(String key, long value)
    // Inserts key to the Hash table
    // If successfully inserted, true
    // otherwise false
    //
    // Parameters:
    //          key key and value to be inserted into Hash Table
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          key and mapped value is either inserted or not inserted
    //          
    // Returns:   
    //          True, if successfully inserted,
    //          False, otherwise
    //
    //
    public boolean insert(String key, long value);
    
    //////////////////////////////////////////////////////////////// remove(String key)
    // Removes the element that corresponds to the key from the table
    // If successful returns true
    // If failed to delete returns false
    //
    // Parameters:
    //          key key to be deleted from Hash Table
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Either key is deleted or not deleted from Hash Table
    //          
    // Returns:   
    //          True, if successfully deleted,
    //          False, otherwise
    //
    //
    public boolean remove(String key);
    
    //////////////////////////////////////////////////////////////// recapacity()
    // Doubles the hash table capacity
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Capacity of Hash Table is doubled
    //          
    // Returns:   
    //          none
    //
    //
    public void recapacity();
    
    //////////////////////////////////////////////////////////////// toString()
    // Returns the detail of the element
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Element is unchanged     
    //          
    // Returns:   
    //          arraylist of values of the element
    //
    // Called by: 
    //          HashTable
    //              toString()
    //
    // Calls:   
    //         none
    //
    public String toString();
}
