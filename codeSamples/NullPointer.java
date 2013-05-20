class MyObj {
	String name;
	int age;

	MyObj() {
		// do nothing
	}

	MyObj(String name) {
		this.name = name;
	}

	MyObj(int age) {
		this.age = age;
	}

	void print() {
		System.out.println("Name: " + name + " age: " + age);
	}

}

public class NullPointer { 

	public static void main (String args[]) {
	MyObj o = new MyObj(10);

	o.print();
	System.out.println(o.name.length());

	}
}
