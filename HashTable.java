public class HashTable<E extends Comparable<E>> {

    private Node<E>[] underlying;
    private int usage;
    private int totalNodes;
    private double loadFactor;

    private static final int DEFAULT_SIZE = 4;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    public HashTable(int size) {
        if (size <= 0)
            size = DEFAULT_SIZE;
        this.underlying = new Node[size];
        this.usage = 0;
        this.totalNodes = 0;
        this.loadFactor = 0.0;
    }

    public HashTable() {
        this(DEFAULT_SIZE);
    }

    /**
     * Adds a node, with the specified content, to a linked list in the underlying array.
     * If the load factor exceeds threshold, rehashing is triggered.
     *
     * @param content E The content of a new node.
     */
    public void add(E content) {
        // Check load factor before adding
        this.loadFactor = (double) this.usage / this.underlying.length;
        if (this.loadFactor >= LOAD_FACTOR_THRESHOLD) {
            rehash();
        }

        Node<E> newNode = new Node<E>(content);
        int position = Math.abs(content.hashCode()) % this.underlying.length;

        if (this.underlying[position] == null) {
            this.underlying[position] = newNode;
            this.usage += 1;
        } else {
            newNode.setNext(this.underlying[position]);
            this.underlying[position] = newNode;
        }

        this.totalNodes += 1;
        this.loadFactor = (double) this.usage / this.underlying.length;
    }

    /**
     * Rehashes the hashtable by doubling the size of the underlying array
     * and reinserting all existing nodes.
     */
    private void rehash() {
        Node<E>[] oldArray = this.underlying;
        Node<E>[] newArray = new Node[oldArray.length * 2];

        int newUsage = 0;

        for (int i = 0; i < oldArray.length; i++) {
            Node<E> current = oldArray[i];
            while (current != null) {
                Node<E> nextNode = current.getNext();
                current.setNext(null); // Detach node

                int newPos = Math.abs(current.getContent().hashCode()) % newArray.length;

                if (newArray[newPos] == null) {
                    newUsage++;
                    newArray[newPos] = current;
                } else {
                    current.setNext(newArray[newPos]);
                    newArray[newPos] = current;
                }

                current = nextNode;
            }
        }

        this.underlying = newArray;
        this.usage = newUsage;
        this.loadFactor = (double) this.usage / this.underlying.length;
    }

    /**
     * Searches for the target in the hashtable.
     *
     * @param target E value to search for
     * @return true if found, false otherwise
     */
    public boolean contains(E target) {
        int position = Math.abs(target.hashCode()) % this.underlying.length;
        Node<E> current = this.underlying[position];
        while (current != null) {
            if (current.getContent().compareTo(target) == 0) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    private static final String LINKED_LIST_HEADER = "\n[ %2d ]: ";
    private static final String EMPTY_LIST_MESSAGE = "null";
    private static final String ARRAY_INFORMATION = "Underlying array usage / length: %d/%d";
    private static final String NODES_INFORMATION = "\nTotal number of nodes: %d";
    private static final String NODE_CONTENT = "%s --> ";

    public String toString() {
        StringBuilder sb = new StringBuilder(
                String.format(ARRAY_INFORMATION, this.usage, this.underlying.length));
        sb.append(String.format(NODES_INFORMATION, this.totalNodes));
        for (int i = 0; i < underlying.length; i++) {
            sb.append(String.format(LINKED_LIST_HEADER, i));
            Node head = this.underlying[i];
            if (head == null) {
                sb.append(EMPTY_LIST_MESSAGE);
            } else {
                Node cursor = head;
                while (cursor != null) {
                    sb.append(String.format(NODE_CONTENT, cursor));
                    cursor = cursor.getNext();
                }
            }
        }
        return sb.toString();
    }
}
