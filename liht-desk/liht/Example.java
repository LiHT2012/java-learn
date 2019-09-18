package com.dbcool.api.liht;

public class Example {

	String str = new String("good");
	char[] ch = {'a','b','c'};
	public static void main(String[] args) {
//		Example ex = new Example();
//		ex.change(ex.str, ex.ch);
//		System.out.println(ex.str);
//		System.out.println(ex.ch[0]+ex.ch[1]+ex.ch[2]);
		int a =1000,b=1000;
		System.out.println(a == b);
		
		Integer c = 1000,d = 1000;
		System.out.println(c == d);
		
		Integer e = 127,f=127;//Integer (-128~127)用==比较为真，除此外都是false
		System.out.println(e == f);
//		System.out.println(Integer.MAX_VALUE);
//		System.out.println(Integer.MIN_VALUE);
	}
	public void change(String str, char ch[]) {
		str = "test ok";
		ch[0] = 'g';
	}
}
