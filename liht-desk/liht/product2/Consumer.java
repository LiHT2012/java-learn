package com.dbcool.api.liht.product2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dbcool.api.liht.product1.PCData;

public class Consumer implements Runnable {
	private List<PCData> queue;

	public Consumer(List<PCData> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (Thread.currentThread().isInterrupted())
					break;
				PCData data = null;
				synchronized (queue) {
					if (queue.size() == 0) {
						queue.notifyAll();
						queue.wait();
					}
					data = queue.remove(0);
				}
				System.out.println(Thread.currentThread().getId() + " 消费了:" + data.getData() + " result:"
						+ (data.getData() * data.getData()));
				Thread.sleep(1000);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		List<PCData> queue = new ArrayList<PCData>();
		int length = 10;
		Producer p1 = new Producer(queue, length);
		Producer p2 = new Producer(queue, length);
		Producer p3 = new Producer(queue, length);
		Consumer c1 = new Consumer(queue);
		Consumer c2 = new Consumer(queue);
		Consumer c3 = new Consumer(queue);
		ExecutorService service = Executors.newCachedThreadPool();
		service.execute(p1);
		service.execute(p2);
		service.execute(p3);
		service.execute(c1);
		service.execute(c2);
		service.execute(c3);

	}
}