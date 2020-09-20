import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Main {
    public static void main(String[] args) {
        // put your code here
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        sc.nextLine();
        int[] ii = Arrays.stream(sc.nextLine().split("\\s"))
                .mapToInt(Integer::parseInt).toArray();
        int count = 0;
        for (int i = 1; i < ii.length; i++) {
           int ele = ii[i];
           int j = i - 1;

           while (ii[j] < ele) {
               ii[j + 1] = ii[j];
               count++;
               break;
           }

        }
        System.out.println(count);
    }
}