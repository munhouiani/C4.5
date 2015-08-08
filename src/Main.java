/**
 * Created by mhwong on 8/5/15.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length != 3 || !(args[2].equals("gini") || args[2].equals("ig"))) {
            System.out.println("Usage: java " + Main.class.getCanonicalName() + " input_file target " + "gini|ig");
        } else {
            Runtime runtime = Runtime.getRuntime();
            long startTime = System.currentTimeMillis();
            System.out.println("File: " + args[0]);
            System.out.println("target: " + args[1]);
            System.out.println("measurement: " + args[2]);
            new DecisionTree(args[0], args[1], args[2]);
            long endTime = System.currentTimeMillis();
            System.out.println("Execution Time: " + (endTime - startTime) / 1000 + "s");
            System.out.println("Execution Time: " + (endTime - startTime) + "ms");
            runtime.gc();
            long memory = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Used Memory: " + memory + "b");
            System.out.println("Used Memory: " + memory / (1024L) + "MB");
            System.out.println();
            System.out.println();
        }


//        new DecisionTree();
    }

}
