package org.com.lucene.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestListContain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		List<String> sList = new ArrayList<String>();
		
		if(!sList.contains("a")) {
			System.out.println("������");
			sList.add("a");
			System.out.println(sList.contains("aa"));
		} 
		
		System.out.println("��������");
		List<String> ssList = new ArrayList<String>();
		Map<String,List<String>> hasMap = new HashMap<String, List<String>>();
		hasMap.put("nice", ssList);
		System.out.println(hasMap.get(hasMap.keySet()));
	}

}
