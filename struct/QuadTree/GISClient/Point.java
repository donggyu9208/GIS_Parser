package struct.QuadTree.GISClient;
import java.util.ArrayList;
import struct.QuadTree.Compare2D;
import struct.QuadTree.Direction;

//Project J4 for CS 3114 Summer I 2017
//
//Programmer:    Dong Gyu Lee
//OS:            Windows 10 Education
//System:        i5-4300U, 8 GB Memory
//Compiler:      Eclipse Neon.3 Release (4.6.3)
//Last modified: June 23, 2017
//
// Point is an element that Coord Index stores
// Point stores both location data and its corresponding offset
// Since there can exists more than one offsets at one location
// Point stores its offsets as a list
//
public class Point implements Compare2D<Point> {

	private double xcoord; // x coordinate of the point
	private double ycoord; // y coordinate of the point
	
	public ArrayList<Double> offsetList; // List of offsets
	
    //////////////////////////////////////////////////////////////// Point(double x, double y)
    // Constructor for the Point that is initialized with only x and y coordinate
	// offset is not initially added and this constructor is used usually to check in which
	// quadrant the point is supposed to go in
    //
    // Parameters:
    //          x x-coordinate of the point
	//          y y-coordinate of the point
    //
    // Pre:       
    //          None
    //
    // Post:    
    //          Point is initialized with given x and y coordinate
    //
	public Point(double x, double y) {
        xcoord = x;
        ycoord = y;
        
        offsetList = new ArrayList<Double>();
    }
	
    //////////////////////////////////////////////////////////////// Point(double x, double y, double offset)
    // Constructor for the Point that is initialized with x and y coordinate and its corresponding offset
    // offset is added in the beginning of the list that Point stores
    //
    // Parameters:
    //          x       x-coordinate of the point
    //          y       y-coordinate of the point
	//          offset offset to be stored in the Point
    //
    // Pre:       
    //          None
    //
    // Post:    
    //          Point is initialized with given x and y coordinate and an offset
    //
	public Point(double x, double y, double offset) {
		xcoord = x;
		ycoord = y;
		
		offsetList = new ArrayList<Double>();
		offsetList.add(offset);
	}
	
    //////////////////////////////////////////////////////////////// getX()
    // Returns the x-coordinate field of Point
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Point is unchanged     
    //          
    // Returns:   
    //          x-coordinate field of Point
    //
	public double getX() {
		return xcoord;
	}
	
    //////////////////////////////////////////////////////////////// getY()
    // Returns the y-coordinate field of Point
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Point is unchanged     
    //          
    // Returns:   
    //          y-coordinate field of Point
    //
	public double getY() {
		return ycoord;
	}
	
    //////////////////////////////////////////////////////////////// getOffsetList()
    // Returns the y-coordinate field of Point
    //
    // Parameters:
    //          none
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Point is unchanged     
    //          
    // Returns:   
    //          y-coordinate field of Point
    //
	public ArrayList<Double> getOffsetList() {
	    return offsetList;
	}
	
    //////////////////////////////////////////////////////////////// addOffset(Point dupPoint)
    // Add offset to the Point list
    //
    // Parameters:
    //          dupPoint    The offset that is added to the list is the duplicate point
	//                      that shares the same location data
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Offset list in the point is added with dupPoint's offset
    //          
    // Returns:   
    //          none
    //
	public void addOffset(Point dupPoint) {
	    
	    // You only add offset of the dupPoint in the offset list
	    for (int i = 0; i < dupPoint.offsetList.size(); i++) {
	        
	        if (!this.offsetList.contains(dupPoint.offsetList.get(i))) {
	            offsetList.add(dupPoint.offsetList.get(i));
	        }
	    }	    
	}
	
    //////////////////////////////////////////////////////////////// directionFrom(double X, double Y)
	// Returns indicator of the direction to the user data object from the 
    // location (X, Y) specified by the parameters.
    // The indicators are defined in the enumeration Direction, and are used
    // as follows:
    //
    //    NE:  locations are the same, or vector from (X, Y) to user data object
    //         has direction in [0, 90) degrees
    //    NW:  vector from (X, Y) to user data object has direction in [90, 180) 
    //    SW:  vector from (X, Y) to user data object has direction in [180, 270)
    //    SE:  vector from (X, Y) to user data object has direction in [270, 360)  
	//
    // Parameters:
    //          x   x-coordinate of the specified location where you want to know which direction it is in 
	//          y   y-coordinate of the specified location where you want to know which direction it is in 
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          Direction from (x, y) to Point is determined 
    //          
    // Returns:   
    //          Direction from x, y to the point either by N, S, E, W
    //
	public Direction directionFrom(double X, double Y) {
		if ((this.ycoord >= Y && this.xcoord > X) ||
	        (this.ycoord == Y && this.xcoord == X)) {
		    return Direction.NE;
		}
		else if (this.ycoord > Y && this.xcoord <= X) {
		    return Direction.NW;
		}
		else if (this.ycoord <= Y && this.xcoord < X) {
		    return Direction.SW;
		}
		else if (this.ycoord < Y && this.xcoord >= X) {
		    return Direction.SE;
		}
		else {
	        return Direction.NOQUADRANT;
		}
	}
	
