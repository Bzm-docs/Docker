# docker

[TOC]

## 什么是容器

![img](https://img-blog.csdnimg.cn/20191202174630282.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

---

![img](https://img-blog.csdnimg.cn/2019120218023664.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

```
在一个宿主机上跑多个应用，之前物理机不能做到隔离，现在容器可以进行隔离
```

##### namespace

```
容器就是用这个进行隔离的
```

##### cgroup

- cgroup不是完整的限制，而是优先级
- 硬件资源限制（cpu用量，mem内存用量，disk硬盘用量）



![img](https://img-blog.csdnimg.cn/20191202184203406.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

##### 容器和虚拟机

![img](https://img-blog.csdnimg.cn/2019120218034867.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

```
容器的性能已经接近裸机性能
```

##### docker的特性

![img](https://img-blog.csdnimg.cn/20191202184741103.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/2019120218571699.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20191202190033100.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

##### docker的核心组件

![img](https://img-blog.csdnimg.cn/20191202190310342.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

##### docker架构图

![img](https://img-blog.csdnimg.cn/20191202191225960.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

##### docker镜像

![img](https://img-blog.csdnimg.cn/20191202191802163.png)

```
镜像是一个动态的，通过一个镜像部署一个容器，是不会修改镜像本身的
```

**命名规则**

![img](https://img-blog.csdnimg.cn/2019120219240381.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

##### docker容器

![img](https://img-blog.csdnimg.cn/20191202193720694.png)

##### 镜像仓库

![img](https://img-blog.csdnimg.cn/20191202193846340.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

**Build,Ship,Run**

![img](https://img-blog.csdnimg.cn/20191202193949145.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

## docker安装与基本配置

##### 关闭防火墙

```
firewalld
selinux
[root@docker ~]# iptables -F
[root@docker ~]# iptables -X
[root@docker ~]# iptables -Z
[root@docker ~]# iptables-save 
```

##### 检查内核转发功能

```
sysctl -a 看所有

/etc/sysctl.conf

net.ipv4.ip_forward = 1			IP转发功能
net.ipv4.conf.default.rp_filter = 0
net.ipv4.conf.all.rp_filter = 0

若无添加以上内容，sysctl -p	将读取里面的内容


```

##### 安装docker服务

##### internet安装

[阿里云镜像站安装](https://developer.aliyun.com/mirror/)

[docker 安装报错 container-selinux >= 2.9 解决](https://blog.csdn.net/qq_41772936/article/details/81080284)

[Docker 国内镜像库加速](https://www.jianshu.com/p/1a4025c5f186)

## docker容器管理

- 工具类的容器

    ```
    web server ,database等
    ```

- 服务类的容器

    ```
    容器为任务而生，如curl容器，redis-cli容器
    
    [root@server images]# docker run centos /bin/echo hello
    hello
    ```

##### 容器的启动过程

![img](https://img-blog.csdnimg.cn/20191206184708262.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

##### docker run

```
等于执行的docker create 和docker start

[root@server ~]# docker -d -p <宿主机端口>:<容器端口> httpd
首先会查看本地有没有这个镜像

-p		<宿主机端口>:<容器端口>	映射
-P		随机映射
-i		以交互模式运行容器，通常与 -t 同时使用
-t		为容器重新分配一个伪输入终端，通常与 -i 同时使用
-d		将容器运行在后台并打印id

--name		给容器指定名字
-e		
```

##### docker ps

| CONTAINER ID | 容器id         |
| ------------ | -------------- |
| IMAGE        | 使用的哪个镜像 |
| COMMAND      | 命令           |
| CREATED      | 什么时候创建的 |
| STATUS       | 持续运行的时间 |
| PORTS        | 映射           |
| NAMES        | 容器的名字     |

```
[root@server ~]# docker ps -a
列出容器的信息
-a		显示所有容器（默认显示正在运行的）
```

##### docker create

```
容器创建
```

##### docker start

```
启动一个或者更多的容器

[root@server ~]# docker start [id...]
```

##### docker stop/docker kill

```
容器停止
```

##### docker restart 

```
容器重启
```

##### docker rm

```
删除容器

docker rm <id>
删除镜像
docker -rmi <id>
```

```
# 停止和删除Exited的docker实例
docker ps -a | grep "Exited" | awk '{print $1 }'|xargs docker stop
docker ps -a | grep "Exited" | awk '{print $1 }'|xargs docker rm
# 删除none的镜像
docker images|grep none|awk '{print $3 }'|xargs docker rmi
```

##### docker exec

```
进入容器

用于容器启动后，执行其他任务
docker exec -it $container_name /bin/bash
docker exec -it $container_id /bin/bash

[root@server ~]# docker exec -it 45804b /bin/bash
root@45804bb864d9:/usr/local/tomcat#            
```

##### docker cp

```
在容器和本地文件系统之间复制文件/文件夹

docker cp /www/runoob 96f7f14e99ab:/www/

docker cp  96f7f14e99ab:/www /tmp/
```

##### docker inspect

```
查看容器的详细信息
```

##### docker search

```
[root@server ~]# docker search <关键字>

就会列出与这个关键性相关的所有容器
```



##### 容器生命周期管理

![img](https://upload-images.jianshu.io/upload_images/15104791-82be76d368e9159d.jpg?imageMogr2/auto-orient/strip|imageView2/2)

##### 五种状态

- created：初建状态
- running：运行状态
- stopped：停止状态
- paused： 暂停状态
- deleted：删除状态

## 容器的资源限制

##### 为什么要限制资源

```
一个docker host上会运行若干个容器，每个容器都需要CPU、内存和io资源。对于VMware等虚拟化技术，用户可以控制分配多少CPU、内存资源给每个虚拟机。对于容器，docker也提供了类似的机制避免某个容器因占用太多资源而影响其他容器乃至整个host的性能。
```

##### 内存限制

```
用cgroup做的

-m		设置容器使用内存最大值
--memory-swap string   允许分配内存和swap的总大小
--memory-swappiness int		控制内存和swap的比例

[root@server ~]# docker run -it -m 200M --memory-swap 300M centos:lastest
[root@e50318a8287e /]# 

```

测试

```
docker run -it -m 200M -memory-swappiness 0 progrium/stree --vm 1 --vm-bytes 180M
--vm		设置内存工作线程数
--vm-bytes		设置单个内存工作线程使用内存大小

progrium/stree		压力测试专用容器
```

###### cgroup

centos7

```
[root@server ~]# lssubsys -am
cpuset /sys/fs/cgroup/cpuset
cpu,cpuacct /sys/fs/cgroup/cpu,cpuacct
memory /sys/fs/cgroup/memory
devices /sys/fs/cgroup/devices
freezer /sys/fs/cgroup/freezer
net_cls /sys/fs/cgroup/net_cls
blkio /sys/fs/cgroup/blkio
perf_event /sys/fs/cgroup/perf_event
hugetlb /sys/fs/cgroup/hugetlb

查看系统中已经存在的参cgroup子系统以及子系统的挂载点
[root@server ~]# cd /sys/fs/cgroup/
[root@server cgroup]# ls
blkio  cpu  cpuacct  cpu,cpuacct  cpuset  devices  freezer  hugetlb  memory  net_cls  perf_event  systemd
```

在linux系统中有/etc/cgconfig.conf 文件，在这个文件里面可以创建自动开机自动启动的挂载条目：

```
      mount {#定义需要创建的cgroup子系统及其挂载点，这里创建cpu与cpuacct（统计）两个cgroup子系统
                     cpu = /mnt/cgroups/cpu;
                     cpuacct = /mnt/cgroups/cpu;
              }
```

```
[root@server blkio]# cat blkio.throttle.read_iops_device 
一般其中没有值，就是不进行限制
```

![img](https://img-blog.csdnimg.cn/20191213105642591.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

vim /etc/cgrules.conf 

```
这里面可以做到更精细化的控制
```

![img](https://img-blog.csdnimg.cn/20191213143239419.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

/etc/init.d/cgred start

```
启动规则
```

##### CPU限额

```
-c 		设置容器使用cpu的权重
docker run --name 'container A' -c 1024 progrium/stree

--cpu		用来设置cpu工作线程的数量
```



##### Block IO 限额

```
默认情况下，容器能平等的读写磁盘
-blkio-weight		设置block IO的优先级
同样是相对权重值，默认500
```

##### 限制bps和iops

![img](https://img-blog.csdnimg.cn/20191210100039748.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

```
docker run -it --device-write-bps 
```

## docker镜像管理

#### 镜像的基本操作

![img](https://img-blog.csdnimg.cn/20191210105428683.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

##### docker images

```
列出镜像
```

##### docker rmi

```
删除容器
[root@server ~]# docker rm <id>
-f		强制删除
```

##### docker pull

```
从镜像仓库拉取或者更新指定镜像

下载到本地
```

##### docker push 

```
推送镜像
```

##### docker save -o

```
导出镜像

[root@server ~]# docker save -o nginx.tar nginx
[root@server ~]# ls  
nginx.tar
```

##### docker load

```
导入镜像
```

#### 镜像的命令规范

![img](https://img-blog.csdnimg.cn/20191210113215592.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

#### Docker文件系统

![img](https://img-blog.csdnimg.cn/20191210201928930.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

docker base镜像

![img](https://img-blog.csdnimg.cn/20191210202213939.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

镜像分层结构

![img](https://img-blog.csdnimg.cn/20191210202408743.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

docker存储驱动

![img](https://img-blog.csdnimg.cn/20191210202507595.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20191213153508555.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

```
所有的操作是写在读写层的
```

![img](https://img-blog.csdnimg.cn/20191211083807573.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

**常用存储驱动对比**

| 存储驱动     |                   特点                   |                             优点                             |                             缺点                             |              适用场景              |
| ------------ | :--------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: | :--------------------------------: |
| AUFS         | 联合文件系统、未并入内核主线、文件级存储 | 作为docker的第一个存储驱动，已经有很长的历史，比较稳定，且在大量的生产中实践过，有较强的社区支持 | 有多层，在做写时复制操作时，如果文件比较大且存在比较低的层，可能会慢一些 |         大并发但少IO的场景         |
| overlayFS    |  联合文件系统、并入内核主线、文件级存储  |                           只有两层                           | 不管修改的内容大小都会复制整个文件，对大文件进行修改显示要比小文件消耗更多的时间 |         大并发但少IO的场景         |
| Devicemapper |          并入内核主线、块级存储          | 块级无论是大文件还是小文件都只复制需要修改的块，并不是整个文件 | 不支持共享存储，当有多个容器读同一个文件时，需要生成多个复本，在很多容器启停的情况下可能会导致磁盘溢出 |          适合io密集的场景          |
| Btrfs        |        并入linux内核、文件级存储         |   可以像devicemapper一样直接操作底层设备，支持动态添加设备   | 不支持共享存储，当有多个容器读同一个文件时，需要生成多个复本 | 不适合在高密度容器的paas平台上使用 |
| ZFS          |  把所有设备集中到一个存储池中来进行管理  |         支持多个容器共享一个缓存块，适合内存大的环境         | COW使用碎片化问题更加严重，文件在硬盘上的物理地址会变的不再连续，顺序读会变的性能比较差 |       适合paas和高密度的场景       |

![img](https://img-blog.csdnimg.cn/2019121109191495.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

- Backing Filesystem指得是在/var/lib/docker创建主机本地的存储区域文件系统

![img](https://img-blog.csdnimg.cn/2019121109254114.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

[docker存储驱动](https://www.cnblogs.com/breezey/p/9589288.html)

#### 制作Docker镜像

##### docker commit

![img](https://img-blog.csdnimg.cn/20191211093219251.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20191211095718590.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

**supermin5**

```
-v：显示调试消息
--prepare：后跟要安装的软件包
-o：输出目录
--build：构建完整镜像
```

##### Dockerfile

- 基础镜像信息
- 维护者信息
- 镜像操作指令
- 容器启动时执行指令。

![img](https://img2018.cnblogs.com/blog/450977/201905/450977-20190512115951746-136143052.png)

示例

![img](https://img-blog.csdnimg.cn/20191211110403360.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

- 字段越少越好，每加一个就会在原本镜像上加一个读写层

```
FROM 10.0.3.138:5000/centos:latest			#导入一个模板
MAINTAINER Xiandian		#维护者信息
RUN rm -fv /etc/yum.repos.d/*
ADD local.repo /etc/yum.repos.d/		#ADD没有加路径，就是从当前目录添加到容器的目录下
ADD yum.tar /opt/
RUN yum clean all
RUN yum install -y java unzip
ENV LC_ALL en_US.UTF-8
ADD apache-tomcat-7.0.56.zip  /root/apache-tomcat-7.0.56.zip
RUN unzip /root/apache-tomcat-7.0.56.zip -d /root/
EXPOSE 8081
RUN chmod u+x /root/apache-tomcat-7.0.56/bin/*
ADD jenkins.war /root/apache-tomcat-7.0.56/webapps/ROOT.war
ENV CATALINA_HOME /root/apache-tomcat-7.0.56
CMD ${CATALINA_HOME}/bin/catalina.sh run
```

![img](https://img-blog.csdnimg.cn/20191211193741530.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20191211194939444.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20191216094907806.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20191216095116793.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20191216103558797.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20191216105134473.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

**FROM**

```
指明构建的新镜像是来自于哪个基础镜像

FROM centos:6
```

**MAINTAINET**

```
指明镜像维护着及其联系方式（一般是邮箱地址）

MAINTAINER Edison Zhou <edisonchou@hotmail.com>
```

**RUN**

```
构建镜像时运行的Shell命令

RUN ["yum", "install", "httpd"]
RUN yum install httpd
```

**CMD**

```
启动容器时执行的Shell命令

CMD ["-C", "/start.sh"] 
CMD ["/usr/sbin/sshd", "-D"] 
CMD /usr/sbin/sshd -D
```

**EXPOSE**

```
暴露端口
```

**ENV**

```
可以用于docker容器设置环境变量

ENV <key> <value>
ENV <key>=<value> ...

ENV TZ "Asia/Shanghai"
```

**ADD**

```
ADD <src> <dest>
复制

<src>可以是Dockerfile所在的相对路径，也可以是一个url，或者是tar文件(会自动解压)
ADD local.repo /etc/yum.repos.d/
```

**COPY**

```
COPY <src> <dest>
和ADD类似
<src>必须是build下中的路径，不能是其父目录中的文件
如果<src>是目录，则其内部文件或子目录会被递归复制，但<src>目录自身不会被复制
如果指定了多个<src>,或在<src>中使用了通配符，则<dest>必须是一个目录，且必须以/结尾
如果<dest>事先不存在，它将会被自动创建，这包括其父目录路径。
```

**WORKDIR**

```
为Dockerfile中所有RUN、CMD、ENTRYPOINT、COPY和ADD指令设定工作目录

WORKDIR <相对路径或者绝对路径>
相对路径是相对于上一个WORKDIR指令的路径，如果上面没有WORKDIR指令，那就是当前Dockerfile文件的目录。

进入容器时，默认进的目录
```

**USER**

```
用于指定运行image时的或运行Dockerfile中任何RUN、CMD或ENTRYPOINT指令指定的程序时的用户名或UID
默认情况下，container的运行身份为root用户

USER <UID>|<UserName>
```

**VOLUME**

```
创建一个挂载点

指定容器挂载点到宿主机自动生成的目录或其他容器

VOLUME /storage
在这个目录的文件，在容器删除时候，文件可能不会被删除

这个挂载点，会和宿主机一个目录相关联，在其进行复制删除等操作是同效的

[root@server ~]# docker inspect -f {{.Mounts}} 083b
[{429861b2793a124699e2f381b693e9567b6911858c26cc3d337ea42bde8bdb6c /var/lib/docker/volumes/429861b2793a124699e2f381b693e9567b6911858c26cc3d337ea42bde8bdb6c/_data /DATA local  true }]
查看在宿主机挂载的目录
```

**ONBUILD**

```

```

**构建镜像**

```
[root@server web]# docker build -t http:v1.0 .
```

##### Dockerfile最佳实践

![img](https://img-blog.csdnimg.cn/20191216105517878.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

##### 镜像存储位置

```
[root@server ~]# vim /etc/docker/daemon.json 

[root@server ~]# cd /var/lib/docker/
[root@server docker]# ls
containers  devicemapper  image  network  swarm  tmp  trust  volumes
```

## 容器的迁移与管理

### 容器的迁移

ff

**方法一**

- 导出容器成镜像tar

    ```
    docker export <id> > <name>
    id:容器id
    name:导成文件后的名字
    
    [root@server Dockerfile]# docker export db04cbb >centos2.0.tar
    [root@server Dockerfile]# ls
    centos2.0.tar  Dockerfile
    ```

- 导入镜像

    ```
    cat <tar名> | docker import - <name>
    name；导入成镜像的名字
    
    [root@server Dockerfile]#  cat centos2.0.tar | docker import - centos:3.0
    sha256:9f5643a7967839e26c6b76004537ed247aa6b54b9288e0c4c3e4c741404ea8da
    ```

- 缺点

    ```
    之前有分层的全部打包成一层了
    ```

**方法二(优先推荐使用)**

- 导出容器为镜像

    ```
    [root@server ~]# docker commit -m "backup" ce82f2c12a centos:v2.0
    sha256:3f23f4c0459eaba169c311f0d8525da8994c0fd8de5792859125dbb4c74c20b6
    ```

- 将镜像导成tar包

    ```
    [root@server ~]# docker save -o centos.tar centos:v2.0
    ```

- 将tar包导成镜像

    ```
    [root@server ~]# docker load -i centos.tar 
    ```

- 优点

    ```
    将所有元数据都打包了。id都没变
    ```

    

### Docker的数据管理与网络管理

#### Docker的数据管理

##### 数据卷

![img](https://img-blog.csdnimg.cn/20191216180608530.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

```
挂载宿主机的目录作为数据卷(一般是用的分布式存储)

数据卷是设计用来持久化数据的，它的生命周期独立于容器，所有容器被删除后，数据卷默认不会被删除
```

- 创建数据卷

    ```
    docker run -d -v <宿主机目录> <容器目录> centos:lastest /bin/bash
    
    [root@server ~]# docker run -it --name centos -v /mnt/centos/:/mnt/  -P centos:lastest /bin/bash
    
    
    [root@server ~]# docker run -it --name centos -v /mnt/centos/:/mnt/:ro  -P centos:lastest /bin/bash
    ro:只读
    ```

- 删除数据卷

    ```
    docker rm -fv <id>
    ```

- 数据卷容器

    ![img](https://img-blog.csdnimg.cn/2019121710042244.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

- 数据卷容器备份与恢复

    ![img](https://img-blog.csdnimg.cn/20191217100657517.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

#### Docker的网络管理

##### 查看docker网络

```
[root@server ~]# docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
58c2eb597e8d        bridge              bridge              local               
6a12938542d2        host                host                local               
7c0135e547cc        none                null                local               
```

```
docker创建时候，会默认创建这三个网络 
```



##### Docker网络模式

![img](https://img-blog.csdnimg.cn/20191217102803955.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

```
[root@server ~]# docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
58c2eb597e8d        bridge              bridge              local               
6a12938542d2        host                host                local               
7c0135e547cc        none                null                local             
```

![img](https://img-blog.csdnimg.cn/20191217110749667.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

```
底层就是调用的iptables规则
```

![img](https://img-blog.csdnimg.cn/20191217140458109.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

##### bridge网络

![img](https://img-blog.csdnimg.cn/20191217144245463.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

```
默认的网关就是宿主机的docker0的ip
```



##### none网络

![img](https://img-blog.csdnimg.cn/20191217145735608.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNjYwNjIw,size_16,color_FFFFFF,t_70)

```
比如某个容器唯一的用途是生成随机密码，就可以放到none网络中，防止被窃取
```

##### host网络

![img](https://img-blog.csdnimg.cn/20191217152732118.png)

```
用得就是宿主机的网络(和宿主机的网络一样)

这样最大的好处就是性能，如果容器对网络的传输效率有较高的要求，则可以采用host网络。

缺点：就是要考虑端口冲突的问题
```

##### 自定义网络

创建网络

```
docker network create --driver=bridge <网络名>

--subnet <子网>
--subnet=192.168.10.0/24
```

```
[root@server ~]# docker network create --driver=bridge my_net
2951565c719c585ff345127315a211b5516e859f0137a17e45c6d0b634cc1e55
[root@server ~]# docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
58c2eb597e8d        bridge              bridge              local               
6a12938542d2        host                host                local               
2951565c719c        my_net              bridge              local               
7c0135e547cc        none                null                local               
```



#### Flanne1的网络配置

### Docker镜像仓库

==21==

