package application ;

//necessary classes and libraries:
import java.io.IOException ;
import java.util.List ;
import java.util.Map ;
import javafx.event.ActionEvent ;
import javafx.fxml.* ;
import javafx.scene.Parent ;
import javafx.scene.Scene ;
import javafx.scene.canvas.Canvas ;
import javafx.scene.canvas.GraphicsContext ;
import javafx.scene.control.Alert ;
import javafx.scene.control.Button ;
import javafx.scene.control.Slider ;
import javafx.scene.control.Alert.AlertType ;
import javafx.scene.paint.Color ;
import javafx.scene.text.Font ;
import javafx.scene.text.FontWeight ;
import javafx.stage.Stage ;

public class OutputController 
{
	private Stage stage ;  //Reference to the main stage of the application
    private Scene scene ;  //Reference to the current scene
    private Parent root ;  //Reference to the root node of the FXML layout

    @FXML
    private Canvas mapCanvas ;  //canvas to draw vehicles paths

    @FXML
    private Slider zoomSlider ;  //slider to control horizontal zoom

    @FXML
    private Slider verticalZoomSlider ;  //slider to control vertical zoom  
    
    @FXML
    private Button Back ;  //Button to switch back to the Algorithms page

    private Map<Vehicle, List<Package>> solution ;  //to store result(vehicles and their assigned packages)
    
    private double scaleX = 5.0 ;  //Horizontal zoom scale factor
    private double scaleY = 5.0 ;  //Vertical zoom scale factor

    //Array of colors for different vehicles:
    private final Color[] vehicleColors = {Color.BLUE , Color.RED , Color.GREEN , Color.ORANGE ,Color.PURPLE , Color.BROWN , Color.CYAN , Color.MAGENTA , Color.WHITE} ;

    @FXML
    private void initialize() 
    {
        //Horizontal zoom:
        zoomSlider.setMin(1.0) ;  //set min value
        zoomSlider.setMax(20.0) ;  //set max value
        zoomSlider.setValue(scaleX) ;  //set default value
        zoomSlider.valueProperty().addListener((obs , oldVal , newVal) -> 
        {
            scaleX = newVal.doubleValue() ;  //update scaleX  
            if (solution != null) 
            {
            	drawPaths(solution) ;  //redraw paths when slider changes
            }
        }) ;

        //Vertical zoom:
        zoomSlider.setMin(1.0) ;  //set min value
        zoomSlider.setMax(20.0) ;  //set max value
        verticalZoomSlider.setValue(scaleY) ;  //set default value
        verticalZoomSlider.valueProperty().addListener((obs , oldVal , newVal) -> 
        {
            scaleY = newVal.doubleValue() ;  //update scaleY
            if (solution != null) 
            {
            	drawPaths(solution) ;  //redraw paths when slider changes
            }
        }) ;
    }

    public void setSolution(Map<Vehicle , List<Package>> solution) 
    {
        this.solution = solution ;  //get result from AlgorithmController class and set as solution
        drawPaths(solution) ;  //dra the vehicles paths on the canvas
    }

