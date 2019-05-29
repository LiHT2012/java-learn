package com.dbcool.api.liht;

class MyThread extends Thread {
	MyThread() {
		System.out.println(" MyThread");
	}
	public void run() {
		System.out.println(" bar");
	}
	public void run(String str) {
		System.out.println(" bazzz");
	}
}
public class TestThreads {

	public static void main (String[] args) throws ClassNotFoundException {
//		Class<MyThread> a = MyThread.class;
//		Class<?> b = Class.forName("com.dbcool.api.liht.MyThread");
//		MyThread m = new MyThread();
//		Class<? extends MyThread> c = m.getClass();
		Thread t = new MyThread() {
			public void run() {
				System.out.println(" foo");
			}
		};
		t.start();
	}
}
