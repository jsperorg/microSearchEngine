package searchengine.test;


import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import searchengine.spider.analyze.*;



public class Test {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConcurrentSkipListMap<String,String> waitingURL = new ConcurrentSkipListMap<String,String>();
		String url="http://news.163.com/";
		System.out.println(url.trim());
		HTMLAnalyze alz = new HTMLAnalyze(url);
		System.out.println("正在连接URL："+url);
		boolean flag = alz.connection();
		if(flag){
			HashMap<String,String> hrefs = alz.get_A();
			for(String href:hrefs.keySet()){
				waitingURL.put(href, "");
			}
		}
		
		for(String href:waitingURL.keySet()){
			System.out.println(href);
		}
	}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		System.out.println(alz.get_Keywords());
//	  HashMap<String,String> a = alz.get_A();
//	  System.out.println("总共URL："+a.size()+"条");
//	  for(Object o:a.keySet()){
//	  	System.out.println(o+" : "+a.get(o));
//	  }
	//  HashMap<String,String> img = alz.get_IMG();
//		for(Object o:img.keySet()){
//			System.out.println(o+" : "+img.get(o));
//		}
//		System.out.println("----------------------------------");
	//  ArrayList<String> list = alz.get_Tag_IMG();
	//  for(String str:list){
//	  	System.out.println(str);
	//  }
	  
	//  System.out.println(alz.get_Removed_A()); 
		
		
//		long begintime = System.currentTimeMillis();
//		seg.fileSegment("E:/dict/平凡的世界.txt");
//		long endtime = System.currentTimeMillis();
//		
//		System.out.println("总共用时："+((double) (endtime-begintime)/1000)+"秒");
		
//	}
	

}
