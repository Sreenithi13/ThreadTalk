import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

//create a hashmap to store the user and manage concurrency

    private static final ConcurrentHashMap<String, user>  users = new ConcurrentHashMap<>();



   //add the user

    public boolean addUser(user a )
    {
        //check if the username exists in the hashmap
         if(users.containsKey(a.getUsername()))
         {
             System.out.println("User Already Exists");
             return false ;
         }

         users.put(a.getUsername(),a);
         return true ;
    }

    //remove a user from the hashmap

    public boolean removeuser(user  a)
    {
        if(users.containsKey(a.getUsername()))
        {
            users.remove(a.getUsername());
            return true ;
        }

        return false ;

    }

    //get user

    public  user getuser(String username)
    {
        return  this.users.get(username);
    }

    //check if the userexists

    public boolean userexists (String username )
    {
         if(users.containsKey(username))
         {

             return true;
         }
         return false;
    }





}
