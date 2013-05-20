public class NullPointer2 {
	public static void main(String args[])
	{
		// Accept up to 3 parameters
		String[] list = new String[3];

		int index = 0;

		while ( (index < args.length) && ( index < 3 ) )
		{
			list[index++] = args[index];
		}

		// Check all the parameters 
		for (int i = 0; i < list.length; i++)
		{
			if (list[i].equals("-help"))
			{
				// .........
			}
			else
				if (list[i].equals("-cp"))
				{
					// .........
				}
			// else .....
		}	
	}
}
