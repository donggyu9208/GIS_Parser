// On my honor:
//
// - I have not discussed the Java language code in my program with
// anyone other than my instructor or the teaching assistants
// assigned to this course.
//
// - I have not used Java language code obtained from another student,
// or any other unauthorized source, either modified or unmodified.
//
// - If any Java language code or documentation used in my program
// was obtained from another source, such as a text book or course
// notes, that has been clearly noted with a proper citation in
// the comments of my program.
//
// - I have not designed this program in such a way as to defeat or
// interfere with the normal operation of the grading code.
//
// Dong Gyu Lee

// The test harness will belong to the following package; the quadtree
// implementation must belong to it as well. In addition, the quadtree
// implementation must specify package access for the node types and tree
// members so that the test harness may have access to it.

package struct.QuadTree;
import java.util.ArrayList;

import stringFormat.StringFormat;
import struct.QuadTree.GISClient.Point;

@SuppressWarnings("unchecked")
// Project J4 for CS 3114 Summer I 2017
//
// Programmer:    Dong Gyu Lee
// OS:            Windows 10 Education
// System:        i5-4300U, 8 GB Memory
// Compiler:      Eclipse Neon.3 Release (4.6.3)
// Last modified: June 23, 2017
//
// Purpose
//
// Bucket PR quadtree serves as a data structure to index 
// GIS records with its coordinates. Each leaf stores up to
// 4 data objects (Points).Upon insertion, if the added value
// falls under the leaf that is already full, that nodes
// partitions into quadrants and the added value is added to
// the appropriate quadrant.
//
// Object (Point) that this PR quadtree stores both a geographic 
// coordinate and its offset. As discussed in the Point class 
// description, there can exist a same geographic point with
// different offsets and even in such case the point is inserted.
//
// by geographic coordinate. This geographic index supports finding
// offsets of GIS records that match a given primary latitude and primary longitude
//
public class prQuadTree< T extends Compare2D<? super T> > {
    private final int BUCKET_CAPACITY = 4; // Bucket capacity is given as 4
    
    //////////////////////////////////////////////////////////////// prQuadNode
    // Abstract class for prQuadNode that extends to both prQuadLeaf and prQuadInternal
    // Both prQuadLeaf and prQuadInternal must have the fields for boundary
    //
	abstract class prQuadNode {
	    double xLo, xHi, yLo, yHi;
    }
    
    //////////////////////////////////////////////////////////////// prQuadLeaf
    // prQuadLeaf class that stores both the coordinate data and offset
	// Each Leaf node can hold up to 4 Points
    //
	private class prQuadLeaf extends prQuadNode {
		private ArrayList<T> Elements;    // List that stores Points 
        
        //////////////////////////////////////////////////////////////// prQuadLeaf(T data, double xLo, double xHi, double yLo, double yHi) 
        // Constructor for prQuadLeaf
		// Initializes with its size by xLo, xHi, yLo, yHi
		// Initializes the list of Points with the Max Bucket capacity of 4
        //
        // Parameters:
		//            data    Data to be inserted in the leaf
		//            xLo     x-Lower bounds for the dimension
		//            xHi     x-Upper bounds for the dimension
		//            yLo     y-Lower bounds for the dimension
		//            yHi     y-Upper bounds for the dimension
        //
        // Pre:       
        //            None
        //
        // Post:    
        //            prQuadLeaf is initialized 
        //
        public prQuadLeaf(T data, double xLo, double xHi, double yLo, double yHi) {

            this.xLo = xLo;
            this.xHi = xHi;
            this.yLo = yLo;
            this.yHi = yHi;
            
            Elements = new ArrayList<T>(BUCKET_CAPACITY);
            Elements.add(data);
        }
	}
	
    //////////////////////////////////////////////////////////////// prQuadLeaf
    // prQuadInternal class that stores prQuadNode that can be either prQuadLeaf
	// or another prQuadInternal
	// prQuad Internal holds 4 different sub node, each specified with the
	// Location enum NW, SW, SE, NE;
    //
    private class prQuadInternal extends prQuadNode {
    	        
