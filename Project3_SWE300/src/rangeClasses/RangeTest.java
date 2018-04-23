package rangeClasses;

import static org.junit.Assert.*;

import org.junit.Test;

public class RangeTest {
	
	static int testStart = 100;
	static int testEnd = 200;

	@Test
	public void testSum() {
		Range r = new Range(testStart, testEnd);
		assertEquals(14950, r.sumUp());
	}

	@Test
	public void testSumFast() {
		Range r = new Range(testStart, testEnd);
		assertEquals(14950, r.sumUpFast());
	}

	@Test
	public void testMixed() {
		testEnd = 150;
		Range r1 = new Range(testStart, testEnd);
		testStart = 150; 
		testEnd = 200;
		Range r2 = new Range(testStart, testEnd);
		assertEquals(14950, r1.sumUpFast()+r2.sumUp());
	}

	@Test
	public void testReversed() {
		Range r = new Range(testEnd, testStart);
		assertEquals(14950, r.sumUp());
	}

	@Test
	public void testReversed2() {
		Range r = new Range(testEnd, testStart);
		assertEquals(14950, r.sumUpFast());
	}
}
