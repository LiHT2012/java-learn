package com.dbcool.api.liht;

import java.util.concurrent.locks.*;

/**
 * 多生产多消费模式

有多个生产者线程，多个消费者线程，生产者将生产的面包放进篮子(集合或数组)里，消费者从篮子里取出面包。
生产者判断继续生产的依据是篮子已经满了，消费者判断继续消费的依据是篮子是否空了。此外，当消费者取出面包后，对应的位置又空了，
生产者可以回头从篮子的起始位置继续生产，这可以通过重置篮子的指针来实现。

在这个模式里，除了描述生产者、消费者、面包，还需要描述篮子这个容器。假设使用数组作为容器，生产者每生产一个，生产指针向后移位，
消费者每消费一个，消费指针向后移位
 */
class Basket {
	private Bread[] arr;

	// the size of basket
	Basket(int size) {
		arr = new Bread[size];
	}

	// the pointer of in and out
	private int in_ptr, out_ptr;
	// how many breads left in basket
	private int left;
	private Lock lock = new ReentrantLock();
	private Condition full = lock.newCondition();
	private Condition empty = lock.newCondition();

	// bread into basket
	public void in() {
		lock.lock();
		try {
			while (left == arr.length) {
				try {
					full.await();
				} catch (InterruptedException i) {
					i.printStackTrace();
				}
			}
			arr[in_ptr] = new Bread("MianBao", Producer.num++);
			System.out.println("Put the bread: " + arr[in_ptr].getName() + "------into basket[" + in_ptr + "]");
			left++;
			if (++in_ptr == arr.length) {
				in_ptr = 0;
			}
			empty.signal();
		} finally {
			lock.unlock();
		}
	}

	// bread out from basket
	public Bread out() {
		lock.lock();
		try {
			while (left == 0) {
				try {
					empty.await();
				} catch (InterruptedException i) {
					i.printStackTrace();
				}
			}
			Bread out_bread = arr[out_ptr];
			System.out.println("Get the bread: " + out_bread.getName() + "-----------from basket[" + out_ptr + "]");
			left--;
			if (++out_ptr == arr.length) {
				out_ptr = 0;
			}
			full.signal();
			return out_bread;
		} finally {
			lock.unlock();
		}
	}
}

class Bread {
	private String name;

	Bread(String name, int num) {
		this.name = name + num;
	}

	public String getName() {
		return this.name;
	}
}

class Producer implements Runnable {
	private Basket basket;
	public static int num = 1; // the first number for Bread's name

	Producer(Basket b) {
		this.basket = b;
	}

	public void run() {
		while (true) {
			basket.in();
			try {
				Thread.sleep(10);
			} catch (InterruptedException i) {
			}
		}
	}
}

class Consumer implements Runnable {
	private Basket basket;
	private Bread i_get;

	Consumer(Basket b) {
		this.basket = b;
	}

	public void run() {
		while (true) {
			i_get = basket.out();
			try {
				Thread.sleep(10);
			} catch (InterruptedException i) {
			}
		}
	}
}

public class ProduceConsume_7 {
	public static void main(String[] args) {
		Basket b = new Basket(20); // the basket size = 20
		Producer pro = new Producer(b);
		Consumer con = new Consumer(b);
		Thread pro_t1 = new Thread(pro);
		Thread pro_t2 = new Thread(pro);
		Thread con_t1 = new Thread(con);
		Thread con_t2 = new Thread(con);
		Thread con_t3 = new Thread(con);
		pro_t1.start();
		pro_t2.start();
		con_t1.start();
		con_t2.start();
		con_t3.start();
	}
}
