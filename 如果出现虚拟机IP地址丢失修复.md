# 虚拟机IP地址丢失修复指南

## 问题描述

虚拟机中配置的静态IP地址丢失或无法获取IP地址。

## 常见原因

1. **配置的IP地址已经被占用**
   - 网络中其他设备使用了相同的IP地址
   
2. **配置文件写错了**
   - 网络配置文件语法错误或参数设置不正确
   
3. **有之前的网络缓存**
   - NetworkManager服务缓存了旧的网络配置

## 解决方法

### 方法1：更换一个不重复的IP地址

```bash
# 编辑网络配置文件
sudo vi /etc/sysconfig/network-scripts/ifcfg-ens33

# 修改IP地址为未被占用的地址
IPADDR=192.168.100.11  # 确保此IP未被使用
```

### 方法2：检查配置文件

```bash
# 查看网络配置
cat /etc/sysconfig/network-scripts/ifcfg-ens33

# 确认配置正确，示例：
BOOTPROTO=static
IPADDR=192.168.100.10
NETMASK=255.255.255.0
GATEWAY=192.168.100.1
DNS1=8.8.8.8
ONBOOT=yes
```

### 方法3：清除网络缓存

```bash
# 停止NetworkManager服务
systemctl stop NetworkManager

# 清除缓存
rm -rf /var/lib/NetworkManager/*

# 启动NetworkManager服务
systemctl start NetworkManager

# 重启网络服务
systemctl restart network
```

## 验证修复

```bash
# 查看IP地址
ip addr show

# 测试网络连接
ping -c 4 8.8.8.8

# 查看路由
ip route
```

## 预防措施

1. **使用静态IP时，记录已使用的IP地址**，避免冲突
2. **定期备份网络配置文件**
3. **使用虚拟机快照**，便于快速恢复

