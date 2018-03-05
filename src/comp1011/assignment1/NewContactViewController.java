package comp1011.assignment1;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
    
    /**
     * This method will read from the scene and try to create a new instance of
     * Contact. If a contact was successfully created, it is updated in the
     * database.
     */
    public void saveContactButtonPushed(ActionEvent event){
        try{
            Contact contact = new Contact(firstNameTextField.getText(),lastNameTextField.getText(),addressTextField.getText(),phoneNumberTextField.getText(), birthdayDatePicker.getValue());
            errMsgLabel.setText("");  //do not show errors if successful
        }
        catch (Exception e){
            errMsgLabel.setText(e.getMessage());
        }
    }
            
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        errMsgLabel.setText("");
    }    
    
}
