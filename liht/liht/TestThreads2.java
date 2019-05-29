package com.dbcool.api.liht;

public class TestThreads2  extends Thread{

	int tally = 0;//glable

	void ThreadProc()
	{
	       for(int i = 1; i <= 50; i++)
	              tally += 1;
	}
	public void run() {
		for(int i = 1; i <= 50; i++)
            tally += 1;
		System.out.println(tally);
	}
	public static void main(String[] args) {
		TestThreads2 a = new TestThreads2();
		TestThreads2 b = new TestThreads2();
		a.start();
		b.start();
	}
}
