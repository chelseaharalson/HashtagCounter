import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 *
 * @author chelseametcalf
 */
public class FibonacciHeap {
    private FibonacciNode maxNode;
    private int numOfNodes;
    private int maxDegree;
    Hashtable<String, FibonacciNode> fibTable = new Hashtable<String, FibonacciNode>();
    
    public FibonacciHeap() {
        maxNode = null;
        maxDegree = 0;
    }
    
    public FibonacciNode getMaxNode() {
        return maxNode;
    }
    
    public int getHeapSize() {
        return numOfNodes;
    }
    
    public boolean isHeapEmpty() {
        if (maxNode == null) {
            return true;
        }
        return false;
    }
    
    // Insert works by creating a new heap with one element and then gets added to the root list
    public void insertNode(String hashtag, int count) {
        System.out.println("Inserting node: " + hashtag + "," + count);
        FibonacciNode currentNode = new FibonacciNode(hashtag, count);
        currentNode.hashtagCountKey = count;
        // Store node info in hashtable
        //System.out.println("Putting in hashtable: " + hashtag + "," + currentNode);
        fibTable.put(hashtag, currentNode);

        // Finding max node
        if (maxNode != null) {
            currentNode.leftSiblingNode = maxNode;
            currentNode.rightSiblingNode = maxNode.rightSiblingNode;
            maxNode.rightSiblingNode = currentNode;
            currentNode.rightSiblingNode.leftSiblingNode = currentNode;
            // The max pointer is updated if necessary
            if (count > maxNode.hashtagCountKey) {
                maxNode = currentNode;
            }
        }
        else {
            maxNode = currentNode;     // This is for the first tree
        }
        numOfNodes++;
        if (maxDegree < currentNode.degree) {
            maxDegree = currentNode.degree;
        }
        //System.out.println("MAX NODE FROM INSERT: " + maxNode.hashtagCountKey);
    }
    
    /*public void removeNode(FibonacciNode currentNode) {
        
    }*/
    
    // If the heap order is not violated, just increase the key of the node
    // Otherwise, cut the tree rooted at the currentNode and meld into root list
    public void increaseKey(FibonacciNode currentNode, int newVal) {
        if (newVal < currentNode.hashtagCountKey) {
            System.out.println("ERROR: The increase key was less than the original key.");
            return;
        }
        currentNode.hashtagCountKey = newVal;
        FibonacciNode parentNodeOfCurrentNode = currentNode.parentNode;
        if ( (parentNodeOfCurrentNode != null) && (currentNode.hashtagCountKey > parentNodeOfCurrentNode.hashtagCountKey) ) {
            cut(currentNode, parentNodeOfCurrentNode);
            cascadingCut(parentNodeOfCurrentNode);
        }
        // Update the hashtable references
        System.out.println("Update hashtable");
        fibTable.replace(currentNode.hashtag, currentNode);
        // Update max node if necessary
        if (currentNode.hashtagCountKey > maxNode.hashtagCountKey) {
            maxNode = currentNode;
        }
    }
    
    public FibonacciNode removeMax() {
        FibonacciNode currentNode = maxNode;
        //System.out.println("CURRENT NODE: " + currentNode.hashtagCountKey);
        if (currentNode != null) {
            int numOfChildrenNodes = currentNode.degree;
            FibonacciNode childNode = currentNode.childNode;
            FibonacciNode tempChildNode;
            while (numOfChildrenNodes > 0) {
                tempChildNode = childNode.rightSiblingNode;
                // Remove child node
                childNode.leftSiblingNode.rightSiblingNode = childNode.rightSiblingNode;
                childNode.rightSiblingNode.leftSiblingNode = childNode.leftSiblingNode;
                // Add child node to root list
                childNode.leftSiblingNode = maxNode;
                childNode.rightSiblingNode = maxNode.rightSiblingNode;
                maxNode.rightSiblingNode = childNode;
                maxNode.rightSiblingNode.leftSiblingNode = childNode;
                // Set parent pointer to null
                childNode.parentNode = null;
                childNode = tempChildNode;
                numOfChildrenNodes--;
            }
            // Remove max and do pairwise combine
            currentNode.leftSiblingNode.rightSiblingNode = currentNode.rightSiblingNode;
            currentNode.rightSiblingNode.leftSiblingNode = currentNode.leftSiblingNode;
            if (currentNode == currentNode.rightSiblingNode) {      // Single node
                maxNode = null;
            }
            else {
                maxNode = currentNode.rightSiblingNode;
                //System.out.println("maxNode: " + maxNode.hashtagCountKey);
                pairwiseCombine();
            }
            numOfNodes--;
        }
        System.out.println("Removed: " + currentNode.hashtagCountKey + ", " + currentNode.hashtag);
        return currentNode;
    }
    
