package struct.HashTable;

import java.util.ArrayList;

// Project J4 for CS 3114 Summer I 2017
//
// Programmer:    Dong Gyu Lee
// OS:            Windows 10 Education
// System:        i5-4300U, 8 GB Memory
// Compiler:      Eclipse Neon.3 Release (4.6.3)
// Last modified: June 23, 2017
//
// Hash table is used for name index 
// (feature name:state abbreviation, and the offset(s) of matching record(s))
//
// Hash table entry will store a feature name and state abbreviation 
// (concatenated) and the file offset(s) of the matching record(s).  
// 
// The Hash Table uses quadratic probing to resolve collisions with the quadratic 
// function of (n^2 + n) / 2 to compute the step size.
// 
// The hash table uses a contiguous physical structure (array).
//
// The initial capacity of the table is 1024, and the table resizes
// itself automatically when it becomes 70% full, by doubling its capacity
//
// elfhash() function is used from the course notes and 
// apply it to the concatenation of the feature name and state field
// of the data records
//
public class HashTable implements Hash {
    
    //////////////////////////////////////////////////////////////// Element
    // Class for element in the hash table
    // Element stores String key and ArrayList<Long> value
    // Since there can exist more than one offset at the single key,
    // we need an array to store that offset
    //
    private class Element {
        private String key;                 // Key that stores concatenated Feature Name and State Name
        private ArrayList<Long> valueList;  // List that stores offset at the key
        
        //////////////////////////////////////////////////////////////// Element(String key, Long value)
        // Constructor for Element that initializes the array list of offsets and connects
        // key to the value.
        // if value is null, array list is set to null
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
        public Element(String key, Long value) {
            this.valueList = new ArrayList<Long>();
            this.key = key;
            
            if (value != null) {
                this.valueList.add(value);
            }
            else {
                valueList = null;
            }
        }
        
        //////////////////////////////////////////////////////////////// getKey()
        // Gets a key from the element
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
        //          key of the element
        //
        // Called by: 
        //          HashTable
        //              findKey(String key)
        //              recapacity()
        //              toString()
        //
        // Calls:   
        //         none
        //
        public String getKey() {
            return key;
        }
        
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
        public ArrayList<Long> getValues() {
            return valueList;
        }
        
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
        public String toString() {
            StringBuilder builder = new StringBuilder();
            
            builder.append('[');
            builder.append(valueList.get(0));
            
            // There can exist multiple values in an element
            for (int i = 1; i < valueList.size(); i++) {
                builder.append(", " + valueList.get(i));
            }
            
            builder.append(']');
            return builder.toString();
        }
    }
    
    private int capacity;           // Total number of elements that can be inserted into Hash Table
    private int totalElemCount;     // Number of unique element keys in the list
    private int elemCount;          // Total number of elements including the duplicate keys with many offsets
    
    private Element[] ElementList;  // Element list that holds keys in hash table
    private int key_index;          // Helper index for getKey() findKey() and recapacity()
    private int longest_probe;      // Longest probe in the Hash Table
    
    private final Element TOMB = new Element(null, null);   // Tomb Element Place Holder when the element is removed from the Hash Table
    
    //////////////////////////////////////////////////////////////// HashTable()
    // Initializes Hash Table with 1024 capacity, elemCount = 0, and totalElemCount = 0
    // Hash Table initially contains no data
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          None
    //
    // Post:    
    //          Capacity       == 1024;
    //          totalElemCount == 0;
    //          elemCount      == 0;
    //          ElementList is initialized
    //
    public HashTable() {
        capacity = 1024;
        totalElemCount = 0;
        elemCount = 0;
        ElementList = new Element[capacity];
    }
    