  	   private prQuadNode NW, SW, SE, NE;    // Node at each quadrant of prQuadInternal
       private int nodesNum;                 // Number of occupied nodes
        
       //////////////////////////////////////////////////////////////// prQuadInternal(double xLo, double xHi, double yLo, double yHi)
       // Constructor for prQuadInternal
       // Initializes its dimension and nodes at each quadrant
       //
       // Parameters:
       //           xLo     x-Lower bounds for prQuadInternal dimension
       //           xHi     x-Upper bounds for prQuadInternal dimension
       //           yLo     y-Lower bounds for prQuadInternal dimension
       //           yHi     y-Upper bounds for prQuadInternal dimension
       //
       // Pre:       
       //          None
       //
       // Post:    
       //           prQuadInternal is initialized
       //           Dimension of the internal node is declared and initialized
       //           Each Quadrant set to null
       //           Number of occupied nodes is initialized to 9
       //
       //
       public prQuadInternal(double xLo, double xHi, double yLo, double yHi) {

            this.xLo = xLo;
            this.xHi = xHi;
            this.yLo = yLo;
            this.yHi = yHi;

            NW = null;
            SW = null;
            SE = null;
            NE = null;
            
            nodesNum = 0;
        }
        
       //////////////////////////////////////////////////////////////// getNodesNum()
       // Return the number of nodes that is occupied in the internal node
       //
       // Parameters:
       //          mpme
       //
       // Pre:       
       //          none
       //
       // Post:    
       //          Number of occupied quadrant within internal node is determined
       //          
       // Returns:   
       //          Number of occupied quadrant
       //
       // Called by: 
       //          prQuadInternal
       //               contractBranch(prQuadNode root)
       //
       // Calls:   
       //         none
       //
        public int getNodesNum() {
            nodesNum = 0;
            
            if (NW != null) {
                nodesNum++;
            }
            if (SW != null) {
                nodesNum++;
            }
            if (SE != null) {
                nodesNum++;
            }
            if (NE != null) {
                nodesNum++;
            }
            
            return nodesNum;
        }

        //////////////////////////////////////////////////////////////// contractBranch(prQuadNode root)
        // Contracts unnecessary branch after delete command is executed
        //
        // Parameters:
        //          root of the tree that is to be contracted
        //
        // Pre:       
        //          none
        //
        // Post:    
        //          Unnecessary branch that only stores a single node is contracted    
        //          
        // Returns:   
        //          Root of the contracted tree
        //
        // Called by: 
        //          prQuadTree
        //              delete(T elem, prQuadNode root)
        //
        // Calls:   
        //         none
        //
        public prQuadNode contractBranch(prQuadNode root) {
            // If the internal node is given as null,
            // You cannot contract the tree
            if (root == null) {
                return null;
            }
            
            // Contracts branch by recursively finding the internal node that only a single quadrant 
            // is occupied.
            // If only a single data exists within the internal node 
            // Contract such internal node
            if (root.getClass().equals(prQuadInternal.class)) {
                if (((prQuadInternal) root).getNodesNum() == 1) {
                    
                    if (((prQuadInternal) root).NE != null && ((prQuadInternal) root).NE.getClass().equals(prQuadLeaf.class)) {
                        root = ((prQuadInternal) root).NE;
                    }
                    else if (((prQuadInternal) root).NW != null && ((prQuadInternal) root).NW.getClass().equals(prQuadLeaf.class)) {
                        root = ((prQuadInternal) root).NW;
                    }
                    else if (((prQuadInternal) root).SE != null && ((prQuadInternal) root).SE.getClass().equals(prQuadLeaf.class)) {
                        root = ((prQuadInternal) root).SE;
                    }
                    else if (((prQuadInternal) root).SW != null && ((prQuadInternal) root).SW.getClass().equals(prQuadLeaf.class)) {
                        root = ((prQuadInternal) root).SW;
                    }
                }
            }
            return root;
        }
   }
  
