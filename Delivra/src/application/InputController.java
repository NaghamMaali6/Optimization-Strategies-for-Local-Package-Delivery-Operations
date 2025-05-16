package application ;

//necessary classes and libraries:
import java.io.* ;
import java.util.* ;
import javafx.event.ActionEvent ;
import javafx.fxml.* ;
import javafx.scene.Parent ;
import javafx.scene.Scene ;
import javafx.scene.control.Alert ;
import javafx.scene.control.Alert.AlertType ;
import javafx.scene.control.Button ;
import javafx.scene.control.TextField ;
import javafx.stage.Stage ;

public class InputController 
{
	private Stage stage ;  //Reference to the main stage of the application
	private Scene scene ;  //Reference to the current scene
    
	@FXML
    private TextField fn_V ;  //TextField to get file name where the vehicles info 
	
	@FXML
    private TextField fn_P ;  //TextField to get file name where the packages info 
    
    @FXML
    private Button Next ;  //to switch to the next page
    
    private List<Vehicle> vehicles = new ArrayList<>() ;  //list to hold Vehicle objects
    private List<Package> packages = new ArrayList<>() ;  //list to hold Package objects
	
	public void setStage(Stage stage)  //this Method is to set the primary stage and called from the Main class   
    {
    	this.stage = stage ;  //Set the stage reference for this controller
    	stage.setTitle("Delivra") ;  //Set the title of the application window
    }
	
	private void displayMessage(String message , String messageType)  //Method to display a message alert to the user 
    {
        Alert alert = new Alert("error".equalsIgnoreCase(messageType) ? AlertType.ERROR : AlertType.INFORMATION) ;  //Determine alert type
        alert.setContentText(message) ;  //Set the alert message
        alert.showAndWait() ;  //Display the alert and wait for user acknowledgment
    }
	
	//method triggered when Next button is clicked...
	@FXML
	public void Next_btn(ActionEvent event)
	{
		//get the file names from the TextFields and trim any whitespace
		String vehicleFile = fn_V.getText().trim() ;
        String packageFile = fn_P.getText().trim() ;
        
        if (vehicleFile.isEmpty() || packageFile.isEmpty())  //if either TextField is empty
        {
        	displayMessage("Please enter the name of your file!" , "error") ;
            return ;
        }
        
        //clear old data:
        vehicles.clear() ;
        packages.clear() ;
        
        //load files:
        boolean vehiclesLoader = loadVehicles(vehicleFile) ;
        boolean packagesLoader = loadPackages(packageFile) ;
        
        if(vehiclesLoader && packagesLoader)  //if both files loaded successfully
        {
        	displayMessage("Delivra has your info :)" , "information") ;
        	
        	//print loaded data to console to test files loading:
        	printVehicles() ;
        	printPackages() ;

        	try
        	{
        		stage = (Stage) Next.getScene().getWindow() ;  //Get the current stage
        		stage.close() ;  //Close the current stage
        		FXMLLoader loader = new FXMLLoader(getClass().getResource("Algorithms.fxml")) ;
        		Parent root = loader.load() ;
                AlgorithmsController controller = loader.getController() ;
            	controller.setVehiclesAndPackages(vehicles , packages) ;  //pass lists to AlgorithmsController class
                scene = new Scene(root , 700 , 500) ;  //Set the scene and Apply the new scene to the stage
                stage.setScene(scene) ;  //Apply the new scene to the stage
                stage.show() ;  //Show the stage with the new scene
        	}
        	catch (IOException e) 
            {
                e.printStackTrace() ;  //Print any exceptions that occur
                displayMessage("An error occurred! please try again later.", "error") ;
            }
        	
        }
	}
	
	
	private boolean loadVehicles(String fileName)  //method to load vehicles from file
    {
        try 
        {
            Scanner scanner = new Scanner(new File(fileName)) ;  //open file for reading
            if (scanner.hasNextLine()) 
            {
            	scanner.nextLine() ; //skip headers line
            }

            while (scanner.hasNextLine()) 
            {
                String line = scanner.nextLine() ;  //read line
                String[] parts = line.split(",") ;  //split line by commas ,
                if (parts.length != 2)  
                {
                	continue ;  //skip if not exactly 2 columns
                }

                int id = Integer.parseInt(parts[0].trim()) ;  //read vehicle ID
                double capacity = Double.parseDouble(parts[1].trim()) ;  //read capacity

                vehicles.add(new Vehicle(id , capacity)) ;  //add to the list  
            }
            
            scanner.close() ;  //close the file
            
            return true ;  //loading was successful
        } 
        catch (FileNotFoundException | NumberFormatException e)  //if the file not found or numbers are wrong
        {
            displayMessage("Error reading vehicles file!" , "error") ;
            return false ;
        }
    }

    private boolean loadPackages(String fileName)  //method to load packages from file
    {
        try 
        {
            Scanner scanner = new Scanner(new File(fileName)) ;  //open file for reading
            if (scanner.hasNextLine()) 
            {
            	scanner.nextLine() ;  //skip headers line
            }

            while (scanner.hasNextLine()) 
            {
                String line = scanner.nextLine() ;  //read line
                String[] parts = line.split(",") ;  //split line by commas ,
                if (parts.length != 5) 
                {
                	continue ;  //skip if not exactly 5 columns
                }
                
                //get values:
                int id = Integer.parseInt(parts[0].trim()) ;
                int x = Integer.parseInt(parts[1].trim()) ;
                int y = Integer.parseInt(parts[2].trim()) ;
                double weight = Double.parseDouble(parts[3].trim()) ;
                int priority = Integer.parseInt(parts[4].trim()) ;

                packages.add(new Package(id , x , y , weight , priority)) ;  //add to the list  
            }
            
            scanner.close() ;  //close the file
            
            return true ;  //loading was successful
        } 
        catch (FileNotFoundException | NumberFormatException e)  //if the file not found or numbers are wrong
        {
            displayMessage("Error reading vehicles file!" , "error") ;
            return false ;
        }
    }
    
    //methods print to console to test loading files:
    
    private void printVehicles() 
    {
        System.out.println("=== Vehicles ===") ;
        for (Vehicle v : vehicles) 
        {
            System.out.println("ID: " + v.getID() + ", Capacity: " + v.getCapacity()) ;
        }
    }

    private void printPackages() 
    {
        System.out.println("=== Packages ===") ;
        for (Package p : packages) 
        {
            System.out.println("ID: " + p.getID() + ", X: " + p.getX() + ", Y: " + p.getY() + ", Weight: " + p.getWeight() + ", Priority: " + p.getPriority()) ;
        }
    }

}
