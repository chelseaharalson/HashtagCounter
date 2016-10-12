

/**
 *
 * @author chelseametcalf
 */
public class HashtagCounter {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java HashtagCounter <input.txt>");
            System.exit(1);
        }
        String inputFile = args[0];
        FibonacciHeap fibHeap = new FibonacciHeap();
        fibHeap.readFile(inputFile);
        fibHeap.removeMax();
        //fibHeap.insertNode("#saturday", 10);
    }
    
}