package com.dbcool.api.liht;

public class Threads1 {

	int x = 0;

	public class Runner implements Runnable {

		@Override
		public void run() {
			int current = 0;
			for (int i = 0; i < 4; i++) {
				current = x;
				System.out.print(current + ", ");
				x = current + 2;
			}
		}
	}
	public void go() {
		Runnable r1 = new Runner();
		new Thread(r1).start();
		new Thread(r1).start();
	}
	public static void main(String[] args) {
		new Threads1().go();
	}
	/**
	 * 可能的结果很多种：
	 * 0,2,4,6,0,2,4,6
	 * 0,2,4,6,8,10,12,14
	 * 0,0,2,2,4,4,6,6
	 * .......
	 */
}
