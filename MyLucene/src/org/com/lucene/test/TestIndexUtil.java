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
	
	@Test
	public void testDelete() {
		IndexUtil iu = new IndexUtil();
		iu.delete();
	}
	@Test
	public void testUnDelete() {
		IndexUtil iu = new IndexUtil();
		iu.undelete();
	}
	@Test
	public void testForceDelete() {
		IndexUtil iu = new IndexUtil();
		iu.forceDelete();
	}
	@Test
	public void testMerge() {
		IndexUtil iu = new IndexUtil();
		iu.merge();
	}
	
	@Test
	public void testUpdate() {
		IndexUtil iu = new IndexUtil();
		iu.update();
	}
	
	@Test
	public void testSearch() {
		IndexUtil iu = new IndexUtil();
		iu.search();
	}
	@Test
	public void testSearch01() {
		IndexUtil iu = new IndexUtil();
		for(int i = 0; i < 5; i++) {
			try {
				iu.search01();
				System.out.println("------------------");
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 
	}
}