    public void cascadingCut(FibonacciNode tempParentNode) {
        FibonacciNode tempNode = tempParentNode.parentNode;
        if (tempNode != null) {
            if (tempParentNode.markChildCut == false) {     // First time child is removed
                tempParentNode.markChildCut = true;
            }
            else {
                // If the node is marked, that means the parent has lost a child since it was made the child of its current parent
                // Need to cut from current parent
                // Cut it out and perform cascading cut again
                cut(tempParentNode, tempNode);
                cascadingCut(tempNode);
            }
        }
    }
    
    public void cut(FibonacciNode currentNode, FibonacciNode tempParentNode) {
        // Remove the cut child from the sibling list
        currentNode.leftSiblingNode.rightSiblingNode = currentNode.rightSiblingNode;
        currentNode.rightSiblingNode.leftSiblingNode = currentNode.leftSiblingNode;
        tempParentNode.degree--;
        if (tempParentNode.childNode == currentNode) {
            tempParentNode.childNode = currentNode.rightSiblingNode;
        }
        // In order to cut, degree needs to be at least 0
        if (tempParentNode.degree == 0) {
            tempParentNode.childNode = null;
        }
        // Insert the child that was cut from the root list
        currentNode.leftSiblingNode = maxNode;
        currentNode.rightSiblingNode = maxNode.rightSiblingNode;
        maxNode.rightSiblingNode = currentNode;
        maxNode.rightSiblingNode.leftSiblingNode = currentNode;
        currentNode.parentNode = null;
        currentNode.markChildCut = false;
    }
    
    /*public FibonacciHeap meldHeaps(FibonacciHeap heap1, FibonacciHeap heap2) {
        FibonacciHeap finalHeap = new FibonacciHeap();
        if ( (heap1 != null) && (heap2 != null) ) {
            finalHeap.maxNode = heap1.maxNode;
            if (finalHeap.maxNode != null) {
                if (heap2.maxNode != null) {
                    finalHeap.maxNode.rightSiblingNode.leftSiblingNode = heap2.maxNode.leftSiblingNode;
                    heap2.maxNode.leftSiblingNode.rightSiblingNode = finalHeap.maxNode.rightSiblingNode;
                    finalHeap.maxNode.rightSiblingNode = heap2.maxNode;
                    heap2.maxNode.leftSiblingNode = finalHeap.maxNode;
                    if (heap2.maxNode.hashtagCountKey > heap1.maxNode.hashtagCountKey) {
                        finalHeap.maxNode = heap2.maxNode;
                    }
                }
            }
            else {
                finalHeap.maxNode = heap2.maxNode;
            }
            finalHeap.numOfNodes = heap1.numOfNodes + heap2.numOfNodes;
        }
        return finalHeap;
    }*/
    
    public void pairwiseCombine() {
        //System.out.println("MAX DEGREE: " + maxDegree);
        //int tableSize = maxDegree + 1;
        //System.out.println("Pairwise Table Size: " + tableSize);
        double value = 1.0 / Math.log((1.0 + Math.sqrt(5.0)) / 2.0);     // golden ratio
	int tableSize = ((int) Math.floor(Math.log(numOfNodes) * value)) + 1;
        ArrayList<FibonacciNode> pairwiseTable = new ArrayList<FibonacciNode>(tableSize);
        for (int i = 0; i < tableSize; i++) {
            pairwiseTable.add(null);
        }
        
        // Find total number of root nodes
        int numOfRootNodes = 0;
        FibonacciNode currentNode = maxNode;
        if (currentNode != null) {
            numOfRootNodes++;
            currentNode = currentNode.rightSiblingNode;
            while (currentNode != maxNode) {
                numOfRootNodes++;
                currentNode = currentNode.rightSiblingNode;
            }
        }
        
        // Find trees with the same degree and combine them
        while (numOfRootNodes > 0) {
            int currentDegree = currentNode.degree;
            FibonacciNode nextNode = currentNode.rightSiblingNode;
            while (true) {
                // Get node with same degree
                //System.out.println("CURRENT DEGREE: " + currentDegree);
                FibonacciNode otherNode = pairwiseTable.get(currentDegree);
                if (otherNode == null) {
                    break;
                }
                // Update the max node
                if (currentNode.hashtagCountKey < otherNode.hashtagCountKey) {
                    // Swap values
                    FibonacciNode tempNode = otherNode;
                    otherNode = currentNode;
                    currentNode = tempNode;
                }
                merge(otherNode, currentNode);
                pairwiseTable.set(currentDegree, null);
                currentDegree++;
                // Update max degree
                if (currentNode.degree > maxDegree) {
                    maxDegree = currentNode.degree;
                    //System.out.println("Updating max degree to " + maxDegree);
                }
            }
            pairwiseTable.set(currentDegree, currentNode);
            currentNode = nextNode;
            numOfRootNodes--;
        }
        maxNode = null;
        for (int i = 0; i < tableSize; i++) {
            FibonacciNode node = pairwiseTable.get(i);
            if (node == null) {
                continue;
            }
            if (maxNode != null) {
                node.leftSiblingNode.rightSiblingNode = node.rightSiblingNode;
                node.rightSiblingNode.leftSiblingNode = node.leftSiblingNode;
                // Root list
                node.leftSiblingNode = maxNode;
                node.rightSiblingNode = maxNode.rightSiblingNode;
                maxNode.rightSiblingNode = node;
                node.rightSiblingNode.leftSiblingNode = node;
                if (node.hashtagCountKey > maxNode.hashtagCountKey) {
                    maxNode = node;
                }
            }
            else {
                maxNode = node;
            }
            // Update max degree if necessary
            if (maxDegree < pairwiseTable.get(i).degree) {
                maxDegree = pairwiseTable.get(i).degree;
            }
        }
    }
    
