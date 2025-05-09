package application ;

public class Package 
{
	private int Id ;  //unique identifier for the package
	
	//the destination location of any package is represented by (x, y) coordinates:
	private double x ;  
	private double y ;
	
	private double weight ;  //package weight in kilograms
	
	private int priority ;  //priority of the package between 1 and 5: 1 (highest) to 5 (lowest)
	
	//Constructor of Package object:
	public Package(int Id , double x , double y , double weight , int priority)
	{
		this.Id = Id ;
		this.x = x ;
		this.y = y ;
		this.weight = weight ;
		this.priority = priority ;
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
	
	public double getX()
	{
		return x ;
	}
	
	public void setX(double x)
	{
		this.x = x ;
	}
	
	public double getY()
	{
		return y ;
	}
	
	public void setY(double y)
	{
		this.y = y ;
	}
	
	public double getWeight()
	{
		return weight ;
	}
	
	public void setWeight(double weight)
	{
		this.weight = weight ;
	}
	
	public int getPriority()
	{
		return priority ;
	}
	
	public void setPriority(int priority)
	{
		this.priority = priority ;
	}
	
	//Calculate Euclidean distance from shop (0,0) to the package destination:
    public double getDistanceFromShop() 
    {
        return Math.sqrt(Math.pow(x , 2) + Math.pow(y , 2)) ;
    }

}
