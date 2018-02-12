package stringFormat;
//Project J4 for CS 3114 Summer I 2017
//
//Programmer:    Dong Gyu Lee
//OS:            Windows 10 Education
//System:        i5-4300U, 8 GB Memory
//Compiler:      Eclipse Neon.3 Release (4.6.3)
//Last modified: June 23, 2017
//
// Takes in a line of raw-data and the offset and 
// parses the data into String array
// 
public class GISDataParser implements Comparable<GISDataParser> {
    
    // Data feature at each position(index) of data array:
    // ----------------------------------------------------
    // 0:   Feature ID             [Integer]        ex. 1481345
    // 1:   Feature Name           [String]         ex. Asbury Church
    // 2:   Feature Class          [String]         ex. Church
    // 3:   State Alpha            [String]         ex. VA
    // 4.   State Numeric          [String]         ex. 51
    // 5:   County Name            [String]         ex. Highland
    // 6:   County Numeric         [Integer]        ex. 091
    // 7:   Primary Latitude DMS   [String]         ex. 382607N
    // 8:   Primary Longitude DMS  [String]         ex. 0793312W
    // 9:   Primary Latitude DEC   [Real Number]    ex. 38.4353981
    // 10:  Primary longitude DEC  [Real Number]    ex. -79.5533807
    // 11:  Source Latitude DMS    [String]         ex. 371518N
    // 12:  Source Longitude DMS   [String]         ex. 0814605W
    // 13:  Source Latitude DEC    [Real Number]    ex. 37.255
    // 14:  Source Longitude DEC   [Real Number]    ex. -81.7680556
    // 15:  Elevation (meters)     [Integer]        ex. 603
    // 16:  Elevation (feet)       [Integer]        ex. 1978
    // 17:  Map Name               [String]         ex. Bradshaw
    // 18:  Date Created           [String]         ex. 09/28/1979
    // 19:  Date Edited            [String]         ex. 11/17/2006
    // 20:  Offset                 [Long]           
    // 21:  Full Data Line         [String]         
    // ----------------------------------------------------
    
    private String[] GISData;
    
    //////////////////////////////////////////////////////////////// import_CoordIndex(File DBFile)
    // Constructor for GISDataParser take takes the raw-data line
    // and parses into String array separated by each feature
    //
    // Parameters:
    //          dataLine raw-data line
    //  
    // Pre:       
    //          none
    //
    // Post:    
    //          Raw-data line is parsed
    //
    public GISDataParser(String dataLine) {
        // Since there exists 22 data types explained in the above table 
        // we need a string array of size 22
        GISData = new String[22];
        
        // Split the raw data by '|' character and insert into String array
        String[] temp = dataLine.split("\\|");
        for (int i = 0; i < temp.length; i++) {
            GISData[i] = temp[i];
        }
        
        // Since offset is not part of raw-data we need to insert separately
        GISData[20] = "NULL";
        GISData[21] = dataLine;
    }
    
    //////////////////////////////////////////////////////////////// import_CoordIndex(File DBFile)
    // Constructor for GISDataParser take takes the raw-data line
    // and parses into String array separated by each feature
    //
    // Parameters:
    //          dataLine raw-data line
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Raw-data line is parsed
    //
    public GISDataParser(String dataLine, String offset) {
        // Since there exists 22 data types explained in the above table 
        // we need a string array of size 22
        GISData = new String[22];
        
        // Split the raw data by '|' character and insert into String array
        String[] temp = dataLine.split("\\|");
        for (int i = 0; i < temp.length; i++) {
            GISData[i] = temp[i];
        }
        
        // Since offset is not part of raw-data we need to insert separately
        GISData[20] = offset;
        GISData[21] = dataLine;
    }
    
    // Rest of these methods is to retrieve individual feature from the raw data
    
    public String[] getGISData() {
        return GISData;
    }
    
    public String getdataLine() {
        return GISData[21];
    }
    
    public String getFeatureID() {
        return GISData[0];
    }
    
    public String getFeatureName() {
        return GISData[1];
    }
    
    public String getFeatureClass() {
        return GISData[2];
    }
    
    public String getStateName() {
        return GISData[3];
    }
    
    public String getStateNum() {
        return GISData[4];
    }
    
    public String getCountyName() {
        return GISData[5];
    }
    
    public String getCountyNum() {
        return GISData[6];
    }
    
    public String getPrimaryLatDMS() {
        return GISData[7];
    }
    
    public String getPrimaryLongDMS() {
        return GISData[8];
    }
    
    public String getElevationM() {
        return GISData[15];
    }
    
    public String getElevationFt() {
        return GISData[16];
    }
    
    public String getMapName() {
        return GISData[17];
    }
    
    public String getDateCreated() {
        return GISData[18];
    }
    
    public String getDateEdited() {
        return GISData[19];
    }
    
    public String getOffset() {
        return GISData[20];
    }

    //////////////////////////////////////////////////////////////// compareTo(GISDataParser o)
    // Override the compare to method 
    // This is used to sort the arrary of raw data by the feature name
    //
    @Override
    public int compareTo(GISDataParser o) {
        return getFeatureName().compareTo(o.getFeatureName());
    }
}
