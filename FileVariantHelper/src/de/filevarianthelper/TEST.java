package de.filevarianthelper;

import java.util.ArrayList;
import java.util.List;

public class TEST {
	
	public static void main(String... strings) {
		
		List<String> list = new ArrayList<String>();
		list.add("DDDDD");
		
		Object object = list;
		
		System.out.println(object.getClass().getName());
		
		@SuppressWarnings("unchecked")
		List<Object> olist = (List<Object>) object;
		
		System.out.println(olist.getClass().getName());
		
		Object entry = olist.get(0);
		
		System.out.println(entry.getClass().getName());
		
		Object test = new String[] {"TSET"};
		
		System.out.println(test.getClass().getName());
		
	}
	
}