   private StringBuilder    toStringBuilder;        // Used to get the whole structure of the tree, updated by toString() method
   private prQuadNode       root;                   // Root of the Tree
   private double           xMin, xMax, yMin, yMax; // Dimension of the Tree
   private int              elemCount;              // Element count of the Tree
   private ArrayList<T>     findList;               // Tree that stores the elements updated by find(double xLo, double xHi, double yLo, double yHi)
    
   //////////////////////////////////////////////////////////////// prQuadTree(double xMin, double xMax, double yMin, double yMax)
   // Constructor prQuadTree that initializes its dimension, root, element count and the find list
   //
   // Parameters:
   //           xMin     x-Lower bounds for prQuadTree dimension
   //           xMax     x-Upper bounds for prQuadTree dimension
   //           yMin     y-Lower bounds for prQuadTree dimension
   //           yMax     y-Upper bounds for prQuadTree dimension
   //
   // Pre:       
   //          None
   //
   // Post:    
   //          prQuadTree is initialized
   //
   public prQuadTree(double xMin, double xMax, double yMin, double yMax) {
        root = null;
        elemCount = 0;
        findList = new ArrayList<T>();
        
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
   }
       
   //////////////////////////////////////////////////////////////// insert(T elem)
   // Inserts an element into the tree
   //
   // Parameters:
   //          T    element to be inserted
   //
   // Pre:       
   //          elem != null
   //
   // Post:    
   //          If elem lies within the tree's region, and elem is not already 
   //          present in the tree, elem has been inserted into the tree.
   //          
   // Returns:   
   //          True iff elem is inserted into the tree. 
   //
   // Called by: 
   //          GISData
   //               import_CoordIndex(File DBFile)
   //
   // Calls:   
   //         prQuadTree
   //               insertHelper(prQuadNode root, T elem, double xLo, double xHi, double yLo, double yHi)
   //
   public boolean insert(T elem) {
       int prevNum = elemCount;
		root = insertHelper(this.root, elem, xMin, xMax, yMin, yMax);
        
		// return true if the number of elements in a tree has decreased
        return prevNum < elemCount;
	}
    
