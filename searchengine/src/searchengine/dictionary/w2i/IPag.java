package searchengine.dictionary.w2i;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class IPag {
	//词库文件路径
	public static final String DIRECTORY_PATH = "D:/D/IPag"; 
	//索引文件路径
	public static final String NOTE_PATH = "D:/D/Note/IPagNote.not";
	public static String lastFilePath = "";
	public static boolean isLoad = false;
	
	public static boolean load(){
		boolean complete = false;
		BufferedReader br = null;
		if (!isLoad){
			try{
				if(lastFilePath.equals("")){
					br = new BufferedReader(new FileReader(NOTE_PATH));
					String s ="";
					if((s=br.readLine())!=null){
						lastFilePath = s;
					}
				}
				isLoad = true;
				complete = true;
				System.out.println("索引记录日志加载完毕...");
			}catch(Exception e){
				System.out.println("加载索引记录日志时发生异常："+e.getMessage());
				complete =false;
			}finally{
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{complete = true;}
		return complete;
	}
	
	
	/**
	 * 创建索引。
	 * 详细步骤：循环遍历分词后的词语组，
	 * 
	 * 如果此词语还没有索引，那么就为其创建索引文件，
	 * 并在索引文件中记录该词语所在文档地址，
	 * 并且记录最后一次建立词语索引的文件的地址，
	 * 为下次建词语索引提供依据，
	 * 并且修改该词语在词库中的Value部分，使其指向索引文件。
	 * 
	 * 如果此词语已经有了索引，那么就在该词语的做引文件中添加一条文档记录。
	 * @param text
	 * @param path
	 * @return
	 */
	public static boolean createIndex(String title,String text,ConcurrentSkipListMap<String, String> words,String filePath){
		boolean complete = false;
		String[] items = null;
//		System.out.println("正在循环遍历分词后的索引...");
		for(String word:words.keySet()){
//			System.out.println("已进入循环体内部...");
			items = words.get(word).split(" ");
			BufferedWriter bw = null;
			BufferedReader br = null;
//			System.out.println("正在判断词语“"+word+"”是否已有索引记录...");
			if(items[2].equals("-")){
//				System.out.println("检测到词语“"+word+"”还没有索引记录...");
				//1.1.创建索引文件.文件构建
				//如果D:/D/Note/IPagNote.not的值为空，说明是第一次存储，则要从0目录的0文件开始。
//				System.out.println("正在分析最近一次索引文件创建记录...");
				if(lastFilePath.equals("")){
					//第一次，直接创建文件，调用创建方法。
					lastFilePath=DIRECTORY_PATH+"/0/0";
				}else{
					String[] directorys = lastFilePath.split("/");
					int num1 = Integer.parseInt(directorys[directorys.length-1]);
					if(num1<999){
						directorys[directorys.length-1] = ""+(num1+1);
						String s = "";
						for(int i=0;i<directorys.length;i++){
							if(i==0){
								s+=directorys[i];
							}else{
								s+="/"+directorys[i];
							}
						}
						lastFilePath = s ;
					}else{
						int num2 = Integer.parseInt(directorys[directorys.length-2]);
						if(num2<999){
							directorys[directorys.length-2] = ""+(num2+1);
							directorys[directorys.length-1] = "0";
							String s = "";
							for(int i=0;i<directorys.length;i++){
								if(i==0){
									s+=directorys[i];
								}else{
									s+="/"+directorys[i];
								}
							}
							lastFilePath = s ;
						}
					}
				}
				//创建索引文件。
//				System.out.println("正在为词语“"+word+"”创建索引文件...");
				try {
					bw = new BufferedWriter(new FileWriter(lastFilePath));
					//计算词语是否存在于title中、在文本中大约出现的次数和第一次出现的位置：
					String indexString = filePath;
					if(title.indexOf(word)>-1){
						indexString+="\tt";
					}else{
						indexString+="\tf";
					}
					indexString += " "+(text.split(word).length-1)+" "+text.indexOf(word);
					bw.write(word+"\r\n");
					bw.write("1\r\n");
					bw.write(indexString+"\r\n");
//					System.out.println("为词语“"+word+"”创建索引文件结束...");
				} catch (IOException e) {
					e.printStackTrace();
//					System.out.println("为词语“"+word+"”创建索引文件时发生异常："+e.getMessage());
				}finally{
					try {
						bw.flush();
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//修改词库索引
//				System.out.println("正在修改词语“"+word+"”在词典索引中的记录...");
				items[2]= lastFilePath;
				String ss = "";
				for(int i=0;i<items.length;i++){
					if(i==0){
						ss+=items[i];
					}else{
						ss+=" "+items[i];
					}
				}
				if(Dict.isLoad){
					Dict.dict.put(word, ss);
//					System.out.println("修改词语“"+word+"”在词典索引中的记录结束，修改后的值为："+ss+"...");
				}
				
			}else{
//				System.out.println("检测到词语“"+word+"”已有索引记录...");
//				System.out.println("以下标记块是对词语“"+word+"”的文档索引进行添加的过程...");
//				System.out.println("---------------------------------------------------");
				//如果此词语已经有了索引，那么就在该词语的索引文件中添加一条文档记录。
				StringBuffer sb = new StringBuffer("");
				try {
					br = new BufferedReader(new FileReader(items[2]));
					//计算词语是否存在于title中、在文本中大约出现的次数和第一次出现的位置：
					String indexString = filePath;
					if(title.indexOf(word)>-1){
						indexString+="\tt";
					}else{
						indexString+="\tf";
					}
					indexString += " "+(text.split(word).length-1)+" "+text.indexOf(word);
//					System.out.print("1");
					sb.append(br.readLine()+"\r\n");
//					System.out.print(" 2");
					int count = Integer.parseInt(br.readLine());
//					System.out.print(" 3");
					sb.append((count+1)+"\r\n");
//					System.out.print(" 4");
					sb.append(indexString+"\r\n");
//					System.out.print(" 5");
					if(count<10000){
						String sss = "";
						while((sss=br.readLine())!=null){
							if(!sss.equals(""))
							sb.append(sss+"\r\n");
						}
//						System.out.print(" 6");
					}else if(count==10000){
						String sss = "";
						for(int i=1;i<10000;i++){
							sss = br.readLine();
							if(sss!=null && !sss.equals("")){
								sb.append(br.readLine()+"\r\n");
							}
						}
//						System.out.print(" 6");
					}
//					System.out.print(" 7");
					br.close();
//					System.out.print(" 8");
					//写文件
					bw = new BufferedWriter(new FileWriter(items[2]));
//					System.out.println(" 9");
					bw.write(sb.toString());
//					System.out.println("---------------------------------------------------");
				} catch (IOException e) {
//					System.out.print("修改词语“"+word+"”的文档索引记录时发生异常："+e.getMessage());
					e.printStackTrace();
				}finally{
					try {
						bw.flush();
						bw.close();
					} catch (IOException e) {
						//写日志...
						e.printStackTrace();
					}
				}
			}
			complete = true;
		}
		return complete;
	}
	
	
	public static boolean save(){
		boolean complete = false;
		//1.3.创建索引文件.写索引文件创建日志
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(NOTE_PATH));
			bw.write(lastFilePath);
			System.out.println("持久化索引记录日志完毕...");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("持久化索引记录日志时发生异常："+e.getMessage());
		}finally{
			try {
				bw.flush();
				bw.close();
			} catch (IOException e) {
			}
		}
		return complete;
	}
}

