# 2018-09-30
 * [下载 Solr 7.5](http://mirror.bit.edu.cn/apache/lucene/solr/7.5.0/solr-7.5.0.zip)
 * 安装 Solr
    * 解压缩到 `D:\programs\solr-7.5.0`,`bin`目录位于此目录下面
 * 启动 Solr 服务器
    * 在目录 `D:\programs\solr-7.5.0\bin` 和命令行中执行启动脚本`solr start -p 8984` (这里8984是指定的端口)
    * 访问地址 'http://localhost:8984' 看到 Solr 控制台
 * 创建第一个 Solr Core (使用Solr Admin UI)， 假定要创建的 Core 的名字是 `firstCore`
    * 在目录 `solr-7.5.0\server` 下创建目录 `firstCore` , 目录名字和要新建的 Core 的名字一致
    * 复制目录 `solr-7.5.0\server\solr\configsets\_default\conf` 到新建的目录 `solr-7.5.0\server\firstCore`  下面
    * 在 Solr 控制台的 `Core Admin` 功能区点击按钮 `Add Core`,填写以下信息
        * name : firstCore
        * instanceDir : firstCore
        * dataDir : data
        * config : solrconfig.xml
        * schema : schema.xml
 * 创建第二个 Solr Core (使用Solr command)， 假定要创建的 Core 的名字是 `secondCore`
    * 命令行窗口下运行 `solr create -c secondCore`
```
  WARNING: Using _default configset with data driven schema functionality. NOT RECOMMENDED for production use.
           To turn off: bin\solr config -c secondCore -p 8984 -action set-user-property -property update.autoCreateFields -value false
  INFO  - 2018-09-30 14:47:07.324; org.apache.solr.util.configuration.SSLCredentialProviderFactory; Processing SSL Credential Provider chain: env;sysprop

  Created new core 'secondCore'
```

> 备注 : 一个Solr Core是一个包含索引和配置文件的运行实例，以前Solr Core是单例模式的，后来重构成了多实例的

 * [Solr 安装目录结构的介绍](https://blog.csdn.net/bskfnvjtlyzmv867/article/details/80940089)
    * bin：包括一些使用Solr的重要脚本
        * solr和solr.cmd：分别用于Linux和Windows系统，根据所选参数不同而控制Solr的启动和停止
        * post：提供了一个用于发布内容的命令行接口工具。支持导入JSON，XML和CSV，也可以导入HTML，PDF，Microsoft Office格式（如MS Word），纯文本等等。
        * solr.in.sh和solr.in.cmd：分别用于Linux和Windows系统的属性文件
        * install_solr_services.sh：用于Linux系统将Solr作为服务安装
    * contrib：包含一些solr的一些插件或扩展
        * analysis-extras： 包含一些文本分析组件及其依赖
        * clustering：包含一个用于集群搜索结果的引擎
        * dataimporthandler：把数据从数据库或其它数据源导入到solr
        * extraction：整合了Apache Tika，Tika是用于解析一些富文本(诸如Word，PDF)的框架
        * langid：检测将要索引的数据的语言
        * map-reduce：包含一些工具用于Solr和Hadoop Map Reduce协同工作
        * morphlines-core：包含Kite Morphlines，它用于构建、改变基于Hadoop进行ETL（extract、transfer、load）的流式处理程序
        * uima：包含用于整合Apache UIMA（文本元数据提取的框架）类库
        * velocity：包含基于Velocity模板的简单的搜索UI框架
    * dist：包含主要的Solr的jar文件
    * docs：文档
    * example：包含一些展示solr功能的例子
        * exampledocs：这是一系列简单的CSV，XML和JSON文件，可以bin/post在首次使用Solr时使用
        * example-DIH：此目录包含一些DataImport Handler（DIH）示例，可帮助您开始在数据库，电子邮件服务器甚至Atom订阅源中导入结构化内容。每个示例将索引不同的数据集
        * files：该files目录为您可能在本地存储的文档（例如Word或PDF）提供基本的搜索UI
        * films：该films目录包含一组关于电影的强大数据，包括三种格式：CSV，XML和JSON
    * licenses：包含所有的solr所用到的第三方库的许可证
    * server：solr应用程序的核心，包含了运行Solr实例而安装好的Jetty servlet容器。
        * contexts：这个文件包含了solr Web应用程序的Jetty Web应用的部署的配置文件
        * etc：主要就是一些Jetty的配置文件和示例SSL密钥库
        * lib：Jetty和其他第三方的jar包
        * logs：Solr的日志文件
        * resources：Jetty-logging和log4j的属性配置文件
        * solr：新建的core或Collection的默认保存目录，里面必须要包含solr.xml文件
        * configsets：包含solr的配置文件
        * solr-webapp：包含solr服务器使用的文件；不要在此目录中编辑文件(solr不是JavaWeb应用程序)

* `SolrHome` 和 `SolrCore`
    * SolrHome：SolrHome 是solr服务运行的主目录，一个 SolrHome 目录里面包含多个 SolrCore 目录 ；
    * SolrCore ：SolrCore 目录里面了一个solr实例运行时所需要的配置文件和数据文件，每一个 SolrCore 都可以单独对外提供搜索和索引服务，多个 SolrCore 之间没有关系；

* Solr 查询条件
    * q - 查询关键字，必须的，如果查询所有使用*:*。请求的q是字符串；
        * 例子 : `title:手机 AND sellPoint:移动`
    * fq - (filter query)过虑查询，在q查询符合结果中同时是fq查询符合的。例如：请求fq是一个数组（多个值）；
        * 例子 : `id:[1000000 TO 1200000]`
    * sort - 排序；
        * Solr Admin UI 例子 : `id desc`
        * SolrJ 例子 : `query.addSort("id", SolrQuery.ORDER.desc)`
    * start - 分页显示使用，开始记录下标，从0开始；
    * rows - 指定返回结果最多有多少条记录，配合start来实现分页；
    * fl - 指定返回那些字段内容，用逗号或空格分隔多个 ；
        * Solr Admin UI 例子 : `id,title,sellPoint,price,status`
        * SolrJ 例子 : `query.setFields(new String[]{"id", "title", "sellPoint", "price", "status" })`
    * df-指定一个默认搜索Field；
    * wt - (writer type)指定输出格式，可以有 xml, json, php, phps；
    * hl 是否高亮，设置高亮Field，设置格式前缀和后缀。
        * SolrJ 例子 :
```
    query.setHighlight(true);
    query.addHighlightField("title");
    query.setHighlightSimplePre("<span color='red'>");
    query.setHighlightSimplePost("</span>");
```
* Solr 常用命令

```
solr start –p 端口号 // 单机版启动solr服务

solr restart –p 端口号 // 重启solr服务

solr stop –p 端口号 // 关闭solr服务

solr create –c name // 创建一个core实例(core概念后面介绍)
```
---------------------

本文来自 眼望天空 的CSDN 博客 ，全文地址请点击：https://blog.csdn.net/u010510107/article/details/81051795?utm_source=copy

* 参考资料
    * [solr7.1.0学习笔记](https://blog.csdn.net/column/details/18689.html)

