# vigilant-octo-carnival
```
vigilant-octo-carnival
用途:爬取上海交易所,香港交易所,深圳交易所的股票债券指数的历史交易数据
使用:新建个maven工程文件，代码下载，根据提示安装依赖即可,每个交易所的main函数就是开始爬取,AfterF后缀是经过后复权后的数据
tip:使用前先建个mysql，这是建表语句:
      CREATE TABLE 香港全览_不复权 (
      Ccode varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
      Ctime varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
      Cname varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
      Ctype varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
      Copen float NOT NULL, Chigh float NOT NULL, Clow float NOT NULL, Cpreclose float NOT NULL,
      Cvolume varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL, 
      Ctransactions varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
      Mtype varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
      IsGang varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
      primarykey(Ccode,Ctime)) 
      ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
表明就"全览_"不变 其他根据交易所位置和复权类型改变(不复权,后复权)
ippool的作用:获取代理ip
LinkDatabase的作用:一些数据库的基本操作函数
Spildertools的作用:提供分解字符串和根据url获取页内文本的函数
```
