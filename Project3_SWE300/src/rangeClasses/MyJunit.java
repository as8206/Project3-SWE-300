package rangeClasses;

public class MyJunit {
	
	public static void assertEquals(int expected, int actual) {
		if (expected != actual)
			throw new RuntimeException("Test failed!");
	}
	
	int testStart = 100;
	int testEnd = 200;

	public void testSum() {
		Range r = new Range(testStart, testEnd);
		assertEquals(14950, r.sumUp());
	}

	public void testSumFast() {
		Range r = new Range(testStart, testEnd);
		assertEquals(14950, r.sumUpFast());
	}

	public void testMixed() {
		testEnd = 150;
		Range r1 = new Range(testStart, testEnd);
		testStart = 150; 
		testEnd = 200;
		Range r2 = new Range(testStart, testEnd);
		assertEquals(14950, r1.sumUpFast()+r2.sumUp());
	}

	public void testReversed() {
		Range r = new Range(testEnd, testStart);
		assertEquals(14950, r.sumUp());
	}

	public void testReversed2() {
		Range r = new Range(testEnd, testStart);
		assertEquals(14950, r.sumUpFast());
	}
	
	public static void main(String[] args) {
		MyJunit mj = new MyJunit();
		mj.testSum();
		mj.testSumFast();
		mj.testMixed();
		mj.testReversed();
		mj.testReversed2();
	}
}
