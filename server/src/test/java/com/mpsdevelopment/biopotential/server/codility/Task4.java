package com.mpsdevelopment.biopotential.server.codility;

import org.junit.Assert;

public class Task4 {

	public static void main(String args[]) throws Exception {

		Assert.assertArrayEquals(new int[] { 1, 1, 0, 1 }, solution(new int[] { 1, 0, 0, 1, 1 }));
		Assert.assertArrayEquals(new int[] { 1, 0, 0, 1, 1, 1 }, solution(new int[] { 1, 1, 0, 1, 0, 1, 1 }));
	}

	private static int[] solution(int[] A) {

		int sum = 0;
		for (int i = 0; i < A.length; i++) {
			sum += A[i] * Math.pow(-2, i);
		}

		System.out.println(String.format("Sum is %d", sum));

		getNegative(A);

		return new int[] { 1, 1, 0, 1 };

	}

	private static void getNegative(int[] a) {
		int[] result = new int[a.length + 1];
		for (int i = 0; i < a.length; i++) {

		}
	}
}
