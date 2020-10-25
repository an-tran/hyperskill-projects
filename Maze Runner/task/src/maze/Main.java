package maze;

import java.io.*;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    static int startRow;
    static int destRow;
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[][] maze = null;
        int size = 0;
        while (true) {
            System.out.println("=== Menu ===");
            if (maze == null) {
                System.out.println(
                        "1. Generate a new maze\n" +
                        "2. Load a maze\n" +
                        "0. Exit");
            } else {
                System.out.println(
                        "1. Generate a new maze\n" +
                        "2. Load a maze\n" +
                        "3. Save the maze\n" +
                        "4. Display the maze\n" +
                        "5. Find the escape\n" +
                        "0. Exit");
            }
//            System.out.print(">");
            int choice = sc.nextInt();
            sc.nextLine();
            if (maze ==null && choice > 2) {
                choice = -1;
            }
            switch (choice) {
                case 0:
                    System.out.println("Bye!");
                    return;
                case 1: //gen maze
                    System.out.println("Enter the size of a new maze");
//                    System.out.print(">");
                    size = sc.nextInt();
                    sc.nextLine();
                    maze = generateMaze(size, size);
                    printMaze(size, size, maze);
                    break;
                case 2: //load maze
                    System.out.println("File name:");
//                    System.out.print(">");
                    String fname = sc.nextLine();
                    File f = new File(fname);
                    if (!f.exists()) {
                        System.out.println("The file ... does not exist");
                        break;
                    }
                    try {
                        BufferedReader fr = new BufferedReader(new FileReader(fname));
                        size = Integer.parseInt(fr.readLine());
                        String[] startDest = fr.readLine().split("\\s");
                        startRow = Integer.parseInt(startDest[0]);
                        destRow = Integer.parseInt(startDest[1]);
                        maze = new int[size][size];
                        for (int i = 0; i < size; i++) {
                            int[] tmp = Arrays.stream(fr.readLine().split("\\s")).mapToInt(Integer::parseInt).toArray();
                            System.arraycopy(tmp, 0, maze[i], 0, size);
                        }
                        fr.close();
                    } catch (Throwable e) {
                        System.out.println("Cannot load the maze. It has an invalid format");
                    }
                    break;
                case 3: //save maze
//                    System.out.print(">");
                    String filename = sc.nextLine();
                    try {
                        PrintWriter fw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename))));
                        fw.println(size);
                        fw.println(startRow + " " + destRow);
                        for (int i = 0; i < size; i++) {
                            for (int j = 0; j < size; j++) {
                                fw.print(maze[i][j]);
                                fw.print(" ");
                            }
                            fw.println();
                        }
                        fw.close();
                        System.out.println("Successfully saving file to " + filename);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    printMaze(size, size, maze);
                    break;
                case 5: // escape
                    findEscape(maze);
                    break;
                default:
                    System.out.println("Incorrect option. Please try again");
                    break;
            }

        }
    }

    public static class Pair {
        int r;
        int c;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return r == pair.r &&
                    c == pair.c;
        }

        @Override
        public int hashCode() {
            return Objects.hash(r, c);
        }

        public Pair(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    private static void findEscape(int[][] maze) {
        Deque<Pair> frontier = new ArrayDeque<>();
        Map<Pair, Pair> path = new HashMap<>();
        int nrow = maze.length;
        int ncol = maze[0].length;

        Pair dest = new Pair(destRow, ncol -1);
        Pair start = new Pair(startRow, 0);
        frontier.offerLast(start);
        while (!frontier.isEmpty()) {
            Pair curr = frontier.pollLast();
            List<Pair> neighbors = findNeighbors(curr, nrow, ncol);
            for (Pair e : neighbors) {
                if (maze[e.r][e.c] == 0) { // wall at this location
                   continue;
                }
                if (path.containsKey(e)) { // already added to frontier
                    continue;
                }
                if (e.equals(dest)) {
                    path.put(e, curr);
                    break;
                }
                // add to frontier
                frontier.offerLast(e);
                path.put(e, curr);
            }
        }

        Pair p = dest;
        while (!p.equals(start)) {
            maze[p.r][p.c] = 2;
            p = path.get(p);
        }
        maze[p.r][p.c] = 2;

        printMaze(nrow, ncol, maze);
    }

    private static List<Pair> findNeighbors(Pair curr, int nrow, int ncol) {
        List<Pair> ret = new ArrayList<>();
        if (curr.r - 1 >= 0) { // top
            ret.add(new Pair(curr.r - 1, curr.c));
        }
        if (curr.r + 1 < nrow) { // bottom
            ret.add(new Pair(curr.r + 1, curr.c));
        }
        if (curr.c - 1 >= 0) {
            ret.add(new Pair(curr.r, curr.c - 1));
        }
        if (curr.c + 1 < ncol) {
            ret.add(new Pair(curr.r, curr.c + 1));
        }
        return ret;
    }

    private static void printMaze(int row, int col, int[][] maze) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (maze[i][j] == 0) {
                    System.out.print("\u2588\u2588");
                } else if (maze[i][j] == 2) {
                    System.out.print("//");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
    }

    private static int[][] generateMaze(int row, int col) {
        int nVertextHor = (int) Math.ceil((row - 2)/2.0);
        int nVertextVer = (int) Math.ceil((col - 2)/2.0);
        int totalVertext = nVertextHor * nVertextVer;
        ArrayList<EdgeNode>[] graph = new ArrayList[totalVertext];
        for (int i = 0; i < totalVertext; i++) {
            graph[i] = new ArrayList<>();
        }
        Random rand = new Random();

        int[][] gg = new int[nVertextHor * 2][nVertextVer * 2];
        // 1. create graph
        for (int i = 0; i < nVertextHor; i++) {
            for (int j = 0; j < nVertextVer; j++) {
                int curV = i * nVertextVer + j;
                int leftV = curV + 1;
                int downV = curV + nVertextVer;
                EdgeNode right = null;
                EdgeNode down = null;
                if (j < nVertextVer - 1) { // right
                    right = new EdgeNode(leftV, rand.nextInt(10) + 1);
                    graph[curV].add(right);
                    graph[leftV].add(new EdgeNode(curV, right.weight));
                    gg[i*2][j*2 + 1] = right.weight;
                }
                if (i < nVertextHor - 1) {
                    down = new EdgeNode(downV, rand.nextInt(10) + 1);
                    graph[curV].add(down);
                    graph[downV].add(new EdgeNode(curV, down.weight));
                    gg[i*2 + 1][j*2] = down.weight;
                }
                gg[i * 2][j * 2] = -1;
            }
        }

//        for (int i = 0; i < gg.length; i++) {
//            for (int j = 0; j < gg[i].length; j++) {
//                if (gg[i][j] == -1) {
//                    System.out.print("*  ");
//                } else if (gg[i][j] == 0) {
//                    System.out.print("   ");
//                } else {
//                    System.out.printf("%02d ", gg[i][j]);
//                }
//            }
//            System.out.println();
//        }

        int[][] maze = new int[row][col];
        startRow = rand.nextInt(row - 3) + 1;
        maze[startRow][0] = 1;
        maze[startRow][1] = 1;
        destRow = rand.nextInt(row - 3) + 1;
        maze[destRow][col - 1] = 1;
        maze[destRow][col - 2] = 1;
        // 2. Prim algorithm
        int[] spantree = prim(graph, 0);
//        System.out.println(Arrays.toString(spantree));
//        IntStream.range(0, nVertextHor)
//                .forEach(r -> {
//                    System.out.println(Arrays.toString(Arrays.copyOfRange(spantree, r * nVertextVer, r * nVertextVer + nVertextVer)));
//                });

        for (int i = 0; i < nVertextHor; i++) {
            int r = i * 2 + 1;
            for (int j = 0; j < nVertextVer; j++) {
               int c = j * 2 + 1;
               maze[r][c] = 1;
               int curNode = i * nVertextVer + j;
               int right = curNode + 1;
               int left = curNode - 1;
               int down = curNode + nVertextVer;
               int up = curNode - nVertextVer;
               if (up == spantree[curNode]) {
                   maze[r - 1][c] = 1;
               } else if (down == spantree[curNode]) {
                   maze[r + 1][c] = 1;
               } else if (left == spantree[curNode]) {
                   maze[r][c -1] = 1;
               } else if (right == spantree[curNode]) {
                   maze[r][c + 1] =1;
               }
            }
        }
        return maze;
    }

    public static int[] prim(ArrayList<EdgeNode>[] graph, int start) {
        int[] parent = new int[graph.length];
        boolean[] intree = new boolean[graph.length];
        int[] bestweight = new int[graph.length];

        for (int i = 0; i < intree.length; i++) {
            intree[i] = false;
            parent[i] = -10;
        }

        for (int i = 0; i < bestweight.length; i++) {
            bestweight[i] = Integer.MAX_VALUE;
        }

        int v = start;
        while (intree[v] == false) {
            intree[v] = true;
            ArrayList<EdgeNode> vv = graph[v];
            // update bestweight
            for (EdgeNode n : vv) {
                if (intree[n.node] == false && bestweight[n.node] > n.weight) {
                    bestweight[n.node] = n.weight;
                    parent[n.node] = v;
                }
            }

            // select next v
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < bestweight.length; i++) {
                if (intree[i] == false & best > bestweight[i]){
                    best = bestweight[i];
                    v = i;
                }
            }
        }
        return parent;
    }

    public static class EdgeNode {
        int node;
        int weight;
        EdgeNode next;

        public EdgeNode(int node, int weight) {
            this.node = node;
            this.weight = weight;
        }
    }

}
