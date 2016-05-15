package org.com.lucene.test;

import org.com.lucene.main.HelloLucene;
import org.junit.Test;

public class TestHelloLucene {

	@Test
	public void testIndex(){
		HelloLucene hl = new HelloLucene();
		hl.index(); 
	}
	
	@Test
	public void testSearch() {
		HelloLucene hl = new HelloLucene();
		hl.search();
	}
}
