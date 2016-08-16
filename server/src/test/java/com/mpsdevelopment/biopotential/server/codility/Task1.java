package com.mpsdevelopment.biopotential.server.codility;

public class Task1 {

	public static void main(String args[]) throws Exception {
		int A[] = { -1, 3, -4, 5, 1, -6, 2, 1 };

		solution(A);

		System.out.println(String.format("%d is an equilibrium", solution(A)));

		int B[] = { 0, -2147483648, -2147483648 };
		solution(B);

		System.out.println(String.format("%d is an equilibrium", solution(B)));
	}

	private static int solution(int[] A) {
		for (int a = 0; a < A.length; a++) {
			// System.out.println(String.format("For %d left is %d and right is %d", a, getLeft(A, a), getRight(A, a)));
			if (getLeft(A, a) == getRight(A, a)) {
				return a;
			}
		}
		return -1;

	}

	private static double getLeft(int[] A, int index) {
		double result = 0;
		for (int a = 0; a < index; a++) {
			result += A[a];
		}
		return result;
	}

	private static double getRight(int[] A, int index) {
		double result = 0;
		for (int a = index + 1; a < A.length; a++) {
			result += A[a];
		}
		return result;
	}

}