    //////////////////////////////////////////////////////////////// inQuadrant(double xLo, double xHi, double yLo, double yHi)
	// Returns indicator of which quadrant of the rectangle specified by the
	// parameters that user data object lies in.
	// The indicators are defined in the enumeration Direction, and are used
	// as follows, relative to the center of the rectangle:
	//
	// NE: user data object lies in NE quadrant, including non-negative
	//     x-axis, but not the positive y-axis
	// NW: user data object lies in the NW quadrant, including the
	//     positive y-axis, but not the negative x-axis
	// SW: user data object lies in the SW quadrant, including the
	//     negative x-axis, but not the negative y-axis
	// SE: user data object lies in the SE quadrant, including the
	//     negative y-axis, but not the positive x-axis
	// NOQUADRANT: user data object lies outside the specified rectangle
	//
    // Parameters:
    //          xLo     x - Lower Bound of the specified boundary
    //          xHi     x - Upper Bound of the specified boundary
	//          yLo     y - Lower Bound of the specified boundary
    //          yHi     y - Upper Bound of the specified boundary
	//
    // Pre:       
    //          none
    //
    // Post:    
    //          Direction of which quadrant Point should be in is determined
    //          
    // Returns:   
    //          Direction of which quadrant the point should be in
    //
	public Direction inQuadrant(double xLo, double xHi, double yLo, double yHi) {
	    
	    if (this.xcoord >= xLo && this.xcoord <= xHi &&
	        this.ycoord >= yLo && this.ycoord <= yHi) {
	        
	        double centerX = (xLo + xHi) / 2;
	        double centerY = (yLo + yHi) / 2;
	        
	        return this.directionFrom(centerX, centerY);
	    }
	    else {
	        
	        return Direction.NOQUADRANT;
	    }
	}
	
    //////////////////////////////////////////////////////////////// inBox(double xLo, double xHi, double yLo, double yHi)
	// Returns true iff the user data object lies within or on the boundaries
	// of the rectangle specified by the parameters.
	//
    // Parameters:
    //          xLo     x - Lower Bound of the specified boundary
    //          xHi     x - Upper Bound of the specified boundary
    //          yLo     y - Lower Bound of the specified boundary
    //          yHi     y - Upper Bound of the specified boundary
    //
    // Pre:       
    //          none
    //
    // Post:    
    //          If the point is in the specified boundary is determined
    //          
    // Returns:   
    //          True if the point is in the specified boundary
	//          False, otherwise
    //
	public boolean inBox(double xLo, double xHi, double yLo, double yHi) {
		
	    return (this.xcoord >= xLo && this.xcoord <= xHi &&
		        this.ycoord >= yLo && this.ycoord <= yHi);
	}
	
    //////////////////////////////////////////////////////////////// toString()
    // Provides the details of coordinate and offsets are contained in the point
	//
    // Parameters:
	//         none
    //
    // Pre:       
    //         none
    //
    // Post:    
    //         Details of coordinates and offsets of the Point is formatted
    //          
    // Returns:   
    //         Details of coordinates and offsets of the Point
    //
	public String toString() {
	    StringBuilder builder = new StringBuilder("[(");
	    
	    builder.append((long) xcoord + ", " + (long) ycoord + ")");
	    
	    for (int i = 0; i < offsetList.size(); i++) {
	        builder.append(", " + offsetList.get(i).longValue());
	    }
	    
	    builder.append("] ");
		
	    return builder.toString();
	}
	
    //////////////////////////////////////////////////////////////// equals(Object o)
    // Compares two Point object if they are equal or not
	// Points are compared only with the x coordinate and y-coordinate
	//
    // Parameters:
    //         o    Point that is compared with this Point
    //
    // Pre:       
    //         none
    //
    // Post:    
    //         Whether or not if the two compared Points are equal is determined
    //          
    // Returns:   
    //         
	//         Return True, f x-coordinate and y-coordinate of the point is equal for both the points
	//         Return False, otherwise
    //
	public boolean equals(Object o) {
	    
	    if (this.getClass().equals(o.getClass())) {
	        
	        return (xcoord == ((Point) o).xcoord && ycoord == ((Point) o).ycoord);
	    }
	    else {
	        return false;
	    }
	}
}
