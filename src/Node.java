import java.util.ArrayList;

/**
 * Created by mhwong on 8/5/15.
 */
public class Node {
    ArrayList<Node> child;          // the path
    String attribute;               // the name of splitting attribute, if child = 0
                                    // it record the name of class

    public Node() {
        this("");
    }

    public Node(String attribute) {
        this.child = new ArrayList<>();
        this.attribute = attribute;
    }
}