   //////////////////////////////////////////////////////////////// insert(T elem)
   // Helper method for insert (T elem)
   //
   // Parameters:
   //           root     Root of the tree where elem is inserted
   //           T        element to be inserted
   //           xLo      x-Lower bounds in which element is inserted
   //           xHi      x-Upper bounds in which element is inserted
   //           yLo      y-Lower bounds in which element is inserted
   //           yHi      y-Upper bounds in which element is inserted
   //
   // Pre:       
   //          elem != null
   //
   // Post:    
   //          If elem lies within the tree's region, and elem is not already 
   //          present in the tree, elem has been inserted into the tree.
   //          
   // Returns:   
   //          True iff elem is inserted into the tree. 
   //
   // Called by: 
   //          prQuadTree
   //               insert(T elem)
   //
   // Calls:   
   //          none
   //
    private prQuadNode insertHelper(prQuadNode root, T elem, double xLo, double xHi, double yLo, double yHi) {
        
        // If the elem is not within the range you do not insert the element
        if (!elem.inBox(xLo, xHi, yLo, yHi)) {
            return root;
        }
        
        // if the found node is empty (null), insert the element in that node 
        // Every time insert an element, increment the element number
        if (root == null) {
            this.elemCount++;
            return new prQuadLeaf(elem, xLo, xHi, yLo, yHi);
        }

        // If the encountered node is a leaf node
        if (root.getClass().equals(prQuadLeaf.class)) {
            
            // If location data of one of elements in the node is equal 
            // to the element to be inserted, you do not have to
            // split the leaf node, rather you can insert only the 
            // offset that is different in the currently existing Point
            ArrayList<T> Elements = ((prQuadLeaf) root).Elements;
            for (int i = 0; i < Elements.size(); i++) {
                if (Elements.get(i).equals(elem)) {
                    
                    // For each element checks if it is equal to the element to be added
                    if (elem.getClass().equals(Point.class)) {
                        ((Point) Elements.get(i)).addOffset((Point) elem);
                        this.elemCount++;
                    }
                    return root;
                }
            }
            
            // If the leaf node is not full, we can simply add the element in the 
            // leaf node
            if (((prQuadLeaf) root).Elements.size() < BUCKET_CAPACITY) {
                
                ((prQuadLeaf) root).Elements.add(elem);
                this.elemCount++;
                
                return root;
            }
            
            // If the Leaf node is full (size == 4),
            // We need to split the node so that additional data can be stored in the node
            
            // We start this by creating a new internal node and replace with the existing leaf node
            root = new prQuadInternal(root.xLo, root.xHi, root.yLo, root.yHi);
            
            // Retrieve the center of the leaf node
            double centerX = (root.xLo + root.xHi) / 2;
            double centerY = (root.yLo + root.yHi) / 2;
            
            // for every element in the original leaf node, we determine in which quadrant it should be part of 
            // in the new internal node that is created
            // Then insert the original Point data into newly created internal node in the correct corresponding 
            // quadrant
            for (int i = 0; i < BUCKET_CAPACITY; i++) {
                
                // Determine which quadrant this element should be part of 
                Direction ElemQuad = Elements.get(i).inQuadrant(root.xLo, root.xHi, root.yLo, root.yHi);
                
                switch (ElemQuad) {
                case NE:
                    ((prQuadInternal) root).NE = insertHelper(((prQuadInternal) root).NE, Elements.get(i), centerX,
                            root.xHi, centerY, root.yHi);
                    break;
                case NW:
                    ((prQuadInternal) root).NW = insertHelper(((prQuadInternal) root).NW, Elements.get(i), root.xLo,
                            centerX, centerY, root.yHi);
                    break;
                case SE:
                    ((prQuadInternal) root).SE = insertHelper(((prQuadInternal) root).SE, Elements.get(i), centerX,
                            root.xHi, root.yLo, centerY);
                    break;
                case SW:
                    ((prQuadInternal) root).SW = insertHelper(((prQuadInternal) root).SW, Elements.get(i), root.xLo,
                            centerX, root.yLo, centerY);
                    break;
                default:
                    // For any case, if the element cannot be part of any of the quadrant, returns the error
                    // "Error in inserting the leaf element into internal node"
                    System.out.println("Error in inserting the leaf element into internal node");
                    System.exit(0);
                }
            }
            
            // Since old element is inserted back into newly created node and
            // for every old element inserted, element count is increased 
            // However element count should not increase from inserting back the old original element
            // So we decrement the element count by the number of Bucket Size
            elemCount -= (BUCKET_CAPACITY);
            
            // Now insert the element that is supposed to be inserted into the newly created internal node
            Direction newElemQuad = elem.inQuadrant(root.xLo, root.xHi, root.yLo, root.yHi);
            
            switch (newElemQuad) {
            case NE:
                ((prQuadInternal) root).NE = insertHelper(((prQuadInternal) root).NE, elem, centerX, root.xHi, centerY,
                        root.yHi);
                break;
            case NW:
                ((prQuadInternal) root).NW = insertHelper(((prQuadInternal) root).NW, elem, root.xLo, centerX, centerY,
                        root.yHi);
                break;
            case SE:
                ((prQuadInternal) root).SE = insertHelper(((prQuadInternal) root).SE, elem, centerX, root.xHi, root.yLo,
                        centerY);
                break;
            case SW:
                ((prQuadInternal) root).SW = insertHelper(((prQuadInternal) root).SW, elem, root.xLo, centerX, root.yLo,
                        centerY);
                break;
            default:
                System.out.println("Error in inserting the element into internal node");
                System.exit(0);
            }
        }

        // Case: If the encountered node is an internal node
        // Determine the right quadrant to be in and insert in that quadrant
        else if (root.getClass().equals(prQuadInternal.class)) {
            double centerX = (xLo + xHi) / 2;
            double centerY = (yLo + yHi) / 2;
            switch (elem.inQuadrant(xLo, xHi, yLo, yHi)) {
            case NE:
                ((prQuadInternal) root).NE = insertHelper(((prQuadInternal) root).NE, elem, centerX, root.xHi, centerY,
                        root.yHi);
                break;
            case NW:
                ((prQuadInternal) root).NW = insertHelper(((prQuadInternal) root).NW, elem, root.xLo, centerX, centerY,
                        root.yHi);
                break;
            case SE:
                ((prQuadInternal) root).SE = insertHelper(((prQuadInternal) root).SE, elem, centerX, root.xHi, root.yLo,
                        centerY);
                break;
            case SW:
                ((prQuadInternal) root).SW = insertHelper(((prQuadInternal) root).SW, elem, root.xLo, centerX, root.yLo,
                        centerY);
                break;
            default:
                System.out.println("Error in inserting the element into internal node");
                System.exit(0);
            }
        }

        return root;
    }
    
   
    //////////////////////////////////////////////////////////////// find(T elem)
    // Returns reference to an element x within the tree such that
    // elem.equals(x)is true, provided such a matching element occurs within
    // the tree; returns null otherwise.
    //
    // Parameters:
    //           elem     element to be found from the tree
    //
    // Pre:       
    //          elem != null
    //
    // Post:    
    //          Either element is found or not found from the tree
    //          
    // Returns:   
    //          reference to an element x within the tree
    //
    // Called by: 
    //          prQuadTree
    //               findBool(T elem)
    //
    // Calls:   
    //          prQuadTree
    //              T find(T elem, prQuadNode root)
    //
    public T find(T elem) {
        
        // If elem is not within the root dimension
        // element does not exist in the tree
        if (!elem.inBox(((prQuadInternal)root).xLo, 
                        ((prQuadInternal)root).xHi, 
                        ((prQuadInternal)root).yLo, 
                        ((prQuadInternal)root).yHi)) {
            return null;
        }
        
        return find(elem, root);
    }
    
