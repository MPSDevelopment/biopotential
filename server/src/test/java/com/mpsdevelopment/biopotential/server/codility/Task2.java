package com.mpsdevelopment.biopotential.server.codility;

public class Task2 {

	public static void main(String args[]) throws Exception {
		solution(12);
		System.out.println(String.format("Count is %d", solution(12)));
		System.out.println(String.format("Count is %d", solution(1041)));
		System.out.println(String.format("Count is %d", solution(2147483647)));
	}

	private static int solution(int N) {
		// while (N > 0) {
		// System.out.println(String.format("%s %b", N, N & 1));
		// N = N >> 1;
		// }
		// return N;
		int totalCount = 0;
		int count = 0;
		for (int i = 0; i < getBitsCount(N); i++) {
			if ((N & (1 << i)) != 0) {
//				System.out.println(String.format("Bit %d is set", i));
				count = 0;
			} else {
				count++;
				totalCount = Math.max(totalCount, count);
			}
		}
		return totalCount;
	}

	private static int getBitsCount(int N) {
		int count = 0;
		while (N > 0) {
			N = N >> 1;
			count++;
		}
		return count;
	}

}
