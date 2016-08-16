package com.mpsdevelopment.biopotential.server.codility;

public class Task3 {

	public static void main(String args[]) throws Exception {
		int A[] = { 5, 5, 1, 7, 2, 3, 5 };

		System.out.println(String.format("For 5 result is %d", solution(A, 5)));
		System.out.println(String.format("For 5 result is %d", solution(A, 0)));
	}

	private static int solution(int[] A, int k) {
		System.out.println(String.format("Left is %d", getLeft(A, k)));
		System.out.println(String.format("Right is is %d", getRight(A, k)));
		return getLeft(A, k) + getRight(A, k);

		// int totalCount = 0;
		// int count = 0;
		// for (int i = 0; i < getBitsCount(N); i++) {
		// if ((N & (1 << i)) != 0) {
		// // System.out.println(String.format("Bit %d is set", i));
		// count = 0;
		// } else {
		// count++;
		// totalCount = Math.max(totalCount, count);
		// }
		// }
		// return totalCount;
	}

	private static int getLeft(int[] a, int k) {
		int sum = 0;
		// if (k > a.length) {
		// k = a.length;
		// }
		for (int i = 0; i < k - 1; i++) {
			if (a[i] == k) {
				sum++;
			}
		}
		return sum;
	}

	private static int getRight(int[] a, int k) {
		int sum = 0;
		if (k < 1) {
			k = 1;
		}
		for (int i = k - 1; i < a.length; i++) {
			if (a[i] != k) {
				sum++;
			}
		}
		return sum;
	}

}
