import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // put your code here
        final int a = 53;
        final long m = 1_000_000_000 + 9;
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        sc.nextLine();
        List<Integer[]> pairs = new ArrayList<>();
        while(sc.hasNextLine()) {
            Integer[] p = Arrays.stream(sc.nextLine().trim().split("\\s")).map(Integer::parseInt).toArray(Integer[]::new);
            pairs.add(p);
        }

        long[] hprefixs = new long[s.length() + 1];
        for (int i = 1; i <= s.length(); i++) {
            hprefixs[i] = polyHash(s.substring(0, i), a, m);
            if (i != s.length() ) {
                System.out.print(hprefixs[i] + " ");
            } else {
                System.out.print(hprefixs[i] );
            }
        }
        System.out.println();

        for (int i = 0; i < pairs.size(); i++) {
            int l = pairs.get(i)[0];
            int r = pairs.get(i)[1];
            long ai = (long) Math.pow(a, l);
            long hij = ((hprefixs[r ] - hprefixs[l ])) % m;
            if (l == r) {
                hij = hprefixs[r];
            }
//            System.out.println("ai " + ai);
            if (i != pairs.size() -1) {
                System.out.print(hij + " " + s.substring(l, r) + "  ");
            } else {
                System.out.print(hij + " "  + s.substring(l, r) + "  ");
            }
        }
        System.out.println();
    }

    private static long polyHash(String s, int a, long m) {
        long hash = 0;
        long pow = 1;
        for (int i = 0; i < s.length(); i++) {
            hash += pow * charToLong(s.charAt(i));
            hash %= m;
            pow *= a;
        }
        return hash;
    }

    /* 1 */
    public static long charToLong(char ch) {
        return (long)(ch - 'A' + 1);
    }
}