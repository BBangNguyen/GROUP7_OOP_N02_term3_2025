import java.util.Random;

public class randNum {
    public static int randNum() {
        Random rand = new Random();
        return rand.nextInt(100); // tra ve so ngau nhien tu 0 den 99
    }
}
