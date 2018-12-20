package com.tmkoo.searchapi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListCompare{
	 
    public static void sort(List<String> list) {
 
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                // 返回相反的compare
                return o1.compareTo(o2);
            }
        });
 
//        System.out.println(list);
    }
    
    
    
    public static void main(String args[]){
    	  List<String> list = new ArrayList<String>();
          list.add("2003");
          list.add("2005");
          list.add("2001");
          list.add("2007");
          System.out.println(list);
          
          sort(list);
    }
}