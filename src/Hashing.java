import java.util.ArrayList;
import java.util.List;

public class Hashing
{
    public static void main(String[] args)
    {
        HashingLibrary hasher = new HashingLibrary();
        hasher.initializeObjectList();

        //hasher.initializeDirectAddressTable();

        hasher.initializeHashTableWithChaining();
        hasher.insertAllObjectsIntoHashTableWithChaining();
        //hasher.displayChainedHashTable();
        //hasher.testChainedSearch();
        hasher.testDelete();
    }
}

class HashingLibrary
{
    public static int maxIndex = 50;
    private int numObjects = 10;
    private List<exampleObject> objectList = new ArrayList();
    private exampleObject[] arr = new exampleObject[maxIndex];
    private exampleObjectNode[] hashTableChaining = new exampleObjectNode[maxIndex];

    public void initializeObjectList()
    {
        for (int i = 0; i < numObjects; i++)
            objectList.add(new exampleObject((int)(maxIndex*Math.random())));

        confirmObjList();
    }

    //builds a list of objects, as a prestage to direct address table or hashtable
    private void confirmObjList()
    {
        System.out.println("confirmation of object list:");
        for (exampleObject i:objectList)
            System.out.println(i + " key:" + i.key);
    }

    //adds all elements in list into an array so that search will be O(1)
    //this doesn't solve collisions so the most recent duplicates will only be stored
    public void initializeDirectAddressTable()
    {
        for (exampleObject i: objectList)
        {
            arr[i.key] = i;
        }

        confirmDirectAddressTable();
    }

    private void confirmDirectAddressTable()
    {
        System.out.println("Confirmation of direct address table");
        for (int i=0; i<arr.length; i++)
        {
            System.out.print("index: " + i + " ");
            if (arr[i] == null)
                System.out.print("no object here");
            else
                System.out.print("object key: " + arr[i].key + "\tobject: " + arr[i]);
            System.out.println();
        }
    }

    //initializes all linked lists in the hash table with a null curr object, null prev and next pointers
    public void initializeHashTableWithChaining()
    {
        for (int i = 0; i < maxIndex; i++)
        {
            hashTableChaining[i] = new exampleObjectNode();
        }
    }

    public void insertAllObjectsIntoHashTableWithChaining()
    {
        for (exampleObject i:objectList)
            chainedInsert(i);
    }

    //inserts a given example object into the hash table with chaining
    //solves collisions by having a linked list at every index
    //average (not worst case) search time is O(1) now
    public void chainedInsert(exampleObject x)
    {
        //need to insert each object at the tail of the linked list at that index in the hash table (tail rather than head so earlier occurrences appear earlier in the LL)
        int key = hashCode(x.key); //returns itself, would be more complex if there was an actual application

        exampleObjectNode currLL = hashTableChaining[key];
        //currLL either has (prev,object,null) = (null,null,null) (initial state) or (something,object,null) (need to append it to the head).
        if (currLL.object == null)
        {
            //nothing is in this LL yet, assign the object and continue
            currLL.object = x;
        } else
        {
            currLL = new exampleObjectNode(x,currLL); //newLLNode.object = x, newLLNode.prev = currLL, currLL.next = newLLNode
        }

        hashTableChaining[key] = currLL;
    }

    //tests that insert worked based off of
    public void displayChainedHashTable()
    {
        for (int i = 0; i < maxIndex; i++)
        {
            //for each key, there is a LL: need to print out the prev, curr, and next of each node in the LL;
            exampleObjectNode currLL = hashTableChaining[i];
            System.out.println("curr index/key: " + i);
            while(currLL != null)
            {
                if (currLL.object == null) //nothing is in this LL, go to next index
                    break;

                if (currLL.prev == null)
                    System.out.print("prev: null \t");
                else
                    System.out.printf("prev: %s\t",currLL.prev);
                if (currLL.object == null)
                    System.out.print("curr: null \t");
                else
                    System.out.printf("curr: %s\t",currLL.object);
                if (currLL.next == null)
                    System.out.print("next: null \t");
                else
                    System.out.printf("next: %s\t",currLL.next);

                System.out.println();
                currLL = currLL.prev;
            }
        }
    }

    //searching for a value should take O(1) **on average** (not worst case), you access/traverse the LL to find the object. else, return null
    public exampleObject chainedSearch(int k)
    {
        int hashValue = hashCode(k);
        exampleObjectNode LL = hashTableChaining[hashValue];
        if (LL.object == null) return null;

        //need to recurr backwards from the tail
        while (LL != null)
        {
            if (LL.object.key == k)
                return LL.object;
            LL = LL.prev;
        }

        return null;
    }

    public void testChainedSearch()
    {
        System.out.println("test chained search");
        int randomKey = (int)(Math.random()*maxIndex);
        System.out.println("randomKey: " + randomKey);
        exampleObject result = chainedSearch(randomKey); //this method hashes a given key so you dont need to hash it again

        if (result == null)
            System.out.print("not in hashtable");
        else
            System.out.print("success: " + result);
    }

    //assume x is in the hashtable to start off with
    public void delete(exampleObject x)
    {
        int hashValue = hashCode(x.key);
        exampleObjectNode LLNode = hashTableChaining[hashValue];

        while(LLNode != null)
        {
            if (LLNode.object.key == x.key) //assume all keys are distinct
            {
                if (LLNode.prev == null && LLNode.next != null) //the head the LL
                {
                    LLNode.next.prev = null;
                    LLNode.next = null;
                }else if (LLNode.prev == null && LLNode.next == null) //single object, just make a new null object there
                {
                    LLNode.object=null;
                }else if (LLNode.prev != null && LLNode.next == null) //the tail of the LL
                {
                    LLNode.prev.next = null;
                    LLNode.prev = null;
                }else
                {
                    LLNode.prev.next = LLNode.next;
                    LLNode.next.prev = LLNode.prev;
                    LLNode.next = null;
                    LLNode.prev = null;
                }

                break;
            }
            if (LLNode.prev != null)
                LLNode = LLNode.prev;
        }
    }

    public void testDelete()
    {
        exampleObject toDelete = objectList.get((int)(Math.random()*numObjects));
        System.out.println("to delete: " + toDelete);
        System.out.println("before: ");
        displayChainedHashTable();

        delete(toDelete);
        System.out.println("after: ");
        displayChainedHashTable();

    }

    //custom implementation of the hashing function. since this is just an integer i'll just return the integer
    //im not overriding because its not a java thing tied to a class, this is more of a function to take in a key and return its hash value
    //for other code obv i would put smth else here, but its tricky as the same input has to map to the same output so i can't just use math.random
    public int hashCode(int k)
    { return k; }
}

class exampleObject
{
    public int key;
    public Object data;

    public exampleObject(int key)
    {
        this.key = key;
    }
}

class exampleObjectNode
{
    public exampleObjectNode prev;
    public exampleObject object;
    public exampleObjectNode next;

    public exampleObjectNode(exampleObject x, exampleObjectNode prev)
    {
        object = x;
        this.prev = prev;
        next = null;
        prev.next = this;
    }

    public exampleObjectNode()
    {
        prev= null;
        object = null;
        next = null;
    }
}