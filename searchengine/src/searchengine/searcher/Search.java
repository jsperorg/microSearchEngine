package searchengine.searcher;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.servlet.*;
import javax.servlet.http.*;

import searchengine.dictionary.w2i.Dict;

public class Search extends HttpServlet {

	public void destroy() {
		super.destroy();
	}


	public void service(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		long begintime = System.currentTimeMillis();
		HttpServletResponse resp = (HttpServletResponse)response;
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String words  = request.getParameter("w");
		if(words==null || words.equals("")){
			resp.sendRedirect("http://localhost/index.html");
			return;
		}
		System.out.println("接收到检索请求，关键字："+words);
		//1.正向最大匹配中文分词获得索引
		//词语包含在TITLE标题中文档索引
		ConcurrentSkipListMap<String,String> titleContain = new ConcurrentSkipListMap<String,String>();
		//词语不包含在TITLE标题中文档索引
		ConcurrentSkipListMap<String,String> notTitleContain = new ConcurrentSkipListMap<String,String>();
		
		ConcurrentSkipListMap<String,String> w2i = Dict.getIndexByOnward(words);
		if(w2i.size()<=0){
			request.getRequestDispatcher("/error.html").forward(request, response);
			out.flush();
			out.close();
			return;
		}
//		System.out.println("以下循环体为对分词结构提取索引并去重复...");
//		System.out.println("-----------------------------------------");
		for(String word:w2i.keySet()){
//			System.out.println("已进入循环计算块...");
			System.out.println("正在获取词语索引文件地址...");
			String indexPath = w2i.get(word).split(" ")[2];
//			System.out.println("已取得词语“"+word+"”的索引文件地址："+indexPath);
			if(!indexPath.equals("-")){
				BufferedReader br = null;
				try{
					System.out.println("正在读取词语索引文件内容...");
					br = new BufferedReader(new FileReader(indexPath));
					System.out.println("词语："+br.readLine());
					System.out.println("文档数："+br.readLine());
					String docInfo = "";
					
					while((docInfo = br.readLine())!=null){
//						System.out.println("取得的文档索引地址为："+docInfo);
						//判断处理索引信息，添加进相应的集合。
						//一次分隔，将词语是否在TITLE标签中出现分为两个列表。
						String[] indexInfo = docInfo.split("\t");
						if(indexInfo[1].split(" ")[0].equals("t")){
//							System.out.println("检测到词语“"+word+"”出现在文档“"+indexInfo[0]+"”的<title>中...");
							if(!titleContain.containsKey(indexInfo[0]) && !notTitleContain.containsKey(indexInfo[0])){
//								System.out.println("正在将词语“"+word+"”存放进‘title包含该词语’集合中...");
								titleContain.put(indexInfo[0], indexInfo[1]+" 1");
							}else if(!titleContain.containsKey(indexInfo[0]) && notTitleContain.containsKey(indexInfo[0])){
								String[] strs = indexInfo[1].split(" ");
								strs[1]=(Integer.parseInt(strs[1])+Integer.parseInt(notTitleContain.get(indexInfo[0]).split(" ")[1]))+"";
								StringBuffer sb=new StringBuffer("");
								for(int i=0;i<strs.length;i++){
									if(i==0)
										sb.append(strs[i]);
									else
										sb.append(" "+strs[i]);
								}
								titleContain.put(indexInfo[0],sb.toString()+" 1");
								notTitleContain.remove(indexInfo[0]);
							}else if(titleContain.containsKey(indexInfo[0])){
//								System.out.println("检测到‘title包含该词语’集合中已包含文档“"+indexInfo[0]+"”的记录，正在修改重复次数...");
								String[] strs = indexInfo[1].split(" ");
								strs[1]=(Integer.parseInt(strs[1])+Integer.parseInt(titleContain.get(indexInfo[0]).split(" ")[1]))+"";
								StringBuffer sb=new StringBuffer("");
								for(int i=0;i<strs.length;i++){
									if(i==0)
										sb.append(strs[i]);
									else
										sb.append(" "+strs[i]);
								}
								titleContain.put(indexInfo[0],sb.toString()+" "+(Integer.parseInt(titleContain.get(indexInfo[0]).split(" ")[3])+1));
							}
						}else{
//							System.out.println("检测到词语“"+word+"”未出现在文档的<title>中...");
							if(titleContain.containsKey(indexInfo[0])){
								String[] strs = indexInfo[1].split(" ");
								strs[1]=(Integer.parseInt(strs[1])+Integer.parseInt(titleContain.get(indexInfo[0]).split(" ")[1]))+"";
								StringBuffer sb=new StringBuffer("");
								for(int i=0;i<strs.length;i++){
									if(i==0)
										sb.append(strs[i]);
									else
										sb.append(" "+strs[i]);
								}
								titleContain.put(indexInfo[0],sb.toString()+" "+Integer.parseInt(titleContain.get(indexInfo[0]).split(" ")[3]));
//								notTitleContain.remove(indexInfo[0]);
							}else if(!titleContain.containsKey(indexInfo[0]) && !notTitleContain.containsKey(indexInfo[0])){
//								System.out.println("正在将词语“"+word+"”存放进‘title未包含该词语’集合中...");
								notTitleContain.put(indexInfo[0], indexInfo[1]+" 1");
							}else if(!titleContain.containsKey(indexInfo[0]) && notTitleContain.containsKey(indexInfo[0])){
//								System.out.println("检测到‘title未包含该词语’集合中已包含文档“"+indexInfo[0]+"”的记录，正在修改重复次数...");
								String[] strs = indexInfo[1].split(" ");
								strs[1]=(Integer.parseInt(strs[1])+Integer.parseInt(notTitleContain.get(indexInfo[0]).split(" ")[1]))+"";
								StringBuffer sb=new StringBuffer("");
								for(int i=0;i<strs.length;i++){
									if(i==0)
										sb.append(strs[i]);
									else
										sb.append(" "+strs[i]);
								}
								notTitleContain.put(indexInfo[0], sb.toString()+" "+(Integer.parseInt(notTitleContain.get(indexInfo[0]).split(" ")[3])+1));
							}
						}
					}
				}catch(Exception e){
					System.out.println("文档数据记录日志时发生异常：");
					e.printStackTrace();
//					request.getRequestDispatcher("/error.html").forward(request,response);
					
//					e.printStackTrace();
				}finally{
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("正在索引排序...");
		//对titleContain和notTitleContain进行二次分隔
		//分隔条件为：索引记录是否在分词结果中出现多次。
//		System.out.println("已进入第二次分隔判断...");
		ArrayList<String> rtc = new ArrayList<String>();//用于存储不止一个分词词语包含在TITLE中的索引
		ArrayList<String> nrtc = new ArrayList<String>();//用于存储分词词语不包含在TITLE中的索引
		ArrayList<String> rntc = new ArrayList<String>();//用于存储分词词语不包含在TITLE中，但正文不止一个包含分词词语的索引
		ArrayList<String> nrntc = new ArrayList<String>();//用于存储分词词语不包含在TITLE中，但正文只包含一个分词词语的索引
//		System.out.println("正在循环遍历‘title包含该词语’集合，按重复次数是否大于1分隔成两个list...");
		for(String indexInfo:titleContain.keySet()){
			if(Integer.parseInt(titleContain.get(indexInfo).split(" ")[3])>1){
				rtc.add(indexInfo);
			}else{
				nrtc.add(indexInfo);
			}
		}
//		System.out.println("分隔结束。");
		
//		System.out.println("正在循环遍历‘title未包含该词语’集合，按重复次数是否大于1分隔成两个list...");
		for(String indexInfo:notTitleContain.keySet()){
			if(Integer.parseInt(notTitleContain.get(indexInfo).split(" ")[3])>1){
				rntc.add(indexInfo);
			}else{
				nrntc.add(indexInfo);
			}
		}
//		System.out.println("分隔结束。");
		
		
		//对rtc和rntc进行按词语同档个数冒泡排序(由于nrtc和nrntc始终只有一个词语存在其中，即没有词语同档，无须按按词语同档排序，只需按词语出现的总的个数排序)。
		if(rtc.size()>1){
			rtc = getDescendForRtcOrRntc(rtc,titleContain);
		}
		if(rntc.size()>1){
			rntc = getDescendForRtcOrRntc(rntc,notTitleContain);
		}
		
		//对以上对象：rtc、nrtc、rntc、nrntc分别按词语在文档中出现的次数排序
//		System.out.println("正在对二次分隔后的各个list进行降序冒泡排序...");
		if(rtc.size()>1){
			rtc = getDescendByCount(rtc,titleContain);
		}
		if(nrtc.size()>1){
			nrtc = getDescendByCount(nrtc,titleContain);
		}
		if(rntc.size()>1){
			rntc = getDescendByCount(rntc,notTitleContain);
		}
		if(nrntc.size()>1){
			nrntc = getDescendByCount(nrntc,notTitleContain);
		}
		System.out.println("排序完毕...");
//		System.out.println("正在将分隔后的各个list整合...");
		ArrayList<String> indexs = new ArrayList<String>();
		indexs.addAll(rtc);
		indexs.addAll(nrtc);
		indexs.addAll(rntc);
		indexs.addAll(nrntc);
//		System.out.println("整合完毕...");
//		request.setAttribute("indexs", indexs);
		if(indexs.size()>0){
			StringBuffer text = new StringBuffer("");
			int count = 0;
			if(indexs.size()<20){
				count = indexs.size();
			}else{
				count = 20;
			}
			long endtime = System.currentTimeMillis();
			System.out.println("正在读取文档内容...");
			text.append("<title>搜天下："+words+"</title><table align=center width=500><tr><td width=100%><center><form action=s method=get><input type=text name=w value="+words+" style=width:250px;height:26px;font-family:Arial;font-size:15px;padding-top:3px;/><input type=submit value=搜索 style=width:50px;height:27px;/></form></center></td></tr></table><table align=center><tr><td style=width:550px;font-size:12px;background-color:#D9E1F7;height:20px;margin-bottom:12px>以下是最贴近的"+count+"条信息，用时"+((double) (endtime-begintime)/1000)+"秒</td></tr></table>");
			for(int i=0;i<count;i++){ 
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(indexs.get(i)));
					text.append("<table width=500 align=center><tr><td width=100%  style=font-family:Arial;>");
					String url = br.readLine();
					String title = br.readLine();
					if(title.length()>28){
						title = title.substring(0,28)+"...";
					}
					for(String str:w2i.keySet()){
						title = title.replaceAll(str, "<span style=color:#C60A00>"+str+"</span>");
					}
					text.append("<a href="+url+" target=_blank><font size=3>"+title+"</font></a><br>");
					String txt = "";
					String s;
					while ((s = br.readLine()) != null && txt.length()<114) {
						txt+=s;
					}
					if(txt.length()>114){
						txt=txt.substring(0, 114)+"...";
					}
					for(String str:w2i.keySet()){
						txt = txt.replaceAll(str, "<span style=color:#C60A00>"+str+"</span>");
					}
					if(url.length()>65){
						url = url.substring(0,65)+"...";
					}
					text.append("<font size=-1>"+txt+"<br><span style=color:#008000>"+url+"</span></font></td></tr></table><br>");
				} catch (IOException e) {
					System.out.println("读取文档内容时发生异常："+e.getMessage());
					 //写错误日志。
					e.printStackTrace();
				}finally{
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			text.append("<table align=center width=550><tr><td style=width:550px;font-size:12px;background-color:#D9E1F7;height:20px;margin-bottom:12px>以上内容均来自互联网，不代表本站观点或立场。</td></tr></table><table align=center><tr><td style=width:550px;text-align:center;><form action=s method=get><input type=text name=w value="+words+" style=width:250px;height:26px;font-family:宋体;font-size:15px;padding-top:3px;/><input type=submit value=搜索 style=width:50px;height:27px;/></form></td></tr></table>");
			titleContain.clear();
			notTitleContain.clear();
			System.out.println("正在返回检索结果...");
			out.print(text.toString());
			System.out.println("检索结束...");
		}else{
			resp.sendRedirect("http://localhost/index.html");
		}
		out.flush();
		out.close();
	}
	
	/**
	 * 对rtc和rntc进行按词语同档个数冒泡排序...
	 * @param list
	 * @return
	 */
	public static ArrayList<String> getDescendForRtcOrRntc(ArrayList<String> list,ConcurrentSkipListMap<String,String> src){
		String temp = "";
		for(int i = 0; i<list.size();i++){
			for(int j = 0;j<i;j++){
				if(Integer.parseInt(src.get(list.get(i)).split(" ")[3])>Integer.parseInt(src.get(list.get(j)).split(" ")[3])){
					temp = list.get(j);
					list.set(j, list.get(i));
					list.set(i,temp);
				}
			}
		}
		return list;
	}
	
	
	
	/**
	 * 按关键字在文档中出现的次数冒泡排序...
	 * @param list
	 * @return
	 */
	public static ArrayList<String> getDescendByCount(ArrayList<String> list,ConcurrentSkipListMap<String,String> src){
		String temp = "";
		for(int i = 0; i<list.size();i++){
			for(int j = 0;j<i;j++){
				if(Integer.parseInt(src.get(list.get(i)).split(" ")[2])>Integer.parseInt(src.get(list.get(j)).split(" ")[2])){
					temp = list.get(j);
					list.set(j, list.get(i));
					list.set(i,temp);
				}
			}
		}
		return list;
	}
	
	public void init() throws ServletException {
		searchengine.dictionary.w2i.Dict.load();
	}
}