    public void merge(FibonacciNode node1, FibonacciNode node2) {
        node1.leftSiblingNode.rightSiblingNode = node1.rightSiblingNode;
        node1.rightSiblingNode.leftSiblingNode = node1.leftSiblingNode;
        node1.parentNode = node2;
        if (node2.childNode == null) {
            node2.childNode = node1;
            node2.rightSiblingNode = node1;
            node2.leftSiblingNode = node1;
        }
        else {
            node1.leftSiblingNode = node2.childNode;
            node1.rightSiblingNode = node2.childNode.rightSiblingNode;
            node2.childNode.rightSiblingNode = node1;
            node1.rightSiblingNode.leftSiblingNode = node1;
        }
        node2.degree++;
        node1.markChildCut = false;
    }
    
    public void readFile(String fileName) {
        HashtagObj hObj = new HashtagObj();
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String currentLine = "";
            while ((currentLine = bufferedReader.readLine()) != null) {
                boolean duplicateExists = false;
                //System.out.println("Current Line: " + currentLine);
                //parseFileIntoObject(currentLine);
                hObj = parseFileIntoObject(hObj, currentLine);
                //System.out.println("Term: " + hObj.term + "\t Count: " + hObj.count);
                //insertNode(hObj.term, hObj.count);
                
                // Need to insert the first node
                if (fibTable.isEmpty()) {
                    //System.out.println("INSERTING Term: " + hObj.term + "\t Count: " + hObj.count);
                    insertNode(hObj.term, hObj.count);
                }
                else {
                    // Check to see if there is a duplicate hashtag and if the value needs to be updated
                    // Update in tree and in hashtable
                    Map.Entry<String, FibonacciNode> entry;
                    Iterator<Map.Entry<String, FibonacciNode>> it;
                    it = fibTable.entrySet().iterator();
                    while (it.hasNext()) {
                        entry = it.next();
                        //System.out.println("Visiting " + entry.getKey());
                        if (entry.getKey().equals(hObj.term)) {
                            int newVal = entry.getValue().hashtagCountKey + hObj.count;
                            System.out.println("UPDATING " + entry.getKey() + "," + entry.getValue().hashtagCountKey + " to " + newVal);
                            increaseKey(entry.getValue(), newVal);
                            duplicateExists = true;
                        }
                        /*else {
                            insertNode(hObj.term, hObj.count);
                        }*/
                    }
                }
                if (duplicateExists == false) {
                    insertNode(hObj.term, hObj.count);
                }
            }
            bufferedReader.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        /*System.out.println("MAX NODE: " + maxNode.hashtag);
        System.out.println("Left Sibling: " + maxNode.leftSiblingNode.hashtag);
        System.out.println("Right Sibling: " + maxNode.rightSiblingNode.hashtag);*/
    }
    
    public void printHashtable() {
        Map.Entry<String, FibonacciNode> entry;
        Iterator<Map.Entry<String, FibonacciNode>> it;
        it = fibTable.entrySet().iterator();
        while (it.hasNext()) {
            entry = it.next();
            System.out.println(entry.getKey() + "\t" + entry.getValue().hashtagCountKey + "\t" + entry.getValue());
        }
    }
    
    public HashtagObj parseFileIntoObject(HashtagObj hObj, String currentLine) {
        //HashtagObj hObj = new HashtagObj();
        if (currentLine.contains("#")) {
            String arr[] = currentLine.split(" ");
            hObj.term = arr[0];
            hObj.count = Integer.parseInt(arr[1]);
            //System.out.println("Term: " + hObj.term + "\t Count: " + hObj.count);
        }
        return hObj;
    }
}
