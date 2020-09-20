package analyzer;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Missing arguments");
        }
        final String patternPath = args[1];
        final String baseDirPath  = args[0];

        // * read pattern from DB
        FileType[] fileTypes;
        List<FileType> tmp = new ArrayList<>();
        try (Scanner sc = new Scanner(new BufferedInputStream(new FileInputStream(patternPath)))) {
            while(sc.hasNext()) {
                String[] eles = sc.nextLine().trim().split(";");
                tmp.add(new FileType(Integer.parseInt(eles[0]), eles[1].replaceAll("\"", ""), eles[2].replaceAll("\"", "")));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        // * sort patterns by priority
        fileTypes = tmp.toArray(new FileType[0]);
        mergeSort(fileTypes, 0, fileTypes.length);

        ExecutorService es = Executors.newCachedThreadPool();
        List<Future<String>> futures = new ArrayList<>();
        // * matching multiple files at a time
        File baseDir = new File(baseDirPath);
        if (baseDir.isDirectory()) {
            File[] children = baseDir.listFiles();
            for (int i = 0; i < children.length; i++) {
                final File c = children[i];
                if (c.isDirectory()) {
                    continue;
                }
                Future<String> future = es.submit(() -> {
                    for (int j = 0; j < fileTypes.length; j++) {
                        if (matching(c.getAbsolutePath(), fileTypes[j].pattern, RabinKarp::search)) {
                            return String.format("%s: %s", c.getName(), fileTypes[j].type);
                        }
                    }
                    return String.format("%s: %s", c.getName(), "Unknown file type");
                });
                futures.add(future);
            }
        }

        for (Future<String> f : futures) {
            try {
                System.out.println(f.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        es.shutdown();
    }

    private static boolean matching(String fullName, String pattern, BiFunction<String, String, Boolean> search) {
        try (
                BufferedInputStream is = new BufferedInputStream(new FileInputStream(fullName));
        ) {
            final int BUFF_SIZE = 4096;
            final int p_size = pattern.length();
            byte[] buffer = new byte[BUFF_SIZE];
            int nread = is.read(buffer);
            while (nread > 0) {
                Boolean found = search.apply(new String(buffer), pattern);
                if (found) {
                    return true;
                }
//                buffer[0] = buffer[BUFF_SIZE - 1];
                System.arraycopy(buffer, BUFF_SIZE - p_size, buffer, 0, p_size);
                nread = is.read(buffer, p_size, BUFF_SIZE - p_size);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class RabinKarp {
        public static long a = 53; // polynomial coefficient
        public static long m = 1_000_000_000 + 9; // modular factor
        private static boolean search(String text, String pattern ) {
            int lt = text.length();
            int lp = pattern.length();
            long pattern_hash = 0;
            long cur_hash = 0;
            long power = 1;
            for (int i = 0; i < pattern.length(); i++) {
                pattern_hash = (pattern_hash + charToLong(pattern.charAt(i)) * power % m + m) % m;
                cur_hash = (cur_hash + charToLong(text.charAt(lt - lp + i)) * power) % m;
                if (i != lp -1) {
                    power = power * a % m;
                }
            }

            for (int i = lt - lp - 1; i >= 0; i--) {
                cur_hash = (cur_hash - charToLong(text.charAt(i + lp)) * power % m + m) * a % m;
                cur_hash = (cur_hash + charToLong(text.charAt(i))) % m;
                if (cur_hash == pattern_hash) {
                    return true;
                }
            }
            return false;
        }

        private static long charToLong(char charAt) {
            return charAt - 'A' + 1;
        }
    }

    public static class KMP {
        private static int[] prefix(String pattern) {
            int[] prefixs = new int[pattern.length()];
            for (int i = 1; i < pattern.length(); i++) {
                int pflen = prefixs[i - 1];
                while (true) {
                    if (pflen > 0 && pattern.charAt(pflen) != pattern.charAt(i)) {
                        pflen = prefixs[pflen - 1]; // get prefix length of this prefix
                    } else {
                        break;
                    }
                }

                if (pattern.charAt(pflen) == pattern.charAt(i)) {
                    prefixs[i] = pflen + 1;
                }
            }
            return prefixs;
        }

        private static boolean search(String text, String pattern) {
            if (pattern.isEmpty()) return false;

            int[] prefixs = prefix(pattern);
            int pIdx = prefixs[0];
            for (int i = 0; i < text.length(); i++) {
                while (true) {
                    if (pIdx > 0 && pattern.charAt(pIdx) != text.charAt(i)) {
                        pIdx = prefixs[pIdx - 1];
                    } else {
                        break;
                    }
                }

                if (pattern.charAt(pIdx) == text.charAt(i)) {
                    pIdx++;
                }

                if (pIdx == pattern.length()) {
                    return true;
                }
            }
            return false;
        }
    }

    private static boolean find(byte[] buffer, String pattern) {
        char[] bpat = pattern.toCharArray();
        int start = 0, end = buffer.length - 1;
        int index = 0;
        while (index <= (buffer.length - bpat.length)) {

            boolean found = true;
            for (int i = 0 ; i < bpat.length;  i++) {
               if (buffer[index + i] != bpat[i])  {
                   index++;
                   found = false;
               }
            }
            if (found == true) {
                return true;
            }
        }
        return false;
    }

    public static <T extends Comparable> void mergeSort(T[] array, int leftIncl, int rightExcl) {
        // the base case: if subarray contains <= 1 items, stop dividing because it's sorted
        if (rightExcl <= leftIncl + 1) {
            return;
        }

        /* divide: calculate the index of the middle element */
        int middle = leftIncl + (rightExcl - leftIncl) / 2;

        mergeSort(array, leftIncl, middle);  // conquer: sort the left subarray
        mergeSort(array, middle, rightExcl); // conquer: sort the right subarray

        /* combine: merge both sorted subarrays into sorted one */
        merge(array, leftIncl, middle, rightExcl);
    }

    private static <T extends Comparable> void merge(T[] array, int left, int middle, int right) {
        int i = left;   // index for the left subarray
        int j = middle; // index for the right subarray
        int k = 0;      // index for the temp subarray

        Object[] temp = new Object[right - left]; // temporary array for merging

    /* get the next lesser element from one of two subarrays
       and then insert it in the array until one of the subarrays is empty */
        while (i < middle && j < right) {
            if (array[i].compareTo(array[j]) <= 0) {
                temp[k] = array[i];
                i++;
            } else {
                temp[k] = array[j];
                j++;
            }
            k++;
        }

        /* insert all the remaining elements of the left subarray in the array */
        for (;i < middle; i++, k++) {
            temp[k] = array[i];
        }

        /* insert all the remaining elements of the right subarray in the array */
        for (;j < right; j++, k++) {
            temp[k] = array[j];
        }

        /* effective copying elements from temp to array */
        System.arraycopy(temp, 0, array, left, temp.length);
    }
    private static class FileType implements Comparable<FileType> {
        int priority;
        String pattern;
        String type;

        public FileType(int priority, String pattern, String type) {
            this.priority = priority;
            this.pattern = pattern;
            this.type = type;
        }

        @Override
        public int compareTo(FileType o) {
            return -Integer.compare(this.priority, o.priority);
        }
    }
}
