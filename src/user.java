public class user {
    private String fullname;
    private int age;
    private String email;
    private String password;
    private String username;


    public user(String fullname, int age, String email, String password, String username) {
        this.fullname = fullname;
        this.age = age;
        this.email = email;
        this.password = password;
        this.username = username;
    }


    //get and setters


    public int getAge() {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age ;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;

    }
    public void setPassword(String password) {
        this.password = password;

    }
    public String getUsername() {
        return username;

    }
    public void setUsername(String username) {
        this.username = username;

    }
    public String getFullname() {
        return fullname;

    }
    public void setFullname(String fullname) {
        this.fullname = fullname;

    }


}
