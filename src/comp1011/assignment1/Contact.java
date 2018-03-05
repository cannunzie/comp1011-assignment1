/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comp1011.assignment1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

/**
 *
 * @author Caleb
 */
public class Contact {
    private String firstName, lastName, address, phoneNumber;
    private LocalDate birthday;
    private File imageFile;

    public Contact(String firstName, String lastName, String address, String phoneNumber, LocalDate birthday) {
        setFirstName(firstName);
        setLastName(lastName);
        setAddress(address);
        setPhone(phoneNumber);
        setBirthday(birthday);
        setImageFile(new File("./src/image/default-image.jpeg"));
    }

    public Contact(String firstName, String lastName, String address, String phoneNumber, LocalDate birthday, File imageFile) {
        this(firstName, lastName, address, phoneNumber, birthday);
        setImageFile(imageFile);
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phoneNumber;
    }
    /**
     * area code    city    house
     * NXX          -XXX    -XXXX
     * @param phoneNumber 
     */
    public void setPhone(String phoneNumber) {
        if (phoneNumber.matches("(2-9)\\d{2}[-.]?d{3}[.-]\\d{4}"))
            this.phoneNumber = phoneNumber;
        else
            throw new IllegalArgumentException("Phone numbers must be in the format NXX-XXX-XXXX");
    }
    
    public LocalDate getBirthday() {
        return birthday;
    }
    /**
     * This will validate that the contact has a birthday before now
     * @param birthday 
     */
    public void setBirthday(LocalDate birthday) {
        if(birthday.isBefore(LocalDate.now()))
            this.birthday = birthday;
        else
            throw new IllegalArgumentException("You haven't been born yet...choose a proper birthday");
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }
    
    /**
     * This method will copy the file specified to the image directory on this 
     * server and give it a unique name
     */
    public void copyImageFile() throws IOException{
        //create a new Path to copy the image into a local directory
        Path sourcePath = imageFile.toPath();
        
        String uniqueFileName = getUniqueFileName(imageFile.getName());
        
        Path targetPath = Paths.get("./src/images/" + uniqueFileName);
        
        //copy the file to the new directory
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        //update the imageFile to point to the new File
        imageFile = new File(targetPath.toString());
    }
    
    /**
     * This method will receive a String that represents a file name and return
     * a String with a random and unique set of letters prefixed to it
     */
    private String getUniqueFileName(String oldFileName){
        String newFileName;
        
        //Create a Random Number Generator
        SecureRandom rng = new SecureRandom();
        
        //loop until we have a unique file name
        do{
            newFileName = "";
            
            //generate 32 random characters
            for(int count = 0; count < 32; count++){
                int nextChar;
                
                do{
                    nextChar = rng.nextInt(123);
                } while(!validCharacterValue(nextChar));
                
                newFileName = String.format("%s%c", newFileName, (char)nextChar);
            }
            newFileName += oldFileName;
            
        } while(!uniqueFileInDirectory(newFileName));
        
        return newFileName;
    }
    
    /**
     * This method will search the images directory and ensure the file name
     * is unique
     */
    public boolean uniqueFileInDirectory(String fileName){
        File directory = new File("./src/images/");
        
        File[] dir_contents = directory.listFiles();
        
        for (File file: dir_contents){
            if (file.getName().equals(fileName))
                return false;
        }
        return true;
    }
    
    /**
     * This method will validate if the integer given corresponds to a valid
     * ASCII character that can be used in filenames
     */
    public boolean validCharacterValue(int asciiValue){
        //0-9 ASCII range 48-57
        if(asciiValue >= 48 && asciiValue <= 57)
            return true;
        //A-Z ASCII range 65-90
        if(asciiValue >= 65 && asciiValue <= 90)
            return true;
        //A-Z ASCII range 97-122
        if(asciiValue >= 97 && asciiValue <= 122)
            return true;
        
        return false;
    }
    
    /**
     * This method will return a formatted String with the person's details
     */
    @Override
    public String toString(){
        return String.format("%s %s was born on %s. Their phone number is %s and they live at %s", firstName, lastName, birthday.toString(), phoneNumber, address);
    }
    
    /**
     * This method will write the instance of the Volunteer into the database
     */
    public void insertIntoDB() throws SQLException{
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        
        try{
            //1. Connect to the database
            conn = DriverManager.getConnection("jdbc:sqlserver://comp1011.database.windows.net:1433;database=COMP1011-Assignment1;user=annunzie@comp1011;password={6Maplewood};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;");
            
            //2. Create a String that holds the query with ? as user inputs
            String sql = "INSERT INTO Contacts(firstName, lastName, phoneNumber, birthday, imageFile, address)"
                    + "VALUES (?,?,?,?,?,?)";
            
            //3. Prepare the query
            preparedStatement = conn.prepareStatement(sql);
            
            //4. Convert the birthday into a SQL date
            Date db = Date.valueOf(birthday);
            
            //5. Bind the values to the parameters
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.setDate(4, db);
            preparedStatement.setString(5, imageFile.getName());
            preparedStatement.setString(6, address);
            
            preparedStatement.executeUpdate();
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }
        finally{
            if (preparedStatement != null)
                preparedStatement.close();
            
            if (conn != null)
                conn.close();
        }
    }
    
}
