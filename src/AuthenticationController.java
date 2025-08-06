import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthenticationController {


    //declare database
    Database db;

    //email validation

    //login

     public AuthenticationController() {
         db = new Database();
     }

    public boolean login(String username , String password) throws NoSuchAlgorithmException {
         //check if the user exists
        if(db.userexists(username))
        {
            //get user
             user a = db.getuser(username);
             //validate the password
             if(hashpassword(password).equals(a.getPassword()))
             {
                 return true;
             }
        }
        return false;
    }




    //sign up


    //hashpassword using SHA-256

    public String hashpassword(String password) throws NoSuchAlgorithmException
    {
        try{
         //choose an algorithm to hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //more robust
            byte[] hash =  md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder  str  = new StringBuilder();

            for(byte b : hash )
            {
                str.append(String.format("%02x",b));
            }
             return str.toString();
        } catch (RuntimeException e) {
            throw new RuntimeException("Algorithm doesnt exists");
        }
    }
    //email validator
    public boolean  EmailValidator(String email)
    {

        //check if pattern matches
        String pattern =   "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        //check if it matches
        if(email.matches(pattern))
        {

            return true ;
        }

        System.out.println("Email is not valid");
        return false;



    }

    //password validator

    public boolean PasswordValidator(String password)
    {

        String passwordPattern =
                "^(?=.*[0-9])" +       // at least 1 digit
                        "(?=.*[a-z])" +        // at least 1 lowercase
                        "(?=.*[A-Z])" +        // at least 1 uppercase
                        "(?=.*[@#$%^&+=!])" +  // at least 1 special char
                        "(?=\\S+$)" +          // no whitespace
                        ".{8,}$";              // at least 8 characters
        //check if the password pattern
        if(password.matches(passwordPattern))
        {
            return true;
        }
       System.out.println("Password is not valid");
        return false;


    }


    //sign up
    public boolean signup(String username , String password , String name , int age , String email ) throws NoSuchAlgorithmException
    {

        //check if the username is already exists

        if(db.userexists(username))
        {
            System.out.println("username already exists");
            return false;
        }
        else if(EmailValidator(email) && PasswordValidator(password))
        {
            //create a new user
            String hashedpassword =  hashpassword(password);
            user a =  new  user ( name, age, email, hashedpassword , username);
            db.addUser(a);
            return true;

        }
        return false;



    }

    //check if the userexists
    public boolean userexists(String username)
    {
        return db.userexists(username);

    }




}
