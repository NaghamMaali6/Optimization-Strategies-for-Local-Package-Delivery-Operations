package application ;

import java.util.* ;

public class Vehicle 
{
    private int Id ;  //unique identifier for the vehicle
	
    private double capacity ;  //capacity of the vehicle
    
    private List<Package> assignedPackage ;  //list of packages assigned to the vehicle

    //Constructor of Vehicle object:
    public Vehicle(int Id , double capacity) 
    {
        this.Id = Id ;
    	this.capacity = capacity ;
        this.assignedPackage = new ArrayList<>() ;
    }
    
    //getters and setters:
  	public int getID()
  	{
  		return Id ;
  	}
  	
  	public void setID(int Id)
  	{
  		this.Id = Id ;
  	}
    
    public double getCapacity()
    {
    	return capacity ;
    }
    
    public void setCapacity(double capacity)
    {
    	this.capacity = capacity ;
    }
    
    public List<Package> getAssignedPackages() 
    {
        return assignedPackage ;
    }
    
    public void clearPackages() 
    {
        if (this.assignedPackage != null) 
        {
            this.assignedPackage.clear() ;  //clears the list of packages
        }
    }
    
    public double getTotalWeight()  //calculate total weight of currently assigned packages
    {
    	double total = 0 ;
        for (Package pkg : assignedPackage) 
        {
            total += pkg.getWeight() ;
        }
        return total ;
    }
    
    public boolean canAddPackage(Package p)  //check if a package can be add without exceeding vehicle capacity
    {
        return getTotalWeight() + p.getWeight() <= capacity ;
    }

    public boolean addPackage(Package pkg)  //Add package to vehicle if it fits(if capacity allows)
    {
        if (canAddPackage(pkg)) 
        {
        	assignedPackage.add(pkg) ;
            return true ;
        }
        
        return false ;
    }

    public double getCurrentLoad()  //return total current load on the vehicle
    {
        return assignedPackage.stream().mapToDouble(Package::getWeight).sum() ;
    }
    
    public double getTotalDistance()  //calculate total round-trip distance for delivering all packages and returning to shop
    {
        double distance = 0 ;  //start with 0 distance
        double currentX = 0 ;  //starting x-coordinate
        double currentY = 0 ;  //starting y-coordinate

        for (Package pkg : assignedPackage)  //visit each package's destination
        {
            //using the Euclidean distance:
        	double dx = pkg.getX() - currentX ;
            double dy = pkg.getY() - currentY ;
            distance += Math.sqrt(dx * dx + dy * dy) ;  
            
            //move to new location:
            currentX = pkg.getX() ;
            currentY = pkg.getY() ;
        }

        //Return from last destination to shop
        distance += Math.sqrt(currentX * currentX + currentY * currentY);
        return distance ;  //return total distance
    }
}
