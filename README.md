


# 一个微型java全文搜索引擎. 
该项目实现于2009年。是作者的毕业设计。是一个J2EE开发的极其精简的全文搜索引擎。虽然它很小，但是实现了搜索引擎的基本模块。它包含：爬虫模块、词库、自然语言处理、词语-文档-索引管理、检索模块。  
基本原理如图：

![logic](http://jsper.org/microSearchEngine/logic.png)


### 用法
1. 建立一个词库文本文件，每个词语一行。
2. 修改程序里词库路径的值。
3. 创建一个要抓取的url文本文件。
3. 将程序跑起来，访问localhost/Run启动引擎，访问localhost/Start开始爬网络抓取网页，访问localhost/Stop停止抓取，访问localhost搜索。



