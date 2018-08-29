package searchengine.dictionary.manage;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Manage {

	public static void main(String[] args) {
		
		HashMap<String,String> hm = new HashMap<String,String>();
		
		/** 
		 * 加载原词库文件，去除重复词语。
		 */
		try {
			FileReader fr = new FileReader("E:/dict/W2I");
			BufferedReader in = new BufferedReader(fr);
			String s;
//			String str1;
//			String str2;
//			int i=0;
			String[] strs;
			while ((s = in.readLine()) != null && (!s.equals(" "))) {
				strs = s.split("\t");
//				s=strs[0];
				hm.put(strs[0],"");
//				i++;
//				str2 = s.substring(s.length()-1);
//				str1 = s.substring(0,1);
//				if( str1.equals("哟") && (s.length()==2)/*str1.equals("是") || */){
//					System.out.println(s);
//				}else{
//					hm.put(s, "");
//				}
			}
			
			hm.remove("日或");
			hm.remove("在大");
			hm.remove("不可能有");
			hm.remove("就比");
			hm.remove("也许有");
			hm.remove("人会");
			hm.remove("法分");
			hm.remove("可以将");
			hm.remove("我们不");
			hm.remove("能再");
			hm.remove("当我");
			hm.remove("也会有");
			hm.remove("我们想");
			hm.remove("词库中");
			hm.remove("在整个");
			hm.remove("上做");
			hm.remove("份工");
			hm.remove("拿一");
			
			hm.put("这个", "");
			hm.put("还是", "");
			hm.put("只是", "");
			hm.put("整个", "");
			hm.put("子节点", "");
			hm.put("第一层", "");
			hm.put("第二层", "");
			hm.put("第三层", "");
			hm.put("第四层", "");
			hm.put("第五层", "");
			hm.put("第六层", "");
			hm.put("第七层", "");
			hm.put("第八层", "");
			hm.put("第九层", "");
			hm.put("第十层", "");
			
//			System.out.println("没意义的词语共有："+i+"条");
			in.close();
			fr.close();
			System.out.println("原词库共有："+hm.size()+"条");
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
		
//		/**
//		 * 加载待合并词库文件，读取、判断、合并。
//		 */
//		try {
//			FileReader fr = new FileReader("E:/dict/新建 文本文档 (3).txt");
//			BufferedReader in = new BufferedReader(fr);
//
//			String s;
//			String[] ss;
//			int count=0,cgcount=0,wxcount=0;
//			while ((s = in.readLine()) != null && (!s.equals(" "))) {
//				ss=s.split("\t");
//					hm.remove(ss[0]);
//					cgcount++;
//					count++;
//			}
//			in.close();
//			fr.close();
//			System.out.println("待合并词："+count+"条");
//			System.out.println("无效词："+wxcount+"条");
//			System.out.println("加入新词："+cgcount+"条");
//		} catch (IOException e) {
//			System.out.println("Error: " + e);
//		}
//		
		/**
		 * 将合并后的词保存为文件。
		 */
////		
		try {
			System.out.println("合并后总词条数："+hm.size());
			FileWriter fr = new FileWriter("E:/dict/Dict.txt");
			BufferedWriter in = new BufferedWriter(fr);
			Iterator i = hm.entrySet().iterator();
			while(i.hasNext()){
			    Object o = i.next();
			    in.write(o.toString().replace("=", "")+"\t- - - - - - - - - - - -\r\n");
			    }
			in.flush();
		    fr.flush();
			in.close();
			fr.close();
			System.out.println("筛选结束，已保存！");
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

	}

}
