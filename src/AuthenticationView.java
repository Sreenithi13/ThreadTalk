import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class AuthenticationView {

    Scanner scanner = new Scanner(System.in);
    AuthenticationController auth = new AuthenticationController();

    public void Start() throws NoSuchAlgorithmException {
        while (true) {
            System.out.println("Please enter your choice:");
            System.out.println("1. Login");
            System.out.println("2. Sign up");
            System.out.println("3. Exit");

            int option = scanner.nextInt();
            scanner.nextLine(); // Clear the newline left by nextInt()

            switch (option) {
                case 1:
                    Login();
                    break;
                case 2:
                    signup();
                    break;
                case 3:
                    System.out.println("Thank you for using our application.");
                    return;
                default:
                    System.out.println("Please enter a valid option.");
            }
        }
    }

    public void signup() throws NoSuchAlgorithmException {
        System.out.println("Please enter your name:");
        String name = scanner.nextLine();

        System.out.println("Please enter your Username:");
        String username = scanner.nextLine();

        System.out.println("Please enter your Password:");
        String password = scanner.nextLine();

        System.out.println("Please confirm your Password:");
        String confirmPassword = scanner.nextLine();

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Registration failed.");
            return;
        }

        System.out.println("Please enter your Email:");
        String email = scanner.nextLine();

        System.out.println("Enter your age:");
        int age = scanner.nextInt();
        scanner.nextLine(); // Clear the newline after age

        boolean status = auth.signup(username, password, name, age, email);
        if (status) {
            System.out.println("You have successfully registered.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    public void Login() throws NoSuchAlgorithmException {
        System.out.println("Please enter your Username:");
        String username = scanner.nextLine();

        System.out.println("Please enter your Password:");
        String password = scanner.nextLine();

        boolean status = auth.login(username, password);
        if (status) {

            System.out.println("You have successfully logged in.");
        } else {
            System.out.println("Login failed. Invalid credentials.");
        }
    }
}
