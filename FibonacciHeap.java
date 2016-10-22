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
    
    public FibonacciHeap() {
        maxNode = null;
        maxDegree = 0;
    }
    
    // Insert works by creating a new heap with one element and then it gets added to the root list
    public void insertNode(String hashtag, int count) {
        System.out.println("Inserting node: " + hashtag + "," + count);
        FibonacciNode currentNode = new FibonacciNode(hashtag, count);
        currentNode.hashtagCountKey = count;
        // Store node info in hashtable
        //System.out.println("Putting in hashtable: " + hashtag + "," + currentNode);
        System.out.println("Insert in hashtable");
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

    // If the heap order is not violated, increase the key of the node
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
        // Reset tempParentNode.child if necessary
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
        currentNode.rightSiblingNode.leftSiblingNode = currentNode;
        // Set parent of currentNode to null
        currentNode.parentNode = null;
        // Set marked to false
        currentNode.markChildCut = false;
    }
    
    public void removeMax() {
        System.out.println("OPERATION: REMOVING MAX");
        FibonacciNode currentNode = maxNode;
        if (currentNode != null) {
            int numOfChildren = currentNode.degree;
            FibonacciNode childNodeOfCurrentNode = currentNode.childNode;
            FibonacciNode tempRightNode;
            
            // for each child of the currentNode do:
            while (numOfChildren > 0) {
                tempRightNode = childNodeOfCurrentNode.rightSiblingNode;
                // Remove child of currentNode from the child list
                childNodeOfCurrentNode.leftSiblingNode.rightSiblingNode = childNodeOfCurrentNode.rightSiblingNode;
                childNodeOfCurrentNode.rightSiblingNode.leftSiblingNode = childNodeOfCurrentNode.leftSiblingNode;
                // Add child of currentNode to the root list
                childNodeOfCurrentNode.leftSiblingNode = maxNode;
                childNodeOfCurrentNode.rightSiblingNode = maxNode.rightSiblingNode;
                maxNode.rightSiblingNode = childNodeOfCurrentNode;
                childNodeOfCurrentNode.rightSiblingNode.leftSiblingNode = childNodeOfCurrentNode;
                // Set parent of the child of currentNode to null since it is now apart of the root list
                childNodeOfCurrentNode.parentNode = null;
                childNodeOfCurrentNode = tempRightNode;
                numOfChildren--;
            }
            
            // Remove currentNode from the root list and do pairwise combine
            currentNode.leftSiblingNode.rightSiblingNode = currentNode.rightSiblingNode;
            currentNode.rightSiblingNode.leftSiblingNode = currentNode.leftSiblingNode;
            
            System.out.println("Removed: " + maxNode.hashtagCountKey + ", " + maxNode.hashtag);
            fibTable.remove(maxNode.hashtag, maxNode);
            removedNodes.add(maxNode);
            
            if (currentNode == currentNode.rightSiblingNode) {      // a single tree
                maxNode = null;
            }
            else {
                maxNode = currentNode.rightSiblingNode;
                pairwiseCombine();
            }
            numOfNodes--;
        }
    }
    
    public void pairwiseCombine() {
        System.out.println("OPERATION: PAIRWISECOMBINE");
        double value = 1.0 / Math.log((1.0 + Math.sqrt(5.0)) / 2.0);     // golden ratio
	int tableSize = ((int) Math.floor(Math.log(numOfNodes) * value)) + 1;
        ArrayList<FibonacciNode> pairwiseTable = new ArrayList<FibonacciNode>(tableSize);
        for (int i = 0; i < tableSize; i++) {
            pairwiseTable.add(null);
        }
        
        int numOfRootNodes = 0;
        FibonacciNode currentNode = maxNode;
        // Find number of root nodes
        if (currentNode != null) {
            numOfRootNodes++;
            currentNode = currentNode.rightSiblingNode;
            while (currentNode != maxNode) {
                //System.out.println(currentNode.hashtag);
                numOfRootNodes++;
                currentNode = currentNode.rightSiblingNode;
            }
        }
        
        // for each node in the root list do:
        while (numOfRootNodes > 0) {
            // Get current node's degree
            int currentDegree = currentNode.degree;
            FibonacciNode nextNode = currentNode.rightSiblingNode;
            // See if there's another node with the same degree
            while (true) {
                FibonacciNode currentNodeTableElement = pairwiseTable.get(currentDegree);
                if (currentNodeTableElement == null) {
                    // There is no other node with that degree
                    break;
                }
                // There is a node with that degree, so make one of the nodes a child of the other (bigger one is parent node)
                // Updating the max
                if (currentNode.hashtagCountKey < currentNodeTableElement.hashtagCountKey) {
                    FibonacciNode tempNode = currentNodeTableElement;
                    currentNodeTableElement = currentNode;
                    currentNode = tempNode;
                    // currentNodeTableElement is removed from the root list because it is becoming a child node of currentNode
                    //merge(currentNodeTableElement, currentNode);
                }
                merge(currentNodeTableElement, currentNode);
                /*else {
                    // Updating the max
                    FibonacciNode tempNode = currentNode;
                    currentNode = currentNodeTableElement;
                    currentNodeTableElement = tempNode;
                    // currentNode is removed from the root list because it is becoming a child node of currentNodeTableElement
                    merge(currentNode, currentNodeTableElement);
                }*/
                
                // Degree is handled so go to the next one
                pairwiseTable.set(currentDegree, null);
                currentDegree++;
            }
            
            // Save node for later in case encountering another of the same degree
            pairwiseTable.set(currentDegree, currentNode);
            // Move forward in the list
            currentNode = nextNode;
            numOfRootNodes--;
        }
        maxNode = null;
        for (int i = 0 ; i < pairwiseTable.size(); i++) {
            FibonacciNode currentTableNode = pairwiseTable.get(i);
            if (currentTableNode == null) {
                continue;
            }
            if (maxNode != null) {
                // Remove from root list
                currentTableNode.leftSiblingNode.rightSiblingNode = currentTableNode.rightSiblingNode;
                currentTableNode.rightSiblingNode.leftSiblingNode = currentTableNode.leftSiblingNode;
                // Add to root list again
                currentTableNode.leftSiblingNode = maxNode;
                currentTableNode.rightSiblingNode = maxNode.rightSiblingNode;
                maxNode.rightSiblingNode = currentTableNode;
                currentTableNode.rightSiblingNode.leftSiblingNode = currentTableNode;
                
                // Check if there is a new max
                if (currentTableNode.hashtagCountKey > maxNode.hashtagCountKey) {
                    maxNode = currentTableNode;
                }
            }
            else {
                maxNode = currentTableNode;
            }
        }
    }

    public void merge(FibonacciNode currentNodeTableElement, FibonacciNode currentNode) {
        // Remove currentNodeTableElement from root list
        currentNodeTableElement.leftSiblingNode.rightSiblingNode = currentNodeTableElement.rightSiblingNode;
        currentNodeTableElement.rightSiblingNode.leftSiblingNode = currentNodeTableElement.leftSiblingNode;
        
        // Make currentNodeTableElement a child of currentNode
        currentNodeTableElement.parentNode = currentNode;
        
        if (currentNode.childNode == null) {
            currentNode.childNode = currentNodeTableElement;
            currentNodeTableElement.rightSiblingNode = currentNodeTableElement;
            currentNodeTableElement.leftSiblingNode = currentNodeTableElement;
        }
        else {
            currentNodeTableElement.leftSiblingNode = currentNode.childNode;
            currentNodeTableElement.rightSiblingNode = currentNode.childNode.rightSiblingNode;
            currentNode.childNode.rightSiblingNode = currentNodeTableElement;
            currentNodeTableElement.rightSiblingNode.leftSiblingNode = currentNodeTableElement;
        }
        
        // Increase degree
        currentNode.degree++;
        currentNodeTableElement.markChildCut = false;
        
        System.out.println(currentNodeTableElement.hashtag + "," + currentNodeTableElement.hashtagCountKey + "," + currentNodeTableElement.degree
                + " became a child of " + currentNode.hashtag + "," + currentNode.hashtagCountKey + "," + currentNode.degree);
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
                    System.out.println("QUERY NUMBER: " + queryNum);
                    // Getting queries
                    int q = 0;
                    while (q < queryNum) {
                        removeMax();
                        q++;
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
                    //System.out.println(allHashtags);
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
        System.out.println();
        System.out.println("Writing to output_file.txt...");
        System.out.println(hashtagContent);
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
            System.out.println(entry.getKey() + "," + entry.getValue().hashtagCountKey + "\t" + entry.getValue() + ";" + entry.getValue().degree);
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
