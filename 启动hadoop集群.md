su  hadoop  --切换hadoop用户

start-all.sh  --启动 NameNode、DataNode、ResourceManager 等所有进程。

*stop-all.sh  --停止NameNode、DataNode、ResourceManager 等所有进程。（项目确认没问题再进行此操作）*

**注意：出现（hadoop@0.0.0.0's password: ）密码：1**

[hadoop@master ~]$ jps
2308 NodeManager
1545 NameNode
1850 SecondaryNameNode
2011 ResourceManager
2876 Jps
1677 DataNode
出现这些进程说明启动成功了

浏览器打开以下网址说明集群搭建成功

http://192.168.100.10:50090/

![image-20251101205053944](C:\Users\new\AppData\Roaming\Typora\typora-user-images\image-20251101205053944.png)

http://192.168.100.10:50070/![image-20251101205143863](C:\Users\new\AppData\Roaming\Typora\typora-user-images\image-20251101205143863.png)

http://192.168.100.10:8088/



hadoop fs -cat /pig-system/input/environment_data_20251027093000.csv 
这个命令，其作用是查看 HDFS 中路径 /pig-system/input/ 下名为 environment_data_20251027093000.csv 的文件内容。



