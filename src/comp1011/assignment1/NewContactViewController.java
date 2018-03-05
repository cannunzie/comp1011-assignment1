package comp1011.assignment1;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author Caleb
 */
public class NewContactViewController implements Initializable {

    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField addressTextField;
    @FXML private TextField phoneNumberTextField;
    @FXML private DatePicker birthdayDatePicker;
    @FXML private Label errMsgLabel;
    @FXML private ImageView contactProfileImage;
    
    private File imageFile;
    private boolean imageFileChanged;

    /**
     * When this button is pushed, a FileChooser object is launched to allow the
     * user to browse for a new image file. When that is complete, it will 
     * update the view with a new image
     */
    public void chooseImageButtonPushed(ActionEvent event) throws IOException{
        //get the Stage to open a new window (or Stage in JavaFX)
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    
        //Instantiate a FileChooser object
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        
        //filter filetypes to only .jpg and .png
        FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("Image File (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("Image File (*.png)", "*.png");
        fileChooser.getExtensionFilters().addAll(jpgFilter, pngFilter);
    
        //Set the user's picture directory or user directory if not available
        String  userDirectoryString = System.getProperty("user.home")+"\\Pictures";
        File userDirectory = new File(userDirectoryString);
        
        //if you cannot navigate to the pictures directory, go to the user home
        if(!userDirectory.canRead())
            userDirectory = new File(System.getProperty("user.home"));
        
        fileChooser.setInitialDirectory(userDirectory);
        
        //open the file dialog window
        File tmpImageFile = fileChooser.showOpenDialog(stage);
        
        if(tmpImageFile != null){     
            imageFile = tmpImageFile;
            
            //update the ImageView with the new image
            if(imageFile.isFile()){
                try{
                    BufferedImage bufferedImage = ImageIO.read(imageFile);
                    Image img = SwingFXUtils.toFXImage(bufferedImage, null);
                    contactProfileImage.setImage(img);
                    imageFileChanged = true;
                }
                catch (IOException e){
                    System.err.println(e.getMessage());
                }
            }
        }
    }
    
    /** 
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imageFileChanged = false; //initially the image has not changed, use the default
        errMsgLabel.setText("");
        birthdayDatePicker.setValue(LocalDate.now().minusYears(18));
        
        //load the default image for the avatar
        try{
            imageFile = new File("./src/Images/default-profile.jpg");
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            contactProfileImage.setImage(image);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * This method will read from the scene and try to create a new instance of
     * Contact. If a contact was successfully created, it is updated in the
     * database.
     */
    public void saveContactButtonPushed(ActionEvent event){
        try{
            Contact contact;
            if(imageFileChanged == false)
                contact = new Contact(firstNameTextField.getText(),lastNameTextField.getText(),addressTextField.getText(),phoneNumberTextField.getText(), birthdayDatePicker.getValue());  
            else
                contact = new Contact(firstNameTextField.getText(),lastNameTextField.getText(),addressTextField.getText(),phoneNumberTextField.getText(), birthdayDatePicker.getValue(), imageFile);
            errMsgLabel.setText("");  //do not show errors if successful
            contact.insertIntoDB();
        }
        catch (Exception e){
            errMsgLabel.setText(e.getMessage());
        }
    }
}
