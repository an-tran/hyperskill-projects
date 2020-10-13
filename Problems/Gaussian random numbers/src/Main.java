import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // write your code here
        int k = scanner.nextInt();
        int n = scanner.nextInt();
        double m = scanner.nextDouble();

        int count = 0;
        int seed = k;
        while (true) {
            Random r = new Random(seed);
            boolean found = true;
            for (int i = 0; i < n; i++) {
                if (Double.compare(r.nextGaussian(), m) == 1) {
                    found = false;
                    break;
                }
            }
            if (found) {
                System.out.println(seed);
                break;
            } else {
                seed++;
            }
        }
    }
}