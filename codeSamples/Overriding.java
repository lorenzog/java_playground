class Foo {
	public static void classMethod() {
		System.out.println("classMethod() in Foo");
	}

	public void instanceMethod() {
		System.out.println("instanceMethod() in Foo");
	}
}

class Bar extends Foo {
	public static void classMethod() {
		System.out.println("classMethod() in Bar");
	}

	public void instanceMethod() {
		System.out.println("instanceMethod() in Bar");
	}
}

class Overriding {
	public static void main(String[] args) {
		Foo f = new Bar();
		f.instanceMethod();
		f.classMethod();
		Bar.classMethod();
	}
}

