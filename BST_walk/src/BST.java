import java.util.ArrayList;

public class BST {
	Node root;

	BST() {
		root = null;
	}

	public Node getRoot() {
		return root;
	}

	public void addNode(Node n) {
		Node parent = null;
		Node where = root;
		while (where != null) {
			parent = where;
			if (n.compareTo(where) < 0)
				where = where.getLeftChild();
			else
				where = where.getRightChild();
		}
		n.setParent(parent);
		if (parent == null) // empty tree
			root = n;
		else if (n.compareTo(parent) < 0)
			parent.leftChild = n;
		else
			parent.rightChild = n;
	}
	
	/**
	 * a textbook example, with recursion
	 * 
	 * @param n a node belonging to the tree; root is fine
	 * @return an array of the elements in order
	 */
	// moved out otherwise it stays local to the function, and good-bye recursions
	ArrayList<Integer> out = new ArrayList<Integer>();
	public ArrayList<Integer> walk(Node n) {
		
		if ( n != null )
		{
			walk(n.getLeftChild());
			out.add(n.value);
			walk(n.getRightChild());
		}
		return out;
	}
}
