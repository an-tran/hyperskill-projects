import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;
import java.util.stream.Collectors;

class Main {
    public static void main(String[] args) {
        // put your code here
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] ids = new int[n];
        int[] loads = new int[n];
        for (int i = 0; i < n; i++) {
            ids[i] = sc.nextInt();
            loads[i] = sc.nextInt();
        }

        int l1 = 0;
        int l2 = 0;
        Queue<Integer> qa = new ArrayDeque<>();
        Queue<Integer> qb = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (l1 <= l2) {
                qa.offer(ids[i]);
                l1 += loads[i];
            } else {
                qb.offer(ids[i]);
                l2 += loads[i];
            }
        }
        System.out.println(String.join(" ", qa.stream().map(e -> e.toString()).collect(Collectors.toList())));
        System.out.println(String.join(" ", qb.stream().map(e -> e.toString()).collect(Collectors.toList())));
    }
}