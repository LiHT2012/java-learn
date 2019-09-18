package com.dbcool.api.liht;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.sql.Connection;

public class Symmetric {

	public static void main(String[] args) {
//		String str = "java";
//		str.length();
//		str.toLowerCase();
//		Connection con = null;
//		try {
//			InputStreamReader r = new InputStreamReader(new FileInputStream(""));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		int i =12;
//		System.out.println(i += i -= i *= i);
//		System.out.println("5"+2);
//		B b = new B();//编译报错
		Symmetric s = new Symmetric();
		Symmetric.B b = s.new B();//内部类的创建
	}
	int f(char[] s) {
		int i=0,j=0;
//		while(s[j]) {
//			j++;
//		}
		for(j--;i<j && s[i]==s[j]; i++,j--);
		return i>= j ? 1 :0 ;
	}
	class A {
		public A() {
			this.test();
		}
		public void test() {
			System.out.println("AAAAA.test");
		}
	}
	class B extends A{
		private int i=11;
		public B() {
			System.out.println("bbb");
		}
		public void test() {
//			System.out.println(i);
		}
	}
}
