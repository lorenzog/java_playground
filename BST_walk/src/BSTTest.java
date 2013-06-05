import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;

public class BSTTest {

	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

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
	
	@Test
	public void testWalk() {
		Random r = new Random();
		BST tree = new BST();
		System.out.print("adding values:");
		for ( int i = 0 ; i < 10 ; i++ )
		{
			int val = r.nextInt(42);
			System.out.print(" " + val);
			tree.addNode(new Node(val));
		}
		System.out.println();
		tree.walk(tree.getRoot());
		assertTrue(true);
	}
	
	@Test
	public void testWalkEmptyTree() {
		BST tree = new BST();
		tree.walk(tree.getRoot());
	}

}
