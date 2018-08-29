package searchengine.spider.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import searchengine.dictionary.w2i.Dict;
import searchengine.dictionary.w2i.IPag;
import searchengine.spider.analyze.HTMLAnalyze;
import searchengine.spider.deposit.DataDeposit;
import searchengine.spider.url.PreteritURL;
import searchengine.spider.url.WaitingURL;


public class Main {
	public static boolean start = false;
	public static boolean isSave = true;
	public static int num = 0;
	public static void doMonitor(){
		if(start){
			System.out.println("正在获取URL...");
			if(WaitingURL.waitingURL.size()<=0){
				System.out.println("没有可用的URL资源...");
				return;
			}
			//获取待采集的URL
			for(String url:WaitingURL.waitingURL.keySet()){
				doCollect(url);
				break;
			}
		}else if(isSave==false){
			//停止一切抓取行为，数据持久化：
			//1.将已抓取的URL队列持久化到硬盘
			searchengine.spider.url.PreteritURL.save();
			//2.将待抓取的URL队列持久化到硬盘
			searchengine.spider.url.WaitingURL.save();
			//3.将文档记录持久化。
			searchengine.spider.deposit.DataDeposit.save();
			//4.将词库索引记录持久化。
			searchengine.dictionary.w2i.IPag.save();
			//5.将词库持久化。
			searchengine.dictionary.w2i.Dict.save();
			System.out.println("已暂停采集...");
//			//D:/D/Note/SystemLog.log
//			BufferedWriter bw = null;
//			try {
//				Date today=new Date();
//				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				String nowTime=(String)sdf.format(today);
//				int xq=today.getDay();
//				String XQ="";
//				switch(xq){
//					case 0:XQ="星期日";break;
//					case 1:XQ="星期一";break;
//					case 2:XQ="星期二";break;
//					case 3:XQ="星期三";break;
//					case 4:XQ="星期四";break;
//					case 5:XQ="星期五";break;
//					case 6:XQ="星期六";break;
//				}
//				nowTime = nowTime+XQ;
//				bw = new BufferedWriter(new FileWriter("D:/D/Note/SystemLog.log"));
//				bw.write("最近一次采集结束日期："+nowTime);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}finally{
//				try {
//					bw.flush();
//					bw.close();
//				} catch (IOException e) {
//				}
//			}
			isSave = true;
		}
	}
	
	public static void doCollect(String url){
		num +=1;
		HTMLAnalyze alz = new HTMLAnalyze(url);
		System.out.println("正在连接URL："+url);
		boolean flag = alz.connection();
		ConcurrentSkipListMap<String,String> words = new ConcurrentSkipListMap<String,String>();
		if(flag){
			System.out.println("正在采集第"+num+"个URL...");
			String title = "";//存放最终的文档标题
			//1.获得文本内容，分词，判断分词结果和文本的比例，放弃小于三分之一的分词结果的网页，以排除国外网页。
//			System.out.println("正在进行文本提纯...");
			String text = alz.get_TXT();//存放最终的文档内容
//			System.out.println("文本提纯结束...");
			if(text!=null && text.length()>5 && text.length()<2048){
//				System.out.println("正在分词获取索引...");
				words = Dict.getW2I(text);
//				System.out.println("分词获取索引结束...");
			}
			if((words.size()<text.length()/2)){
				int count= 0;
//				System.out.println("正在获取页面标题...");
				title = alz.get_TITLE();
				if(!title.equals("") && title!=null){
					count=10;
				}
//				System.out.println("获取页面标题结束...");
				
				//2.满足条件1，则获得所有超链接放入待抓取队列
				System.out.println("正在提取超链接...");
				HashMap<String,String> a_herfs = alz.get_A();
//				System.out.println("超链接提取结束...");
//				System.out.println("正在将提取的超链接加入待采集URL队列...");
				for(String href:a_herfs.keySet()){
					if(!PreteritURL.preteritURL.containsKey(href) && !WaitingURL.waitingURL.containsKey(href)){
						//填充进URL队列。
						System.out.println("！！！！！！正在填充URL："+href);
						WaitingURL.waitingURL.put(href, "");
					}
				}
//				System.out.println("将提取的超链接加入待采集URL队列完毕...");
				//3.判断URL是否是网站根目录，如果是，就获得Keyword和Description值作为文本并存库。
//				System.out.println("正在分析页面内容...");
				if(alz.isHome(url)){
					text = alz.get_Keywords()+"\r\n"+alz.get_Description();
					if(!text.equals("") && text!=null)
					count=10;
				}else{
					//4.如果非网站根目录，则获得<title>值跟内容比较，如果内容中不存在<title>值，则放弃该网页
					//对<title>分词
					if(title!="" && text!=""){
						ArrayList<String> list = new ArrayList<String>();
						list = Dict.getWords(title);
						if(list.size()>0){
							for(String str:list){
								if(text.indexOf(str)>=0)
								count++;
							}
						}
					}
				}
				//5.满足条件4，则利用条件1的分词结果，建立索引。
				//如果count>0说明，<title>标签的内容跟实际内容有联系，数据存库、建立索引。
				if(count>0 && title!="" && text!=""){
					//数据存库
//					System.out.println("正在保存文档...");
//					StatusLog.writeStatusLog("D:/D/StatusLog/1.txt", "调用数据库存方法。");
					String filePath = DataDeposit.doSave(url+"\r\n"+title+"\r\n"+text);
//					System.out.println("文档保存结束...");
					if(filePath!=null){
						//建立索引
//						System.out.println("正在创建索引...");
//						StatusLog.writeStatusLog("D:/D/StatusLog/2.txt", "调用索引创建方法。");
						IPag.createIndex(title,text, words, filePath);
//						System.out.println("索引创建结束...");
					}
				}
				alz.HTMLCode = "";
			}
		}
		
		//从待采集的URL队列中删除刚采集结束的URL
//		System.out.println("正在正在将刚采集结束的URL专至已采集的URL队列中...");
		WaitingURL.waitingURL.remove(url);
		//将URL转入已采集的URL队列...
		PreteritURL.preteritURL.put(url, "");
		//6.循环递归以上5步遍历URL队列。
		System.out.println("☆☆☆第"+num+"个URL采集结束☆☆☆");
	}
}
