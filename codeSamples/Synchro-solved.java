public class MyCounter
{
	private int count = 0; // count starts at zero

	public synchronized void setCount(int amount)
	{ 
		count = amount;
	}
	
	public synchronized int getCount()
	{
		return count;
	}
}
