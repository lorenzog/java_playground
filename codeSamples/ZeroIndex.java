public class ZeroIndex {

	public static void main (String args[]) {

		String abc = "abc";

		System.out.println ("Char at offset 0 : " + abc.charAt(0) );
		System.out.println ("Char at offset 1 : " + abc.charAt(1) );
		System.out.println ("Char at offset 2 : " + abc.charAt(2) );

		/* what happens here? */
		System.out.println ("Char at offset 3 : " + abc.charAt(3) );
	}
}