    //////////////////////////////////////////////////////////////// findBool(T elem)
    // If the element is found in the tree, return True
    // else return false
    //
    // Parameters:
    //           elem     element to be found from the tree
    //
    // Pre:       
    //          elem != null
    //
    // Post:    
    //          Either element is found or not found from the tree
    //          
    // Returns:   
    //          True if element is found,
    //          False otherwise
    //
    // Called by: 
    //          none
    //
    // Calls:   
    //          none
    //
    public boolean findBool(T elem) {
        if (find(elem) != null) {
            return true;
        }
        
        return false;
    }
    
    //////////////////////////////////////////////////////////////// find(T elem, prQuadNode root)
    // This is a helper method for find(T elem)
    // If the element is found in the tree, return reference to that element
    // else return false
    //
    // Parameters:
    //           elem     element to be found from the tree
    //
    // Pre:       
    //          Element must be in side the specified world boundary
    //
    // Post:    
    //          Either element is found or not found from the tree
    //          
    // Returns:   
    //          Reference to the found element if found
    //          null if not found
    //
    // Called by: 
    //          prQuadTree
    //              find(T elem)
    //
    // Calls:   
    //          none
    //
    private T find(T elem, prQuadNode root) {        
        
        // if encounters null while traversing, 
        // elem does not exist in the tree
        if (root == null) {
            return null;
        }
        
        // If the root encountered is an internal node
        // proceed traversing to the relevant quadrant
        if (root.getClass().equals(prQuadInternal.class)) {
            
            Direction elemQuad = elem.inQuadrant(root.xLo, root.xHi, root.yLo, root.yHi);
            
            switch (elemQuad) {
            case NE:
                return find(elem, ((prQuadInternal) root).NE);
            case NW:
                return find(elem, ((prQuadInternal) root).NW);
            case SE:
                return find(elem, ((prQuadInternal) root).SE);
            case SW:
                return find(elem, ((prQuadInternal) root).SW);
            default:
                throw new java.lang.RuntimeException("Error in finding the element");
            }
        }
        
        // If the root encountered is a leaf node
        // Check the element in the leaf and 
        // if the the equal element is found, return that element
        // if not return null
        else if (root.getClass().equals(prQuadLeaf.class)) {
            for (int i = 0; i < ((prQuadLeaf) root).Elements.size(); i++) {
                if ((((prQuadLeaf) root).Elements.get(i)).equals(elem)) {
                    return ((prQuadLeaf) root).Elements.get(i);
                }
            }
        }
        return null;
    }

