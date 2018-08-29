package searchengine.spider.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Run extends HttpServlet {

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		System.out.println("正在初始化加载必须的引擎对象...");
		if(searchengine.dictionary.w2i.Dict.isLoad
			&& searchengine.dictionary.w2i.IPag.load()
			&& searchengine.spider.deposit.DataDeposit.load()
			&& searchengine.spider.url.PreteritURL.load()
			&& searchengine.spider.url.WaitingURL.load()){
			System.out.println("初始化完成！等待命令...");
			//执行SPIDER状态监控方法
			while(true){
				searchengine.spider.controller.Main.doMonitor();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					System.out.println("Run.java 调用Main.doMonitor()时发生异常："+e.getMessage());
					e.printStackTrace();
				}
			}
		}
		out.flush();
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		doGet(request,response);
		out.flush();
		out.close();
	}

	public void init() throws ServletException {
	}

}
