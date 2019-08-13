package com.dbcool;

import java.util.Arrays;

public class SortTest {

	/**
	 * 直接插入排序
	 */
	public static void insertSort(int[] arr) {
		for (int i = 1; i < arr.length; i++) {
			// 如果当前遍历数字小于前一个数字
			if (arr[i] < arr[i - 1]) {
				int temp = arr[i];
				for (int j = i; j > 0; j--) {// 有i-1个已经排好序的数待比较
					if (arr[j - 1] > temp) {
						arr[j] = arr[j - 1];// 数组后移操作
					} else {
						arr[j] = temp;
					}
				}
			}
		}
	}

	/**
	 * 希尔排序 arr.lenth/2为首次步长，每次对半直至步长<=0跳出循环
	 */
	public static void sheelSort(int[] arr) {
		for (int d = arr.length; d >= 0; d /= 2) {
			for (int i = d; i < arr.length; i++) {
				for (int j = i - d; j >= 0; j -= d) {
					if (arr[j] > arr[j + d]) {
						swap(arr, j, j + d);// 交换
					}
				}
			}
		}
	}

	/**
	 * 简单选择排序
	 */
	public void selectSort(int[] arr) {
		for (int i = 0; i < arr.length - 1; i++) {
			int index = i;// 记录正在排序的下标
			for (int j = i + 1; j < arr.length; j++) {
				if (arr[j] < arr[index]) {
					index = j;// 找到本次最小的值的位置
				}
			}
			if (i != index) {
				swap(arr, i, index);
			}
		} // 第i趟找到第i小的值填入a[i]
	}

	/**
	 * 堆排序
	 * 1.将序列构建成大顶堆。
	  2.将根节点与最后一个节点交换，然后断开最后一个节点。
	  3.重复第一、二步，直到所有节点断开。
	 */
	public void heapSort(int[] a) {
		// 循环建堆
		for (int i = 0; i < a.length - 1; i++) {
			// 建堆
			buildMaxHeap(a, a.length - 1 - i);
			// 交换堆顶和最后一个元素
			swap(a, 0, a.length - 1 - i);
			System.out.println(Arrays.toString(a));
		}
	}

	// 对data数组从0到lastIndex建大顶堆
	private void buildMaxHeap(int[] data, int lastIndex) {
		// 从lastIndex处节点（最后一个节点）的父节点开始
		for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
			// k保存正在判断的节点
			int k = i;
			// 如果当前k节点的子节点存在
			while (k * 2 + 1 <= lastIndex) {
				// k节点的左子节点的索引
				int biggerIndex = 2 * k + 1;
				// 如果biggerIndex小于lastIndex，即biggerIndex+1代表的k节点的右子节点存在
				if (biggerIndex < lastIndex) {
					// 若果右子节点的值较大
					if (data[biggerIndex] < data[biggerIndex + 1]) {
						// biggerIndex总是记录较大子节点的索引
						biggerIndex++;
					}
				}
				// 如果k节点的值小于其较大的子节点的值
				if (data[k] < data[biggerIndex]) {
					// 交换他们
					swap(data, k, biggerIndex);
					// 将biggerIndex赋予k，开始while循环的下一次循环，重新保证k节点的值大于其左右子节点的值
					k = biggerIndex;
				} else {
					break;
				}
			}
		}
	}

	/**
	 * 冒泡排序
	 */
	public void bubbleSort(int[] arr){
		for(int i=0; i<arr.length; i++) {
			for(int j=i+1; j<arr.length;j++) {
				if(arr[i]>arr[j]) {//向后冒泡，按从小到达排序
					swap(arr,i,j);
				}
			}
		}
	}
	
	/**
	 * 快速排序
	 */
	public static void quickSort(int[] arr, int start, int end) {  
		int standard = arr[start];
		if(start >= end) {
			return;
		}
		int low = start;
		int high = end;
		while(low < high) {
			while(low < high && standard <= arr[high]) {
				high--;
			}
			arr[low]= arr[high];//找到右侧第一个比standard小的放在low位置
			while(low<high && standard >= arr[low]) {
				low++;
			}
			arr[high] = arr[low];
		}
		arr[low] = standard;
		quickSort(arr, start, low-1);
		 quickSort(arr, low+1, end);
	}

	/**
	 * 归并排序
	 * 调用mergeSort(arr,0,arr.length-1)
	 */
	public static void mergeSort(int[] arr, int low, int high) {
		if(high <= low) {
			return;
		}
		int middle = (low + high)/2;
		mergeSort(arr, low, middle);
		mergeSort(arr,middle+1,high);
		merge(arr,low,middle,high);
	}
	private static void merge(int[] arr, int low, int middle, int high) {
		int[] temp = new int[high - low + 1];
		int i = low,j=middle+1, index = 0;
		while(i<=middle && j<=high) {//遍历两边数组，取出晓得放在临时数组中
			if(arr[i] < arr[j]) {
				temp[index] = arr[i];
				i++;
			}else {
				temp[index]=arr[j];
				j++;
			}
			index++;
		}
		//处理多余数据
		while(j<= high) {
			temp[index]=arr[j];
			j++;
			index++;
		}
		while(i<=middle) {
			temp[index]=arr[i];
			i++;
			index++;
		}
		//将临时数组放到原数组
		for(int k=0; k<temp.length;k++) {
			arr[low+k] = temp[k];
		}
	}

	/**
	 * 基数排序
	 */
	public static void radixsort(int[] arr) {
		//首先查找arr中最大的数以确定位数
		int max = arr[0];
		for(int i=1; i< arr.length;i++) {
			if(arr[i] > max) {
				max = arr[i];
			}
		}
		int maxLength = (max +"").length();
		int[][] temp = new int[10][arr.length];//行为0~9
		//创建数组来存temp内层元素个数，即某位为n的元素有几个
		int[] count = new int[10];
		for(int i=0,n=1;i<maxLength;i++,n*=10) {
			for(int j=0;j<arr.length;j++) {
				int num = arr[j]/n%10;
				temp[num][count[num]] = arr[j];
				count[num]++;
			}
			int index = 0;
			for(int x = 0; x < arr.length; x++) {
				if(count[x] !=0) {
					for(int y=0;y<count[x];y++) {
						arr[index]=temp[x][y];
						index++;
					}
					count[x] =0;
				}
			}
		}
	}
	private static void swap(int[] data, int i, int j) {
		int tmp = data[i];
		data[i] = data[j];
		data[j] = tmp;
	}
}