    //////////////////////////////////////////////////////////////// delete(T elem)
    // Delete an element from the tree. Returns true if deleted, otherwise false
    //
    // Parameters:
    //           elem     element to be found from the tree
    //
    // Pre:       
    //          elem != null
    //
    // Post:    
    //          If elem lies in the tree's region, and a matching element occurs
    //          in the tree, then that element has been removed.
    //          
    // Returns:   
    //          Returns true iff a matching element has been removed from the tree.
    //
    // Calls:   
    //          delete(T elem, prQuadNode root)
    //
    public boolean delete(T elem) {
        
        int prevNum = elemCount;
        root = delete(elem, root);
        
        // return true if the number of elements in a tree has decreased
        return prevNum > elemCount;
    }
    
    //////////////////////////////////////////////////////////////// delete(T elem, prQuadNode root)
    // Delete an element from the tree. Returns null either if the root 
    // is element is deleted or elem is null. Otherwise return the root of the tree
    //
    // Parameters:
    //           elem      element to be found from the tree
    //           root      root of the tree to delete element from 
    //
    // Pre:       
    //           elem != null
    //
    // Post:    
    //          If elem lies in the tree's region, and a matching element occurs
    //          in the tree, then that element has been removed.
    //          
    // Returns:   
    //          Returns null either if the root is element is deleted or elem is null
    //          Otherwise return the root of the tree
    //          
    //
    // Calls:   
    //          delete(T elem, prQuadNode root)
    //
    private prQuadNode delete(T elem, prQuadNode root) {
        // if the root is null you return null, nothing is deleted
        if (root == null) {
            return null;
        }
        
        // Case: if the root is a leaf node you delete the element only if they are equal
        if (root.getClass().equals(prQuadLeaf.class)) {
           
            // Every time the element is found decrement the element number
            // Since there can be more than one offset in the element
            // you need to loop through the element
            for (int i = 0; i < ((prQuadLeaf) root).Elements.size(); i++) {
                if ((((prQuadLeaf) root).Elements.get(i)).equals(elem)) {
                    elemCount--;
                    return null;
                }
            }
        }
        
        // Case: If the encountered root is an internal node
        if (root.getClass().equals(prQuadInternal.class)) {
            // Check if the element to be deleted is within the dimension
            if (!elem.inBox(((prQuadInternal)root).xLo, 
                            ((prQuadInternal)root).xHi, 
                            ((prQuadInternal)root).yLo, 
                            ((prQuadInternal)root).yHi)) {
                return null;
            }
            
            // Traverse through the right quadrant of the tree to find the element
            Direction elemQuad = elem.inQuadrant(root.xLo, root.xHi, root.yLo, root.yHi);
            
            switch (elemQuad) {
            case NE:
                ((prQuadInternal) root).NE = delete(elem, ((prQuadInternal) root).NE);
                break;
            case NW:
                ((prQuadInternal) root).NW = delete(elem, ((prQuadInternal) root).NW);
                break;
            case SE:
                ((prQuadInternal) root).SE = delete(elem, ((prQuadInternal) root).SE);       
                break;
            case SW:
                ((prQuadInternal) root).SW = delete(elem, ((prQuadInternal) root).SW);        
                break;
            default:
                throw new java.lang.RuntimeException("Error in deleting the element");
            }
            
            root = ((prQuadInternal) root).contractBranch((prQuadInternal) root);
        }
        
        return root;
    }

