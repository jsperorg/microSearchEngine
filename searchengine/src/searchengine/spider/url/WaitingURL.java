package searchengine.spider.url;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 类名：等待的URL队列
 * 作用：用于存放和提供等待采集的URL资源。
 * 明细：当创建本类对象时，本类内部方法将加载来自磁盘文件内的URL信息到内存，
 *      且在内存中作为一个静态的成员变量存在。
 * @author jianyu.chen
 *
 */
public class WaitingURL {
	/**
	 * waitingURL : 待采集的URL列表
	 */
	public static ConcurrentSkipListMap<String,String> waitingURL = new ConcurrentSkipListMap<String,String>();
	private static String FILE_PATH = "D:/D/URL/WaitingURL";
	public static boolean isLoad = false;
	public static boolean load() {
		boolean complete = false;
		BufferedReader br = null;
		if (!isLoad){
			try {
				br = new BufferedReader(new FileReader(FILE_PATH));
				String s;
				if ((s = br.readLine()) != null) {
					waitingURL.put(s,"");
				}
				 complete = true;
				 isLoad = true;
				 System.out.println("待采集的URL队列加载完毕...");
			} catch (IOException e) {
				 complete = false;
				 //写错误日志。
				 System.out.println("加载待采集的URL队列时发生异常："+e.getMessage());
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
		if (waitingURL.size()>0){
			try {
				bw = new BufferedWriter(new FileWriter(FILE_PATH));
				for(String str:waitingURL.keySet()){
					bw.write(str+"\r\n");
				}
				 complete = true;
				 System.out.println("持久化待采集的URL队列完毕...");
			} catch (IOException e) {
				 complete = false;
				 //写错误日志。
				 System.out.println("持久化待采集的URL队列时发生异常："+e.getMessage());
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
