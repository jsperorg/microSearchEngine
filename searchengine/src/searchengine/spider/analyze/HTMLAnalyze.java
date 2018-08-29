package searchengine.spider.analyze;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import cpdetector.io.*;

	/**
	 * HTML源代码解析类
	 * 该类用于分析HTML源码中的成分
	 * @author 陈建宇
	 * 2009-03-16 16:15:??
	 */
public class HTMLAnalyze {
	
	//↓↓↓:存放HTML源代码，用于其他方法引用处理
	public String HTMLCode = null;
	
	//↓↓↓:存放HTMLCode来自的URL，主要用于匹配不规则的URL
	private String url = null;
	
	public void setHTMLCode(String code) {HTMLCode = code;}
	public String getHTMLCode() {return HTMLCode;}
	public void setUrl(String url) {this.url = url;}
	public String getUrl() {return url;}
	
	public HTMLAnalyze(){
	}
	
	public HTMLAnalyze(String url){
		this.url = url;
	}
	
	
	public boolean connection(){
		boolean flag = false;
		try {
			CodepageDetectorProxy cdp = CodepageDetectorProxy.getInstance();
			cdp.add(JChardetFacade.getInstance());
			java.nio.charset.Charset charset = null;
			URL Url = new URL(url);
			charset = cdp.detectCodepage(Url);
//			System.out.println(charset.name());
			if(charset.name().substring(0,2).equals("GB") || charset.name().equals("UTF-8")){
	            HttpURLConnection con = (HttpURLConnection) Url.openConnection(); 
	            con.setReadTimeout(10000);
//	            HttpURLConnection.setFollowRedirects(true); 
//	            con.setInstanceFollowRedirects(true); 
	            con.connect();
	            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),charset.name())); 
	            String s = ""; 
	            StringBuffer sb = new StringBuffer(); 
	            while ((s = br.readLine()) != null) { 
	                sb.append(s + "\r\n"); 
	            } 
//	            con.disconnect();
	            br.close();
	            setHTMLCode(sb.toString());
	            flag = true;
			}
        } catch (Exception e) { 
        	System.out.println("连接"+url+"失败！");
        	return false;
        	//将异常信息写入日志。
        }
		
        return flag;
	}
	
	/**
	 * 获得所有超链接URL及文本
	 * @return
	 */
	public HashMap<String,String> get_A(){
		HashMap<String,String> a = new HashMap<String,String>();
		if(url.charAt(url.length()-1)=='/'){
			url = url.substring(0,url.length()-1);
		}
		ArrayList<String> links = get_Tag_A();//调用获得所有超链接的方法
		if(links.size()>0){
			for(String link:links){
				Pattern pattern = Pattern.compile("[h|H]+[r|R]+[e|E]+[f|F]+\\s*=\\s*(\"\\s*(.+?)\\s*\"|\'\\s*(.+?)\\s*\'|(.+?))(\\s{1}|>)");
				Matcher matcher = pattern.matcher(link);
				String str = "";
				if(matcher.find()){
					str = matcher.group(1).trim();
				}
				//去除双引号和单引号。
				if(str.substring(0,1).equals("\"") || str.substring(0,1).equals("'")){
					str = str.substring(1,str.length());
				}
				if(str.substring(str.length()-1).equals("\"") || str.substring(str.length()-1).equals("'")){
					str = str.substring(0,str.length()-1);
				}
//				if(str!=null && str.length()>2  && str.length()<150 
//						&& str.indexOf("/#")==-1 
//						&& str.indexOf("/archiver")==-1 
//						&& str.indexOf("/.")==-1 
//						&& str.indexOf("/ ")==-1 
//						&& str.indexOf("javascript:")==-1 
//						&& str.indexOf("JAVASCRIPT:")==-1 
//						&& str.indexOf("mailto:")==-1 
//						&& str.indexOf("MAILTO:")==-1 
//						&& str.indexOf("javascript:")==-1 
//						&& str.indexOf("javascript:")==-1){//
//					去除双引号和单引号。
//					if(str.substring(0,1).equals("\"") || str.substring(0,1).equals("'")){
//						str = str.substring(1,str.length());
//					}
//					if(str.substring(str.length()-1).equals("\"") || str.substring(str.length()-1).equals("'")){
//						str = str.substring(0,str.length()-1);
//					}
//					if(str.substring(0,1).equals("/") && str.indexOf("//")==-1){
//						str = url+str;
//					}else if(!str.substring(0,1).equals("/") && str.indexOf("../")==-1 && str.length()>=11 && !str.substring(0,11).equals("javascript:") && !str.substring(0,7).equals("mailto:") && !str.substring(0,7).equals("http://") && str.indexOf("//")==-1/* && str.indexOf(".")>-1*/){
//						str = url+"/"+str;
//					}else if(!str.substring(0,1).equals("/") && str.length()>=7 &&  !str.substring(0,7).equals("mailto:") && !str.substring(0,7).equals("http://") && str.indexOf("//")==-1/* && str.indexOf(".")>-1*/){
//						str = url+"/"+str;
//					}else if(str.length()<7 && str.indexOf("//")==-1/* && str.indexOf(".")>-1*/){
//						str = url+"/"+str;
//					}
//					//去除结尾符'/'
//					if(str.charAt(str.length()-1)=='/'){
//						str = str.substring(0,str.length()-1);
//					}
//				}
				if(str.length()>7 && str.substring(0,7).equals("http://")){
					a.put(str, link.replaceAll("<.+?>", "").replaceAll(" ", ""));
				}
			}
		}
		return a;
	}
	
	/**
	 * 获得所有超链接标签
	 * @return
	 */
	public ArrayList<String> get_Tag_A(){
		ArrayList<String> links = null;
		String code = getEliminated();
		if(code!=null && code.length()>0){
			links = new ArrayList<String>();
			String[] strs = code.split("<\\s*/\\s*[a|A]+\\s*>");
	        for(int i=0;i<strs.length;i++){
	        	Pattern pattern = Pattern.compile("(<[a|A]+\\s+[^<>]*[h|H]+[r|R]+[e|E]+[f|F]+\\s*=\\s*[^<>]*>.*)");
	    		Matcher matcher = pattern.matcher(strs[i].replaceAll("\r\n", ""));
	    		boolean result = matcher.find(); 
	            if(result){ 
	                links.add(matcher.group(1)+"</a>"); 
	    }}}return links;
	}
	
	/**
	 * 获得所有SCRIPT标签
	 * @return
	 */
	public ArrayList<String> get_Tag_SCRIPT(){
		ArrayList<String> script = null;
		if(HTMLCode!=null && !HTMLCode.equals("")){
			script = new ArrayList<String>();
			String[] strs = HTMLCode.toString().split("<\\s*/\\s*[s|S]+[c|C]+[r|R]+[i|I]+[p|P]+[t|T]+\\s*>");
	        for(int i=0;i<strs.length;i++){
	        	Pattern pattern = Pattern.compile("(<\\s*[s|S]+[c|C]+[r|R]+[i|I]+[p|P]+[t|T]+[^<>]*>.*)");
	    		Matcher matcher = pattern.matcher(strs[i].replaceAll("\r\n", ""));
	    		boolean result = matcher.find(); 
	            if(result){ 
	            	script.add(matcher.group(1)+"</script>"); 
	    }}}return script;
	}
	
	
	/**
	 * 获得所有STYLE标签
	 * @return
	 */
	public ArrayList<String> get_Tag_STYLE(){
		ArrayList<String> style = null;
		if(HTMLCode!=null && !HTMLCode.equals("")){
			style = new ArrayList<String>();
			String[] strs = HTMLCode.toString().split("<\\s*/\\s*[s|S]+[t|T]+[y|Y]+[l|L]+[e|E]+\\s*>");
	        for(int i=0;i<strs.length;i++){
	        	Pattern pattern = Pattern.compile("(<\\s*[s|S]+[t|T]+[y|Y]+[l|L]+[e|E]+[^<>]*>.*)");
	    		Matcher matcher = pattern.matcher(strs[i].replaceAll("\r\n", ""));
	    		boolean result = matcher.find(); 
	            if(result){ 
	            	style.add(matcher.group(1)+"</script>"); 
	    }}}return style;
	}
	
	/**
	 * 获得所有图片标签
	 * @return
	 */
	public ArrayList<String> get_Tag_IMG(){
		ArrayList<String> img = new ArrayList<String>();
		if(HTMLCode!=null && !HTMLCode.equals("")){
			Pattern pattern = Pattern.compile("(<img\\s+[^<>]*>)");
			Matcher matcher = pattern.matcher(HTMLCode);
			boolean result = matcher.find(); 
	        while(result){ 
	           for(int i=1;i<=matcher.groupCount();i++){ 
	            img.add(matcher.group(i).replaceAll("\r\n", ""));
	           } 
	         result=matcher.find(); 
	        } 
		}
		return img;
	}
	
	/**
	 * 获得所有图片URL及文本
	 * @return
	 */
	public HashMap<String,String> get_IMG(){
		ArrayList<String> img = get_Tag_IMG();
		HashMap<String,String> img_text = new HashMap<String,String>();
		if(img!=null && img.size()>0){
			//去除结尾符'/'
			if(url.charAt(url.length()-1)=='/'){
				url = url.substring(0,url.length()-1);
			}
			String img_url = "";
			String img_txt = "";
			Pattern pattern =null;
			Matcher matcher = null;
			boolean result = false;
			for(int i=0;i<img.size();i++){//[h|H]+[r|R]+[e|E]+[f|F]+\\s*=\\s*(\"\\s*(.+?)\\s*\"|\'\\s*(.+?)\\s*\'|(.+?))(\\s{1}|>)
				pattern = Pattern.compile("[a|A]+[l|L]+[t|T]+\\s*=\\s*(\"\\s*(.*?)\\s*\"|\'\\s*(.*?)\\s*\'|(.*?))(\\s{1}|/\\s*>|>)");
				matcher = pattern.matcher(img.get(i));
				result = matcher.find(); 
		        if(result){ 
		        	img_txt = matcher.group(1); 
		        } 
		        pattern = Pattern.compile("[s|S]+[r|R]+[c|C]+\\s*=\\s*(\"\\s*(.+?)\\s*\"|\'\\s*(.+?)\\s*\'|(.+?))(\\s{1}|/\\s*>)");
				matcher = pattern.matcher(img.get(i));
				result = matcher.find(); 
		        if(result){ 
		        	img_url = matcher.group(1); 
		        } 
		        if(img_url.replaceAll(" ", "")!="" && img_txt.replaceAll(" ", "")!= "" && img_txt.length()>1){
		        	//去除双引号和单引号。
		        	if(img_url.substring(0,1).equals("\"") || img_url.substring(0,1).equals("'")){
						img_url = img_url.substring(1,img_url.length());
					}
					if(img_url.substring(img_url.length()-1).equals("\"") || img_url.substring(img_url.length()-1).equals("'")){
						img_url = img_url.substring(0,img_url.length()-1);
					}
					if(img_txt.substring(0,1).equals("\"") || img_txt.substring(0,1).equals("'")){
						img_txt = img_txt.substring(1,img_txt.length());
					}
					if(img_txt.substring(img_txt.length()-1).equals("\"") || img_txt.substring(img_txt.length()-1).equals("'")){
						img_txt = img_txt.substring(0,img_txt.length()-1);
					}
					if(img_url.substring(0,1).equals("/") && img_url.indexOf("//")==-1){
						img_url = url+img_url;
					}else if(!img_url.substring(0,1).equals("/") && img_url.length()>=11 && !img_url.substring(0,11).equals("javascript:") && !img_url.substring(0,7).equals("mailto:") && !img_url.substring(0,7).equals("http://") && img_url.indexOf("//")==-1/* && str.indexOf(".")>-1*/){
						img_url = url+"/"+img_url;
					}else if(!img_url.substring(0,1).equals("/") && img_url.length()>=7 &&  !img_url.substring(0,7).equals("mailto:") && !img_url.substring(0,7).equals("http://") && img_url.indexOf("//")==-1/* && str.indexOf(".")>-1*/){
						img_url = url+"/"+img_url;
					}else if(img_url.length()<7 && img_url.indexOf("//")==-1/* && str.indexOf(".")>-1*/){
						img_url = url+"/"+img_url;
					}
					//去除结尾符'/'
					if(img_url.charAt(img_url.length()-1)=='/'){
						img_url = img_url.substring(0,img_url.length()-1);
					}
					
		        }
		        if(img_url.length()>7 && img_url.substring(0,7).equals("http://") && img_txt.length()>1){
		        	img_text.put(img_url, img_txt);
				}else{
					System.out.println("["+img_url+" : "+img_txt+"] 无效！");
				}
			}
		}
		return img_text;
	}
	
	
	/**
	 * 获得网页标题文本
	 * @return
	 */
	public String get_TITLE(){
		String title = null;
		if(HTMLCode!=null && !HTMLCode.equals("")){
			Pattern pattern = Pattern.compile("<[t|T]+[i|I]+[t|T]+[l|L]+[e|E]+\\s*[^<>]*>([^<>]*)<\\s*/\\s*[t|T]+[i|I]+[t|T]+[l|L]+[e|E]+\\s*>");
			Matcher matcher = pattern.matcher(HTMLCode);
			boolean result = matcher.find(); 
	        if(result){ 
	        	title = matcher.group(1).replaceAll("\r\n", ""); 
	        } 
		}
		return title;
	}
	
	/**
	 * 获得网页标题标签
	 * @return
	 */
	public String get_Tag_TITLE(){
		String title = null;
		if(HTMLCode!=null && !HTMLCode.equals("")){
			Pattern pattern = Pattern.compile("(<[t|T]+[i|I]+[t|T]+[l|L]+[e|E]+\\s*[^<>]*>[^<>]*<\\s*/\\s*[t|T]+[i|I]+[t|T]+[l|L]+[e|E]+\\s*>)");
			Matcher matcher = pattern.matcher(HTMLCode);
			boolean result = matcher.find(); 
	        if(result){ 
	        	title = matcher.group(1); 
	        } 
		}
		return title;
	}
	
	/**
	 * 去除无用标签(已过时)
	 * @return
	 */
	public String getEliminated_2(){
		//去除无用标签：<srcipt>、<style>、<form>、<!--注释内容-->.
		StringBuffer sb = null;
		if(HTMLCode!=null && !HTMLCode.equals("")){
			String[] strs = HTMLCode.split("<\\s*/\\s*[s|S]+[c|C]+[r|R]+[i|I]+[p|P]+[t|T]+\\s*>");
			sb = new StringBuffer();
			System.out.println("正在清除脚本...");
	        for(int i=0;i<strs.length;i++){
	        	sb.append(strs[i].replaceAll("<\\s*[s|S]+[c|C]+[r|R]+[i|I]+[p|P]+[t|T]+(.*(\r\n)*.*)*", ""));
	        }
            /* - - - - - - - - - - - - - - - - - - - - - - */
	        strs = sb.toString().split("<\\s*/\\s*[s|S]+[t|T]+[y|Y]+[l|L]+[e|E]+\\s*>");
	        sb = new StringBuffer();
	        System.out.println("正在清除样式表...");
	        for(int i=0;i<strs.length;i++){
	        	sb.append(strs[i].replaceAll("<\\s*[s|S]+[t|T]+[y|Y]+[l|L]+[e|E]+(.*(\r\n)*.*)*", ""));
	        }
            /* - - - - - - - - - - - - - - - - - - - - - - */
	        strs = sb.toString().split("<\\s*/\\s*[f|F]+[o|O]+[r|R]+[m|M]+\\s*>");
	        sb = new StringBuffer();
	        System.out.println("正在清除form表单...");
	        for(int i=0;i<strs.length;i++){
	        	sb.append(strs[i].replaceAll("<\\s*[f|F]+[o|O]+[r|R]+[m|M]+(.*(\r\n)*.*)*", ""));
	        }
            /* - - - - - - - - - - - - - - - - - - - - - - */
	        strs = sb.toString().split("--\\s*>");
	        sb = new StringBuffer();
	        System.out.println("正在清除HTML注释...");
	        for(int i=0;i<strs.length;i++){
	        	sb.append(strs[i].replaceAll("<!--(.*(\r\n)*.*)*", ""));
	        }
	    }
		return sb.toString();
	}
	
	/**
	 * 去除无用标签(新)
	 * @return
	 */
	public String getEliminated(){
		//去除无用标签：<srcipt>、<style>、<form>、<!--注释内容-->.
		StringBuffer sb = new StringBuffer();;
		if(HTMLCode!=null && !HTMLCode.equals("")){
			sb = new StringBuffer();
			String[] strs = HTMLCode.split("<\\s*/\\s*[s|S]+[c|C]+[r|R]+[i|I]+[p|P]+[t|T]+\\s*>");
	        for(int i=0;i<strs.length;i++){
	        	String[] sss = strs[i].split("\r\n");
	        	int row=sss.length-1;
	        	for(int j=0;j<sss.length;j++){
	        		Pattern pattern = Pattern.compile("<\\s*[s|S]+[c|C]+[r|R]+[i|I]+[p|P]+[t|T]+.*");
		    		Matcher matcher = pattern.matcher(sss[j]);
		    		boolean find = matcher.find(); 
		            if(find){ 
		            	sss[j] = sss[j].replaceAll("<\\s*[s|S]+[c|C]+[r|R]+[i|I]+[p|P]+[t|T]+.*", "");
		            	row = j;
		            	break;
		            }
	        	}
	        	strs[i]="";
	        	for(int k=0;k<=row;k++){
	        		strs[i]+=sss[k]+"\r\n";
	        	}
	        	sb.append(strs[i]);
	        }
            /* - - - - - - - - - - - - - - - - - - - - - - */
	        strs = sb.toString().split("<\\s*/\\s*[s|S]+[t|T]+[y|Y]+[l|L]+[e|E]+\\s*>");
	        sb = new StringBuffer();
	        for(int i=0;i<strs.length;i++){
	        	String[] sss = strs[i].split("\r\n");
	        	int row=sss.length-1;
	        	for(int j=0;j<sss.length;j++){
	        		Pattern pattern = Pattern.compile("<\\s*[s|S]+[t|T]+[y|Y]+[l|L]+[e|E]+.*");
		    		Matcher matcher = pattern.matcher(sss[j]);
		    		boolean find = matcher.find(); 
		            if(find){ 
		            	sss[j] = sss[j].replaceAll("<\\s*[s|S]+[t|T]+[y|Y]+[l|L]+[e|E]+.*", "");
		            	row = j;
		            	break;
		            }
	        	}
	        	strs[i]="";
	        	for(int k=0;k<=row;k++){
	        		strs[i]+=sss[k]+"\r\n";
	        	}
	        	sb.append(strs[i]);
	        }
            /* - - - - - - - - - - - - - - - - - - - - - - */
	        strs = sb.toString().split("<\\s*/\\s*[f|F]+[o|O]+[r|R]+[m|M]+\\s*>");
	        sb = new StringBuffer();
	        for(int i=0;i<strs.length;i++){
	        	String[] sss = strs[i].split("\r\n");
	        	int row=sss.length-1;
	        	for(int j=0;j<sss.length;j++){
	        		Pattern pattern = Pattern.compile("<\\s*[f|F]+[o|O]+[r|R]+[m|M]+.*");
		    		Matcher matcher = pattern.matcher(sss[j]);
		    		boolean find = matcher.find(); 
		            if(find){ 
		            	sss[j] = sss[j].replaceAll("<\\s*[f|F]+[o|O]+[r|R]+[m|M]+.*", "");
		            	row = j;
		            	break;
		            }
	        	}
	        	strs[i]="";
	        	for(int k=0;k<=row;k++){
	        		strs[i]+=sss[k]+"\r\n";
	        	}
	        	sb.append(strs[i]);
	        }
            /* - - - - - - - - - - - - - - - - - - - - - - */
	        strs = sb.toString().split("--\\s*>");
	        sb = new StringBuffer();
	        for(int i=0;i<strs.length;i++){
	        	sb.append(strs[i].replaceAll("<!--(.*(\r\n)*.*)*", ""));
	        }
	    }
		return sb.toString();
	}
	
	/**
	 * 清除所有超链接标签
	 * @return
	 */
	public String get_Removed_A(){
		StringBuffer sb = new StringBuffer("");
		if(HTMLCode!=null && !HTMLCode.equals("")){
			String[] strs = HTMLCode.split("<\\s*/\\s*[a|A]+\\s*>");
	        for(int i=0;i<strs.length;i++){
	        	sb.append(strs[i].replaceAll("(<[a|A]+\\s+[^<>]*>(.*(\r\n)*.*)*)",""));
	    	}
	    }
		return sb.toString();
	}
	
	
	/**
	 * 获得网页纯文本
	 * @return
	 */
	public String get_TEXT(){
		StringBuffer text = new StringBuffer();
		String finalText = null;
		String etd =  getEliminated().replaceAll("(<\\s*[b|B]+[r|R]+\\s*>|<\\s*/\\s*[b|B]+[r|R]+\\s*>|<\\s*[b|B]+[r|R]+\\s*/\\s*>)", "\r\n");
		if(etd!=null && etd.length()>0){
			String string = etd.toString().replaceAll("<.*?(\r\n)*.*?(\r\n)*.*?(\r\n)*.*?(\r\n)*.*?(\r\n)*.*?>","").replaceAll("\t", " ");//
			String[] strs = string.split("\r\n");
			StringBuffer str = new StringBuffer();
			char[] chars1,chars2 = null; 
			for(int i=0;i<strs.length;i++){
				if(strs[i].length()>0 && !strs[i].equals(" ")){
					chars1 = strs[i].toCharArray();
					for(int j=0;j<chars1.length;j++){
						if(chars1[j]!=' '){
							str.append(strs[i].substring(j)+"\r\n");
							break;
			}}}}
			strs = str.toString().split("\r\n");
			for(int i=0;i<strs.length;i++){
				if(strs[i].length()>0 && !strs[i].equals(" ")){
					chars2 = strs[i].toCharArray();
					for(int j=chars2.length-1;j>=0;j--){
						if(chars2[j]!=' '){
							strs[i] = strs[i].substring(0,j+1)+"\r\n";
							break;
			}}}}
			for(int i = 0;i<strs.length;i++){
				if(strs[i].length()<8){
					strs[i] = strs[i].replaceAll("\r\n"," ");
				}
				text.append(strs[i]);
			}
		}
		finalText =text.toString().replaceAll("  ", " ").replaceAll("   ", " ")
		.replaceAll("    ", " ").replaceAll("     ", " ")
		.replaceAll("&(ensp|#8194);", " ").replaceAll("&(emsp|#8195);", " ")
		.replaceAll("&(amp|#38);", "&").replaceAll("&(lt|#60);", "<")
		.replaceAll("&(gt|#62);", ">").replaceAll("&(yen|#165);", "¥")
		.replaceAll("&(macr|#175);", "¯").replaceAll("&(acute|#180);", "´")
		.replaceAll("&(deg|#176);", "°").replaceAll("&(&ordm|#186);", "º")
		.replaceAll("&(quot|#34);", "\"").replaceAll("&(copy|#169);", "©")
		.replaceAll("&(reg|#174);", "®").replaceAll("&(™|#8482);", "™")
		.replaceAll("&(times|#215);", "×").replaceAll("&(divide|#247);", "÷")
		.replaceAll("&(curren|#164);", "¤").replaceAll("&(OElig|#338);", "Œ")
		.replaceAll("&(oelig|#339);", "œ").replaceAll("&(circ|#710);", "ˆ")
		.replaceAll("&(ndash|#8211);", "–").replaceAll("&(mdash|#8212);", "—")
		.replaceAll("&(lsquo|#8216);", "‘").replaceAll("&(rsquo|#8217);", "’")
		.replaceAll("&(sbquo|#8218);", "‚").replaceAll("&(ldquo|#8220);", "“")
		.replaceAll("&(rdquo|#8221);", "”").replaceAll("&(bdquo|#8222);", "„")
		.replaceAll("&(dagger|#8224);", "†").replaceAll("&(Dagger|#8225);", "‡")
		.replaceAll("&(permil|#8240);", "‰").replaceAll("&(nbsp|#160);", " ")
		.replaceAll("&(\\w+|\\d+);", " ").replaceAll("&#(\\d+);", " ");
		return finalText;
	}
	
	
	/**
	 * 获得网页纯文本(去除了超链接文本和标题文本)
	 * @return
	 */
	public String get_TXT(){
		String finalText = "";
		if(isHome(url)){
			finalText = get_Keywords()+"\r\n"+get_Description();
		}else{
			StringBuffer sb = new StringBuffer("");
			StringBuffer text = new StringBuffer("");
			String etd = getEliminated().replaceAll("(<[t|T]+[i|I]+[t|T]+[l|L]+[e|E]+\\s*[^<>]*>[^<>]*<\\s*/\\s*[t|T]+[i|I]+[t|T]+[l|L]+[e|E]+\\s*>)", "");
			String[] strss = etd.split("<\\s*/\\s*[a|A]+\\s*>");
	        for(int i=0;i<strss.length;i++){
	        	sb.append(strss[i].replaceAll("(<[a|A]+\\s+[^<>]*>(.*(\r\n)*.*)*)",""));
	    	}
			etd = sb.toString().replaceAll("(<\\s*[b|B]+[r|R]+\\s*>|<\\s*/\\s*[b|B]+[r|R]+\\s*>|<\\s*[b|B]+[r|R]+\\s*/\\s*>)", "\r\n");
			if(etd!=null && etd.length()>0){
				String string = etd.replaceAll("<.*?(\r\n)*.*?(\r\n)*.*?(\r\n)*.*?(\r\n)*.*?(\r\n)*.*?>","").replaceAll("\t", " ");//<.*?(\r\n)*.*?(\r\n)*.*?(\r\n)*.*?>
				String[] strs = string.split("\r\n");
				StringBuffer str = new StringBuffer();
				char[] chars1,chars2 = null; 
				for(int i=0;i<strs.length;i++){
					if(strs[i].length()>0 && !strs[i].equals(" ")){
						chars1 = strs[i].toCharArray();
						for(int j=0;j<chars1.length;j++){
							if(chars1[j]!=' '){
								str.append(strs[i].substring(j)+"\r\n");
								break;
				}}}}
				strs = str.toString().split("\r\n");
				for(int i=0;i<strs.length;i++){
					if(strs[i].length()>0 && !strs[i].equals(" ")){
						chars2 = strs[i].toCharArray();
						for(int j=chars2.length-1;j>=0;j--){
							if(chars2[j]!=' '){
								strs[i] = strs[i].substring(0,j+1)+"\r\n";
								break;
				}}}}
				for(int i = 0;i<strs.length;i++){
					if(strs[i].length()<8){
						strs[i] = strs[i].replaceAll("\r\n"," ");
					}
					text.append(strs[i]);
				}
			}
			finalText = text.toString().replaceAll("  ", " ").replaceAll("   ", " ")
			.replaceAll("    ", " ").replaceAll("     ", " ")
			.replaceAll("&(ensp|#8194);", " ").replaceAll("&(emsp|#8195);", " ")
			.replaceAll("&(amp|#38);", "&").replaceAll("&(lt|#60);", "<")
			.replaceAll("&(gt|#62);", ">").replaceAll("&(yen|#165);", "¥")
			.replaceAll("&(macr|#175);", "¯").replaceAll("&(acute|#180);", "´")
			.replaceAll("&(deg|#176);", "°").replaceAll("&(&ordm|#186);", "º")
			.replaceAll("&(quot|#34);", "\"").replaceAll("&(copy|#169);", "©")
			.replaceAll("&(reg|#174);", "®").replaceAll("&(™|#8482);", "™")
			.replaceAll("&(times|#215);", "×").replaceAll("&(divide|#247);", "÷")
			.replaceAll("&(curren|#164);", "¤").replaceAll("&(OElig|#338);", "Œ")
			.replaceAll("&(oelig|#339);", "œ").replaceAll("&(circ|#710);", "ˆ")
			.replaceAll("&(ndash|#8211);", "–").replaceAll("&(mdash|#8212);", "—")
			.replaceAll("&(lsquo|#8216);", "‘").replaceAll("&(rsquo|#8217);", "’")
			.replaceAll("&(sbquo|#8218);", "‚").replaceAll("&(ldquo|#8220);", "“")
			.replaceAll("&(rdquo|#8221);", "”").replaceAll("&(bdquo|#8222);", "„")
			.replaceAll("&(dagger|#8224);", "†").replaceAll("&(Dagger|#8225);", "‡")
			.replaceAll("&(permil|#8240);", "‰").replaceAll("&(nbsp|#160);", " ")
			.replaceAll("&(\\w+|\\d+);", " ").replaceAll("&#(\\d+);", " ");
		}
		return finalText ;
	}
	
	/**
	 * 获得网页关键字
	 * @return
	 */
	public String get_Keywords(){
		String keyword = "";
		String str = get_Tag_Keywords();
		if(HTMLCode!=null && !HTMLCode.equals("")){
	        Pattern pattern = Pattern.compile("[c|C]+[o|O]+[n|N]+[t|T]+[e|E]+[n|N]+[t|T]+\\s*=\\s*(\"\\s*(.+?)\\s*\"|\'\\s*(.+?)\\s*\'|(.+?))(\\s{1}|>)");
	        Matcher matcher = pattern.matcher(str);//
			boolean result = matcher.find(); 
	        if(result){ 
	        	keyword = matcher.group(1); 
	        } 
	        if(keyword!=null && keyword.length()>2){
				//去除双引号和单引号。
				if(keyword.substring(0,1).equals("\"") || keyword.substring(0,1).equals("'")){
					keyword = keyword.substring(1,keyword.length());
				}
				if(keyword.substring(keyword.length()-1).equals("\"") || keyword.substring(keyword.length()-1).equals("'")){
					keyword = keyword.substring(0,keyword.length()-1);
				}
			}
		}
		return keyword;
	}
	
	/**
	 * 获得网页关键字标签
	 * @return
	 */
	public String get_Tag_Keywords(){
		String keyword = "";
		if(HTMLCode!=null && !HTMLCode.equals("")){
			Pattern pattern = Pattern.compile("(<\\s*[m|M]+[e|E]+[t|T]+[a|A]+\\s*[^<>]*[n|N]+[a|A]+[m|M]+[e|E]+\\s*=\\s*[^<>]*[k|K]+[e|E]+[y|Y]+[w|W]+[o|O]+[r|R]+[d|D]+[s|S]+[^<>]*>)");
			Matcher matcher = pattern.matcher(HTMLCode);
			boolean result = matcher.find(); 
	        if(result){ 
	        	keyword = matcher.group(1); 
	        } 
		}
		return keyword;
	}
	
	
	/**
	 * 获得网页关描述
	 * @return
	 */
	public String get_Tag_Description(){
		String Description = "";
		if(HTMLCode!=null && !HTMLCode.equals("")){
			Pattern pattern = Pattern.compile("(<\\s*[m|M]+[e|E]+[t|T]+[a|A]+\\s*[^<>]*[n|N]+[a|A]+[m|M]+[e|E]+\\s*=\\s*[^<>]*[d|D]+[e|E]+[s|S]+[c|C]+[r|R]+[i|I]+[p|P]+[t|T]+[i|I]+[o|O]+[n|N]+[^<>]*>)");
			Matcher matcher = pattern.matcher(HTMLCode);
			boolean result = matcher.find(); 
	        if(result){ 
	        	Description = matcher.group(1); 
	        } 
	        
		}
		return Description;
	}
	
	
	/**
	 * 获得网页关描述标签
	 * @return
	 */
	public String get_Description(){
		String Description = "";
		String str = get_Tag_Description();
		if(HTMLCode!=null && !HTMLCode.equals("")){
	        Pattern pattern = Pattern.compile("[c|C]+[o|O]+[n|N]+[t|T]+[e|E]+[n|N]+[t|T]+\\s*=\\s*(\"\\s*(.+?)\\s*\"|\'\\s*(.+?)\\s*\'|(.+?))(\\s{1}|>)");
	        Matcher matcher = pattern.matcher(str);//
			boolean result = matcher.find(); 
	        if(result){ 
	        	Description = matcher.group(1); 
	        } 
	        if(Description!=null && Description.length()>2){
				//去除双引号和单引号。
				if(Description.substring(0,1).equals("\"") || Description.substring(0,1).equals("'")){
					Description = Description.substring(1,Description.length());
				}
				if(Description.substring(Description.length()-1).equals("\"") || Description.substring(Description.length()-1).equals("'")){
					Description = Description.substring(0,Description.length()-1);
				}
			}
		}
		return Description;
	}
	
	/**
	 * 获得主题部分文本内容。
	 * 返回：取TITLE和各个小区(DIV、TABLE)内容比较，包含TITLE关键字、关键字最多、内容最长的
	 * @return
	 */
	public String getThemeText(){
		String theme = null;
		//获得title文本分词
		//获得所有DIV，取文本内容跟TITLE比较。
		return theme;
	}
	
	/**
	 * 判断是否是有效的网页，以排除其他文件。
	 * @param url
	 * @return
	 */
	public boolean canConnection(String url){
		boolean isHTML = false;
		URL Url = null;
		try{
			Url = new URL(url);
			 HttpURLConnection con = (HttpURLConnection) Url.openConnection(); 
			 con.getInputStream(); 
			isHTML = true;
		}catch(Exception e){
			isHTML = false;
		}
		return isHTML;
	}
	
	/**
	 * 判断URL是否是网站根目录。
	 * @return
	 */
	public boolean isHome(String URL){
		String str = URL;
		if(str.charAt(str.length()-1)=='/'){
			str = str.substring(0,str.length()-1);
		}
		boolean isHome = false;
		char[] chars = str.toCharArray();
		int count=0;
		for(int i=0;i<chars.length;i++){
			if(chars[i]=='/'){
				count++;
			}
		}
		if(count==2){
			isHome = true;
		}
		return isHome;
	}
}
