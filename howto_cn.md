### 我是如何实现classnotfound.com.cn的

#### 一、问题的提出

​	在开发java应用时，经常遇到ClassNotFoundException错误，意思是的找不到指定的类，有时也会出现NoClassDefFoundError错误，其原因是缺少相应的jar包，或jar包未放到编译程序可识别的目录下。问题的原因很简单，也很好定位，但单纯通过类名去查找缺少哪个jar，还是有一定难度的。有一个国外网站findjar.com，实现了通过类名查找jar的功能，但这个网站可能是由于访问量较大，网站经常打不开，而且访问速度很慢，所以产生了开发类似网站的想法。

​	经过一段时间的开发和数据整理工作，目前https://classnotfound.com.cn已上线，源代码已在https://github.com/git-cloner/classnotfound开源。

#### 二、原理

​	以类名查找jar，原理很简单，就是找到尽量多的jar，从jar中解析出所有的类名，存到数据库中，建立一个网站供使用者查找，是一个数据积累的过程，主要有以下重点工作：

##### 1、jar包的获取

​	java开发，一般会用到maven方式管理应用，maven仓库成为了jar包最多的地方，从https://repo1.maven.org/maven2/可以找到大部分jar包，开发一个下载程序，将maven仓库中的jar逐个下载到本地，进行解析存储。目前classnotfound.com.cn下载了700多万个jar包。

##### 2、数据库的建立

​	将jar与classname的对应关系建表，存储到mysql数据库中，进行必要的查询条件优化和查询结果分页，就可以建立起数据库。

##### 3、网站的建立

​	使用spring boot和thymeleaf模板，开发一个网页应用，即可提供服务了。

#### 三、关键点

##### 1、递归获取jar包路径

​	对于https://repo1.maven.org/maven2/这个的逐级且深度不定的目录结构，获取最后一级jar的方法是要用到递归的，但由于递归越深，需要的栈空间越大，性能越差，所以在具体实现中，是将前三级目录先用循环方式存储下来，然后以第三级目录（如https://repo1.maven.org/maven2/abbot/costello/）为基础，递归查找jar，且忽略掉test、javadoc之类的辅助jar，这样需要下载的jar的个数就会大大降低。

##### 2、下载jar包并解析

​	按照jar包的路径，逐个下载jar解析里面的classname，解析完成后将jar删除即可。在下载jar时，不需要将所有jar全部下载，只要下载最新的版本即可，这样按类名可以找到最新的jar，如果需要以前的版本下载链接，可以直接从数据库中按jar的路径名抽取版本号后模糊查找。

##### 3、镜像池

​	为了提高下载速度，需要从多个镜像同时下载jar，可用的镜像有maven官方镜像、163镜像和阿里云镜像，下载时随机从镜像池中选取文件下载。jar包主要集中在com、net、io、org等路径，下载时需要按字母表顺序分步下载。