
public class Node implements Comparable<Node> {
	int value;
	Node parent = null;
	Node leftChild = null;
	Node rightChild = null;
	
	Node(int value) {
		this.value = value;
	}
	
	int getValue() {
		return this.value;
	}
	
	Node getLeftChild() {
		return this.leftChild;
	}
	
	Node getRightChild() {
		return this.rightChild;
	}

	public int compareTo(Node what) {
		if (value < what.getValue())
			return -1;
		else if (value > what.getValue()) 
			return 1;
		else 
			return 0;
	}
	
	public boolean equals(Node other) {
		System.out.println("node equals");
		if ( value == other.getValue() )
			return true;
		return false;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}
}