    //////////////////////////////////////////////////////////////// find(double xLo, double xHi, double yLo, double yHi)
    // Returns a collection of (references to) all elements x such that x is
    // in the tree and x lies at coordinates within the defined rectangular
    // region, including the boundary of the region.
    //
    // Parameters:
    //           xLo     x-Lower bounds for searching dimension
    //           xHi     x-Upper bounds for searching dimension
    //           yLo     y-Lower bounds for searching dimension
    //           yHi     y-Upper bounds for searching dimension
    //
    // Pre:       
    //           xLo < xHi and yLo < yHi
    //
    // Post:    
    //          If elem lies in the tree's region, and a matching element occurs
    //          in the tree, then that element has been found and inserted into
    //          the found element list.
    //          
    // Returns:   
    //          List of element that are found in the dimension specified
    //          
    // Calls:   
    //      prQuadTree
    //          storeElementList(prQuadNode root, double xLo, double xHi, double yLo, double yHi)
    //
    public ArrayList<T> find(double xLo, double xHi, double yLo, double yHi) {
        // Clear any of the elements that was originally stored in the list
        findList.clear();
        
        // Reinsert the elements that are within the specified range
        storeElementList(root, xLo, xHi, yLo, yHi);
        
        return findList;
    }
    
    //////////////////////////////////////////////////////////////// storeElementList(prQuadNode root, double xLo, double xHi, double yLo, double yHi)
    // Collection of (references to) all elements x such that x is
    // in the tree and x lies at coordinates within the defined rectangular
    // region, including the boundary of the region is found by the method
    // and inserted into findList list.
    //
    // Parameters:
    //           xLo     x-Lower bounds for searching dimension
    //           xHi     x-Upper bounds for searching dimension
    //           yLo     y-Lower bounds for searching dimension
    //           yHi     y-Upper bounds for searching dimension
    //
    // Pre:       
    //           xLo < xHi and yLo < yHi
    //
    // Post:    
    //          If elem lies in the tree's region, and a matching element occurs
    //          in the tree, then that element has been found and inserted into
    //          the found element list.
    //          
    // Returns:   
    //          none
    //          
    // Called by:
    //          prQuadTree
    //              find(double xLo, double xHi, double yLo, double yHi)
    //
    private void storeElementList(prQuadNode root, double xLo, double xHi, double yLo, double yHi) {
        if (root == null) {
            return;
        }
        
        // If the node is a leaf node, and within the specified range
        // Insert the element to the findList
        if (root.getClass().equals(prQuadLeaf.class)) {
            
            T tempElem;
            for (int i = 0; i < ((prQuadLeaf) root).Elements.size(); i++) {
                
                tempElem = ((prQuadLeaf) root).Elements.get(i);
                
                if (tempElem.inBox(xLo, xHi, yLo, yHi)) {
                    findList.add(tempElem);
                }
            }
            
            
        }
        
        // Traverse only the quadrant that contains the specified range
        if (root.getClass().equals(prQuadInternal.class)) {
            
            // Center coordinates of the internal node
            double centerX = (root.xLo + root.xHi) / 2;
            double centerY = (root.yLo + root.yHi) / 2;
            
            // Represent each of vertices at 4 corners of the specified dimension
            Point pNW = new Point(root.xLo, root.yHi);
            Point pNE = new Point(root.xHi, root.yHi);
            Point pSW = new Point(root.xLo, root.yLo);
            Point pSE = new Point(root.xHi, root.yLo);
            
            // Only if each of four vertices lies at the correct quadrant relative to the internal node size
            // you traverse through that quadrant
            
            if (pNW.directionFrom(centerX, centerY) == Direction.NW) {
                storeElementList(((prQuadInternal) root).NW, xLo, xHi, yLo, yHi);
            }
            if (pNE.directionFrom(centerX, centerY) == Direction.NE) {
                storeElementList(((prQuadInternal) root).NE, xLo, xHi, yLo, yHi);
            }
            if (pSW.directionFrom(centerX, centerY) == Direction.SW) {
                storeElementList(((prQuadInternal) root).SW, xLo, xHi, yLo, yHi);
            }
            if (pSE.directionFrom(centerX, centerY) == Direction.SE) {
                storeElementList(((prQuadInternal) root).SE, xLo, xHi, yLo, yHi);
            }
        }
    }
	
    
    //////////////////////////////////////////////////////////////// toStringLeaf(prQuadLeaf leaf)
    // Helper method to toStringLeaf(prQuadLeaf leaf) that prints out the details of leaf
    //
    // Parameters:
    //           leaf   leaf node to be printed out
    //
    // Pre:       
    //           none
    //
    // Post:    
    //          none
    //          
    // Returns:   
    //          String the details the elements in the leaf
    //          
    // Called by:
    //          prQuadTree
    //              toString(prQaudNode root, int level)
    //
    private String toStringLeaf(prQuadLeaf leaf) {
        StringBuilder builder = new StringBuilder();
        
        int leafSize = leaf.Elements.size();
        
        for (int i = 0; i < leafSize; i++) {
            builder.append(leaf.Elements.get(i).toString());
        }
        
        return builder.toString();
    }
    
