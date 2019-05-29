package com.dbcool.api.liht.product2;

import java.util.List;
import java.util.Random;

import com.dbcool.api.liht.product1.PCData;

public class Producer implements Runnable {
	private List<PCData> queue;
	private int length;

	public Producer(List<PCData> queue, int length) {
		this.queue = queue;
		this.length = length;
	}

	@Override
	public void run() {
		try {
			while (true) {

				if (Thread.currentThread().isInterrupted())
					break;
				Random r = new Random();
				long temp = r.nextInt(100);
				System.out.println(Thread.currentThread().getId() + " 生产了：" + temp);
				PCData data = new PCData(length);
//                data.set(temp);
				synchronized (queue) {
					if (queue.size() >= length) {
						queue.notifyAll();
						queue.wait();
					} else
						queue.add(data);
				}
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
