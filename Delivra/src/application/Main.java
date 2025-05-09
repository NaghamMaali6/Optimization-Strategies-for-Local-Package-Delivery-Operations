/* Delivra */

/* Nagham Maali-1212312
 * Dr. Samah Alaydi
 * Section 2 */

/*The Main class is the entry point of the JavaFX application*/

package application ;

//necessary classes and libraries:
import javafx.animation.PauseTransition ;
import javafx.application.Application ;
import javafx.stage.Stage ;
import javafx.util.Duration ;
import javafx.scene.Scene ;
import javafx.scene.layout.AnchorPane ;
import javafx.fxml.FXMLLoader ;


public class Main extends Application 
{
	@Override
	public void start(Stage primaryStage) 
	{
		try 
		{
            AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("CoverPage.fxml")) ;  //Load the cover page layout from the FXML file
            
            Scene scene = new Scene(root , 340 , 500) ;  //Create a scene with the root layout and set dimensions
            
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()) ;  //Apply a CSS stylesheet to the scene(not used)
            
            primaryStage.setScene(scene) ;  //Set the scene to the primary stage
			
            primaryStage.setTitle("Delivra") ;  //Set the title of the primary stage
			
			PauseTransition pause = new PauseTransition(Duration.seconds(3)) ;  //Create a PauseTransition to delay for 3 seconds
			pause.setOnFinished(_ -> 
            {
            	try 
                {
            		//Define the action to perform after the pause ends...
                	//Load the Input page layout from the FXML file:
            		FXMLLoader loader = new FXMLLoader(getClass().getResource("Input.fxml")) ;
                    AnchorPane Root = loader.load() ;

                    Scene Scene = new Scene(Root , 750 , 500) ;  //Create a new scene 
                    primaryStage.setScene(Scene) ;  //Set the new scene to the primary stage

                    InputController controller = loader.getController() ;  //Retrieve the controller 
                    controller.setStage(primaryStage) ;  //Pass the primary stage to the controller
                }
            	catch (Exception e) 
                {
                    e.printStackTrace() ;  //Print any exceptions that occur
                }
            }) ;
			
			pause.play() ;  //Start the pause timer
			
			primaryStage.show() ;  //Display the primary stage
		} 
		catch(Exception e) 
		{
			e.printStackTrace() ;  //Print any exceptions that occur
		}
	}
	
	public static void main(String[] args) 
	{
		launch(args) ;  //Launch the JavaFX application
	}
}
