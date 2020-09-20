import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
         // put your code here
        Scanner sc = new Scanner(System.in);
        String si = sc.nextLine().trim();
        int k = sc.nextInt();

//        long m = 1_000_000_000_000_000L + 9;
//        long m = 1107419712071l;
//        if (si.length() > 1000000) {
//            System.out.println(si);
//        }
        long start = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
        long m = 67280421310721l;
        long a = 2111;
        int ls = si.length();
        long pow = 1;
        long hash = 0;
        Map<Long, List<Integer>> hh = new HashMap<>();
        for (int i = ls - k; i < ls; i++) {
            hash = (hash + charToLong(si.charAt(i)) * pow) % m;
            if (i < ls - 1) {
                pow = pow * a % m;
            }
        }
        hh.put(hash, new ArrayList<>() {{ add(ls - k); }});

        for (int i = ls - k - 1; i >= 0; i--) {
            hash = (hash - charToLong(si.charAt(i + k)) * pow % m + m ) * a % m;
            hash = (hash + charToLong(si.charAt(i))) % m;
            String sub = si.substring(i, i+k);
            if (hh.containsKey(hash)) {
                for (Integer ii : hh.get(hash)) {
                   if (sub.equals(si.substring(ii, ii + k))) {
                       System.out.println(sub);
                       System.out.println("Elapse " + (TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS) - start));
                       return;
                   }
                }
//                if (hh.get(hash).contains(sub)) {
//                } else {
//                }
                hh.get(hash).add(i);
            } else {
                int finalI = i;
                hh.put(hash, new ArrayList<>() {{
                    add(finalI);
                }});
            }
        }

        System.out.println("does not contain");
    }
    public static long charToLong(char ch) {
        return (long)(ch - 'A' + 1);
    }
}