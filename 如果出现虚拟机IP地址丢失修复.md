原因：
1.配置的ip地址已经被占用

2.配置文件写错了

3.有之前的网络缓存

解决：

1.更换一个不重复的ip地址

2.检查配置文件

3.删除之前的缓存
systemctl stop NetworkManager   #停止NetworkManager 服务

 rm -rf /var/lib/NetworkManager/* #清除缓存

systemctl start NetworkManager  #开启NetworkManager 服务

