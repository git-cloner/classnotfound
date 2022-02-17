### How do I implement classnotfound.com.cn

#### Chapter I. raising problems

​    When developing Java applications, we often encounter ClassNotFoundException errors, which means that the specified class cannot be found, and sometimes NoClassDefFoundError errors occur. The reason is that the corresponding jar package is missing or the jar package is not placed in a directory recognized by the compiler. The reason for the problem is simple and easy to locate, but it is still difficult to find which jar is missing simply by class name. There is a foreign website findjar.com, which realizes the function of finding jar through class name, but this website may be due to the large number of visits, the website often can't open, and the access speed is very slow, so the idea of developing similar websites came into being.

​     After a period of development and data sorting, at present https://classnotfound.com.cn Online, source code in https://github.com/git-cloner/classnotfound Open source.

#### Chapter II. principle

​	The principle of finding jars by class name is very simple. It is to find as many jars as possible, parse all class names from jars, store them in the database, and establish a website for users to find. It is a process of data accumulation. The main tasks are as follows:

##### 1. Acquisition of jar packae

​     Java development generally uses Maven to manage applications. Maven warehouse has become the place with the most jar packages https://repo1.maven.org/maven2/ You can find most jar packages, develop a download program, download the jars in Maven warehouse one by one to the local, analyze and store them. Currently classnotfound.com.cn downloaded more than 7 million jar packages.

##### 2. Establishment of database

​    The database can be established by building a table of the corresponding relationship between jar and classname, storing it in the mysql database, optimizing the necessary query conditions and paging the query results.

##### 3. Establishment of website

​    Use spring boot and thymeleaf template to develop a web application and provide services.

#### Chapter III. key points

##### 1. Get jar package path recursively

 	about https://repo1.maven.org/maven2/ In this hierarchical and variable depth directory structure, recursion is used to obtain the last level jar. However, the deeper the recursion is, the larger the stack space is required and the worse the performance is. Therefore, in the specific implementation, the first three levels of directories are stored in a circular manner, and then in the third level directory (such as https://repo1.maven.org/maven2/abbot/costello/ ）Based on, recursively find jars and ignore auxiliary jars such as test and javadoc, so the number of jars to download will be greatly reduced.

##### 2. Download the jar package and parse it

 	Download the jars one by one according to the path of the jar package, parse the classname inside, and delete the jars after parsing. When downloading jars, you don't need to download all jars, just download the latest version. In this way, you can find the latest jar by class name. If you need the download link of previous version, you can directly extract the version number from the database according to the path name of jar and fuzzy search.

##### 3. Mirror pool

 	In order to improve the download speed, you need to download jars from multiple images at the same time. The available images include Maven official image, 163 image and Alibaba cloud image. When downloading, randomly select files from the image pool to download. Jar packages are mainly concentrated in com, net, io, org and other paths. When downloading, you need to download them step by step in alphabetical order.

