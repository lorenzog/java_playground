class MyObj {
	String s;
}

public class Passing {

	private void manipulator(MyObj arg) {
		arg.s = "xyz";
	}

	MyObj o = new MyObj();

	private void run() {
		o.s = "abc";

		System.out.println("o.s: " + o.s);
		manipulator(o);
		System.out.println("o.s: " + o.s);
	}

	public static void main (String args[]) {
		Passing demo = new Passing();
		demo.run();
	}
}
