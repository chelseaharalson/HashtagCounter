/**
 *
 * @author chelseametcalf
 */
public class FibonacciNode {
    int hashtagCountKey;
    String hashtag;
    int degree;
    boolean markChildCut;
    FibonacciNode childNode;
    FibonacciNode leftSiblingNode;
    FibonacciNode rightSiblingNode;
    FibonacciNode parentNode;
    
    public FibonacciNode(String ht, int count) {
        this.hashtag = ht;
        this.hashtagCountKey = count;
        leftSiblingNode = this;
        rightSiblingNode = this;
    }
}
