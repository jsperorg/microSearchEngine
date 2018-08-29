package searchengine.dictionary.w2i;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class Dict {
	
	public static HashMap<String,String> dict = new HashMap<String,String>();
	public static boolean isLoad = false;

	public static boolean load() {
		System.out.println("正在加载词典索引...");
		boolean complete = false;
		BufferedReader br = null;
		if (!isLoad){
			try {
				br = new BufferedReader(new FileReader("D:/D/W2I"));
				String s;
				String[] words;
				while ((s = br.readLine()) != null) {
					words = s.split("\t");
					dict.put(words[0], words[1]);
				}
				 complete = true;
				 isLoad = true;
				 System.out.println("词典索引加载完毕...");
			} catch (IOException e) {
				 complete = false;
				 //写错误日志。
				 System.out.println("加载词典索引时发生异常："+e.getMessage()+"...");
			}finally{
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{complete = true;}
		return  complete;
	}
	
	public static boolean add(String word){
		boolean isExists=false;
		if(!dict.containsKey(word)){
			dict.put(word, "- - - - - - - - - - - -");
			isExists=true;
		}
		return isExists;
	}
	
	/**
	 * 获得词语索引指针
	 * @param text
	 * @return
	 */
	public static ConcurrentSkipListMap<String,String> getW2I(String text){
		ConcurrentSkipListMap<String,String> w2i1 = getIndexByOnward(text);
		ConcurrentSkipListMap<String,String> w2i2 = getIndexByInverted(text);
		if(w2i1.size()>w2i2.size() || w2i1.size()==w2i2.size()){
			w2i2.clear();
			return w2i1;
		}else{
			w2i1.clear();
			return w2i2;
		}
	}
	
	
	
	
	/**
	 * 正向最大匹配分词，获取索引
	 * 	 * @param key
	 */
	public static ConcurrentSkipListMap<String,String> getIndexByOnward(String text){
		ConcurrentSkipListMap<String,String> w2i = new ConcurrentSkipListMap<String,String>();
		int len = text.length(),chars = 7;
		while(len>0){
			if(len<chars){chars = len;}
			for(int i=chars;i>0;i--){
				String str = text.substring(text.length()-len,text.length()-len+i );
				String value = dict.get(str);
				if(value!=null && !value.equals("")){
					w2i.put(str, value);
					len=len-i;
					break;
				}else if(i==1){
//					System.out.print(" "+str+" ");
					len=len-i;
					break;
				}
			}
		}
		return w2i;
	}
	
	/**
	 * 反向最大匹配分词，获取索引
	 * @param key
	 */
	public static ConcurrentSkipListMap<String,String> getIndexByInverted(String text){
		ConcurrentSkipListMap<String,String> w2i = new ConcurrentSkipListMap<String,String>();
		int len = text.length(),chars = 7;
		while(len>0){
			if(len<chars){chars = len;}
			for(int i=chars;i>0;i--){
				String str = text.substring(len-i,len );
				String value = dict.get(str);
				if(value!=null && !value.equals("")){
					w2i.put(str, value);
					len=len-i;
					break;
				}else if(i==1){
//					System.out.print(" "+str+" ");
					len=len-i;
					break;
				}
			}
		}
		return w2i;
	}
	
	/**
	 * 获得文本最切近分词数组(双向匹配算法)
	 * @param text
	 * @return
	 */
	public static ArrayList<String> getWords(String text){
		ArrayList<String> list1 = getOnwardWords(text);
		ArrayList<String> list2 = getInvertedWords(text);
		if(list1.size()>list2.size() || list1.size()==list2.size()){
			list2.clear();
			return list1;
		}else{
			list1.clear();
			return list2;
		}
	}
	
	
	/**
	 * 获得正向最大分词数组。
	 * @param text
	 * @return
	 */
	public static ArrayList<String> getOnwardWords(String text){
		ArrayList<String> list = new ArrayList<String>();
		int len = text.length(),chars = 7;
		while(len>0){
			if(len<chars){chars = len;}
			for(int i=chars;i>0;i--){
				String str = text.substring(text.length()-len,text.length()-len+i );
				if(dict.containsKey(str)){
					list.add(str);
					len=len-i;
					break;
				}else if(i==1){
					len=len-i;
					break;
				}
			}
		}
		return list;
	}
	
	/**
	 * 获得反向最大分词数组。
	 * @param text
	 * @return
	 */
	public static ArrayList<String> getInvertedWords(String text){
		ArrayList<String> list = new ArrayList<String>();
		int len = text.length(),chars = 7;
		while(len>0){
			if(len<chars){chars = len;}
			for(int i=chars;i>0;i--){
				String str = text.substring(len-i,len );
				if(dict.containsKey(str)){
					list.add(str);
					len=len-i;
					break;
				}else if(i==1){
					len=len-i;
					break;
				}
			}
		}
		return list;
	}
	
	
	public static boolean save(){
		boolean complete = false;
		BufferedWriter bw = null;
		if(dict.size()>0){
			try {
				bw = new BufferedWriter(new FileWriter("D:/D/W2I"));
				for (String word:dict.keySet()) {
					bw.write(word+"\t"+dict.get(word)+"\r\n");
				}
				 complete = true;
				 System.out.println("持久化词典完毕...");
			} catch (IOException e) {
				 complete = false;
				 //写错误日志。
				 System.out.println("持久化词典索引时发生异常："+e.getMessage());
			}finally{
				try {
					bw.flush();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return complete;
	}
	
	
	
	/**
	 * 正向最大匹配算法
	 * 	 * @param key
	 */
	public void getOnward(String key){
		int len = key.length(),chars = 7;
		while(len>0){
			if(len<chars){chars = len;}
			for(int i=chars;i>0;i--){
				String str = key.substring(key.length()-len,key.length()-len+i );
				if(dict.containsKey(str)){
					System.out.print("["+str+"]");
					len=len-i;
					break;
				}else if(i==1){
					System.out.print(" "+str+" ");
					len=len-i;
					break;
				}
			}
		}
		System.out.println();
	}
	
	
	/**
	 * 反向最大匹配
	 * @param key
	 */
	public void getInverted(String key){
		int len = key.length(),chars = 7;
		while(len>0){
			if(len<chars){chars = len;}
			for(int i=chars;i>0;i--){
				String str = key.substring(len-i,len );
				if(dict.containsKey(str)){
					System.out.print("["+str+"]");
					len=len-i;
					break;
				}else if(i==1){
					System.out.print(" "+str+" ");
					len=len-i;
					break;
				}
			}
		}
		System.out.println();
	}
}
