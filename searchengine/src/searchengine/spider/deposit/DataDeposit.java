package searchengine.spider.deposit;

import java.io.*;

public class DataDeposit {
	public static final String DIRECTORY_PATH = "D:/D/DPag"; 
	public static final String NOTE_PATH = "D:/D/Note/DPagNote.not";
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
				complete = true;
				isLoad = true;
				System.out.println("文档数据记录日志加载完毕...");
			}catch(Exception e){
				System.out.println("文档数据记录日志时发生异常："+e.getMessage());
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
	 * 文档创建方法，自动识别文件个数，自动寻找目录
	 * @param text
	 * @return
	 */
	public static String doSave(String text){
		boolean successful = false;
		//如果D:/D/Note/DPagNote.not的值为空，说明是第一次存储，则要从0目录的0文件开始。
		if(lastFilePath.equals("")){
			//第一次，直接创建文件，调用文档创建方法。
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
		successful = saveFile(text,lastFilePath);
		if(successful){
			return lastFilePath;
		}else{
			return null;
		}
	}
	
	/**
	 * 文件保存方法
	 * @param text
	 * @param path
	 * @return
	 */
	public static boolean saveFile(String text,String path){
		boolean successful = false;
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(path));
			bw.write(text);
			successful = true;
		} catch (IOException e) {
			System.out.println("保存网页文档时发生IO异常！");
			e.printStackTrace();
		}finally{
			try {
				bw.flush();
				bw.close();
			} catch (IOException e) {
			}
		}
		return successful;
	}
	
	public static boolean save(){
		return saveFile(lastFilePath,NOTE_PATH);
	}
}
