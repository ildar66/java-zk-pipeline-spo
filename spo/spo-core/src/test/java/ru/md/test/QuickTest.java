package ru.md.test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import ru.masterdm.spo.utils.Formatter;

import static org.mockito.Mockito.*;

public class QuickTest extends Assert {
	private static final Logger LOGGER = Logger.getLogger(QuickTest.class.getName());
	
    @Test(timeout=2000)
    public void test1() {
		String data_start = "27.03.2013 12:42|27.03.2013 12:42|";
    	String data_end = "27.03.2013 12:42|";
    	LOGGER.info(String.valueOf(data_start.split("\\|").length));
    	LOGGER.info(String.valueOf(data_end.split("\\|").length));
    	assertTrue(data_start.split("\\|").length>data_end.split("\\|").length);
    }
	@Test(timeout=2000)
	public void test2() {
		BigDecimal sum = new BigDecimal(1000000.0);
		LOGGER.info(Formatter.format1point(sum.doubleValue()));
		BigDecimal mln = sum.divide(new BigDecimal(1000000.0), 2, RoundingMode.UP);
		LOGGER.info(String.valueOf(mln.doubleValue()).replaceAll(".00", "").replaceAll(".0", ""));
	}
	@Test(timeout=2000)
	public  void testMockito() {
		// you can mock concrete classes, not only interfaces
		LinkedList mockedList = mock(LinkedList.class);

		// stubbing appears before the actual execution
		when(mockedList.get(0)).thenReturn("first");

		// the following prints "first"
		System.out.println(mockedList.get(0));

		// the following prints "null" because get(999) was not stubbed
		System.out.println(mockedList.get(999));
	}
}
