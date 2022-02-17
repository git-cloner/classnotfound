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

​    For the hierarchical and indeterminate directory structure of https://repo1.maven.org/maven2/, the method of obtaining the last level of jars requires recursion, but the deeper the recursion, the larger the stack space required , the worse the performance, so in the specific implementation, the first three-level directory is stored in a circular manner, and then the third-level directory (such as https://repo1.maven.org/maven2/abbot/costello/) is used as Basically, recursively search for jars, and ignore auxiliary jars such as test and javadoc, so that the number of jars to be downloaded will be greatly reduced.

##### 2. Download the jar package and parse it

​    Download the jars one by one according to the path of the jar package, parse the classname inside, and delete the jars after parsing. When downloading jars, you do not need to download all the jars, just download the latest version, so that the latest jar can be found by the class name. If you need the download link of the previous version, you can directly extract the version number from the database according to the path name of the jar and then look for it fuzzy.

##### 3. Mirror pool

​    In order to improve the download speed, you need to download jars from multiple mirrors at the same time. The available mirrors include maven official mirror, 163 mirror and Alibaba Cloud mirror. When downloading, files are randomly selected from the mirror pool to download. The jar package is mainly concentrated in com, net, io, org and other paths, and needs to be downloaded step by step in alphabetical order.

