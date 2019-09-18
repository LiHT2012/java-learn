package com.dbcool.api.liht;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

/**
 * 有四个线程1、2、3、4。线程1的功能就是输出1，线程2的功能就是输出2，以此类推………现在有四个文件ABCD。初始都为空。现要让四个文件呈如下格式：
A：1 2 3 4 1 2….
B：2 3 4 1 2 3….
C：3 4 1 2 3 4….
D：4 1 2 3 4 1….

请设计程序。
解题思路

                    线程1   线程2   线程3 线程4
第一轮输出的文件顺序  A       B       C       D
第二轮输出的文件顺序  D       A       B       C
第三轮输出的文件顺序  C       D       A       B
第四轮输出的文件顺序  B       C       D       A   
第五轮输出的文件顺序  A       B       C       D               

如上所示，程序有两要点
要点一
四个线程必须要共同完成每一轮的输出，在进入下一轮输出。可以利用java.util.concurrent.CyclicBarrier完成该功能。

要点二
每轮输出过后要向后偏移文件的顺序。可以利用java.util.Collections#rotate来实现
 *
 */
public class WriteData implements Runnable {
	/**
	 * 该线程能输出的数值
	 */
	private int num;
	/**
	 * 包含A, B, C, D四个文件的列表
	 */
	private List<File> files;
	/**
	 * 栏栅
	 */
	private CyclicBarrier barrier;

	public WriteData(int num, List<File> files, CyclicBarrier barrier) {
		this.num = num;
		this.files = files;
		this.barrier = barrier;
	}

	@Override
	public void run() {
		while (true) {
			Writer writer = null;
			try { // 向文件写入数值
				writer = new PrintWriter(new FileWriter(files.get(num - 1), true), true);
				writer.write(String.valueOf(num)); // 等待其它线程完成此轮工作
				barrier.await();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * } 创建 A, B, C, D四个文件
	 */
	public static List<File> createFiles() {
		LinkedList<File> files = new LinkedList<>();
		final char fileChar = 'A';
		for (int i = 0; i < 4; i++) {
			String fileName = String.valueOf((char) (fileChar + i));
			files.add(new File(fileName));
		}
		return files;
	}

	/**
	 * 向后偏移文件的顺序 A, B, C, D 经向后偏移后顺序为 D, A, B, C
	 */
	public static void rorate(List<File> files) {
		Collections.rotate(files, 1);
	}

	public static void main(String[] args) {
		List<File> files = createFiles();
		CyclicBarrier barrier = new CyclicBarrier(4, () -> {
			rorate(files);
		});
		for (int i = 1; i <= 4; i++) {
			new Thread(new WriteData(i, files, barrier)).start();
		}
	}
}
