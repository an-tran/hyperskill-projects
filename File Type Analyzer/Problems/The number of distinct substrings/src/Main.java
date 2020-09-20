import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // put your code here
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine().trim();
        final int a = 53;
        final long m = 1_000_000_000 + 9;

        int ls = s.length();
        long[] hprefixs = new long[ls + 1];
        long[] powhash= new long[ls + 1];
        powhash[0] = 1;
        for (int i = 1; i <= ls; i++) {
            powhash[i] = powhash[i - 1] * a % m;
            hprefixs[i] = (hprefixs[i - 1] + charToLong(s.charAt(i - 1)) * powhash[i - 1]) % m;
        }

        int count = 0;
        for (int l = 1; l <= ls; l++) {
            Map<Long, String> hashes = new HashMap<>();
            for (int j = 0 ; j <= ls - l; j++) {
                long h = (m + hprefixs[j + l] - hprefixs[j]) % m; // hji * a^j = h
                h = h * powhash[ls - j - 1] % m;
                hashes.put(h, "");
            }
            count += hashes.size();
        }
        System.out.println(count + 1);
//        System.out.println(hh.size() + 1);
//        System.out.println(hashes);
//        System.out.println(hashes.values().stream().sorted((o1, o2) -> o1.length()  ==  o2.length() ? o1.compareTo(o2) : o1.length() - o2.length()).collect(Collectors.joining(" ")));
//        Map<String, Integer> sh = hashes.values().stream().collect(Collectors.toMap(s1 -> s1, o -> 1, (o, n) -> o + 1));
//        sh.entrySet().stream().filter(e -> e.getValue() > 1).forEach(e -> System.out.println(e.getKey() + " " + e.getValue()));
//        System.out.println(fixSizeHash(s, 1, a, m));
//        System.out.println(fixSizeHash(s, 2, a, m));
//        System.out.println(fixSizeHash(s, 3, a, m));
//        System.out.println(fixSizeHash(s, 4, a, m));
//        System.out.println(fixSizeHash(s, ls, a, m));
    }

    private static Map<Long, Integer> fixSizeHash(String s, int size, int a, long m) {
        Map<Long, Integer> ret = new HashMap<>();
        long hash = polyHash(s.substring(s.length() - size), a, m);
        ret.put(hash, s.length() - size);

        long pow = 1;
        for (int i = 1; i < size; i++) {
            pow = pow * a % m;
        }

        for (int i = s.length() - size - 1; i >= 0 ; i--) {
            int j = i + size ;
            hash = (hash - (charToLong(s.charAt(j)) * pow % m) + m) * a % m;
            hash = (hash + charToLong(s.charAt(i))) % m;
            ret.put(hash, i);
        }

        return ret;
    }

    private static long polyHash(String s, int a, long m) {
        long hash = 0;
        long pow = 1;
        for (int i = 0; i < s.length(); i++) {
            hash += pow * charToLong(s.charAt(i));
            hash %= m;
            pow = pow * a % m;
        }
        return hash;
    }

    /* 1 */
    public static long charToLong(char ch) {
        return (long)(ch - 'A' + 1);
    }
}
