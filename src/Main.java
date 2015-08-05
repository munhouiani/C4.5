/**
 * Created by mhwong on 8/5/15.
 */
public class Main {
    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        long startTime = System.currentTimeMillis();
        System.out.println("File: " + "/home/mhwong/Desktop/c4.5_dataset/CUSTOMER.TXT");
        new DecisionTree();
        long endTime = System.currentTimeMillis();
        System.out.println("Execution Time: " + (endTime - startTime)/1000 + "s");
        System.out.println("Execution Time: " + (endTime - startTime) + "ms");
        runtime.gc();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used Memory: " + memory + "b");
        System.out.println("Used Memory: " + memory/(1024L) + "KB");
        System.out.println();
        System.out.println();
    }
}