    //////////////////////////////////////////////////////////////// elfHash(String toHash)
    // elfHash Function used to probe the sequence, given in the course notes
    // Given a key string, provides an index after calculated by elfHash
    //
    // Parameters:
    //          toHash key of the element to be inserted in to Hash Table
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          index of the element in calculated
    //          
    //          
    // Returns:   
    //          index of the element
    //
    // Called by: 
    //          HashTable
    //              findKey(String key)
    //
    public long elfHash(String toHash) {
        long hashValue = 0;
        
        for (int Pos = 0; Pos < toHash.length(); Pos++) {       // use all elements
            
            hashValue = (hashValue << 4) + toHash.charAt(Pos);  // shift/mix
            
            long hiBits = hashValue & 0xF000000000000000L;      // get high nybble
            
            if (hiBits != 0) {
                hashValue ^= hiBits >> 56;          // xor high nybble with second nybble
            }
            
            hashValue &= ~hiBits;               // clear high nybble
        }
        return hashValue;
    }
    
    //////////////////////////////////////////////////////////////// getValues(String key)
    // Given a key, get the corresponding values in the hash table
    // This method is not really use for the current case, however this method is 
    // important to exist for Hash Table structure.
    // Although this method is not used in this project, it exists wince this is the Hash Table structrue class
    //
    // Parameters:
    //          key key of the element values to be retrieved from Hash Tale
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Element to the key is either found or not found
    //          Key_index is updated
    //          
    // Returns:   
    //          If element is found in the Hash Table appropriate array list of values is returned
    //          If not found returns null
    //
    //
    public ArrayList<Long> getValues(String key) {
        boolean found = findKey(key);
        
        // if found get return the list of values
        if (found) {
            return ElementList[key_index].getValues();
        }
        
        // if not found return null
        return null;
    }
    
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
    public boolean findKey(String key) {
        int index;          // changing index as probing through the table
        int start_index;    // starting index of the key 
        int iter;           // number of probe sequence
        key_index = -1;     // key_index starts as -1 and probes through the table
        
        // if the key is null there is no such data exists in the table and returns false
        if (key == null) {
            return false;
        }
        
        iter = 0;               // probe sequence starts as 0 probe sequence
        index = (int) ((elfHash(key) + ((iter * iter + iter) / 2)) % capacity); // convert long to integer value
        start_index = index;    // starting index of the key
        iter++;
        do {
            Element elem;
            elem = ElementList[index];
            
            // Case: if the slot is not empty there is either TOMB element or some value stored in the slot
            if (elem != null) {
                
                // Case for TOMB
                if (elem == TOMB) {
                    
                    // if it is TOMB just skip to the next slot and update key_index
                    if (key_index < 0) {
                        key_index = index;
                    }
                }
                
                // Case for finding an element that matches with the key
                else if (key.equals(elem.getKey())) {
                    
                    // update the key_index and return true
                    key_index = index;
                    return true;
                }
                
                // if it is the second probe sequence we calculate the next probe sequence with
                // quadratic probing method
                if (iter >= 1) {
                    
                    // For every probe, check if it is bigger than the longest previous probe
                    // and update longest_probe
                    if (iter > this.longest_probe) {
                        longest_probe = iter;
                    }
                    
                    // Use the formula: Next(k) = (F(key) + ((k^2 + k) / 2) % M since
                    // the number of slots in the table is a power of 2 and the period is 
                    // guaranteed to be M == capacity
                    index = (int) ((elfHash(key) + ((iter * iter + iter) / 2)) % capacity);
                    iter++;
                }
            }
            
            // Case: of slot it empty
            else {
                
                // update key_index for the empty slot and reutrn false
                if (key_index < 0) {
                    key_index = index;
                    return false;
                }
            }
        
        // continue looping until we encounter the start index
        } while (index != start_index);
        
        // At this point probe has once looped the whole hash table 
        // and we failed to find the key in the table
        return false;
    }
    
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
    public boolean insert(String key, long value) {
        // Every time size of the Hash table reaches 70% of the capacity
        // double the size of the hash table
        if (elemCount > capacity * 0.7) {
            recapacity();
        }
        
        boolean found = findKey(key); // Checks if key exists in the Hash Table
        
        // This counts for duplicate names but with different offsets
        // Only the imported names count increases but the acutal number of 
        // elements in the hash table does not increase
        
        // Case: if key is found in the Hash Table
        if (found) {
            
            // we still insert the value (offset) in the table
            // since there can exist the same name but different offsets
            ArrayList<Long> tempValueList = ElementList[key_index].valueList;
            for (int i = 0; i < tempValueList.size(); i++) {
                if (value == tempValueList.get(i)) {
                    return false;
                }
            }
            
            // Add only the value in the value list
            // Because key already exists in the list
            ElementList[key_index].valueList.add(value);
            totalElemCount++;
            return true;
        }
        
        // if key is not found, key_index would have already been updated from
        // find(String key) command
        // We just need to insert the key and the value into the Hash Table
        if (key_index >= 0) {
            ElementList[key_index] = new Element(key, value);
            
            totalElemCount++;
            elemCount++;
            
            return true;
        }
        
        return false;
    }
    
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
    public boolean remove(String key) {
        
         
        boolean found = findKey(key); 
        
        // If the key is not found we cannot delete
        if (!found) {
            return false;
        }
        
        // total Element count should be decremented by the number of values (offsets)
        // the key holds
        totalElemCount -= ElementList[key_index].getValues().size();
        
        // Always replace the slot with TOMB when deleted
        ElementList[key_index] = TOMB;
        
        // element count is decremented
        elemCount--;
        return true;
    }
    
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
    public void recapacity() {
        
        Element[] oldList = ElementList;        // stores old hash table
        
        capacity = capacity * 2;                // Capacity is doubled
        ElementList = new Element[capacity];    
        
        Element oldElem;
        
        // Transfers all elements in the old list to the new ElementList
        for (int i = 0; i < oldList.length; i++) {
            oldElem = oldList[i];
            
            // For each element in the old list, find the index value and place it accordingly
            if (oldElem != null && oldElem != TOMB) {
                findKey(oldElem.getKey()); 
                ElementList[key_index] = oldElem;
            }
        }
    }
    
