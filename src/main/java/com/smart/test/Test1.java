package com.smart.test;

public class Test1 {

	private static void object(Object obj) {
		System.out.println("obj iml");
	}

	private static void object(String str) {
		System.out.println("str impl");
	}


	public static void main(String[] args) {

		object(null);
	}
}
