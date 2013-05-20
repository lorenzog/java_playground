class smallTips {
	public static void main (String args[]) {
		/* true or false? */
		//System.out.println("abc == abc? " + ("abc" == "abc") );

		/* what about this? */
		String a = "abc";
		String b = "abc";
		System.out.println("a == b? " + (a == b ) );
		String c = new String("abc");
		String d = new String("abc");
		System.out.println("c == d? " + (c == d));

		// XXX
		System.exit(0);

		/* and this? */
		System.out.print("And this? ");
		System.out.println(a == b);
		System.out.println("What about this one?" + a == b );
		

		/* true or false? */
		System.out.println("Double.NaN == Double.NaN? " 
				+ ( Double.NaN == Double.NaN));
	}
}
