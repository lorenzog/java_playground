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
	 * @param n a node belonging to the tree; root is fine
	 */
	public void walk(Node n) {
		if ( n != null )
		{
			walk(n.getLeftChild());
			System.out.print(" " + n.value);
			walk(n.getRightChild());
		}
			
	}
}