    private void drawPaths(Map<Vehicle , List<Package>> solution) 
    {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D() ;  //get drawing context
        double w = mapCanvas.getWidth() ;  //canvas width
        double h = mapCanvas.getHeight() ;  //canvas height
        double offsetX = w / 2 ;  //center x  
        double offsetY = h / 2 ;  //center y

        gc.clearRect(0 , 0 , w , h) ;  //clear canvas

        //draw Grid lines:
        gc.setStroke(Color.LIGHTGRAY) ;  //light gray color
        gc.setLineWidth(0.5) ;  //make it thin lines
        int gridStep = 10 ;  //grid step size
        
        //draw vertical lines:
        for (int x = -200 ; x <= 200 ; x += gridStep)  
        {
            double cx = offsetX + x * scaleX ;
            gc.strokeLine(cx , 0 , cx , h) ;
        }
        
        //draw horizontal lines:
        for (int y = -200 ; y <= 200 ; y += gridStep) 
        {
            double cy = offsetY - y * scaleY ;
            gc.strokeLine(0 , cy , w , cy) ;
        }

        //Axes X and Y:
        gc.setStroke(Color.DARKGRAY) ;  //dark gray color
        gc.setLineWidth(1.5) ;  //axes lines width 
        gc.strokeLine(0 , offsetY , w , offsetY) ;  // X-axis
        gc.strokeLine(offsetX , 0 , offsetX , h) ;  // Y-axis

        //Shop at origin:
        gc.setFill(Color.GOLD) ;  //gold color for the shop location
        gc.fillOval(offsetX - 7 , offsetY - 7 , 14 , 14) ;  //filled circle
        gc.setStroke(Color.GOLD) ;  //gold color for border of the circle
        gc.strokeOval(offsetX - 7 , offsetY - 7 , 14 , 14) ;  //border of the circle
        gc.setFont(Font.font("Times New Roman" , FontWeight.BOLD , 17)) ;  //style of the shop label
        gc.setFill(Color.GOLD) ;  //gold for shop label
        gc.fillText("🏪 Shop (0 , 0)" , offsetX + 12 , offsetY + 25) ;  //shop label

        //Draw each vehicle's path from the shop and back:
        int i = 0 ;
        for (Map.Entry<Vehicle , List<Package>> entry : solution.entrySet()) 
        {
        	//pick color for eac vehicle randomly and set line width:
        	Color color = vehicleColors[i % vehicleColors.length] ;  
            gc.setStroke(color) ;  //draw line
            gc.setLineWidth(2) ;  //set line width
            gc.setFill(color) ;

            double lastX = offsetX ;
            double lastY = offsetY ;

            //draw lines between packages for each vehicle:
            for (Package pkg : entry.getValue())
            {
                double x = offsetX + pkg.getX() * scaleX ;
                double y = offsetY - pkg.getY() * scaleY ;

                gc.strokeLine(lastX , lastY , x , y) ;  //draw line to next package
                gc.fillOval(x - 4 , y - 4 , 8 , 8) ;  //mark package location
                gc.setFont(Font.font("Times New Roman" , 14)) ;  //style for package label(coordinate)
                gc.fillText("(" + pkg.getX() + " , " + pkg.getY() + ")" , x + 5 , y - 5) ;  //show coordinates

                lastX = x ;
                lastY = y ;
            }

            gc.strokeLine(lastX , lastY , offsetX , offsetY) ;  //Line back to shop            
            gc.setFont(Font.font("Times New Roman" , 14)) ;  //style for vehicle label
            gc.fillText("Vehicle " + (i + 1) , lastX + 10 , lastY + 10) ;  //vehicle label
            i++ ; 
        }

        //draw Legend:
        
        //set legend location:
        double legendX = w - 800 ;
        double legendY = 15 ;
        
        gc.setFont(Font.font("Times New Roman" , FontWeight.BOLD , 17)) ;  //style legend labels
        gc.setFill(Color.WHITE) ;  //white color
        gc.fillText("Legend:", legendX , legendY) ;  //legend label 

        i = 0 ;
        for (Vehicle v : solution.keySet()) 
        {
            Color c = vehicleColors[i % vehicleColors.length] ;  
            gc.setFill(c) ;
            gc.fillRect(legendX , legendY + 15 + i * 20 , 12 , 12) ;  //color box
            gc.setFill(Color.WHEAT) ;
            gc.setFont(Font.font("Times New Roman" , 14)) ;
            gc.fillText("Vehicle " + (i + 1) , legendX + 20 , legendY + 25 + i * 20) ;  //label
            i++ ;
        }
    }
    
    @FXML 
    void GoBack(ActionEvent event)  //Method executed when the "Back!" button is clicked
    {
    	try
    	{
    		stage = (Stage) Back.getScene().getWindow() ;  //Get the current stage
            stage.close() ;  //Close the current stage
            root = FXMLLoader.load(getClass().getResource("Input.fxml")) ;  //Load the new scene
            scene = new Scene(root , 750 , 500) ;  //Set the scene
            stage.setScene(scene) ;  //Apply the new scene to the stage
            stage.show() ;  //Show the stage with the new scene
    	} 
    	catch (IOException e) 
        {
            e.printStackTrace() ;  //Print any exceptions that occur during the process
            displayMessage("Error!!!" , "error") ;  //Show error if login fails
        }
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
