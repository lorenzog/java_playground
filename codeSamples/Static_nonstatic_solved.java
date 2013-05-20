public class Static_nonstatic_solved {
	public String someData = "somedata";

	public static void main (String args[])
	{
		Static_nonstatic_solved demo = 
			new Static_nonstatic_solved();

		System.out.println ("I will now print " + demo.someData );
	}
}
