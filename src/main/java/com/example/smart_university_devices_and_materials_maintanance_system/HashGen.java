
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGen {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("Admin@2024: " + encoder.encode("Admin@2024"));
        System.out.println("Password@2024: " + encoder.encode("Password@2024"));
    }
}
