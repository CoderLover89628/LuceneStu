package org.com.lucene.test;

import org.com.lucene.main.IndexUtil;
import org.junit.Test;

public class TestIndexUtil {

	@Test
	public void testIndex() {
		IndexUtil iu = new IndexUtil();
		iu.index();
	}
	
	@Test
	public void testQuery() {
		IndexUtil iu = new IndexUtil();
		iu.query();
	}
}
