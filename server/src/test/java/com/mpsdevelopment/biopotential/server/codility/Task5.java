package com.mpsdevelopment.biopotential.server.codility;

import org.junit.Assert;

public class Task5 {

	public static void main(String args[]) throws Exception {

		Assert.assertEquals(3, solution(4, 5));
		Assert.assertEquals(3, solution(5, 4));
		Assert.assertEquals(3, solution(5, -4));
		Assert.assertEquals(3, solution(-5, 4));

		Assert.assertEquals(4, solution(3, 5));
		Assert.assertEquals(4, solution(4, 4));
		
		Assert.assertEquals(5, solution(5, 6));
		Assert.assertEquals(3, solution(0, 1));
		Assert.assertEquals(3, solution(1, 4));
		Assert.assertEquals(3, solution(2, 5));
		
	}

	private static int solution(int A, int B) {
		// System.out.println(String.format("Left is %d", getLeft(A, k)));

		if (A < 0) {
			A = -1 * A;
		}

		if (B < 0) {
			B = -1 * B;
		}

		int vl = A % 3;
		int hl = B % 3;

		System.out.println(String.format("V is %d H is %d diff = %d", vl, hl, (vl + hl) % 3));

		if ((vl + hl) % 3 == 0) {
			return (A + B) / 3;
		}
		if ((vl + hl) == 1) {
			return ((A - 1) + (B - 1)) / 3 + 3;
		}
		if ((vl + hl) % 2 == 0 && (vl + hl) % 3 != 0) {
			return ((A - 1) + (B - 1)) / 3 + 2;
		}

		return 0;

	}
}
