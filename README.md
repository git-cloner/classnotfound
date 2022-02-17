# classnotfound
classnotfound.com.cn,The Chinese version of the findjar.com synchronizes the jar information from the maven center, parses out the class name stored in the database for retrieval, and solves the classnotfoundexception.
## build
```
mvnw package
```
## run
```
export CNF_DBHOST=database host
export CNF_DBUSER=database user
export CNF_DBPASS=database password
nohup java -Xss100m -Xmn350m -Xms1024m -Xmx1024m -jar classnotfound-1.0.0-SNAPSHOT.jar > output.log 2>&1 &
```
## step
### 1. sync first and second root path
```
curl -H "Content-Type: application/json" -X POST -d '{"token":"Y2xhc3Nub3Rmb3VuZC5jb20uY24="}' "http://127.0.0.1:8000/getroots"
##For deep directories, special handling is required,such as:
curl -H "Content-Type: application/json" -X POST -d '{"token":"Y2xhc3Nub3Rmb3VuZC5jb20uY24=","root":"https://repo1.maven.org/maven2/com/google/apis/"}' "http://127.0.0.1:8000/getdeeproot"
```
### 2. sync all jar path
```
curl -H "Content-Type: application/json" -X POST -d '{"token":"Y2xhc3Nub3Rmb3VuZC5jb20uY24="}' "http://127.0.0.1:8000/getalljars"
```
### 3. download lasteast jar and parse classes
```	
curl -H "Content-Type: application/json" -X POST -d '{"headwords": "a,b,d,e,f,g,h,i","token":"Y2xhc3Nub3Rmb3VuZC5jb20uY24="}' "http://127.0.0.1:8000/parseclasses"
curl -H "Content-Type: application/json" -X POST -d '{"headwords": "j,k,l,m,n,p,q,r","token":"Y2xhc3Nub3Rmb3VuZC5jb20uY24="}' "http://127.0.0.1:8000/parseclasses"
curl -H "Content-Type: application/json" -X POST -d '{"headwords": "s,t,u,v,w,x,y,z","token":"Y2xhc3Nub3Rmb3VuZC5jb20uY24="}' "http://127.0.0.1:8000/parseclasses"
curl -H "Content-Type: application/json" -X POST -d '{"headwords": "c,o","token":"Y2xhc3Nub3Rmb3VuZC5jb20uY24="}' "http://127.0.0.1:8000/parseclasses"
```
## db struct
```
CREATE TABLE `cnf_classes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `jar_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `class_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_cnf_classes_class` (`class_name`),
  KEY `idx_cnf_classes_class1` (`jar_hash`,`class_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `cnf_findjars` (
  `id` bigint NOT NULL,
  `class_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `jar` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `size` bigint NOT NULL,
  `upt_date` datetime(6) DEFAULT NULL,
  `file_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `mirror1` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `mirror2` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `mirror3` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `pom_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `cnf_jars` (
  `id` int NOT NULL AUTO_INCREMENT,
  `jar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `jar_hash` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `upt_date` datetime NOT NULL,
  `size` bigint DEFAULT NULL,
  `download_flag` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `short_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_cnf_jar_hash` (`jar_hash`) USING BTREE,
  KEY `idx_cnf_jars_jar` (`jar`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `cnf_otherver` (
  `id` bigint NOT NULL,
  `file_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `jar` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `mirror1` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `mirror2` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `mirror3` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `size` bigint NOT NULL,
  `upt_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `cnf_roots` (
  `id` int NOT NULL AUTO_INCREMENT,
  `root` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `sync_flag` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0',
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_cnf_roots` (`root`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `cnf_visits` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `visit_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0',
  `visit_time` datetime DEFAULT NULL,
  `use_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
```
## jar install
### jre for ubuntu:
```
sudo apt install default-jre
```
### centos install jre17:

```
rpm -qa |grep java
rpm -qa |grep jdk
rpm -qa |grep gcj
rpm -qa | grep java | xargs rpm -e --nodeps
wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.rpm
sudo rpm -ivh jdk-17_linux-x64_bin.rpm

https://www.itzgeek.com/how-tos/linux/how-to-install-oracle-java-jdk-17-on-linux.html
```

