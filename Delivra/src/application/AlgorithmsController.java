package application;

import java.io.*;
import java.util.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AlgorithmsController 
{
	private Stage stage ;  //Reference to the main stage of the application

	@FXML
    private Button SA_btn ;  //button for simulated annealing algorithm
    
    @FXML
    private Button GA_btn ;  //button for genetic algorithm
    
    @FXML
    private Button SP ;  //button to show the result
    
    @FXML
    private ImageView Del_img ;  //to show an image just as an additional thing for nice design
    
    private List<Vehicle> vehicles ;  //list to store vehicles data
    private List<Package> packages ;  //list to store packages data
    
    Map<Vehicle , List<Package>> result ;  //to store the algorithm result
    
    public void setVehiclesAndPackages(List<Vehicle> vehicles , List<Package> packages)  //method to set vehicles and packages from InputController class 
    {
        this.vehicles = vehicles ;
        this.packages = packages ;
    }
	
	@FXML
    public void initialize()  //Initialize method is called automatically when the FXML is loaded
    {
        String imagePath = "C:\\Users\\User\\Desktop\\AI\\Project1\\1212312_Project\\vehicle.jpg" ;  //Define the file path to the image
        
        //Load the image and set it to the ImageView:
        File file = new File(imagePath) ;  //Create a File object for the image
        
        if (file.exists())  //Check if the image file exists at the given path
        {
        	//If the file exists: 
        	Image image = new Image(file.toURI().toString()) ;  //create an Image object from the file and set it to the ImageView  
        	Del_img.setImage(image) ;  //Display the image in the ImageView
        } 
        else 
        {
            System.err.println("Image is not found: " + imagePath) ;  //If the file does not exist, print an error message to the console
        }
        
        //Set button click actions:
        SA_btn.setOnAction(_ -> SAA()) ;  //call SAA() method when SA button is clicked
        GA_btn.setOnAction(_ -> GA()) ;  //claa GA() method when GA button is clicked 
        
        SP.setOnAction(_ -> 
        {
	        if (result == null || result.isEmpty()) 
	        {
	            displayMessage("you have to pick an algorithm first!" , "error") ;  //if SP button is clicked while result isn't ready, show error message
	            return ;
	        }
        }) ;
    }
	
	@FXML
	void SAA()  //When SA_btn is clicked
	{
	    System.out.println("Simulated Annealing selected.") ;  //print to console to test SA_btn
	    result = SimulatedAnnealing.optimize(vehicles , packages) ;  //run SAA 
	    SimulatedAnnealing.printSolution(result) ;  //print result to console to test that the algorithm is working
	    
	    //when SP is clicked:
	    SP.setOnAction(_ -> 
	    {
	        if (result == null || result.isEmpty())  //if result is empty
	        {
	            displayMessage("generating result failed!" , "error") ;
	            return ;
	        }

	        try 
	        {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("Output.fxml")) ;
	            Parent root = loader.load() ;
	            OutputController controller = loader.getController() ;
	            controller.setSolution(result) ;

	            stage = (Stage) SP.getScene().getWindow() ;
	            stage.setScene(new Scene(root , 930 , 690)) ;
	            stage.show() ;
	        }
	        catch (IOException e) 
	        {
	            e.printStackTrace() ;
	        }
	    }) ;
	}

	@FXML
	void GA()  //When GA_btn is clicked
	{
	    System.out.println("Genetic Algorithm selected.") ;  //print to console to test GA_btn
	    result = Genetic.runGeneticAlgorithm(packages , vehicles) ;  //run GA
	    Genetic.printSolution(result) ;  //print result to console to test that the algorithm is working

	    //when SP is clicked:
	    SP.setOnAction(_ -> 
	    {
	        if (result == null || result.isEmpty())  //if result is empty 
	        {
	            displayMessage("generating result faild!" , "error") ;
	            return ;
	        }

	        try 
	        {
	        	FXMLLoader loader = new FXMLLoader(getClass().getResource("Output.fxml")) ;  //Load the Output page layout from the FXML file
	            Parent root = loader.load() ;  //load root node
	            OutputController controller = loader.getController() ;  //get the controller
	            controller.setSolution(result) ;  //pass result to the controller

	            stage = (Stage) SP.getScene().getWindow() ;  //get the current window
	            stage.setScene(new Scene(root , 930 , 690)) ;  //Create a new scene and set dimensions
	            stage.show() ;  //Display the stage
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace() ;  //Print any exceptions that occur
	        }
	    }) ;
	}
	
	private void displayMessage(String message , String messageType)  //Method to display a message alert to the user 
    {
        Alert alert = new Alert("error".equalsIgnoreCase(messageType) ? AlertType.ERROR : AlertType.INFORMATION) ;  //Determine alert type
        alert.setContentText(message) ;  //Set the alert message
        alert.showAndWait() ;  //Display the alert and wait for user acknowledgment
    }
	
	public void setStage(Stage stage)  //this Method is to set the primary stage and called from the Main class   
    {
    	this.stage = stage ;  //Set the stage reference for this controller
    	stage.setTitle("Delivra") ;  //Set the title of the application window
    }
}