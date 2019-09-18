package com.dbcool.api.liht;

public class MaxNumFromStack {
	public static void main(String[] args) {
//		MyStack stack = new MyStack();
//		stack.push(2);
//		stack.push(3);
//		stack.push(4);
//		stack.push(5);
//		stack.push(1);
//		stack.push(10);
//		stack.push(8);
//		for (int i = 0; i < 6; i++) {
//			System.out.println("TheNum:" + stack.pop());
//			System.out.println("MaxNum:" + stack.getMax());
//			System.out.println("=========================");
//		}
		int n = 100, m3 = 1, m5 = 1, p = 1;
		int i=1;
		int j=1;
		while (m3*3  <= n || m5*5 <=n) {
			if (m3 * 3 < m5 * 5) {
				p = m3 * 3;
				m3++;
				System.out.println(p + "*");
				i++;
			} else if (m3 * 3 > m5 * 5) {
				p = m5 * 5;
				m5++;
				System.out.println(p + "#");
				i++;
			} else {
				p = m3 * 3;
				System.out.println(p + "*#");
				i++;
				m3++;
				m5++;
			}
			j++;
		}
		System.out.println("i:"+i+";j:"+j);
		String s ;
//		System.out.println(s);//需初始化值，否则编译不过
	}
}

class MyStack {
	private int top;
	private int maxIndex;
	private int MAX;// 栈的容量
	private int[] data;// 原数据顺序的数组
	private int[] maxStackIndex;// 最大值的位置index数组

	public MyStack() {
		MAX = 10;
		top = -1;
		maxIndex = -1;
		data = new int[MAX];
		maxStackIndex = new int[MAX];
	}

	// 入栈
	public void push(int num) {
		top++;
		if (top >= MAX) {
			System.out.println("栈已满，不能入栈");
			return;
		}
		data[top] = num;// 先添加到栈中
//		if (num > getMax()) {// 大于当前最大值
//			maxStackIndex[top] = top;// 当前是最大值
//			maxIndex = top;
//		} else {
//			// 保留最大的值
//			maxStackIndex[top] = maxIndex;
//		}
		if (num > getMax()) {// 大于当前最大值
			maxIndex = top;
		}
		maxStackIndex[top] = maxIndex;
	}

	// 出栈
	public int pop() {
		if (top < 0) {
			System.out.println("栈为空，不能出栈");
			return Integer.MIN_VALUE;
		}
		int num = data[top];
		if (top == 0) {
			maxIndex = -1;
		} else if (top == maxIndex) {// 当前栈顶是最大值,调整最大值
			maxIndex = maxStackIndex[top - 1];
		}
		top--;
		return num;
	}

	public int getMax() {
		if (maxIndex >= 0) {
			return data[maxIndex];
		} else {
			// 返回最小值
			return Integer.MIN_VALUE;
		}
	}
}