    //////////////////////////////////////////////////////////////// toStringLeaf(prQuadLeaf leaf)
    // Helper method to toString() that prints out the details of the tree from the specified level
    //
    // Parameters:
    //           root    root of the tree to be printed out
    //           level   level of the root that is passed through
    //
    // Pre:       
    //           none
    //
    // Post:    
    //          none
    //          
    // Returns:   
    //          String the details the elements in the tree
    //          
    // Called by:
    //          prQuadTree
    //              toString()
    //
    private void toString(prQuadNode root, int level) {
        
        if (root == null) {
            toStringBuilder.append(StringFormat.addSpaces("*", 3 * level) + '\n');
            return;
        }
        
        if (root.getClass().equals(prQuadLeaf.class)) {
            toStringBuilder.append(StringFormat.addSpaces(toStringLeaf((prQuadLeaf) root), 3 * level) + '\n');
            return;
        }
        
        if (root.getClass().equals(prQuadInternal.class)) {
            toString(((prQuadInternal) root).SW, level + 1);
            toString(((prQuadInternal) root).SE, level + 1);
            toStringBuilder.append(StringFormat.addSpaces("@", 3 * level) + '\n');
            toString(((prQuadInternal) root).NE, level + 1);
            toString(((prQuadInternal) root).NW, level + 1);
        }
    }
    
    //////////////////////////////////////////////////////////////// toStringLeaf(prQuadLeaf leaf)
    // Returns the element count of the tree
    //
    // Parameters:
    //           none
    //
    // Pre:       
    //           none
    //
    // Post:    
    //          none
    //          
    // Returns:   
    //          element count of the tree
    //          
    // Called by:
    //          Commands
    //              execute_import()
    //
    public int getElemCount() {
        return this.elemCount;
    }
    
    //////////////////////////////////////////////////////////////// toStringLeaf(prQuadLeaf leaf)
    // Helper method to toString() that prints out the details of this tree
    //
    // Parameters:
    //           none
    //
    // Pre:       
    //           none
    //
    // Post:    
    //          none
    //          
    // Returns:   
    //          String the details the elements in the tree
    //
    public String toString() {
        // Initialize the StringBuilder here
        toStringBuilder = new StringBuilder();
        
        // root of the tree starts from 0, set the level parameter to 0
        // StringBuilder is appended through toString(prQuadNode root, int level) method
        toString(root, 0);
        
        return toStringBuilder.toString();
    }  
}
