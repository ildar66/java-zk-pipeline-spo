package ru.md.test;

import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

public class QuickTests extends Assert {
	private static final Logger LOGGER = Logger.getLogger(QuickTests.class.getName());
	
    @Test(timeout=1000)
    public void test1() {
    	String data_start = "27.03.2013 12:42|27.03.2013 12:42|";
    	String data_end = "27.03.2013 12:42|";
    	LOGGER.info(String.valueOf(data_start.split("\\|").length));
    	LOGGER.info(String.valueOf(data_end.split("\\|").length));
    	assertTrue(data_start.split("\\|").length>data_end.split("\\|").length);
    }

	@Test(timeout=1000)
	public void test2() {
		assertEquals(134L, (new Double(Math.ceil(400.0 / 3))).longValue());
	}
}
