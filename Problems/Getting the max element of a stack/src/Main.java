import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        // put your code here
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        Deque<Integer> stack = new ArrayDeque<>();
        Deque<Integer> maxStack = new ArrayDeque<>();
        Queue<Integer> ret = new ArrayDeque<>();


        Integer max = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            String action = sc.next();
            switch (action) {
                case "push":
                    int v = sc.nextInt();
                    stack.offerLast(v);
                    if (v > max) {
                        max = v;
                    }
                    maxStack.offerLast(max);
                    break;
                case "pop":
                    stack.pollLast();
                    maxStack.pollLast();
                    max = maxStack.peekLast();
                    max = max == null ? Integer.MIN_VALUE : max;
                    break;
                case "max":
                    ret.offer(max);
                    break;
                default:
                    break;
            }
        }
        for (Integer e : ret) {
            System.out.println(e);
        }
    }
}