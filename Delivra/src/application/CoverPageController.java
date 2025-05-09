package application ;

//necessary classes and libraries:
import java.io.File ;
import javafx.fxml.FXML ;
import javafx.scene.image.* ;

public class CoverPageController 
{
	@FXML
    private ImageView coverImage ;  //Declare an ImageView element from the FXML file
	
	@FXML
    public void initialize()  //Initialize method is called automatically when the FXML is loaded
    {
        String imagePath = "C:\\Users\\User\\Desktop\\AI\\Project1\\1212312_Project\\Cover_Page.png" ;  //Define the file path to the cover page image
        
        //Load the image and set it to the ImageView:
        File file = new File(imagePath) ;  //Create a File object for the image
        
        if (file.exists())  //Check if the image file exists at the given path
        {
        	//If the file exists: 
        	Image image = new Image(file.toURI().toString()) ;  //create an Image object from the file and set it to the ImageView  
            coverImage.setImage(image) ;  //Display the image in the ImageView
        } 
        else 
        {
            System.err.println("Image not found: " + imagePath) ;  //If the file does not exist, print an error message to the console
        }
    }
	
}
