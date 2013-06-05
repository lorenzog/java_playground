import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BSTTest {

	
//	@Before
//	public void setUp() throws Exception {
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}

	@Test
	public void testAddOneNode() {
		BST tree = new BST();
		Node v = new Node(0);
		tree.addNode(v);
		assertEquals(tree.root,v);
	}
	
	@Test
	public void testAddManyNodes() {
		BST tree = new BST();
		
		Node b = new Node (2);
		Node c = new Node(3);
		Node a = new Node(1);
		
		tree.addNode(b);
		tree.addNode(a);
		tree.addNode(c);
		assertEquals(tree.root, b);
	}
	
	/**
	 * just to make sure
	 */
	@Test
	public void testWalkEmptyTree() {
		BST tree = new BST();
		ArrayList<Integer> result = tree.walk(tree.getRoot());
		assertEquals(result.size(), 0);
	}
	
	/**
	 * checking in-tree walk with random elements
	 */
	@Test
	public void testWalk() {
		Random r = new Random();
		BST tree = new BST();
		
		// using arraylists because I'm lazy
		ArrayList<Integer> values = new ArrayList<Integer>();
		System.out.print("adding values:");
		for ( int i = 0 ; i < 10 ; i++ )
		{
			int val = r.nextInt(42);
			values.add(val);
			System.out.print(" " + val);
			tree.addNode(new Node(val));
		}
		System.out.println();
		Collections.sort(values);
		System.out.println("sorted: " + values.toString());
		ArrayList<Integer> result = tree.walk(tree.getRoot());
		System.out.println("tree walk: " + result.toString());
		for ( int i = 0 ; i < result.size() ; i++ )
			assertEquals(values.get(i),result.get(i));

	}

}