    //////////////////////////////////////////////////////////////// getLongestProbe()
    // Returns the longest probe sequence of Hash Table
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Hash Table is unchanged
    //          
    // Returns:   
    //          Longest probe sequence of hash table
    //
    //
    public int getLongestProbe() {
        return longest_probe;
    }
    
    //////////////////////////////////////////////////////////////// getElemCount()
    // Returns the number of elements in the hash table
    // excluding the duplicates at different offsets
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Hash Table is unchanged
    //          
    // Returns:   
    //          number of elements in the hash table
    //
    //
    public int getElemCount() {
        return this.elemCount;
    }
    
    //////////////////////////////////////////////////////////////// getTotalElemCount()
    // This returns the total number of elements in the hash table
    // including the duplicates at different offsets
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Hash Table is unchanged
    //          
    // Returns:   
    //          total number of elements in the hash table
    //
    //
    public int getTotalElemCount() {
        return this.totalElemCount;
    }
    
    //////////////////////////////////////////////////////////////// toString()
    // Display contents of the hash table in a readable manner
    // 
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Hash Table is unchanged
    //          
    // Returns:   
    //          Contents of the hash table
    //
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("Format of display is\n");
        builder.append("Slot number: data record\n");
        builder.append("current table size is " + this.capacity + "\n");
        builder.append("Number of elements in table is " + this.elemCount + "\n\n");
        
        Element elem;
        for (int i = 0; i < ElementList.length; i++) {
            
            elem = ElementList[i];
            if (elem != null) {
                builder.append(String.format("%8d:", i));
                if (elem != TOMB) {
                    builder.append(String.format("  [%s, %s]", elem.getKey(), elem.toString()));
                }
                builder.append('\n');
            }
        }
        return builder.toString();
    }
}
