/**
 * Created by mhwong on 8/5/15.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length < 3  || args.length > 4 || !(args[2].equals("gini") || args[2].equals("ig"))) {
            System.out.println("Usage: java " + Main.class.getCanonicalName() + " input_file target " + "gini|ig [output_file]");
        } else if(args.length == 3){
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
            System.out.println("Used Memory: " + memory / (1024L) + "KB");
            System.out.println();
            System.out.println();
        }
        else {
            Runtime runtime = Runtime.getRuntime();
            long startTime = System.currentTimeMillis();
            System.out.println("File: " + args[0]);
            System.out.println("target: " + args[1]);
            System.out.println("measurement: " + args[2]);
            System.out.println("output: " + args[3]);
            new DecisionTree(args[0], args[1], args[2], args[3]);
            long endTime = System.currentTimeMillis();
            System.out.println("Execution Time: " + (endTime - startTime) / 1000 + "s");
            System.out.println("Execution Time: " + (endTime - startTime) + "ms");
            runtime.gc();
            long memory = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Used Memory: " + memory + "b");
            System.out.println("Used Memory: " + memory / (1024L) + "KB");
            System.out.println();
            System.out.println();
        }


//        new DecisionTree();
    }

}
