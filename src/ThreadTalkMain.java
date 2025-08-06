import java.security.NoSuchAlgorithmException;

public class ThreadTalkMain {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        AuthenticationView authview = new AuthenticationView();

        authview.Start();
    }
}
