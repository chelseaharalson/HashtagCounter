import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    ArrayList<FibonacciNode> removedNodes = new ArrayList<FibonacciNode>();
    static String allHashtags = "";
    static boolean first = false;
    
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
    
    public void removeMax() {
        // If the max does exist, then remove it's child list
        // Then set each child's parent to null and meld the child list with the root list
        // Determine new max
        if (maxNode == null) {
            System.out.println("ERROR: Tried to remove a null max");
            return;
        }
        if (maxNode == maxNode.leftSiblingNode && maxNode.childNode == null) {
            maxNode = null;
            return;
        }
        
        // Remove first
        if (first == true) {
            maxNode.leftSiblingNode.rightSiblingNode = maxNode.rightSiblingNode;
            maxNode.rightSiblingNode.leftSiblingNode = maxNode.leftSiblingNode;
            System.out.println("Removed: " + maxNode.hashtagCountKey + ", " + maxNode.hashtag);
            fibTable.remove(maxNode.hashtag, maxNode);
            removedNodes.add(maxNode);
            first = false;
        }
        
        FibonacciNode tempNode = null;
        FibonacciNode child = null;
        FibonacciNode tempLeftNode = maxNode.leftSiblingNode;
        FibonacciNode tempRightNode = maxNode.rightSiblingNode;
        maxDegree = 0;
        
        if (maxNode.childNode == null) {
            if (maxNode == maxNode.leftSiblingNode) {
                maxNode = null;
                return;
            }
            tempLeftNode.rightSiblingNode = tempRightNode;
            tempRightNode.leftSiblingNode = tempLeftNode;
            
            // Determine the new max
            maxNode = tempLeftNode;
            maxDegree = maxNode.degree;
            tempNode = maxNode.leftSiblingNode;
            //System.out.println("MAX NODE: " + maxNode.hashtag + "," + maxNode.hashtagCountKey);
            //System.out.println("TEMP NODE: " + tempNode.hashtag + "," + tempNode.hashtagCountKey);
            while (tempNode != maxNode) {
                if (maxDegree < tempNode.degree) {
                    maxDegree = tempNode.degree;
                }
                if (maxNode.hashtagCountKey < tempNode.hashtagCountKey) {
                    maxNode = tempNode;
                    //System.out.println("NEW MAX: " + maxNode.hashtag + "," + maxNode.hashtagCountKey);
                }
                tempNode = tempNode.leftSiblingNode;
            }
        }
        else {
            // When the child is NOT null
            System.out.println("There was a child to remove");
            maxNode = null;
            child = maxNode.childNode;
            tempNode = child;
            
            // Remove child list (reset all of the max kid's parent pointers to null)
            do {
                tempNode.parentNode = null;
                tempNode = tempNode.leftSiblingNode;
            } while (tempNode != child);
            
            // Meld the children and roots (combine the child list and the root list)
            FibonacciNode tempLeftChild = child.leftSiblingNode;
            if (tempLeftChild.leftSiblingNode != tempLeftChild) {
                System.out.println("ERROR: Should NOT be the case");
            }
            
            // maxNode should NOT be the only element in the root list at this point
            tempLeftNode.rightSiblingNode = child;
            child.leftSiblingNode = tempLeftNode;
            tempRightNode.leftSiblingNode = tempLeftChild;
            tempLeftChild.rightSiblingNode = tempRightNode;
            
            // New max - also determines the new max
            System.out.println("Child: " + child.hashtag);
            pairwiseCombine(child);
        }
        numOfNodes--;
        System.out.println("Removed: " + maxNode.hashtagCountKey + ", " + maxNode.hashtag);
        fibTable.remove(maxNode.hashtag, maxNode);
        removedNodes.add(maxNode);
    }
    
    /*public FibonacciNode removeMax() {
        FibonacciNode currentMaxNode = maxNode;
        //System.out.println("CURRENT NODE: " + currentNode.hashtagCountKey);
        if (currentMaxNode != null) {
            int numOfChildrenNodes = currentMaxNode.degree;
            FibonacciNode childNode = currentMaxNode.childNode;
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
            currentMaxNode.leftSiblingNode.rightSiblingNode = currentMaxNode.rightSiblingNode;
            currentMaxNode.rightSiblingNode.leftSiblingNode = currentMaxNode.leftSiblingNode;
            if (currentMaxNode == currentMaxNode.rightSiblingNode) {      // Single node
                maxNode = null;
            }
            else {
                maxNode = currentMaxNode.rightSiblingNode;
                //System.out.println("@@@maxNode: " + currentNode.hashtagCountKey);
                pairwiseCombine();
            }
            numOfNodes--;
        }
        System.out.println("Removed: " + currentMaxNode.hashtagCountKey + ", " + currentMaxNode.hashtag);
        fibTable.remove(currentMaxNode.hashtag, currentMaxNode);
        removedNodes.add(currentMaxNode);
        return currentMaxNode;
    }*/
    
    public void updateMaxNode(FibonacciNode maxNode) {
        Map.Entry<String, FibonacciNode> entry;
        Iterator<Map.Entry<String, FibonacciNode>> it;
        it = fibTable.entrySet().iterator();
        while (it.hasNext()) {
            entry = it.next();
            System.out.println("Visiting " + entry.getKey());
            if (entry.getValue().hashtagCountKey > maxNode.hashtagCountKey) {
                maxNode = entry.getValue();
                System.out.println("Updating max node to: " + maxNode.hashtag + "," + maxNode.hashtagCountKey);
            }
        }
    }
    
    /*public void pairwiseCombine() {
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
            //System.out.println("CUR NODE: " + currentNode.hashtag + "," + currentNode.hashtagCountKey);
            currentNode = currentNode.rightSiblingNode;
            //System.out.println("MAX NODE: " + maxNode.hashtag + "," + maxNode.hashtagCountKey);
            ArrayList<FibonacciNode> visitedCurrentNodes = new ArrayList<FibonacciNode>();
            while (currentNode != maxNode) {
                /*numOfRootNodes++;
                currentNode = currentNode.rightSiblingNode;*/
                /*if (visitedCurrentNodes.contains(currentNode)) {
                    return;
                }
                if (!visitedCurrentNodes.contains(currentNode)) {
                    numOfRootNodes++;
                    visitedCurrentNodes.add(currentNode);
                }
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
                //System.out.println("Other Node: " + otherNode.hashtag + "\t Current Node: " + currentNode.hashtag);
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
            //System.out.println("@@@CUR NODE: " + currentNode.hashtag);
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
            /*if (maxDegree < pairwiseTable.get(i).degree) {
                maxDegree = pairwiseTable.get(i).degree;
            }*/
        //}
    //}
    
    public void pairwiseCombine(FibonacciNode startNode) {
        double value = 1.0 / Math.log((1.0 + Math.sqrt(5.0)) / 2.0);     // golden ratio
	int tableSize = ((int) Math.floor(Math.log(numOfNodes) * value)) + 1;
        ArrayList<FibonacciNode> pairwiseTable = new ArrayList<FibonacciNode>(tableSize);
        FibonacciNode currentTableElement;
        FibonacciNode tempNode;
        FibonacciNode currentNode = startNode;
        
        while (currentNode.leftSiblingNode != currentNode) {
            // If there is no element in the pairwise table with the current subtree's degree then insert into table
            if (pairwiseTable.get(currentNode.degree) == null) {
                pairwiseTable.set(currentNode.degree, currentNode);
                // Continue to the root list
                currentNode = currentNode.leftSiblingNode;
            }
            // Else, if there is a subtree in the table with the same degree as the current subtree, then combine
            else {
                currentTableElement = pairwiseTable.get(currentNode.degree);
                // The one with the smaller key becomes the child of the other
                if (currentTableElement.hashtagCountKey > currentNode.hashtagCountKey) {
                    currentNode.parentNode = currentTableElement;
                    //currentNode.markChildCut = false;
                    
                    // Remove currentNode from the root list
                    currentNode.leftSiblingNode.rightSiblingNode = currentNode.rightSiblingNode;
                    currentNode.rightSiblingNode.leftSiblingNode = currentNode.leftSiblingNode;
                    
                    // Add currentNode to the child list of the currentTableElement
                    if (currentTableElement.childNode != null) {
                        currentNode.leftSiblingNode = currentTableElement.childNode;
                        tempNode = currentTableElement.childNode.rightSiblingNode;
                        currentTableElement.childNode.rightSiblingNode = currentNode;
                        currentNode.rightSiblingNode = tempNode;
                        tempNode.leftSiblingNode = currentNode;
                    }
                    else {
                        currentTableElement.childNode = currentNode;
                        currentNode.leftSiblingNode = currentNode;
                        currentNode.rightSiblingNode = currentNode;
                    }
                    
                    // Increase degree of the table element
                    currentTableElement.degree++;
                    
                    // Update the pairwise table
                    pairwiseTable.set(currentNode.degree, null);
                    pairwiseTable.set(currentTableElement.degree, currentTableElement);
                    
                    // Continue to the root list
                    currentNode = currentTableElement.leftSiblingNode;
                }
                else {      // if currentNode.hashtagCountKey > currentTableElement.hashtagCountKey
                    currentTableElement.parentNode = currentNode;
                    //currentTableElement.markChildCut = false;
                    
                    // Remove the currentTableElement from the root list
                    currentTableElement.leftSiblingNode.rightSiblingNode = currentTableElement.rightSiblingNode;
                    currentTableElement.rightSiblingNode.leftSiblingNode = currentTableElement.leftSiblingNode;
                    
                    // Add the currentTableElement to the child list of the currentNode
                    currentTableElement.leftSiblingNode = currentNode.childNode;
                    tempNode = currentNode.childNode.rightSiblingNode;
                    currentNode.childNode.rightSiblingNode = currentTableElement;
                    currentTableElement.rightSiblingNode = tempNode;
                    tempNode.leftSiblingNode = currentTableElement;
                    
                    // Increase degree of the table element
                    currentNode.degree++;
                    
                    // Update the pairwise table
                    pairwiseTable.set(currentNode.degree-1, null);
                    pairwiseTable.set(currentNode.degree, currentNode);
                    
                    // Continue to the root list
                    currentNode = currentTableElement.leftSiblingNode;
                }
            }
        }
        
        // Determine the new max
        maxNode = null;
        for (int i = 0; i < pairwiseTable.size(); i++) {
            if (pairwiseTable.get(i) != null) {
                if (maxNode == null || maxNode.hashtagCountKey < pairwiseTable.get(i).hashtagCountKey) {
                    maxNode = pairwiseTable.get(i);
                }
                if (maxDegree < pairwiseTable.get(i).degree) {
                    maxDegree = pairwiseTable.get(i).degree;
                }
            }
        }
    }
    
    public void printPairwiseTable() {
        System.out.println("Printing pairwise table...");
        Map.Entry<String, FibonacciNode> entry;
        Iterator<Map.Entry<String, FibonacciNode>> it;
        it = fibTable.entrySet().iterator();
        while (it.hasNext()) {
            entry = it.next();
            System.out.println(entry.getKey() + "," + entry.getValue().hashtagCountKey + "\t\t\t" + entry.getValue().degree);
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
                //hObj = parseFileIntoObject(hObj, currentLine);
                //System.out.println("Term: " + hObj.term + "\t Count: " + hObj.count);
                //insertNode(hObj.term, hObj.count);
                if (currentLine.startsWith("#")) {
                    hObj = parseFileIntoObject(hObj, currentLine);
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
                        }
                        if (duplicateExists == false) {
                            insertNode(hObj.term, hObj.count);
                        }
                    }
                }
                else if ( !currentLine.trim().equalsIgnoreCase("stop") ) {
                    System.out.println(currentLine);
                    printHashtable();
                    int queryNum = Integer.parseInt(currentLine);
                    System.out.println("QUERY NUM: " + queryNum);
                    // Getting queries
                    for (int i = 0; i < queryNum-1; i++) {
                        if (i == 0) {
                            first = true;
                            removeMax();
                        }
                        else {
                            removeMax();
                        }
                    }
                    // Insert nodes
                    for (int i = 0; i < removedNodes.size(); i++) {
                        String term = removedNodes.get(i).hashtag.substring(1,removedNodes.get(i).hashtag.length());
                        // For hashtag string
                        if (i != removedNodes.size()-1) {
                            allHashtags = allHashtags + term + ",";
                        }
                        else if (i == removedNodes.size()-1) {
                            allHashtags = allHashtags + term + "\n";
                        }
                        //System.out.println(allHashtags);
                        
                        // Insert nodes back into heap
                        insertNode(removedNodes.get(i).hashtag, removedNodes.get(i).hashtagCountKey);
                    }
                    removedNodes.clear();
                    //System.out.println();
                    //System.out.println(allHashtags);
                }
                else if ( currentLine.trim().equalsIgnoreCase("stop") ) {
                    System.out.println();
                    System.out.println(allHashtags);
                    writeToOutputFile(allHashtags.trim());
                    return;
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
    
    public void writeToOutputFile(String hashtagContent) throws IOException {
        System.out.println(hashtagContent);
        
        //String folder = "/home/chelsea/Dropbox/Documents/Research/GenerateLockTypes/" + projectName + "/";
        //String folder = "/Users/chelseametcalf/Dropbox/Documents/Research/AliasedLockOrder/" + projectName + "/";
        //FileWriter writer = new FileWriter(folder + "scope2.txt");
        FileWriter writer = new FileWriter("output_file.txt");
        writer.append(hashtagContent);
        writer.close();
    }
    
    public void printHashtable() {
        System.out.println("Printing hashtable...");
        Map.Entry<String, FibonacciNode> entry;
        Iterator<Map.Entry<String, FibonacciNode>> it;
        it = fibTable.entrySet().iterator();
        while (it.hasNext()) {
            entry = it.next();
            System.out.println(entry.getKey() + "," + entry.getValue().hashtagCountKey + "\t" + entry.getValue());
        }
    }
    
    public HashtagObj parseFileIntoObject(HashtagObj hObj, String currentLine) {
        if (currentLine.contains("#")) {
            String arr[] = currentLine.split(" ");
            hObj.term = arr[0];
            hObj.count = Integer.parseInt(arr[1]);
        }
        return hObj;
    }
}
