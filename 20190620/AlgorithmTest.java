package com.dbcool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class AlgorithmTest {
	static List<String> rList = new ArrayList<>();

	public static List<String> restoreIpAddresses(String s) {
		if (s.length() < 4) {
			return rList;
		}
		split(s, 0, 1);
		return rList;
	}

	/**
	 * 给定一个只包含数字的字符串，复原它并返回所有可能的 IP 地址格式。 思路： 采用了递归的思想
	 * IP地址一共分成四段，每一段长度为最小为1，最大为3，同层从1遍历到3 递归层（递进层）则是上一层插入分割点的字符串 回溯的找到符合的解
	 */
	private static void split(String s, int l, int seg) {
		if (seg != 4) {
			for (int i = 1; i <= 3; i++) {
				if (l + i <= s.length()) {
					int sub = Integer.parseInt(s.substring(l, l + i));
					if (sub >= 0 && sub < 256) {
						StringBuilder sb = new StringBuilder(s);
						sb.insert(l + i, '.');
						split(sb.toString(), l + i + 1, seg + 1);
					}
					if (sub == 0) {
						break;
					}
				} else {
					break;
				}
			}
		} else if (seg == 4 && l < s.length() && s.length() - l <= 3) {
			if (Integer.parseInt(s.substring(l, s.length())) < 256 && !((s.length() - l) > 1 && s.charAt(l) == '0'))
				for (int k = 0; k < s.length(); k++) {
					System.out.println(s.charAt(k));
				}
			rList.add(s);
		}
	}

	// TODO 待测试
	public static ArrayList<String> restoreIpAddresses2(String s) {
		ArrayList<String> result = new ArrayList<>();
		if (s.length() > 12 || s.length() < 4) {
			return result;
		}
		int startPartNum = 1;// 从第一段开始
		String tmpResultStr = "";// 最开始，临时结果集字符串是空串
		// 输入的字符串就是原始s
		getIpAddress(s, startPartNum, tmpResultStr, result);
		return result;
	}

	public static void getIpAddress(String input, int partsNum, String tmpResultStr, ArrayList<String> resultList) {
		if (input.length() == 0) {
			return;
		}
		/**
		 * 第一种判断，如果是最后一段了
		 */
		if (partsNum == 4) {// 如果此次递归是第四段，也就是最后一段了
			int number = Integer.parseInt(input);// 将剩余的source字符串转换成整型，毕竟范围必须是0~255

			if (input.charAt(0) == '0') {// 如果剩余这部分的开头是0,而且长度大于1，总体值也不是0，
				// 意思就是这个输入不是只有一个字符'0'的字符串
				if ((input.length() == 1 && number == 0) == false) {
					// 因为长度是2或者3位的输入input字符串，如果开头是0，它是不能作为ip的一部分，所以return跳过这个可能的子集
					return;
				}
			}

			if (number <= 255) {
				// 达到了第四段，就必须考虑增加到结果子集了。将前三段的临时str加上这个input组成
				tmpResultStr = tmpResultStr + input;
				resultList.add(tmpResultStr);
				return;
			}
		} else {
			/**
			 * 如果不是最后一段,那就是对1，2，3段的每种可能集都找到，因为每个段可能是1个字符，或者2个字符，或者3个字符
			 */
			// 第一种可能，这个端是一个字符，临时结果集末尾增加头部，作为新子集，除了头部字符其他输入，作为新的输入继续递归
			if (input.length() >= 1) {
				// 临时结果集新增这个input输入的头部字符，比如最开始时候，可能第一部分就是一个字符
				getIpAddress(input.substring(1), partsNum + 1, tmpResultStr + input.substring(0, 1) + ".", resultList);
			}

			// 第二种可能，这个端是2个字符，且首字符不是0开始
			if (input.length() >= 2 && input.charAt(0) != '0') {
				getIpAddress(input.substring(2), partsNum + 1, tmpResultStr + input.substring(0, 2) + ".", resultList);
			}

			// 第三种可能，这个端是3个字符，且首字符不是0开始,并且值不大于255
			if (input.length() >= 3 && input.charAt(0) != '0') {
				// 截取这三个字符，计算他的整型值
				int num = Integer.parseInt(input.substring(0, 3));
				if (num <= 255) {
					getIpAddress(input.substring(3), partsNum + 1, tmpResultStr + input.substring(0, 3) + ".",
							resultList);
				}

			}
		}
	}

	/*
	 * 给定一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0
	 * ？找出所有满足条件且不重复的三元组。
	 * 
	 * 注意：答案中不可以包含重复的三元组。
	 */
	public static List<List<Integer>> threeSum(int[] num) {
		{
			List<List<Integer>> res = new ArrayList<List<Integer>>();
			if (num == null || num.length < 3)// 如果只有2个数字 或者为空 返回空
				return res;
			Arrays.sort(num);
			for (int i = 0; i < num.length - 2; i++)// 保证最后得有num.length-1 和num.length-2两个数，才可以进循环
			{
				if (i > 0 && num[i] == num[i - 1]) {// 如果要是有重复的直接跳过。
					continue;
				}
				ArrayList<ArrayList<Integer>> cur = twoSum(num, i + 1, -num[i]); // 得到当前数字可以组合出来的数字序列的子集。
				res.addAll(cur);
			}

			return res;
		}
	}

	// 注：num是有序的，在调用之前已经经过排序了
	public static ArrayList<ArrayList<Integer>> twoSum(int[] num, int start, int target) {

		ArrayList<ArrayList<Integer>> res = new ArrayList<ArrayList<Integer>>();
		if (num == null || num.length < 2)
			return res;
		int l = start;// 起始位置
		int pri = start - 1;// 当前数字
		int r = num.length - 1;// 终止位置
		while (l < r)// 采用夹逼，逼近target
		{
			if (num[l] + num[r] == target)//
			{
				ArrayList<Integer> te = new ArrayList<Integer>();// 符合的话 把三个数放入到list中
				te.add(num[pri]);
				te.add(num[l]);
				te.add(num[r]);
				res.add(te);
				int k = l + 1;// 从l的下一个开始 去除重复，牢记num是有序的数组。
				while (k < r && num[k] == num[l])
					k++;
				l = k;
				k = r - 1;// 去除R的重复
				while (k > l && num[k] == num[r])
					k--;
				r = k;
			} else if (num[l] + num[r] > target)// 夹逼，因为num是有序的
				r--;
			else
				l++;
		}

		return res;
	}

	/**
	 * 给定一个包含了一些 0 和 1的非空二维数组 grid , 一个 岛屿 是由四个方向 (水平或垂直) 的 1 (代表土地)
	 * 构成的组合。你可以假设二维矩阵的四个边缘都被水包围着。 找到给定的二维数组中最大的岛屿面积。(如果没有岛屿，则返回面积为0。)
	 * 
	 */
	public static int maxAreaOfIsland(int[][] grid) {
		int max = 0;
		for (int i = 0; i < grid.length; i++) {// 有多少航
			for (int j = 0; j < grid[0].length; j++) {// 有多少列，
				if (grid[i][j] == 1) {
					int num = deepSearch(grid, i, j);
					max = Math.max(num, max);
				}
			}
		}
		return max;
	}

	/**
	 * DFS 深度优先搜索实现的核心是借助递归，沿着某一条路径一路往下走到不能再走为止。
	 * https://www.cnblogs.com/BlueskyRedsea/p/9348338.html
	 */
	public static int deepSearch(int[][] grid, int i, int j) {
		if (i >= 0 && i < grid.length && j >= 0 && j < grid[0].length && grid[i][j] == 1) {
			grid[i][j] = 0;
			int num = 1 + deepSearch(grid, i - 1, j) + deepSearch(grid, i + 1, j) + deepSearch(grid, i, j - 1)
					+ deepSearch(grid, i, j + 1);
			return num;
		} else
			return 0;
	}

	/**
	 * TODO DFS:广度优先搜索实现的核心是借助栈来进行层层遍历。
	 */

	/**
	 * 搜索旋转排序数组 假设按照升序排序的数组在预先未知的某个点上进行了旋转。
	 * 
	 * ( 例如，数组 [0,1,2,4,5,6,7] 可能变为 [4,5,6,7,0,1,2] )。
	 * 
	 * 搜索一个给定的目标值，如果数组中存在这个目标值，则返回它的索引，否则返回 -1 。
	 * 
	 * 你可以假设数组中不存在重复的元素。
	 * 
	 * 你的算法时间复杂度必须是 O(log n) 级别。
	 */
	public static int search(int[] nums, int target) {
		int left = 0;
		int right = nums.length - 1;
		while (left <= right) {
			int mid = left + (right - left) / 2;
			if (nums[mid] == target) {
				return mid;
			}
			if (nums[mid] >= nums[left]) {// mid 和 left在同一个递增数组
				if (nums[mid] > target && nums[left] <= target) {
					right = mid - 1;
				} else {
					left = mid + 1;
				}
			} else {// mid 和 right在同一个递增数组
				if (nums[mid] < target && nums[right] >= target) {
					left = mid + 1;
				} else {
					right = mid - 1;
				}
			}
		}
		return -1;
	}

	/**
	 * 给定一个未经排序的整数数组，找到最长且连续的的递增序列。\
	 */
	public static int findLengthOfLCIS(int[] nums) {
		if (nums.length == 0)
			return 0;
		int max = 0;
		int count = 1;
		List<Integer> maxList = new ArrayList<>();
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < nums.length - 1; i++) {
			list.add(nums[i]);
			if (nums[i] < nums[i + 1]) {
				count++;
				if (i + 1 == nums.length - 1) {
					list.add(nums[i + 1]);
				}
			} else {
				maxList = (maxList.size() > list.size()) ? maxList : list;
				max = Math.max(count, max);
				count = 1;
				list = new ArrayList<Integer>();
			}
		}
		max = Math.max(count, max);
		maxList = (maxList.size() > list.size()) ? maxList : list;
		System.out.println(maxList);// 如果有多个max长度的升序子串，只能得到最后一个
		return max;
	}

	/**
	 * 实现int sqrt(int x)函数。 计算并返回x的平方根，其中x 是非负整数。 由于返回类型是整数，结果只保留整数的部分，小数部分将被舍去。
	 */
	public static int mySqrt(int x) {// 二分法
		if (x <= 1)
			return x;
		int left = 1, right = x;
		while (left <= right) {
			int mid = left + (right - left) / 2;
			int sqrt = x / mid;
			if (sqrt == mid)
				return mid;
			else if (sqrt < mid)
				right = mid - 1;
			else
				left = mid + 1;
		}
		return right;
	}

	public static int mySqrt2(int x) {
		long r = x;
		while (r * r > x)
			r = (r + x / r) / 2;
		return (int) r;
	}

	/**
	 * 给定一个未排序的整数数组，找出最长连续序列的长度。 要求算法的时间复杂度为 O(n)。
	 */
	public int longestConsecutive(int[] num) {
		if (num == null || num.length == 0)
			return 0;
		int max = 1;
		Map<Integer, Integer> map = new HashMap<>();
		for (int i = 0; i < num.length; i++) {
			if (!map.containsKey(num[i])) {
				map.put(num[i], 1);
				if (map.containsKey(num[i] - 1)) {
					max = Math.max(max, merge(map, num[i] - 1, num[i]));
				}
				if (map.containsKey(num[i] + 1)) {
					max = Math.max(max, merge(map, num[i], num[i] + 1));
				}
			}
		}
		return max;
	}

	public static int merge(Map<Integer, Integer> map, int less, int more) {
		int left = less - map.get(less) + 1;
		int right = more + map.get(more) - 1;
		int len = right - left + 1;
		map.put(left, len);
		map.put(right, len);

		return len;
	}

	/**
	 * 给出集合 [1,2,3,…,n]，其所有元素共有 n! 种排列。
	 * 
	 * 按大小顺序列出所有排列情况，并一一标记，当 n = 3 时, 所有排列如下：
	 * 
	 * "123" "132" "213" "231" "312" "321" 给定 n 和 k，返回第 k 个排列。
	 * 
	 * 说明：给定 n 的范围是 [1, 9]。 给定 k 的范围是[1, n!]。
	 * 
	 * 大致思路是根据k与总排列数(n!)的关系来推断出第k个排列，而不用依次求出排列直到第k个。
	 * 以每个数字开头的排列都有(n-1)!个，k/(n-1)!即可以求出字符串index为n-k-1处的字符。
	 * （注意字符串的index并非n-k，需要在开头加k--），以此类推更新k为除以(n-1)!所得的余数，更新n为n-1，再求下一位，直到n为1时终结。
	 * 如果使用递归则思路简单但很容易超时，基本也就是尾递归，用循环代替即可。
	 */
	public String getPermutation(int n, int k) {
		k--;
		List<Integer> nums = new ArrayList<>();
		for (int i = 1; i <= n; i++)
			nums.add(i);
		int fac = 1;
		for (int i = 2; i < n; i++)
			fac *= i;// 阶乘
		int count = n - 1;
		StringBuilder res = new StringBuilder();
		while (count >= 0) {
			res.append(nums.remove(k / fac));
			k %= fac;
			if (count > 0)
				fac /= count;
			count--;
		}
		return res.toString();
	}

	/**
	 * 班上有 N 名学生。其中有些人是朋友，有些则不是。他们的友谊具有是传递性。如果已知 A 是 B 的朋友，B 是 C 的朋友，那么我们可以认为 A 也是 C
	 * 的朋友。所谓的朋友圈，是指所有朋友的集合。 给定一个 N * N 的矩阵 M，表示班级中学生之间的朋友关系。如果M[i][j] = 1，表示已知第 i
	 * 个和 j 个学生互为朋友关系，否则为不知道。你必须输出所有学生中的已知的朋友圈总数。
	 * 
	 * 法一思路： 遍历所有人，对于每一个人，寻找他的好友，找到好友后再找这个好友的好友，这样深度优先遍历下去， 设置一个visited记录是否已经遍历了这个人。
	 * 因为如果m个人最多m个朋友圈，设置后visited后， 相同的朋友圈会检测到visited[i]!=0就会不算数
	 */
	public int findCircleNum(int[][] M) {
		int res = 0;
		int[] visited = new int[M.length];
		for (int i = 0; i < M.length; i++) {
			if (visited[i] == 0) {
				// 没有访问到，就把当前的组+1，并把可以包含在朋友圈的所有的有关系的好友标记出来
				res++;
				dfs(M, visited, i);
			}
		}
		return res;
	}

	private void dfs(int[][] M, int[] visited, int i) {
		visited[i] = 1;
		for (int j = 0; j < M.length; j++) {
			if (M[i][j] == 1 && visited[j] == 0) {
				dfs(M, visited, j);// 再去找这个好友的好友
			}
		}
	}

	/**
	 * 并查集算法求朋友圈个数
	 */
	public static int findCircleNum2(int[][] source) {
		if (source == null || source.length == 0) {
			return 0;
		}
		int m = source.length;
		UnionFind unionFind = new UnionFind(m);
		for (int i = 0; i < m; i++) {
			// 此前的元素，已在此前
			for (int j = i + 1; j < m; j++) {
				if (source[i][j] == 1) {
					unionFind.union(i, j);
				}
			}
		}
		return unionFind.count;
	}

	/**
	 * 并查集 判断节点联通性
	 */
	public static class UnionFind {
		// 不同树的个数
		private int count;
		private int[] root;// 联通的根节点

		public UnionFind(int m) {
			root = new int[m];// 比如三行三列，最多也是3个人，最多也是3个朋友圈
			for (int i = 0; i < m; i++) {
				root[i] = i;// 便于构建和查询，起始值为自己的索引值
			}
			count = m;// 起始值为，每个人一个朋友圈
		}

		public void union(int i, int j) {
			int x = find(i);// i所属的树或者组
			int y = find(j);
			if (x != y) {
				count--;// 如果此前不属于同一个树（组），则建立关系，归为一组；
				root[x] = y;// path compression
			}
		}

		private int find(int i) {
			if (root[i] == i) {
				return i;
			}
			return find(root[i]);// 压缩路径，同一组，都指向跟索引节点
		}
	}

	// 并查集算法
	private int find(int x, int[] pre) {//// 找到x属于哪一个组,如果不是自成一组，在往下找pre[x]属于哪个组
		return pre[x] == x ? x : find(pre[x], pre);
	}

	public int findCircleNum3(int[][] M) {
		if (M.length == 0)
			return 0;
		int pre[] = new int[M.length];
		for (int i = 0; i < M.length; i++)
			pre[i] = i;// 先各自为组，组名也为自己的序号
		int group = M.length;// 一开始有多少人就有多少个朋友圈，当每出现一对朋友时就减1，最后就是总的朋友圈数量了。
		for (int i = 0; i < M.length; i++) {
			for (int j = 0; j < M.length; j++) {
				if (i != j && M[i][j] == 1) {
					int x1 = find(i, pre);// x1为i所属的组
					int x2 = find(j, pre);// x2为j所属的组
					if (x1 != x2) {
						// 如果不属于同个朋友圈的话就把i归为j的组
						pre[x1] = x2;
						group--;
					}
				}
			}
		}
		return group;
	}

	/**
	 * 给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。
	 */
	public int trap(int[] height) {
		int l = 0, r = height.length - 1, level = 0, res = 0;
		while (l < r) {
			int lower = height[(height[l] < height[r]) ? l++ : r--];
			level = Math.max(level, lower);
			res += level - lower;
		}
		return res;
	}

	public int trap2(int[] height) {
		int n = height.length;
		int result = 0;
		if (n == 0 || n == 1) {
			return result;
		}
		int left = 0;
		int right = n - 1;
		int leftHeight = 0;
		int rightHeight = 0;
		while (left < right) {
			if (height[left] <= height[right]) {
				leftHeight = Math.max(leftHeight, height[left]);
				result += leftHeight - height[left];
				left++;
			} else {
				rightHeight = Math.max(rightHeight, height[right]);
				result += rightHeight - height[right];
				right--;
			}
		}
		return result;
	}

	static class ListNode {
		int val;
		ListNode next;

		ListNode(int x) {
			val = x;
		}
	}

	public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
		if (l1 == null)
			return l2;
		if (l2 == null)
			return l1;
		if (l1.val < l2.val) {
			l1.next = mergeTwoLists(l1.next, l2);
			return l1;
		} else {
			l2.next = mergeTwoLists(l1, l2.next);
			return l2;
		}
	}

	public static ListNode reverseList(ListNode head) {
		if (head == null) {
			return null;
		}
		ListNode pre = null;
		ListNode next = null;
		while (head != null) {
			next = head.next;
			head.next = pre;
			pre = head;
			head = next;
		}
		return pre;

	}

	public static ListNode reverse(ListNode head) {
		if (head == null) {
			return null;
		} else {
			return reverse(head, null);
		}
	}

	public static ListNode reverse(ListNode cur, ListNode prev) {
		if (cur == null) {
			return prev;
		}
		ListNode curNext = cur.next;
		cur.next = prev;
		prev = cur;
		return reverse(curNext, prev);
	}

	/**
	 * 两个逆序存在链表中的数相加
	 */
	public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
		if (l1 == null || l2 == null) {
			return l1 == null ? l2 : l1;
		}
		int value = l1.val + l2.val;
		ListNode result = new ListNode(value % 10);
		if (value >= 10) {
			if (l1.next == null) {
				l1.next = new ListNode(value / 10);
			} else {
				l1.next.val += value / 10;
			}
		}
		result.next = addTwoNumbers(l1.next, l2.next);
		return result;
	}

	/**
	 * 链表排序，要求时间复杂度为O(n*logn)
	 */
	public ListNode sortList(ListNode head) {
		if (head == null) {
			return head;
		}
		List<ListNode> list = new LinkedList<ListNode>();

		ListNode node = head;
		while (node != null) {
			list.add(node);
			node = node.next;
		}

		list.sort(new Comparator<ListNode>() {

			@Override
			public int compare(ListNode o1, ListNode o2) {
				return o1.val > o2.val ? 1 : (o1.val == o2.val ? 0 : -1);
			}
		});
		head = new ListNode(0);
		ListNode r = head;
		for (ListNode n : list) {
			System.out.print(n.val + " ");
			r.next = n;
			r = n;
		}
		r.next = null;

		return head.next;
	}

	/**
	 * 给定一个链表，返回链表开始入环的第一个节点。 如果链表无环，则返回 null。 为了表示给定链表中的环，我们使用整数 pos
	 * 来表示链表尾连接到链表中的位置（索引从 0 开始）。 如果 pos 是 -1，则在该链表中没有环。 说明：不允许修改给定的链表。
	 */
	public ListNode detectCycle(ListNode head) {
		ListNode fast = head;
		ListNode slow = head;
		while (fast != null && fast.next != null) {
			fast = fast.next.next;
			slow = slow.next;
			// 每一次发生变化，这里都需要进行判断
			if (fast == slow) {
				break;
			}
		}
		if (fast == null || fast.next == null) {
			return null;
		}
		slow = head;
		while (slow != fast) {
			slow = slow.next;
			fast = fast.next;
		}
		return slow;
	}

	public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
		// 比较两个链表的长度，较长的head先行至与较短的链表长度相同的位置。
		if (headA == null || headB == null) {
			return null;
		}
		ListNode tempNode = headA;
		int sizeA = 1;
		while (tempNode != null) {
			sizeA++;
			tempNode = tempNode.next;
		}
		tempNode = headB;
		int sizeB = 1;
		while (tempNode != null) {
			sizeB++;
			tempNode = tempNode.next;
		}
		if (sizeA > sizeB) {
			for (int i = 0; i < sizeA - sizeB; i++) {
				headA = headA.next;
			}
		} else if (sizeA < sizeB) {
			for (int i = 0; i < sizeB - sizeA; i++) {
				headB = headB.next;
			}
		}
		while (headA != null && headB != null) {
			if (headA.val == headB.val) {// 不是很懂题目，没有输入intersectVal的地方
				break;
			}
			headA = headA.next;
			headB = headB.next;
		}
		return headA;
	}

	/**
	 * 合并 k 个排序链表，返回合并后的排序链表。请分析和描述算法的复杂度。
	 * 两两合并链表，按顺序合并显然太过麻烦。于是，让第i个和i+m比较（m指的是链表长度的一半），
	 * m=（lists.length+1）/2。**为什么加1？**为了当链表个数为奇数之时，可以将中间那条链表掠过，
	 * 比如有a，b，c，d，e共5个链表，那么m=3，会将c那条链表掠过，a和d合并，b和e合并。
	 */
	public ListNode mergeKLists1(ListNode[] lists) {

		int n = lists.length;
		if (n == 0)
			return null;
		while (n > 1) {
			int m = (n + 1) / 2;
			for (int i = 0; i < n / 2; i++)// 是n/2
			{
				lists[i] = compare(lists[i], lists[i + m]);
			}
			n = m;
		}
		return lists[0];
	}

	ListNode compare(ListNode x, ListNode y) {
		ListNode list = new ListNode(0);
		ListNode list2 = list;
		while (x != null && y != null) {
			if (x.val > y.val) {
				list.next = y;
				y = y.next;
			} else {
				list.next = x;
				x = x.next;
			}
			list = list.next;
		}
		if (y == null) {
			list.next = x;
		} else {
			list.next = y;
		}
		return list2.next;
	}

	/**
	 * 时间复杂度也为 O(nlog(k)) O(n\log(k))O(nlog(k))，k 为链表个数，n 为总的结点数，空间复杂度为 O(1)。
	 * 时间复杂度分析：两两归并，每个结点会被归并 log(k) 次，所以总的时间复杂度为 O(nlog(k))
	 */
	// 借助归并排序的思想，只有治没有分
	public ListNode merge(ListNode l1, ListNode l2) {
		ListNode res = new ListNode(0);
		ListNode tail = res;
		while (l1 != null && l2 != null) {
			if (l1.val < l2.val) {
				tail.next = l1;
				l1 = l1.next;
			} else {
				tail.next = l2;
				l2 = l2.next;
			}
			tail = tail.next;
		}
		if (l1 != null) {
			tail.next = l1;
		} else {
			tail.next = l2;
		}
		return res.next;
	}

	// 原地归并，并不申请新的数组空间，算法实现上，其实是找规律。
	public ListNode mergeKLists(ListNode[] lists) {
		// 步长为 2 时，和后面的第 1 个合并
		// 步长为 4 时，和后面的第 2 个合并
		// ...
		if (lists == null) {
			return null;
		}
		int len = lists.length;
		int interval = 1;
		while (interval < len) {
			for (int i = 0; i + interval < len; i += 2 * interval) {
				lists[i] = merge(lists[i], lists[i + interval]);
			}
			interval *= 2;
		}
		return len != 0 ? lists[0] : null;
	}

	/**
	 * 给定一个二叉树, 找到该树中两个指定节点的最近公共祖先。
	 * 
	 * 百度百科中最近公共祖先的定义为：“对于有根树 T 的两个结点 p、q，最近公共祖先表示为一个结点 x，满足 x 是 p、q 的祖先且 x 的深度尽可能大
	 * （一个节点也可以是它自己的祖先）。”
	 * 二叉树的查找问题一般都是从左右子树递归去解决，也往往都能得到答案，因此，这道题可以考虑是否能从左右子树进行递归去解决呢？
	 * 首先，要想通过递归来实现，就需要先确定临界条件，那么临界条件是什么呢？换句话说，临界条件就是递归中能够直接返回的特殊情况，第一点则是最常见的“判空”，判断根结点是否是空节点，如果是，那么肯定就可以马上返回了，这是一个临界条件；再来考虑题意，在以root为根结点的树中找到p结点和q结点的最近公共祖先，那么特殊情况是什么呢？很显然，特殊情况就是根结点就等于q结点或p结点的情况，想一下，如果根结点为二者之一，那么根结点就必定是最近公共祖先了，这时直接返回root即可。由此看来，这道题就一共有三种特殊情况，root
	 * == q 、root == p和root==null，这三种情况均直接返回root即可。
	 * 根据临界条件，实际上可以发现这道题已经被简化为查找以root为根结点的树上是否有p结点或者q结点，如果有就返回p结点或q结点，否则返回null。
	 * 这样一来其实就很简单了，从左右子树分别进行递归，即查找左右子树上是否有p结点或者q结点，就一共有4种情况：
	 * 第一种情况：左子树和右子树均找没有p结点或者q结点；（这里特别需要注意，虽然题目上说了p结点和q结点必定都存在，但是递归的时候必须把所有情况都考虑进去，因为题目给的条件是针对于整棵树，而递归会到局部，不一定都满足整体条件）
	 * 第二种情况：左子树上能找到，但是右子树上找不到，此时就应当直接返回左子树的查找结果；
	 * 第三种情况：右子树上能找到，但是左子树上找不到，此时就应当直接返回右子树的查找结果；
	 * 第四种情况：左右子树上均能找到，说明此时的p结点和q结点分居root结点两侧，此时就应当直接返回root结点了。
	 */
	class TreeNode {
		int val;
		TreeNode left;
		TreeNode right;

		TreeNode(int x) {
			val = x;
		}
	}

	public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
		if (root == null || root == p || root == q) {
			return root;
		}
		TreeNode left = lowestCommonAncestor(root.left, p, q);
		TreeNode right = lowestCommonAncestor(root.right, p, q);
		if (left != null && right != null) {
			return root;
		}
		return left == null ? right : left;
	}

	/**
	 * 给定一个二叉树，返回其节点值的锯齿形层次遍历。（即先从左往右，再从右往左进行下一层遍历，以此类推，层与层之间交替进行）。
	 * 层次遍历，应该很容易想到BFS（宽度优先搜索算法），此处是锯齿形，即一层是从左往右，下一层就是从右往左。
	 * 
	 * 解决办法是每一层都按照从左往右遍历，只是如果是偶数层的话，当遍历完该层后执行一层逆序即可。
	 * 
	 * 这里有一个很好用的集合方法，快速逆序集合： Collections.reverse(line);
	 */
	public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
		List<List<Integer>> ans = new ArrayList<>();
		List<Integer> line = new ArrayList<>();

		if (root == null)
			return ans;
		// 树的层次遍历用队列
		Queue<TreeNode> queue = new LinkedList<>();
		queue.add(root);

		line.add(root.val);
		ans.add(line);// 处理根节点
		line = new ArrayList<>();

		int size = queue.size();
		int flag = 1;
		while (!queue.isEmpty()) {
			TreeNode tmp = queue.poll();
			if (tmp.left != null) {
				queue.add(tmp.left);
				line.add(tmp.left.val);
			}
			if (tmp.right != null) {
				queue.add(tmp.right);
				line.add(tmp.right.val);
			}
			size--;
			if (size == 0 && line.size() > 0) {
				if (flag == 1) {// 偶数行要反转序列
					Collections.reverse(line);
					flag = 0;
				} else {
					flag = 1;
				}
				size = queue.size();
				ans.add(line);// 一层结束，开始下一层
				line = new ArrayList<>();
			}
		}
		return ans;
	}

	/**
	 * 给定一个数组，它的第 i 个元素是一支给定股票第 i 天的价格。
	 * 如果你最多只允许完成一笔交易（即买入和卖出一支股票），设计一个算法来计算你所能获取的最大利润。 注意你不能在买入股票前卖出股票。
	 * 
	 */
	public static int maxProfit(int[] prices) {
		if (prices == null || prices.length == 0) {
			return 0;
		}
		int[] dp = new int[prices.length];
		int minPrice = prices[0];
		for (int i = 1; i < prices.length; i++) {
			minPrice = Math.min(minPrice, prices[i]);
			dp[i] = Math.max(dp[i - 1], prices[i] - minPrice);// 只需记住当前的最小值即可
		}
		return dp[prices.length - 1];
	}

	public static int maxProfit2(int[] prices) {
		int len = prices.length;
		// 边界情况
		if (len == 0) {
			return 0;
		}
		int max_profit = 0;
		int min_price = prices[0];
		for (int i = 1; i < len; i++) {
			min_price = Math.min(min_price, prices[i]);
			max_profit = Math.max(max_profit, prices[i] - min_price);
		}
		return max_profit;
	}

	/**
	 * 给定一个数组，它的第 i 个元素是一支给定股票第 i 天的价格。
	 * 设计一个算法来计算你所能获取的最大利润。你可以尽可能地完成更多的交易（多次买卖一支股票）。
	 * 注意：你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。
	 * 
	 * 思路： 首先当数组的长度为0或者1以下的时候，我们所买的股票价格为不买股票且没赚头：所有return 0
	 * 在第二步使用，所有只要拥有后面值比前面值大的数值都将他们的差累加起来进行最终累加。
	 */
	public int maxProfitII(int[] prices) {
		int size = prices.length;
		if (size <= 1) {
			return 0;
		} else {
			int max = 0;
			for (int i = 1; i < size; i++) {
				if (prices[i] > prices[i - 1]) {
					max += (prices[i] - prices[i - 1]);
				}
			}
			return max;
		}
	}

	/**
	 * 在一个由 0 和 1 组成的二维矩阵内，找到只包含 1 的最大正方形，并返回其面积。
	 */
	/**
	 * 动态规划方式实现 动态规划问题我使用一个和0 1矩阵相同大小的矩阵存储以该点为右下角的正方形的最大边长，最后返回最大边长的平方。 主要思想：
	 * 1.dp[i][j]表示的就是以(i,j)为结尾的最大正方形的边长
	 * 2.对于（i+1，j+1），分别向左和向上查找1的数量（1的数量小于等于dp[i][j]，超过了没有意义，根本组成不了正方形）
	 * 3.dp[i+1][j+1]=min(左边1的长度，上边1的长度）
	 */
	public int maximalSquareWithDp(char[][] matrix) {
		if (matrix.length == 0 || matrix[0].length == 0)
			return 0;
		int i, j, h = matrix.length, l = matrix[0].length, ans = 0;
		int dp[][] = new int[h][l];
		for (j = 0; j < l; j++) {
			dp[0][j] = matrix[0][j] - '0';
			if (dp[0][j] == 1)
				ans = 1;
		}

		for (i = 0; i < h; i++) {
			dp[i][0] = matrix[i][0] - '0';
			if (dp[i][0] == 1)
				ans = 1;
		}

		for (i = 1; i < h; i++) {
			for (j = 1; j < l; j++) {
				if (matrix[i][j] == '1') {
					if (matrix[i - 1][j - 1] == '0')
						dp[i][j] = 1;
					else {
						int heng = 1, zong = 1;
						while (heng <= dp[i - 1][j - 1] && matrix[i - heng][j] == '1')
							heng++;
						while (zong <= dp[i - 1][j - 1] && matrix[i][j - zong] == '1')
							zong++;
						dp[i][j] = Math.min(heng, zong);
					}
					ans = Math.max(dp[i][j], ans);
				}
			}
		}
		return ans * ans;
	}

	// 非动态规划实现
	public int maximalSquare(char[][] matrix) {
		int res = 0;
		int max = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] != '1')
					continue;
				res = 1;
				int remain = Math.min(matrix.length - i, matrix[i].length - j);
				for (int k = 1; k < remain; k++) {
					int flag = 1;
					for (int i1 = i; i1 <= i + k; i1++) {
						if (matrix[i1][j + k] != '1') {
							flag = 0;
							break;
						}
					}
					if (flag == 0)
						break;
					for (int j1 = j; j1 <= j + k; j1++) {
						if (matrix[i + k][j1] != '1') {
							flag = 0;
							break;
						}
					}
					if (flag == 0)
						break;
					res++;
				}
				if (res > max)
					max = res;
			}
		}
		return max * max;
	}

	/**
	 * 给定一个整数数组 nums ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。 如果你已经实现复杂度为 O(n)
	 * 的解法，尝试使用更为精妙的分治法求解。
	 */
	public int maxSubArray(int[] nums) {
		if (nums.length == 0)
			return -1;
		int sum = nums[0];
		int maxsum = nums[0];
		for (int i = 1; i < nums.length; ++i) {
			if (sum > 0)
				sum = sum + nums[i];
			else
				sum = nums[i];
			if (maxsum < sum)
				maxsum = sum;
		}
		return maxsum;
	}

	/**
	 * 给定一个三角形，找出自顶向下的最小路径和。每一步只能移动到下一行中相邻的结点上。 如果你可以只使用 O(n) 的额外空间（n
	 * 为三角形的总行数）来解决这个问题，那么你的算法会很加分。
	 */
	public int minimumTotalN(List<List<Integer>> triangle) {
		if (triangle.size() == 0) {
			return 0;
		}

		int[] dp = new int[triangle.size()];
		for (int i = 0; i < triangle.size(); i++) {// 行数0～n-1
			dp[i] = triangle.get(triangle.size() - 1).get(i);// 最后一行的数据（第i行就有i个数据）
		}

		for (int i = triangle.size() - 2; i >= 0; i--) {
			for (int j = 0; j <= i; j++) {
				dp[j] = triangle.get(i).get(j) + Math.min(dp[j], dp[j + 1]);
			}
		}

		return dp[0];
	}

	public int minimumTotal(List<List<Integer>> triangle) {
		int n = triangle.size();
		int[][] dp = new int[n + 1][n + 1];// 防止下标越界---这个不符合O(n)的额外空间要求
		for (int i = n - 1; i >= 0; i--)// 思路很简单，自底向上
		{
			for (int j = 0; j < triangle.get(i).size(); j++) {
				dp[i][j] = Math.min(dp[i + 1][j], dp[i + 1][j + 1]) + triangle.get(i).get(j);// 递推公式还是很明显的，就是list里的元素
			}
		}
		return dp[0][0];
	}

	/**
	 * 给定一些标记了宽度和高度的信封，宽度和高度以整数对形式 (w, h)
	 * 出现。当另一个信封的宽度和高度都比这个信封大的时候，这个信封就可以放进另一个信封里，如同俄罗斯套娃一样。
	 * 
	 * 请计算最多能有多少个信封能组成一组“俄罗斯套娃”信封（即可以把一个信封放到另一个信封里面）。
	 * 
	 * 说明: 不允许旋转信封。
	 * 
	 * @return
	 */
	public int maxEnvelopes(int[][] envelopes) {
		if (envelopes.length == 0)
			return 0;
		if (envelopes.length == 1)
			return 1;

		Arrays.sort(envelopes, new Comparator<int[]>() {
			public int compare(int[] a, int[] b) {
				if (a[0] != b[0]) {
					return a[0] - b[0];
				} else {
					return a[1] - b[1];
				}
			}
		});
		int[] dp = new int[envelopes.length];
		int max = 0;
		for (int i = 0; i < envelopes.length; i++) {
			dp[i] = 1;
			for (int j = i - 1; j >= 0; j--) {
				if (envelopes[i][0] > envelopes[j][0] && envelopes[i][1] > envelopes[j][1]) {
					dp[i] = Math.max(dp[i], dp[j] + 1);
				}
			}
			max = Math.max(max, dp[i]);

		}
		return max;
	}

	/**
	 * 设计最小栈 在入栈时，如果这个元素小于等于最小值，那么，我们让最小值入栈，然后将元素值赋给最小值，即新的最小值，然后正常将这个元素入栈；
	 * 在出栈时，如果栈顶元素与最小值相等，说明他的下一个元素是之前push的最小值（上一个），出栈后，将这个之前的最小值赋值给最新的最小值。
	 * （每次push进去的最小值都是下面元素的最小值）
	 */
	class MinStack {

		Stack<Integer> stack = new Stack<>();

		int min = Integer.MAX_VALUE;

		/** initialize your data structure here. */
		public MinStack() {

		}

		public void push(int x) {
			if (x <= min) {
				stack.push(min);
				min = x;
			}
			stack.push(x);
		}

		public void pop() {
			Integer result = stack.pop();
			if (result == min) {
				min = stack.pop();
			}
		}

		public int top() {
			return stack.peek();
		}

		public int getMin() {
			return min;
		}

	}

	/**
	 * 运用你所掌握的数据结构，设计和实现一个 LRU (最近最少使用) 缓存机制。它应该支持以下操作： 获取数据 get 和 写入数据 put 。
	 * 
	 * 获取数据 get(key) - 如果密钥 (key) 存在于缓存中，则获取密钥的值（总是正数），否则返回 -1。 写入数据 put(key, value)
	 * - 如果密钥不存在，则写入其数据值。当缓存容量达到上限时，它应该在写入新数据之前删除最近最少使用的数据值，从而为新的数据值留出空间。
	 * 
	 * 进阶: 你是否可以在 O(1) 时间复杂度内完成这两种操作？ 思路： ​
	 * 由题目中要求的O(1)时间复杂度想到缓存可以想到用一个map来存储key、value结点，题目最近最少使用到的（缓存）放到最后，最新访问的（缓存）放到最前面，
	 * 可以考虑用双端队列来实现，这样，这个map的key对应的是缓存的Key,
	 * value对应的是双端队列的一个节点，即队列的节点同时存在map的value中。​
	 * 这样，当新插入一个节点时，它应该在这个双端队列的队头处，同时把这个节点的key和这个节点put到map中保留下来。当LRU缓存队列容量达到最大又要插入新元素时，
	 * 把队列的最后一个节点删除掉，同时在map中移除该节点对应的key。这个双端队列的数据结构如下：
	 */
	class DLinkedList {
		int key;
		int value;
		DLinkedList pre;
		DLinkedList next;
	}

	class LRUCache {

		private DLinkedList head;
		private DLinkedList tail;
		private Map<Integer, DLinkedList> cache;
		private int count;
		private int capacity;

		public LRUCache(int capacity) {
			count = 0;
			this.capacity = capacity;
			cache = new HashMap<Integer, DLinkedList>();
			head = new DLinkedList();
			tail = new DLinkedList();
			head.pre = null;
			head.next = tail;
			tail.next = null;
			tail.pre = head;
		}

		public void add(DLinkedList node) {
			node.pre = head;
			node.next = head.next;
			head.next.pre = node;
			head.next = node;
		}

		public void remove(DLinkedList node) {
			DLinkedList pre = node.pre;
			DLinkedList next = node.next;
			pre.next = next;
			next.pre = pre;
		}

		public void moveToHead(DLinkedList node) {
			remove(node);
			add(node);
		}

		// 删除队尾元素
		public DLinkedList popTail() {
			DLinkedList res = tail.pre;
			remove(res);
			return res;
		}

		public int get(int key) {
			DLinkedList node = cache.get(key);
			if (node == null) {
				return -1;
			}
			moveToHead(node);
			return node.value;
		}

		public void put(int key, int value) {
			DLinkedList node = cache.get(key);
			if (node != null) {
				node.value = value;
				moveToHead(node);
				return;
			}
			// 如果队列数量大于容量，删掉队尾最近最少使用的那个元素
			if (++count > capacity) {
				DLinkedList delNode = popTail();
				cache.remove(delNode.key);
			}
			node = new DLinkedList();
			node.key = key;
			node.value = value;
			add(node);
			cache.put(key, node);
		}
	}

	/**
	 * 全 O(1) 的数据结构 实现一个数据结构支持以下操作： Inc(key) - 插入一个新的值为 1 的 key。或者使一个存在的 key 增加一，保证
	 * key 不为空字符串。 Dec(key) - 如果这个 key 的值是 1，那么把他从数据结构中移除掉。否者使一个存在的 key 值减一。如果这个 key
	 * 不存在，这个函数不做任何事情。key 保证不为空字符串。 GetMaxKey() - 返回 key
	 * 中值最大的任意一个。如果没有元素存在，返回一个空字符串""。 GetMinKey() - 返回 key
	 * 中值最小的任意一个。如果没有元素存在，返回一个空字符串""。 挑战：以 O(1) 的时间复杂度实现所有操作。
	 * 
	 * 解题思路：实现O(1)的时间复杂度，首先想到哈希，方法和题目146.LRU缓存机制类似，使用哈希+双向链表，本题的关键元素有3个，key，value和节点位置，
	 * 因此需要维护2张哈希表（分别保存key-value的map和value-node的map）和1个双向链表（升序）。
	 */
	class DListNode{
		int value;
		HashSet<String> keySet;
		DListNode pre;
		DListNode next;
		DListNode(){
		    keySet = new HashSet<>();
		}
		}
		class AllOne {
		    HashMap<String,Integer> keyMap = new HashMap<>();
		HashMap<Integer,DListNode> valueMap = new HashMap<>();
		DListNode head,tail;

		/** Initialize your data structure here. */
		public AllOne() {
		    head = new DListNode();
		    tail = new DListNode();
		    head.next = tail;
		    tail.pre = head;
		}

		/** Insert new node in the order list. */
		public void insertNode(String key,int curvalue,int dir){
		    DListNode newnode = new DListNode();
		    valueMap.put(curvalue + dir, newnode);
		    newnode.keySet.add(key);
		    if(curvalue == 0){
		        newnode.next = head.next;
		        head.next.pre = newnode;
		        newnode.pre = head;
		        head.next = newnode;
		        return;
		    }

		    DListNode curnode = valueMap.get(curvalue);
		    if(dir == 1) {
		        newnode.next = curnode.next;
		        curnode.next.pre = newnode;
		        newnode.pre = curnode;
		        curnode.next = newnode;
		    } else if(dir == -1) {
		        newnode.pre = curnode.pre;
		        newnode.next = curnode;
		        curnode.pre.next = newnode;
		        curnode.pre = newnode;
		    }
		}

		/** Remove the empty node of the order list. */
		public void removeNode(int curvalue){
		    DListNode curnode = valueMap.get(curvalue);
		    if(curnode.keySet.isEmpty()){
		        valueMap.remove(curvalue);
		        curnode.next.pre = curnode.pre;
		        curnode.pre.next = curnode.next;
		        curnode.next = null;
		        curnode.pre = null;
		    }
		}

		/** Inserts a new key <Key> with value 1. Or increments an existing key by 1. */
		public void inc(String key) {
		    if(keyMap.containsKey(key)) {
		        int curvalue = keyMap.get(key);
		        valueMap.get(curvalue).keySet.remove(key);
		        keyMap.replace(key,curvalue + 1);
		        if(valueMap.containsKey(curvalue + 1)) {
		            valueMap.get(curvalue + 1).keySet.add(key);
		        } else {
		            insertNode(key,curvalue,1);
		        }
		        removeNode(curvalue);
		    } else {
		        keyMap.put(key,1);
		        if(valueMap.containsKey(1)) {
		            valueMap.get(1).keySet.add(key);
		        } else {
		            insertNode(key,0,1);
		        }
		    }
		}

		/** Decrements an existing key by 1. If Key's value is 1, remove it from the data structure. */
		public void dec(String key) {
		    if(keyMap.containsKey(key)) {
		        int curvalue = keyMap.get(key);
		        DListNode curnode = valueMap.get(curvalue);
		        curnode.keySet.remove(key);
		        if(curvalue == 1){
		            keyMap.remove(key);
		            removeNode(curvalue);
		            return;
		        }
		        keyMap.replace(key,curvalue - 1);
		        if(valueMap.containsKey(curvalue - 1)) {
		            valueMap.get(curvalue - 1).keySet.add(key);
		        } else {
		            insertNode(key,curvalue,- 1);
		        }
		        removeNode(curvalue);
		    }
		}

		/** Returns one of the keys with maximal value. */
		public String getMaxKey() {
		    return (tail.pre == head)?"":tail.pre.keySet.iterator().next();
		}

		/** Returns one of the keys with Minimal value. */
		public String getMinKey() {
		    return (head.next == tail)?"":head.next.keySet.iterator().next();
		}
		}


	public static void main(String[] args) {
//		List<String> list = restoreIpAddresses("25525511135");
//		int[] nums = { 1, 3, 5, 4, 7, 6, 8, 9 };
//		List<List<Integer>> list = threeSum(nums);
//		System.out.println(list);
//		System.out.println(findLengthOfLCIS(nums));
//		System.out.println(mySqrt(35));
//		ListNode head = new ListNode(2);
//		ListNode next = new ListNode(4);
//		head.next = next;
//		ListNode next2 = new ListNode(3);
//		next.next = next2;
//		next2.next = null;
//
//		ListNode head2 = new ListNode(5);
//		ListNode next21 = new ListNode(6);
//		head2.next = next21;
//		ListNode next22 = new ListNode(4);
//		next21.next = next22;
//		next22.next = null;
////		reverseList(head);
////		reverse(head);
//		ListNode result = addTwoNumbers(head, head2);
		int[] prices = { 7, 1, 5, 3, 6, 4 };
		System.out.println(maxProfit2(prices));
		System.out.println("sucess");
	}

}
