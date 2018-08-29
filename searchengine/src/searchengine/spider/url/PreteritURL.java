package searchengine.spider.url;

import java.io.*;
import java.util.HashMap;

/**
 * 类名：过时的URL队列
 * 作用：用于存放已经请求过的URL，同时用于避免重复抓取URL。
 * 明细：当创建本类对象时，本类内部方法将加载来自磁盘文件内的URL信息到内存，
 *      且在内存中作为一个静态的成员变量存在。
 * @author jianyu.chen
 *
 */
public class PreteritURL {
	
	/**
	 * preteritURL : 过时的URL队列
	 */
	public static HashMap<String,String> preteritURL = new HashMap<String,String>();
	private static String FILE_PATH = "D:/D/URL/PreteritURL";
	public static boolean isLoad = false;
	public static boolean load() {
		boolean complete = false;
		BufferedReader br = null;
		if (!isLoad){
			try {
				br = new BufferedReader(new FileReader(FILE_PATH));
				String s;
				if ((s = br.readLine()) != null) {
					preteritURL.put(s,"");
				}
				 complete = true;
				 isLoad = true;
				 System.out.println("已采集的URL队列加载完毕...");
			} catch (IOException e) {
				 complete = false;
				 //写错误日志。
				 System.out.println("加载已采集的URL队列时发生异常："+e.getMessage());
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
	
	
	public static boolean save(){
		boolean complete = false;
		BufferedWriter bw = null;
		if (preteritURL.size()>0){
			try {
				bw = new BufferedWriter(new FileWriter(FILE_PATH));
				for(String str:preteritURL.keySet()){
					bw.write(str+"\r\n");
				}
				 complete = true;
				 System.out.println("持久化已采集的URL队列完毕...");
			} catch (IOException e) {
				 complete = false;
				 //写错误日志。
				 System.out.println("持久化已采集的URL队列时发生异常："+e.getMessage());
			}finally{
				try {
					bw.flush();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return  complete;
	}
	
}
