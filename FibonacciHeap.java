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
    Hashtable<String, FibonacciNode> fibTable = new Hashtable<String, FibonacciNode>();
    ArrayList<FibonacciNode> removedNodes = new ArrayList<FibonacciNode>();     // nodes that were removed from removeMax() and need to be inserted back in
    static String allHashtags = "";
    
    public FibonacciHeap() {
        maxNode = null;
    }
    
    // Insert works by creating a new heap with one element and then it gets added to the root list
    public void insertNode(String hashtag, int count) {
        System.out.println("INSERTING NODE: " + hashtag + "," + count);
        FibonacciNode insertedNode = new FibonacciNode(hashtag, count);
        insertedNode.hashtagCountKey = count;
        // Store node info in hashtable
        //System.out.println("Putting in hashtable: " + hashtag + "," + currentNode);
        System.out.println("***Insert in hashtable");
        fibTable.put(hashtag, insertedNode);

        // Finding max node
        if (maxNode != null) {
            insertedNode.leftSiblingNode = maxNode;
            insertedNode.rightSiblingNode = maxNode.rightSiblingNode;
            maxNode.rightSiblingNode = insertedNode;
            insertedNode.rightSiblingNode.leftSiblingNode = insertedNode;
            // The max pointer is updated if necessary
            if (count > maxNode.hashtagCountKey) {
                maxNode = insertedNode;
                System.out.println("Max node was updated in insertNode() to " + maxNode.hashtag + "," + maxNode.hashtagCountKey);
            }
        }
        else {
            maxNode = insertedNode;     // This is for the first tree (no node has been inserted yet, so no max)
        }
        numOfNodes++;
        //System.out.println("MAX NODE AFTER INSERTION: " + maxNode.hashtagCountKey);
    }

    // If the heap order is not violated, increase/update the key of the node
    // Otherwise, (NEED TO DO CASCADING CUT) cut the tree rooted at the currentNode and merge into root list
    public void increaseKey(FibonacciNode currentNodeToBeUpdated, int newVal) {
        if (newVal < currentNodeToBeUpdated.hashtagCountKey) {
            System.out.println("ERROR: The increase key was less than the original key.");
            return;
        }
        currentNodeToBeUpdated.hashtagCountKey = newVal;
        FibonacciNode parentNodeOfCurrentNodeToBeUpdated = currentNodeToBeUpdated.parentNode;
        // Checking if heap order is violated. If so, perform cascading cut.
        if ( (parentNodeOfCurrentNodeToBeUpdated != null) && (currentNodeToBeUpdated.hashtagCountKey > parentNodeOfCurrentNodeToBeUpdated.hashtagCountKey) ) {
            cut(currentNodeToBeUpdated, parentNodeOfCurrentNodeToBeUpdated);
            cascadingCut(parentNodeOfCurrentNodeToBeUpdated);
        }
        // Update the hashtable references
        System.out.println("***Update hashtable");
        fibTable.replace(currentNodeToBeUpdated.hashtag, currentNodeToBeUpdated);
        // Update max node if necessary
        if (currentNodeToBeUpdated.hashtagCountKey > maxNode.hashtagCountKey) {
            maxNode = currentNodeToBeUpdated;
        }
    }
    
    public void cascadingCut(FibonacciNode currentNode) {
        FibonacciNode parentNodeOfCurrentNode = currentNode.parentNode;
        if (parentNodeOfCurrentNode != null) {
            if (currentNode.markChildCut == false) {     // First time child is removed
                currentNode.markChildCut = true;
            }
            else {
                // If the node is marked, that means the parent has lost a child since it was made the child of its current parent
                // Need to cut from current parent
                // Cut it out and perform cascading cut again
                cut(currentNode, parentNodeOfCurrentNode);
                cascadingCut(parentNodeOfCurrentNode);
            }
        }
    }
    
    public void cut(FibonacciNode cutChildNode, FibonacciNode parentNodeOfCutChildNode) {
        // Remove the cut child from the sibling list
        cutChildNode.leftSiblingNode.rightSiblingNode = cutChildNode.rightSiblingNode;
        cutChildNode.rightSiblingNode.leftSiblingNode = cutChildNode.leftSiblingNode;
        parentNodeOfCutChildNode.degree--;
        // Reset parentNodeOfCutChildNode.childNode if necessary
        if (parentNodeOfCutChildNode.childNode == cutChildNode) {
            parentNodeOfCutChildNode.childNode = cutChildNode.rightSiblingNode;
        }
        // In order to cut, degree needs to be at least 0 / have children
        if (parentNodeOfCutChildNode.degree == 0) {
            parentNodeOfCutChildNode.childNode = null;
        }
        
        // Insert the child that was cut to the root list
        cutChildNode.leftSiblingNode = maxNode;
        cutChildNode.rightSiblingNode = maxNode.rightSiblingNode;
        maxNode.rightSiblingNode = cutChildNode;
        cutChildNode.rightSiblingNode.leftSiblingNode = cutChildNode;
        // Set parent of currentNode to null since it is part of the root list now
        cutChildNode.parentNode = null;
        // Set marked to false
        cutChildNode.markChildCut = false;
    }
    
    public void removeMax() {
        System.out.println("OPERATION: REMOVING MAX");
        //printHashtable();
        FibonacciNode currentNode = maxNode;
        if (currentNode != null) {
            int totalNumOfChildren = currentNode.degree;
            FibonacciNode childNodeOfCurrentNode = currentNode.childNode;
            FibonacciNode tempChildNode;
            
            // For each child of the currentNode do: (need to move up to the root list)
            while (totalNumOfChildren > 0) {
                tempChildNode = childNodeOfCurrentNode.rightSiblingNode;
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
                childNodeOfCurrentNode = tempChildNode;
                totalNumOfChildren--;
            }
            
            // Remove currentNode from the root list and do pairwise combine
            currentNode.leftSiblingNode.rightSiblingNode = currentNode.rightSiblingNode;
            currentNode.rightSiblingNode.leftSiblingNode = currentNode.leftSiblingNode;
            
            // Removing from hashtable
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
        double value = 1.0 / Math.log((1.0 + Math.sqrt(5.0)) / 2.0);     // golden ratio to find pairwise table size
	int tableSize = ((int) Math.floor(Math.log(numOfNodes) * value)) + 1;
        ArrayList<FibonacciNode> pairwiseTable = new ArrayList<FibonacciNode>(tableSize);
        for (int i = 0; i < tableSize; i++) {
            pairwiseTable.add(null);
        }
        
        int numOfRootNodes = 0;
        FibonacciNode currentNode = maxNode;
        // Find number of root nodes for loop condition
        if (currentNode != null) {
            numOfRootNodes++;
            currentNode = currentNode.rightSiblingNode;
            while (currentNode != maxNode) {
                //System.out.println(currentNode.hashtag);
                numOfRootNodes++;
                currentNode = currentNode.rightSiblingNode;
            }
        }
        
        // For each node in the root list do:
        while (numOfRootNodes > 0) {
            // Get current node's degree
            int currentDegree = currentNode.degree;
            // Need to assign here because currentNode changes and need this to increment the loop
            FibonacciNode nextNode = currentNode.rightSiblingNode;
            // Check to see if there is another node with the same degree
            while (true) {
                FibonacciNode currentNodePTableElement = pairwiseTable.get(currentDegree);
                if (currentNodePTableElement == null) {
                    // There is no other node with that degree
                    break;
                }
                // There is a node with that degree, so make one of the nodes a child of the other (bigger one is parent node)
                // Updating the max
                if (currentNode.hashtagCountKey < currentNodePTableElement.hashtagCountKey) {
                    FibonacciNode tempNode = currentNodePTableElement;
                    currentNodePTableElement = currentNode;
                    currentNode = tempNode;
                }
                // currentNodePTableElement is removed from the root list because it is becoming a child node of currentNode
                mergeNodes(currentNodePTableElement, currentNode);
                
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

    public void mergeNodes(FibonacciNode currentNodePTableElement, FibonacciNode currentNode) {
        // Remove currentNodePTableElement from root list
        currentNodePTableElement.leftSiblingNode.rightSiblingNode = currentNodePTableElement.rightSiblingNode;
        currentNodePTableElement.rightSiblingNode.leftSiblingNode = currentNodePTableElement.leftSiblingNode;
        
        // Make currentNodeTableElement a child of currentNode
        currentNodePTableElement.parentNode = currentNode;
        
        if (currentNode.childNode == null) {
            currentNode.childNode = currentNodePTableElement;
            currentNodePTableElement.rightSiblingNode = currentNodePTableElement;
            currentNodePTableElement.leftSiblingNode = currentNodePTableElement;
        }
        else {
            currentNodePTableElement.leftSiblingNode = currentNode.childNode;
            currentNodePTableElement.rightSiblingNode = currentNode.childNode.rightSiblingNode;
            currentNode.childNode.rightSiblingNode = currentNodePTableElement;
            currentNodePTableElement.rightSiblingNode.leftSiblingNode = currentNodePTableElement;
        }
        
        // Increase degree
        currentNode.degree++;
        currentNodePTableElement.markChildCut = false;
        
        System.out.println(currentNodePTableElement.hashtag + "," + currentNodePTableElement.hashtagCountKey + ", degree: " + currentNodePTableElement.degree
                + " became a child of " + currentNode.hashtag + "," + currentNode.hashtagCountKey + ", degree: " + currentNode.degree);
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
                if (currentLine.startsWith("#")) {
                    hObj = parseFileIntoObject(hObj, currentLine);
                    // Need to insert the first node
                    if (fibTable.isEmpty()) {
                        //System.out.println("INSERTING Term: " + hObj.term + "\t Count: " + hObj.count);
                        insertNode(hObj.term, hObj.count);
                    }
                    else {
                        // Check to see if there is a duplicate hashtag and if the value needs to be updated
                        // Update in heap and in hashtable
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
