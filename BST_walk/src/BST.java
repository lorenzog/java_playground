import java.util.ArrayList;
import java.util.Stack;

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
	 * usual problems: with deep trees the recursion overhead could become
	 * significant
	 * 
	 * complexity comparable to the height of the tree. As this is just a 
	 * binary search tree (and not a RB tree or similar), there is 
	 * no guarantee that the height of the tree is kept roughly constant.
	 * 
	 * inspired by: Introduction to Algorithms, second edition 
	 * 	(Cormen, Leiserson, Rivest, Stein), Chapter 12.
	 * 
	 * @param n a node belonging to the tree; root is fine
	 * @return an array of the elements in order
	 */
	public ArrayList<Integer> recursiveWalk(Node n) {
		ArrayList<Integer> out = new ArrayList<Integer>();
		// using a hidden function to build the result in an arraylist without
		// having to make it available outside this function 
		// (which could be a problem for parallel processing, etc.)
		_recursiveWalk(n, out);
		return out;
	}
	
	private ArrayList<Integer> _recursiveWalk(Node n, ArrayList<Integer> out) {
		if ( n != null )
		{
			_recursiveWalk(n.getLeftChild(), out);
			out.add(n.value);
			_recursiveWalk(n.getRightChild(), out);
		}
		return out;
	}
	
	/**
	 * a non-recursive version, using a stack
	 * (useful for deep trees, when memory is an issue for recursion)
	 * 
	 * other examples could modify the nodes, adding 'visited' fields, 
	 * but I wanted to keep this simple without changing the original data structure.
	 * 
	 * inspired by: https://web.cs.dal.ca/~sedgwick/3110/a9_soln.txt
	 */
	public ArrayList<Integer> nonRecursiveWalk(Node n) {
		ArrayList<Integer> out = new ArrayList<Integer>();
		Stack<Node> s = new Stack<Node>();
		Node where = n;
		while ( !s.isEmpty() )
		{
			s.push(where);
			while ( where.getLeftChild() != null )
			{
				s.push(where.getLeftChild());
				where = where.getLeftChild();
			}
			boolean doneThisBranch = true;
			while ( doneThisBranch == true)
			{
				where = s.pop();
				out.add(where.getValue());
				if ( where.getRightChild() != null )
				{
					s.push(where.getRightChild());
					where = where.getRightChild();
					doneThisBranch = false;
				}
			}
			doneThisBranch = true;
		}
		return out;
	}
}
