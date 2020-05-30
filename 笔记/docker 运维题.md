# docker 运维题

[TOC]

配置 docker 容器实现 nginx 的负载均衡，需要修改 nginx 的配置文件，请把定义 tomcat 负载均衡的参数以文本形式提交到答题框。

```
//运行两台tomcat服务 随机映射端口 并命名tom1 and tom2
[root@server ~]# docker run -d -P --name tom1 tomcat
412c6d52ba4c417a5359dc8de53b6a74443af5841d91a45fb3af7dd7d7b85973
[root@server ~]# docker run -d -P --name tom2 tomcat
9782d5c8403369535cc76c104035bd1c9729f2373c3c1c6318f3431b80bdd33c

//运行nginx服务 随机映射端口 并命名ng1
[root@server ~]# docker run -d -P --name ng1 --restart=always nginx
479a752cf563ef4b4ef9b030a5b1a128b11da00578c4b4618c45f86c39d39c71

//查看运行状态
[root@server ~]# docker ps -a
CONTAINER ID        IMAGE                       COMMAND                  CREATED             STATUS              PORTS                              NAMES
479a752cf563        nginx                       "nginx -g 'daemon off"   23 seconds ago      Up 21 seconds       0.0.0.0:32770->80/tcp              ng1
9782d5c84033        tomcat                      "catalina.sh run"        3 minutes ago       Up 3 minutes        0.0.0.0:32769->8080/tcp            tom2
412c6d52ba4c        tomcat                      "catalina.sh run"        3 minutes ago       Up 3 minutes        0.0.0.0:32768->8080/tcp            tom1
2fff83d58ef2        rancher/server:v1.6.5       "/usr/bin/entry /usr/"   35 minutes ago      Up 13 minutes       3306/tcp, 0.0.0.0:8080->8080/tcp   trusting_lichterman
fef447dfdfc0        docker.io/registry:latest   "/entrypoint.sh /etc/"   42 minutes ago      Up 13 minutes       0.0.0.0:5000->5000/tcp             registry
[root@server ~]#

//进入tom1交互中端，写入内容到index.jsp
[root@server ~]# docker exec -it tom1 bash
root@412c6d52ba4c:/usr/local/tomcat# echo 11111 > webapps/ROOT/index.jsp
root@412c6d52ba4c:/usr/local/tomcat#

//进入tom2互中端，写入内容到index.jsp
[root@server ~]# docker exec -it tom2 bash
root@9782d5c84033:/usr/local/tomcat# echo 22222 > webapps/ROOT/index.jsp
root@9782d5c84033:/usr/local/tomcat#

//把ng1的nginx.conf配置文件复制出来做修改
[root@server ~]# docker cp ng1:/etc/nginx/nginx.conf /root/
[root@server ~]# vi nginx.conf
[root@server ~]# tail nginx.conf
    #tcp_nopush     on;

    keepalive_timeout  65;

server {listen 80; location /{proxy_pass http://zhouxiang;}} upstream zhouxiang {server 192.168.20.22:32768; server 192.168.20.22:32769}

    #gzip  on;

    include /etc/nginx/conf.d/*.conf;
}
[root@server ~]#

//修改完成后再复制回ng1
[root@server ~]# docker cp nginx.conf ng1:/etc/nginx/nginx.conf

//进入ng1交互终端，查看配置，并重启nginx服务
[root@server ~]# docker exec -it  ng1 bash
root@479a752cf563:/# tail /etc/nginx/nginx.conf
    #tcp_nopush     on;

    keepalive_timeout  65;

server {listen 80; location /{proxy_pass http://zhouxiang;}} upstream zhouxiang {server 192.168.20.22:32768; server 192.168.20.22:32769;}

    #gzip  on;

    include /etc/nginx/conf.d/*.conf;
}
root@479a752cf563:/#
root@479a752cf563:/# /etc/init.d/nginx restart
Restarting nginx: nginx
[root@server ~]#

//测试结果
[root@server ~]# curl http://192.168.20.22:32768
11111
[root@server ~]#

[root@server ~]# curl http://192.168.20.22:32769
22222
[root@server ~]#

```

##### 1

1.在 server 节点使用 netstat 命令查询仓库监听端口号，查询完毕后通过 lsof命令（如命令不存在则手工安装）查询使用此端口号的进程。将以上所有操作命令和输出结果以文本形式提交到答题框。

```
[root@server ~]# netstat -tln		
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State      
tcp        0      0 0.0.0.0:22              0.0.0.0:*               LISTEN     
tcp        0      0 127.0.0.1:25            0.0.0.0:*               LISTEN     
tcp6       0      0 :::22                   :::*                    LISTEN     
tcp6       0      0 ::1:25                  :::*                    LISTEN      

[root@server ~]# lsof -i:5000
COMMAND     PID USER   FD   TYPE DEVICE SIZE/OFF NODE NAME
docker-pr 14920 root    4u  IPv6  36475      0t0  TCP *:commplex-main (LISTEN)

```

##### 2

2.在 server 节点通过 netstat 命令（如命令不存在则手工安装）查询 docker 镜像仓库 PID，使用 top 命令查询上一步查询到的 PID 的资源使用情况。将以上所有操作命令和输出结果以文本形式提交到答题框。

```
[root@server ~]# netstat -tlnp 
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name    
tcp        0      0 0.0.0.0:22              0.0.0.0:*               LISTEN      1454/sshd           
tcp        0      0 127.0.0.1:25            0.0.0.0:*               LISTEN      2519/master         
tcp6       0      0 :::5000                 :::*                    LISTEN      14920/docker-proxy- 
tcp6       0      0 :::8080                 :::*                    LISTEN      16601/docker-proxy- 
tcp6       0      0 :::22                   :::*                    LISTEN      1454/sshd           
tcp6       0      0 ::1:25                  :::*                    LISTEN      2519/master    

[root@server ~]# top -p 14920
top - 10:54:56 up 41 min,  3 users,  load average: 0.03, 0.47, 0.74
Tasks:   1 total,   0 running,   1 sleeping,   0 stopped,   0 zombie
%Cpu(s):  0.0 us,  0.3 sy,  0.0 ni, 99.7 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
KiB Mem :  3866920 total,   214912 free,   868748 used,  2783260 buff/cache
KiB Swap:  8384508 total,  8384416 free,       92 used.  2706288 avail Mem 

   PID USER      PR  NI    VIRT    RES    SHR S %CPU %MEM     TIME+ COMMAND                                                                                 
 14920 root      20   0  100028   1620   1184 S  0.0  0.0   0:00.00 docker-proxy-cu  
```

##### 3

3.在 server 节点通过 docker 命令查询 docker registry 容器最后几条日志，将以上所有操作命令和输出结果以文本形式提交到答题框。

```
[root@server ~]# docker ps -a
CONTAINER ID        IMAGE                       COMMAND                  CREATED             STATUS              PORTS                              NAMES
30468c72c0c8        rancher/server:v1.6.5       "/usr/bin/entry /usr/"   15 minutes ago      Up 15 minutes       3306/tcp, 0.0.0.0:8080->8080/tcp   serene_yalow
ff80f1a5d264        docker.io/registry:latest   "/entrypoint.sh /etc/"   20 minutes ago      Up 20 minutes       0.0.0.0:5000->5000/tcp             registry

[root@server ~]# docker logs -f  --tail=1  30468c72c0c8 
time="2019-12-09T16:00:47Z" level=error msg="Failed to update existing repo cache: exit status 128" 
time="2019-12-09T16:01:27Z" level=error msg="Failed to update existing repo cache: exit status 128" 
```

##### 4

4.在 server 节点，查询 rancher/server 容器的进程号，建立命名空间/var/run/netns 并与 rancher/server 容器进行连接，通过 ip netns 相关命令查询该容器的 ip，将以上操作命令及检查结果填入答题框。

```
[root@server rancher-server]# docker inspect --format '{{.State.Pid}}' 3046
16616

[root@server ~]# mkdir /var/run/netns/rancher-server  -p  
[root@server netns]# ln -s /proc/16616/ns/net /var/run/netns/rancher-server

[root@server netns]# ip netns exec rancher-server ip add
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN 
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host 
       valid_lft forever preferred_lft forever
7: eth0@if8: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue state UP 
    link/ether 02:42:ac:11:00:03 brd ff:ff:ff:ff:ff:ff link-netnsid 0
    inet 172.17.0.3/16 scope global eth0
       valid_lft forever preferred_lft forever
    inet6 fe80::42:acff:fe11:3/64 scope link 
       valid_lft forever preferred_lft forever

```

##### 5

5.在 server 节点查询当前 cgroup 的挂载情况，将以上操作命令及检查结果填入答题框。

```
 [root@server ~]# mount -t cgroup
cgroup on /sys/fs/cgroup/systemd type cgroup (rw,nosuid,nodev,noexec,relatime,xattr,release_agent=/usr/lib/systemd/systemd-cgroups-agent,name=systemd)
cgroup on /sys/fs/cgroup/net_cls type cgroup (rw,nosuid,nodev,noexec,relatime,net_cls)
cgroup on /sys/fs/cgroup/blkio type cgroup (rw,nosuid,nodev,noexec,relatime,blkio)
cgroup on /sys/fs/cgroup/cpuset type cgroup (rw,nosuid,nodev,noexec,relatime,cpuset)
cgroup on /sys/fs/cgroup/freezer type cgroup (rw,nosuid,nodev,noexec,relatime,freezer)
cgroup on /sys/fs/cgroup/perf_event type cgroup (rw,nosuid,nodev,noexec,relatime,perf_event)
cgroup on /sys/fs/cgroup/devices type cgroup (rw,nosuid,nodev,noexec,relatime,devices)
cgroup on /sys/fs/cgroup/cpu,cpuacct type cgroup (rw,nosuid,nodev,noexec,relatime,cpuacct,cpu)
cgroup on /sys/fs/cgroup/memory type cgroup (rw,nosuid,nodev,noexec,relatime,memory)
cgroup on /sys/fs/cgroup/hugetlb type cgroup (rw,nosuid,nodev,noexec,relatime,hugetlb)

```

##### 6

6.在 server 节点创建 memory 控制的 cgroup，名称为：xiandian，创建完成后将当前进程移动到这个 cgroup 中，通过 cat 相关命令查询 cgroup 中的进程 ID， 将以上操作命令及检查结果填入答题框。

```
 [root@server ~]# mkdir /sys/fs/cgroup/memory/xiandian
[root@server ~]# echo $$
17138
[root@server ~]# echo $$ >> /sys/fs/cgroup/memory/xiandian/tasks
[root@server ~]# cat /proc/17138/cgroup
10:hugetlb:/
9:memory:/xiandian
8:cpuacct,cpu:/user.slice
7:devices:/user.slice
6:perf_event:/
5:freezer:/
4:cpuset:/
3:blkio:/user.slice
2:net_cls:/
1:name=systemd:/user.slice/user-0.slice/session-8.scope
```

##### 7

7.在 server 节点创建 cpu 控制的 cgroup，名称为：xiandian。假设存在进程号为 8888 的进程一直占用 cpu，并且达到 100%，严重影响系统的正常运行。使用cgroup 相关知识在创建的 cgroup 中将此进程操作 cpu 配额调整为 30%。将以上操作命令及检查结果填入答题框。

```
 [root@server ~]# mkdir /sys/fs/cgroup/cpu/xiandian
[root@server ~]# echo 30000 > /sys/fs/cgroup/cpu/xiandian/cpu.cfs_quota_us
[root@server ~]# echo 8888 > /sys/fs/cgroup/cpu/xiandian//tasks
-bash: echo: write error: No such process
```

##### 8

8.在 server 节点使用 nginx 镜像创建一个容器，只能使用特定的内核，镜像使用 nginx：latest，并通过查看 cgroup 相关文件查看内核使用情况，将以上操作命令及检查结果填入答题框（提示，首先要修改 cpuset.cpus 的参数）。

```
[root@server netns]# docker run -itd --name 1daoyun --cpuset-cpus='0' nginx:latest /bin/bash
ed55850ea765f256a3fd9b468a42586be18fcf3dbe1e3b3da8c5f562c4723c33

[root@server netns]# cat /sys/fs/cgroup/cpuset/system.slice/docker-ed55850ea765f256a3fd9b468a42586be18fcf3dbe1e3b3da8c5f562c4723c33.scope/cpuset.cpus 
0
```

##### 9

9.在 server 节点创建目录，创建完成后启动镜像为 nginx:latest 的容器，并指定此目录为容器启动的数据卷，创建完成后通过 inspect 命令指定查看数据卷的情况。将以上操作命令及检查结果填入答题框

```
[root@server ~]# mkdir /opt/xiandian
[root@server images]# docker run -dit -P -v /opt/xiandian  nginx:latest /bin/bash
9f6b2106376d571969436fa530a3f01b7e5de47d8d0fc80894c72d641e2e6b25

[root@server images]# docker inspect -f {{.Mounts}} 9f6b21
[{dd9286f2d9ffcc7c6d3c4bb832a90c04ffcddd1a02ad5b1e9d37647655197c44 /var/lib/docker/volumes/dd9286f2d9ffcc7c6d3c4bb832a90c04ffcddd1a02ad5b1e9d37647655197c44/_data /opt/xiandian local  true }]


```

##### 10

10.在 server 节点创建目录，创建完成后启动镜像为 nginx:latest 的容器，并指定此目录为容器数据卷/opt 的挂载目录，设置该数据卷为只读模式，创建完成后通过 inspect 命令指定查看 HostConfig 内的 Binds 情况。将以上操作命令及检查结果填入答题框

```
[root@server ~]# mkdir /opt/xiandian
[root@server images]# docker run -dit -P -v /opt/xiandian  nginx:latest /bin/bash
9f6b2106376d571969436fa530a3f01b7e5de47d8d0fc80894c72d641e2e6b25

[root@server images]# docker inspect -f {{.Mounts}} 9f6b21
[{dd9286f2d9ffcc7c6d3c4bb832a90c04ffcddd1a02ad5b1e9d37647655197c44 /var/lib/docker/volumes/dd9286f2d9ffcc7c6d3c4bb832a90c04ffcddd1a02ad5b1e9d37647655197c44/_data /opt/xiandian local  true }]

```

##### 11

11.在 server 节点使用 docker 相关命令使用 mysql:8.0 镜像创建名为 mysqldb 的容器，使用镜像 nginx:latest 创建名为 nginxweb 容器，容器连接 mysqldb 容器内数据库，操作完成后使用 inspect 查看有关链接内容的字段，将以上操作命令及检查结果填入答题框。

```
[root@server images]# docker run -d  -P -e MYSQL_ROOT_PASSWORD=000000 --name mysqldb  mysql:8.0
b6fb3c579afbff16ff585578f326622a707df7c7eb1ea42ffe7aa2af70ba9256

[root@server images]# docker run -idtP --name nginxweb --link mysqldb:db nginx:latest /bin/bash
5fc08ba205f94e032e49a9d9c334f32ea9da3a6c3a223679c5b13339300b6c3c

[root@server images]# docker inspect --format {{.HostConfig.Links}} 5fc08
[/mysqldb:/nginxweb/db]

```

##### 12

12.在 server 节点通过 bridge 命令（如果不存在则安装该命令 bridge-utils）查看网桥列表，将以上操作命令及检查结果填入答题框。

```
 [root@server ~]# brctl show
bridge name	bridge id		STP enabled	interfaces
docker0		8000.02424a7c6f31	no		veth80e463e
							veth87da900
							vethd82933f

```

##### 13

13.在 server 节点创建 xd_br 网桥，设立网络的网络地址和掩码为192.168.2.1/24，创建完成后启动该网桥，完成后查看 xd_br 网卡和网桥详细信息， 将以上操作命令及检查结果填入答题框。

```
[root@server ~]# brctl addbr xd_br
[root@server ~]# ip addr add 192.168.2.1/24 dev xd_br
[root@server ~]# ip link set dev xd_br up
[root@server ~]# ifconfig xd_br
xd_br: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 192.168.2.1  netmask 255.255.255.0  broadcast 0.0.0.0
        inet6 fe80::24d7:49ff:fe00:fc77  prefixlen 64  scopeid 0x20<link>
        ether 26:d7:49:00:fc:77  txqueuelen 0  (Ethernet)
        RX packets 0  bytes 0 (0.0 B)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 10  bytes 732 (732.0 B)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

```

##### 14

14.在 server 节点利用 nginx:latest 镜像运行一个无网络环境的容器，使用inspect 命令只查看该容器的 networks 信息，将以上操作命令及返回结果以文本形式填入答题框。

```
[root@server ~]# docker run -itd -P --net=none nginx:latest /bin/bash
65255cacbeb5cf7d5bcca5802951e772efe37558dfc4151d706a8ff00be6795a

[root@server ~]# docker inspect -f {{.NetworkSettings.Networks}} 65255cac
map[none:0xc4200be180]

```

##### 15

15.在 client 节点拉取 mysql:8.0 镜像，拉取完成后查询 docker 镜像列表目录， 将以上操作命令及检查结果填入答题框。

```
[root@clinet ~]# docker pull mysql:8.0
Trying to pull repository 192.168.100.110:5000/mysql ... 
8.0: Pulling from 192.168.100.110:5000/mysql
f49cf87b52c1: Pull complete 
78032de49d65: Pull complete 
837546b20bc4: Pull complete 
9b8316af6cc6: Pull complete 
1056cf29b9f1: Pull complete 
86f3913b029a: Pull complete 
88ee0a67381b: Pull complete 
03bcb8298f42: Pull complete 
e7f60b590748: Pull complete 
0dca38a0e6a0: Pull complete 
87c6d825c7b1: Pull complete 
Digest: sha256:c6a388006b8f706b031279a0102c3b454d9cbee74390a84f3735769f3070d07b

```

##### 16

16.在 server 节点运行 mysql:8.0 镜像，设置数据库密码为 xd_root，将宿主机13306 端口作为容器 3306 端口映射，进入容器后创建数据库 xd_db，创建用户xiandian，密码为 xd_pass，将此用户对 xd_db 拥有所有权限和允许此用户远程访问，完成后使用 xiandian 用户远程登录数据库查询数据库内的数据库列表，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker run --name mysql -d --restart=always -p 13306:3306 -e MYSQL_ROOT_PASSWORD=xd_root mysql:8.0
7b6911858c26cc3d337ea42bde8bdb6cd8a8597ce2122d55f88688f251736304
[root@server ~]# docker exec -it 7b691 /bin/bash
root@7b6911858c26:/# mysql -uroot -pxd_root
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 7
Server version: 8.0.3-rc-log MySQL Community Server (GPL)

Copyright (c) 2000, 2017, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> grant all privileges on xd_db.* to 'xiandian'@'%' identified by 'xd_pass';
Query OK, 0 rows affected, 1 warning (0.01 sec)

root@7b6911858c26:/# ip a
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default 
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host 
       valid_lft forever preferred_lft forever
20: eth0@if21: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue state UP group default 
    link/ether 02:42:ac:11:00:05 brd ff:ff:ff:ff:ff:ff
    inet 172.17.0.5/16 scope global eth0
       valid_lft forever preferred_lft forever
    inet6 fe80::42:acff:fe11:5/64 scope link 
       valid_lft forever preferred_lft forever

root@7b6911858c26:/# mysql -h 172.17.0.5 -uxiandian -pxd_pass

```

##### 17

17.在server 节点将mysql 镜像导出，导出名称为mysql_images.tar，放在/media目录下，导出后，查看目录，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker export -o /media/mysql_images.tar 7b6911858c26 
[root@server ~]# ls /media/
mysql_images.tar

```

##### 18

18.在 server 节点，运行数据库容器，设置数据库密码，使用镜像为 mysql:8.0， 运行之后，使用命令将容器导出，导出名称为 mysql_container.tar，放在/media 目录下，导出后，查看目录，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker run -d -P mysql:8.0 MYSQL_ROOT PASSWORD=000000
e6a6667963c79026edd7c6b3d5ad8c5bdc4faebfc6c92b4baa091b4c42e7f465
[root@server ~]# docker export -o /media/mysql_container.tar e6a666796
[root@server ~]# ls /media/
mysql_container.tar  mysql_images.tar

```

##### 19

19.在 server 节点将 tomcat_latest.tar 镜像导入，并打标签，上传至仓库中， 将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server images]# docker load -i  tomcat_latest.tar
e27a10675c56: Loading layer [==================================================>]   105 MB/105 MB
851f3e348c69: Loading layer [==================================================>] 24.72 MB/24.72 MB
06f4de5fefea: Loading layer [==================================================>] 7.995 MB/7.995 MB
a4d7b0ac0438: Loading layer [==================================================>] 2.172 MB/2.172 MB
6ef532e39c1e: Loading layer [==================================================>] 3.584 kB/3.584 kB
7ffe9646653b: Loading layer [==================================================>] 1.536 kB/1.536 kB
93742a1ae069: Loading layer [==================================================>] 415.8 MB/415.8 MB
c571db474d75: Loading layer [==================================================>] 400.9 kB/400.9 kB
04178c827c65: Loading layer [==================================================>]  2.56 kB/2.56 kB
c4d99fd86e94: Loading layer [==================================================>] 1.966 MB/1.966 MB
a8e949e6c4fd: Loading layer [==================================================>] 18.87 MB/18.87 MB
f17807115ffa: Loading layer [==================================================>] 2.048 kB/2.048 kB
Loaded image ID: sha256:a92c139758db4c89d0cdeaa641566d0cb3305f9f6d2dbd2ca50dd361c02328da B/2.048 kB
[root@server images]# docker tag a92 tomcat:latest  
[root@server images]# docker push tomcat:latest
The push refers to a repository [192.168.100.110:5000/tomcat]
f17807115ffa: Pushed 
a8e949e6c4fd: Pushed 
c4d99fd86e94: Pushed 
04178c827c65: Pushed 
c571db474d75: Pushed 
93742a1ae069: Pushed 
7ffe9646653b: Pushed 
6ef532e39c1e: Pushed 
a4d7b0ac0438: Pushed 
06f4de5fefea: Pushed 
851f3e348c69: Pushed 
e27a10675c56: Pushed 
latest: digest: sha256:90b2bee496f433123469c91586b19b6e4b1b0c417356ba0240bdcbea1b474a46 size: 2836
 
```

##### 20

20.在 server 节点运行 mysql 容器，使用镜像为 mysql:8.0 指定 mysql 密码， 容器运行在后台，使用随机映射端口，容器运行完成后查询容器列表，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server images]# docker run -d  -P -e MYSQL_ROOT_PASSWORD=000000 mysql:8.0
8061f58096ae25121b1c9dfb3a14546bca6a2bd2f6baef76935fc81527db8c71
[root@server images]# docker ps |grep mysql
8061f58096ae        mysql:8.0                   "docker-entrypoint.sh"   34 seconds ago      Up 32 seconds       0.0.0.0:32774->3306/tcp            amazing_pare
7b6911858c26        mysql:8.0                   "docker-entrypoint.sh"   50 minutes ago      Up 50 minutes       0.0.0.0:13306->3306/tcp            mysql

```

##### 21

在 server 节点运行 mysql 容器，使用镜像为 mysql:8.0 指定 mysql 密码， 容器运行在后台，使用随机映射端口，容器运行完成后查询容器列表，然后将运行的 mysql 容器停止，完成后查询容器状态，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker run -d  -P -e MYSQL_ROOT_PASSWORD=000000 mysql:8.0
9f6b2106376d571969436fa530a3f01b7e5de47d8d0fc80894c72d641e2e6b25

[root@server ~]# docker ps |grep mysql
9f6b2106376d        mysql:8.0                   "docker-entrypoint.sh"   About a minute ago   Up 58 seconds       0.0.0.0:32768->3306/tcp            pensive_bhaskara

[root@server ~]# docker stop 9f6b2106376d
9f6b2106376d

[root@server ~]# docker ps -a|grep mysql
9f6b2106376d        mysql:8.0                   "docker-entrypoint.sh"   2 minutes ago       Exited (0) 47 seconds ago                                      pensive_bhaskara

```

##### 22

在 server 节点，将上题停止的容器启动运行，完成后查询容器状态，将操作命令及返回结果以文本形式提交到答题框。

```
 [root@server ~]# docker start 9f6b2106376d
9f6b2106376d
[root@server ~]# docker ps -a|grep mysql
9f6b2106376d        mysql:8.0                   "docker-entrypoint.sh"   3 minutes ago       Up 2 seconds        0.0.0.0:32769->3306/tcp            pensive_bhaskara
```

##### 23

在 server 节点，将运行的 mysql 容器重启，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker restart 9f6b2106376d
9f6b2106376d
[root@server ~]# docker ps -a |grep mysql
9f6b2106376d        mysql:8.0                   "docker-entrypoint.sh"   4 minutes ago       Up 9 seconds        0.0.0.0:32771->3306/tcp            pensive_bhaskara

```

##### 24

在 server 节点，执行一条命令使用 exec 获取 rancher/server 容器正在运行的网络套接字连接情况，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker exec 30468c7 ss
Netid  State      Recv-Q Send-Q   Local Address:Port       Peer Address:Port   
u_str  ESTAB      0      0                    * 50813                 * 0      
u_str  ESTAB      0      0                    * 48878                 * 0      
tcp    ESTAB      0      0            127.0.0.1:34430         127.0.0.1:mysql   
tcp    ESTAB      0      0            127.0.0.1:34436         127.0.0.1:mysql   
tcp    ESTAB      0      0            127.0.0.1:mysql         127.0.0.1:34430   
tcp    ESTAB      0      0            127.0.0.1:mysql         127.0.0.1:34436   
tcp    ESTAB      0      0            127.0.0.1:mysql         127.0.0.1:34518   
tcp    ESTAB      0      0            127.0.0.1:mysql         127.0.0.1:34401   
tcp    ESTAB      0      0            127.0.0.1:mysql         127.0.0.1:34147   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:55104      ::ffff:127.0.0.1:omniorb 
tcp    ESTAB      0      0                  ::1:http-alt             ::1:48924   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:39717      ::ffff:127.0.0.1:8090    
tcp    ESTAB      0      0                  ::1:http-alt             ::1:48922   
tcp    ESTAB      0      0                  ::1:48922               ::1:http-alt 
tcp    ESTAB      0      0                  ::1:48924               ::1:http-alt 
tcp    ESTAB      0      0         ::ffff:127.0.0.1:34147      ::ffff:127.0.0.1:mysql   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:55001      ::ffff:127.0.0.1:omniorb 
tcp    ESTAB      0      0         ::ffff:127.0.0.1:55215      ::ffff:127.0.0.1:omniorb 
tcp    ESTAB      0      0                  ::1:tproxy              ::1:55741   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:omniorb    ::ffff:127.0.0.1:55215   
tcp    ESTAB      0      0                  ::1:55739               ::1:tproxy  
tcp    ESTAB      0      0         ::ffff:127.0.0.1:39715      ::ffff:127.0.0.1:8090    
tcp    ESTAB      0      0         ::ffff:127.0.0.1:39716      ::ffff:127.0.0.1:8090    
tcp    ESTAB      0      0         ::ffff:127.0.0.1:55107      ::ffff:127.0.0.1:omniorb 
tcp    ESTAB      0      0                  ::1:55751               ::1:tproxy  
tcp    ESTAB      0      0                  ::1:55801               ::1:tproxy  
tcp    ESTAB      0      0        ::ffff:172.17.0.3:http-alt  ::ffff:192.168.100.120:32941   
tcp    ESTAB      0      0                  ::1:tproxy              ::1:55801   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:34518      ::ffff:127.0.0.1:mysql   
tcp    ESTAB      0      0                  ::1:tproxy              ::1:55750   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:omniorb    ::ffff:127.0.0.1:55025   
tcp    ESTAB      0      0                  ::1:55741               ::1:tproxy  
tcp    ESTAB      0      0                  ::1:tproxy              ::1:55751   
tcp    ESTAB      0      0                  ::1:55748               ::1:tproxy  
tcp    ESTAB      0      0                  ::1:55743               ::1:tproxy  
tcp    ESTAB      0      0                  ::1:48920               ::1:http-alt 
tcp    ESTAB      0      0                  ::1:tproxy              ::1:55739   
tcp    ESTAB      0      0        ::ffff:172.17.0.3:http-alt  ::ffff:192.168.100.120:32940   
tcp    ESTAB      0      0        ::ffff:172.17.0.3:http-alt  ::ffff:192.168.100.120:40026   
tcp    ESTAB      0      0        ::ffff:172.17.0.3:http-alt  ::ffff:192.168.100.120:53958   
tcp    ESTAB      0      0                  ::1:55737               ::1:tproxy  
tcp    ESTAB      0      0        ::ffff:172.17.0.3:http-alt  ::ffff:192.168.100.120:45906   
tcp    ESTAB      0      0                  ::1:tproxy              ::1:55737   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:8090       ::ffff:127.0.0.1:39716   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:8090       ::ffff:127.0.0.1:39714   
tcp    ESTAB      0      0        ::ffff:172.17.0.3:http-alt  ::ffff:192.168.100.120:56646   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:39714      ::ffff:127.0.0.1:8090    
tcp    ESTAB      0      0                  ::1:tproxy              ::1:55748   
tcp    ESTAB      0      0                  ::1:http-alt             ::1:48918   
tcp    ESTAB      0      0        ::ffff:172.17.0.3:http-alt  ::ffff:192.168.100.120:45908   
tcp    ESTAB      0      0                  ::1:tproxy              ::1:55743   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:55037      ::ffff:127.0.0.1:omniorb 
tcp    ESTAB      0      0         ::ffff:127.0.0.1:omniorb    ::ffff:127.0.0.1:55001   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:55025      ::ffff:127.0.0.1:omniorb 
tcp    ESTAB      0      0         ::ffff:127.0.0.1:omniorb    ::ffff:127.0.0.1:55037   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:34401      ::ffff:127.0.0.1:mysql   
tcp    ESTAB      0      0                  ::1:http-alt             ::1:48716   
tcp    ESTAB      0      0                  ::1:http-alt             ::1:48927   
tcp    ESTAB      0      0                  ::1:55750               ::1:tproxy  
tcp    ESTAB      0      0                  ::1:48918               ::1:http-alt 
tcp    ESTAB      0      0         ::ffff:127.0.0.1:omniorb    ::ffff:127.0.0.1:55104   
tcp    ESTAB      0      0                  ::1:48927               ::1:http-alt 
tcp    ESTAB      0      0        ::ffff:172.17.0.3:http-alt  ::ffff:192.168.100.120:46350   
tcp    ESTAB      0      0                  ::1:http-alt             ::1:48920   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:8090       ::ffff:127.0.0.1:39717   
tcp    ESTAB      0      0         ::ffff:127.0.0.1:omniorb    ::ffff:127.0.0.1:55107   
tcp    ESTAB      0      0                  ::1:48716               ::1:http-alt 
tcp    ESTAB      0      0         ::ffff:127.0.0.1:8090       ::ffff:127.0.0.1:39715   

```

##### 25

在 server 节点，使用 inspect 只查询 rancher/server 容器的 NetworkSettings内 Networks 网桥信息，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker inspect -f {{.NetworkSettings.Networks}} 30468c7
map[bridge:0xc4200be0c0]
```

##### 26

在 server 节点，使用 inspect 只查询 rancher/server 容器的 PortBindings 信息，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker inspect -f {{.HostConfig.PortBindings}} 30468c7
map[8080/tcp:[{ 8080}]] 
```

##### 27

在 server 节点，使用 inspect 只查询 rancher/server 容器的 NetworkSettings内 Ports 信息，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker inspect -f {{.NetworkSettings.Ports}} 30468c7
map[3306/tcp:[] 8080/tcp:[{0.0.0.0 8080}]]
```

##### 28

在 server 节点，使用 inspect 只查询 rancher/server 镜像的 Volumes 卷组信息，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker inspect -f {{.Config.Volumes}} 30468c7
map[/var/lib/cattle:{} /var/lib/mysql:{} /var/log/mysql:{}]
```

##### 29

在 server 节点，使用 inspect 只查询 rancher/server 镜像的 Entrypoint 信息， 将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker inspect -f {{.Config.Entrypoint}} 30468c7
[/usr/bin/entry]
```

##### 30

在 server 节点，使用 docker 命令查询 rancher/server 容器的进程，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker top 30468c7
UID                 PID                 PPID                C                   STIME               TTY                 TIME                CMD
root                16616               16605               0                   10:41               ?                   00:00:00            /usr/bin/s6-svscan /service
root                16662               16616               0                   10:41               ?                   00:00:00            s6-supervise cattle
root                16663               16616               0                   10:41               ?                   00:00:00            s6-supervise graphite_exporter
root                16664               16616               0                   10:41               ?                   00:00:00            s6-supervise mysql
root                16666               16662               1                   10:41               ?                   00:01:50            java -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -Xms128m -Xmx2g -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/lib/cattle/logs -Dlogback.bootstrap.level=WARN -cp /usr/share/cattle/cd62f7c4954133b023a2baf35df47f69:/usr/share/cattle/cd62f7c4954133b023a2baf35df47f69/etc/cattle io.cattle.platform.launcher.Main
102                 16688               16664               0                   10:41               ?                   00:00:19            /usr/sbin/mysqld --basedir=/usr --datadir=/var/lib/mysql --plugin-dir=/usr/lib/mysql/plugin --user=mysql --log-error=/var/log/mysql/error.log --pid-file=/var/run/mysqld/mysqld.pid --socket=/var/run/mysqld/mysqld.sock --port=3306
root                16911               16666               0                   10:42               ?                   00:00:13            websocket-proxy
root                16927               16666               0                   10:42               ?                   00:00:00            webhook-service
root                16928               16666               0                   10:42               ?                   00:00:00            secrets-api server --enc-key-path .
root                16941               16666               0                   10:42               ?                   00:00:00            rancher-auth-service
root                16945               16666               0                   10:42               ?                   00:00:03            rancher-catalog-service --config repo.json --refresh-interval 300
root                17000               16666               0                   10:42               ?                   00:00:00            telemetry client
root                17097               16666               0                   10:48               ?                   00:00:02            go-machine-service
root                17098               16666               0                   10:48               ?                   00:00:00            rancher-compose-executor
root                28530               16945               0                   13:00               ?                   00:00:00            git -C cache/global/afd982c6cb1b8417dc9b0244e48cbbf8 fetch
root                28531               28530               0                   13:00               ?                   00:00:00            git-remote-https origin https://gitee.com/onlytaicai/rancher-catalog.git

```

##### 31

在 server 节点，使用 docker 命令查列出 rancher/server 容器内发生变化的文件和目录，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker diff 30468c7
C /service
C /service/.s6-svscan
A /service/.s6-svscan/lock
A /service/.s6-svscan/control
C /service/cattle
A /service/cattle/supervise
A /service/cattle/supervise/control
A /service/cattle/supervise/lock
A /service/cattle/supervise/status
A /service/cattle/event
C /service/graphite_exporter
A /service/graphite_exporter/event
A /service/graphite_exporter/supervise
A /service/graphite_exporter/supervise/control
A /service/graphite_exporter/supervise/lock
A /service/graphite_exporter/supervise/status
C /service/mysql
A /service/mysql/event
A /service/mysql/supervise
A /service/mysql/supervise/control
A /service/mysql/supervise/lock
A /service/mysql/supervise/status
C /etc
C /etc/mysql
C /etc/mysql/my.cnf
C /run
C /run/mysqld
A /run/mysqld/mysqld.pid
A /run/mysqld/mysqld.sock
C /tmp
C /tmp/hsperfdata_root
A /tmp/hsperfdata_root/8
A /tmp/jetty-0.0.0.0-8081-cd62f7c4954133b023a2baf35df47f69-_-any-4752918750666406408.dir
 
```

##### 32

在 server 节点，使用 docker 命令查看最后退出的容器的 ID，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker ps -l
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                     NAMES
9f6b2106376d        mysql:8.0      
```

##### 33

在 server 节点，将运行的 mysql 容器创建为镜像，完成后查询该镜像， 将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker commit 9f6b2106376d mysql:11
sha256:7d2f2f2541009c6f66337a576287fb4a9a4850fe9b491a3cd4bbdabb9d2a7064
[root@server ~]# docker images |grep mysql
mysql                                          11                  7d2f2f254100        20 seconds ago      342.5 MB
192.168.100.110:5000/mysql                     8.0                 26bd364f80bf        24 months ago       342.5 MB
```

##### 34

在 server 节点查询 registry 容器的 CPU、内存等统计信息，将以上操作命令及检查结果以文本形式填入答题框 。

```
[root@server ~]# docker stats ff80f1a5d264
CONTAINER           CPU %               MEM USAGE / LIMIT       MEM %               NET I/O             BLOCK I/O             PIDS
ff80f1a5d264        0.00%               5.207 MiB / 3.688 GiB   0.14%               679.3 MB / 201 MB   277.8 MB / 1.271 GB   0

```

##### 35

在 server 节点修改运行的 rancher/server 容器的名称，修改名称为xiandian_server，完成后查询容器列表，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker rename 30468c72c0c8 xiandian_server
[root@server ~]# docker ps -a
CONTAINER ID        IMAGE                       COMMAND                  CREATED             STATUS              PORTS                              NAMES
9f6b2106376d        mysql:8.0                   "docker-entrypoint.sh"   27 minutes ago      Up 23 minutes       0.0.0.0:32771->3306/tcp            pensive_bhaskara
30468c72c0c8        rancher/server:v1.6.5       "/usr/bin/entry /usr/"   2 hours ago         Up 2 hours          3306/tcp, 0.0.0.0:8080->8080/tcp   xiandian_server
ff80f1a5d264        docker.io/registry:latest   "/entrypoint.sh /etc/"   2 hours ago         Up 2 hours          0.0.0.0:5000->5000/tcp             registry
```

##### 36

在 server 节点，使用 docker 命令列举所有的网络，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
58c2eb597e8d        bridge              bridge              local               
6a12938542d2        host                host                local               
7c0135e547cc        none                null                local     
```

##### 37

在 server 节点，使用 docker 命令查询 bridge 网络的所有详情，将以上操作命令及检查结果填入答题框。

```
[root@server ~]# docker network inspect bridge
[
    {
        "Name": "bridge",
        "Id": "58c2eb597e8d1d0f9875a0b878653be7aa70a360b9fd17bc6fcca5a639873fd1",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": null,
            "Config": [
                {
                    "Subnet": "172.17.0.0/16",
                    "Gateway": "172.17.0.1"
                }
            ]
        },
        "Internal": false,
        "Containers": {
            "30468c72c0c8ee4ec395162c8579d73caeb715e4cacbe9168dbd6630c354ff0b": {
                "Name": "xiandian_server",
                "EndpointID": "4dcfc3e7c2a06db75aac12764e161841e16a16aaddaede74626e8c11ef2ea22d",
                "MacAddress": "02:42:ac:11:00:03",
                "IPv4Address": "172.17.0.3/16",
                "IPv6Address": ""
            },
            "9f6b2106376d571969436fa530a3f01b7e5de47d8d0fc80894c72d641e2e6b25": {
                "Name": "pensive_bhaskara",
                "EndpointID": "9d9bd8681762ec0adecb5ebaf55ae358bc2b0d5e0b93456ead3babb35556f947",
                "MacAddress": "02:42:ac:11:00:04",
                "IPv4Address": "172.17.0.4/16",
                "IPv6Address": ""
            },
            "ff80f1a5d264ae013d8be5a736a6474f78024813eaee0808e59e5fb3d610a302": {
                "Name": "registry",
                "EndpointID": "b59d875095d47bb880a20f049d3393c467ebc3eb425e05588db09fe46d63a583",
                "MacAddress": "02:42:ac:11:00:02",
                "IPv4Address": "172.17.0.2/16",
                "IPv6Address": ""
            }
        },
        "Options": {
            "com.docker.network.bridge.default_bridge": "true",
            "com.docker.network.bridge.enable_icc": "true",
            "com.docker.network.bridge.enable_ip_masquerade": "true",
            "com.docker.network.bridge.host_binding_ipv4": "0.0.0.0",
            "com.docker.network.bridge.name": "docker0",
            "com.docker.network.driver.mtu": "1500"
        },
        "Labels": {}
    }
] 
```

##### 38

在 server 节点，使用 docker 命令创建名为 xd_net 的网络，网络网段为192.168.3.0/24，网关为 192.168.3.1，创建完成后查询网络列表，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker network create --subnet 192.168.3.0/24 --gateway 192.168.3.1 xd_net
f12a484baa62661b52272a831824a46bb2af59bfe6c596fa12da35c1c04c816b 

[root@server ~]# docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
58c2eb597e8d        bridge              bridge              local               
6a12938542d2        host                host                local               
7c0135e547cc        none                null                local               
f12a484baa62        xd_net              bridge              local        
```

##### 39

在 server 节点，使用 docker 命令创建名为 xd_net 的网络，网络网段为192.168.3.0/24，网关为 192.168.3.1，创建完成后查询此网络的详细信息，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker network create --subnet 192.168.3.0/24 --gateway 192.168.3.1 xd_net
f12a484baa62661b52272a831824a46bb2af59bfe6c596fa12da35c1c04c816b 

[root@server ~]# docker network inspect xd_net
[
    {
        "Name": "xd_net",
        "Id": "f12a484baa62661b52272a831824a46bb2af59bfe6c596fa12da35c1c04c816b",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "192.168.3.0/24",
                    "Gateway": "192.168.3.1"
                }
            ]
        },
        "Internal": false,
        "Containers": {},
        "Options": {},
        "Labels": {}
    }
]
```

##### 40

在 server 节点，使用 docker 命令创建名为 xd_net 的网络，网络网段为192.168.3.0/24，网关为 192.168.3.1，创建镜像为 centos:latest，容器名称为 centos， 使用 docker 网络为 xd_net，创建完成后查询容器使用的网络名称和查询该容器的运行状态，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker network create --subnet 192.168.3.0/24 --gateway 192.168.3.1 xd_net
f12a484baa62661b52272a831824a46bb2af59bfe6c596fa12da35c1c04c816b 

[root@server images]# docker run -itd --net=xd_net --name centos centos:latest
45804bb864d9b625a31baef93587500706a65aac64ba907b1f6ff3c23bef5c2f
[root@server images]# docker ps -a |grep centos
45804bb864d9        centos:latest               "/bin/bash"              7 seconds ago       Up 6 seconds         
```

##### 41

在 server 节点，使用 docker 命令创建名为 xd_net 的网络，网络网段为192.168.3.0/24，网关为 192.168.3.1，创建镜像为 centos:latest，容器名称为 centos， 使用 docker 网络为 xd_net，创建完成后查询容器 IP 地址，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker network create --subnet 192.168.3.0/24 --gateway 192.168.3.1 xd_net
f12a484baa62661b52272a831824a46bb2af59bfe6c596fa12da35c1c04c816b 

[root@server ~]#docker run -itd --net=xd_net --name centos centos:latest
[root@server ~]# docker inspect 45804bb864d9 |grep IPAdd
            "SecondaryIPAddresses": null,
            "IPAddress": "",
                    "IPAddress": "192.168.3.2",
```

##### 42

在 server 节点，使用 docker 命令创建名为 xd_net 的网络，网络网段为192.168.3.0/24，网关为 192.168.3.1，创建完成后，查询网络列表，接着删除 docker 网络 xd_net，完成后查询 docker 网络列表，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker network create --subnet 192.168.3.0/24 --gateway 192.168.3.1 xd_net
 [root@server ~]# docker network rm xd_net
xd_net
```

##### 43

在 server 节点，使用 docker 命令只列举 rancher/server 容器的端口映射状态，将以上操作命令及检查结果以文本形式填入答题框 。

```
 [root@server ~]# docker inspect -f {{.NetworkSettings.Ports}} 30468c72c0c8
map[3306/tcp:[] 8080/tcp:[{0.0.0.0 8080}]]
```

##### 44

在 server 节点，使用 docker 命令打印 rancher/server 镜像的大小，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker inspect -f {{.Size}} f89070da75
984901875
[root@server ~]#  docker images |grep server
192.168.100.110:5000/rancher/server            v1.6.5              f89070da7581        22 months ago       984.9 MB
```

##### 45

在 server 节点，使用 docker 命令运行 centos 镜像，运行输出打印“Hello world”，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker run centos /bin/echo 'hello word!'
hello word!
```

##### 46

在 server 节点，使用 docker 命令运行 centos 镜像，运行输出打印“Hello world”，要求启动命令包含打印完成后自动删除此容器及产生的数据，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker run --rm centos /bin/echo 'hello word!'
hello word!
```

##### 47

在 server 节点，使用 docker 命令将 rancher/server 容器内的/opt/目录拷贝到宿主机的/media/目录下，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker cp 30468c72c0c8:/opt/ /media/
[root@server ~]# ll /media/
total 0
drwxr-xr-x. 2 root root 6 May  3  2016 opt
```

##### 48

在 server 节点，使用 docker 命令将当前操作系统的 yum 源的 local.repo 文件拷贝到 rancher/server 容器内的/opt/目录下。完成后使用 exec 命令查询容器的/opt 目录下的所有文件列表，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server ~]# docker cp /etc/yum.repos.d/centos.repo 30468c72c0c8:/opt/
[root@server ~]# docker exec 30468c72c0 ls /opt/
centos.repo
```

##### 49

在 server 节点，使用 docker 查询当前系统使用的卷组信息，将以上操作命令及检查结果以文本形式填入答题框

```
[root@server ~]# docker info |grep -i Volume
 WARNING: Usage of loopback devices is strongly discouraged for production use. Use `--storage-opt dm.thinpooldev` to specify a custom block storage device.
WARNING: bridge-nf-call-iptables is disabled
WARNING: bridge-nf-call-ip6tables is disabled
 Volume: local
```

##### 50

在 server 节点，使用 centos:latest 的镜像创建容器，容器挂载使用创建的xd_volume 卷组挂载到 root 分区，完成后通过 inspect 指定查看容器的挂载情况， 将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker run -itd -v xd_Volume:/root/ centos:latest /bin/bash
12f8e37396f97e3a7659333e390b3306083badf81b3436312a876cc18eff5928
[root@server ~]# docker inspect -f {{.Mounts}} 12f8e37396f9
[{xd_Volume /var/lib/docker/volumes/xd_Volume/_data /root local z true rprivate}]
```

##### 51

使用 supermin5 命令（若命令不存在则自己安装）构建 centos7 系统的docker 镜像，镜像名称为 centos-7，镜像预装 yum、net-tools、initscripts 和 vi 命令，构建完成后提交镜像仓库上传操作，并查看此镜像，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# yum install supermin5 -y
[root@server ~]# supermin5 -v --prepare bash yum net-tools initscripts vi coreutils -o supermin.d
[root@server ~]# supermin5 -v --build --format chroot supermin.d/ -o appliance.d
[root@server ~]# echo 7 > appliance.d/etc/yum/vars/releasever
[root@server ~]# tar --numeric-owner -cpf centos_latest.tar -C appliance.d .
[root@server ~]# cat centos_latest.tar | docker import - centos-7      
sha256:c3dc1f7bcfb6e31ff9bb30d6508a3853e4faedefe2671e284d52e88623afab92
[root@server ~]# docker images |grep centos-7
centos-7                                       latest              c3dc1f7bcfb6        About a minute ago   261.5 MB
```

##### 52

编写以上题构建的 centos-7 镜像为基础镜像，构建 http 服务，Dockerfile 要求删除镜像的 yum 源，使用当前系统的 yum 源文件，完成后安装 http 服务， 此镜像要求暴露 80 端口。构建的镜像名字叫 http:v1.0。完成后查看 Dockerfile 文件并查看镜像列表，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server web]# cat Dockerfile 
FROM centos-7
MAINTAINER Xiandian
RUN rm -fv /etc/yum.repos.d/*
ADD local.repo /etc/yum.repos.d/
RUN yum clean all
RUN yum install -y httpd
EXPOSE 80

[root@server web]# cat local.repo 
[centos]
name=centos
baseurl=ftp://192.168.100.30/centos
gpgcheck=0

[docker]
name=docker
baseurl=ftp://192.168.100.30/docker/docker
gpgcheck=0

[root@server web]# docker build -t http:v1.0 . 
[root@server web]# docker images |grep http
http                                           v1.0                5c0973658f6c        2 minutes ago       461.1 MB
```

##### 53

编写以上题构建的 centos-7 镜像为基础镜像，构建数据库服务，Dockerfile 要求删除镜像的 yum 源，使用当前系统的 yum 源文件，完成后安装 mariadb 服务，使用 mysql 用户初始化数据库，添加 MYSQL_USER=xiandian、MYSQL_PASS=xiandian 环境变量，要求数据库支持中文，暴露端口 3306，容器开机运行 mysld_safe 命令，完成后启动创建的镜像并查询 Dockerfile 文件，进入容器查看容器的数据库列表，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server web]# cat Dockerfile 
FROM centos-7
MAINTAINER Xiandian
RUN rm -fv /etc/yum.repos.d/*
ADD local.repo /etc/yum.repos.d/
RUN yum clean all
RUN yum install -y mariadb mariadb-server
RUN mysql_install_db --user=mysql
ENV M   YSQL_USER=xiandian
ENV MYSQL_PASS=xiandian
EXPOSE 3306
CMD mysqld_safe

[root@server web]# docker build -t mysql3306 .
[root@server web]# docker run -itd -P mysql3306
d896d5904f92057c28d198b5bdb618620d301c5f54742004316085a136c6b516

[root@server web]# docker exec -it d896d59 /bin/bash
bash-4.2# mysql
Welcome to the MariaDB monitor.  Commands end with ; or \g.
Your MariaDB connection id is 1
Server version: 5.5.56-MariaDB MariaDB Server

Copyright (c) 2000, 2017, Oracle, MariaDB Corporation Ab and others.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

MariaDB [(none)]> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| test               |
+--------------------+
4 rows in set (0.00 sec)



```

##### 54

编写以上题构建的centos-7 镜像为基础镜像，构建Tomcat 服务，Dockerfile要求删除镜像的 yum 源，使用当前系统的 yum 源文件，安装 java 和 unzip 服务， 将提供的 apache-tomcat.zip 文件添加到/root/目录下，暴露端口 8080，将提供的index.html 文件添加到 tomcat 的网页运行的目录下，容器开机运行 catalina.sh 脚本，完成后查询 Dockerfile 文件，查询镜像列表，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server web]# cat Dockerfile 
FROM centos-7
MAINTAINER Xiandian
RUN rm -fv /etc/yum.repos.d/*
ADD local.repo /etc/yum.repos.d/
RUN yum clean all
RUN yum install -y java unzip 
ENV LC_ALL en_US.UTF-8
EXPOSE 8080
ADD apache-tomcat-7.0.56.zip /root
RUN unzip /root/apache-tomcat-7.0.56.zip
ADD index.html /apache-tomcat-7.0.56/webapps
CMD catalina.sh

[root@server web]# docker build -t tomcat:8080 .
[root@server web]# docker images |grep tomcat
tomcat                                         8080                f497c878ccc6        21 seconds ago      581.6 MB
192.168.100.110:5000/tomcat                    latest              a92c139758db        22 months ago       557.4 MB

```

##### 55

在 server 节点通过 docker api 查询 docker 的系统信息，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server web]# echo OPTIONS='-H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock' >> /etc/sysconfig/docker
[root@server web]# systemctl restart docker
[root@server web]# curl 192.168.100.110:2375/info
```

##### 56

在 server 节点通过 docker api 查询 docker 的版本，将以上操作命令及检查结果以文本形式填入答题框。

```
 [root@server web]# echo OPTIONS='-H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock' >> /etc/sysconfig/docker
 
[root@server web]# curl 192.168.100.110:2375/version
{"Version":"1.12.6","ApiVersion":"1.24","GitCommit":"3e8e77d/1.12.6","GoVersion":"go1.8.3","Os":"linux","Arch":"amd64","KernelVersion":"3.10.0-327.el7.x86_64","BuildTime":"2018-01-30T09:17:00.069703428+00:00","PkgVersion":"docker-1.12.6-71.git3e8e77d.el7.centos.1.x86_64"}
```

##### 57

在 server 节点通过 docker api 查询 docker 内所有容器，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server web]# curl 192.168.100.110:2375/containers/json
[{"Id":"30468c72c0c8ee4ec395162c8579d73caeb715e4cacbe9168dbd6630c354ff0b","Names":["/xiandian_server"],"Image":"rancher/server:v1.6.5","ImageID":"sha256:f89070da7581b401a04667ae33e2a6dea560cc43916342e4f128d4a1a025287b","Command":"/usr/bin/entry /usr/bin/s6-svscan /service","Created":1575906066,"Ports":[{"IP":"0.0.0.0","PrivatePort":8080,"PublicPort":8080,"Type":"tcp"},{"PrivatePort":3306,"Type":"tcp"}],"Labels":{},"State":"running","Status":"Up 3 minutes","HostConfig":{"NetworkMode":"default"},"NetworkSettings":{"Networks":{"bridge":{"IPAMConfig":null,"Links":null,"Aliases":null,"NetworkID":"6aecde8865e5d53f09381b3c332cc29836190233f3d1a80969aa6a3d1edab23c","EndpointID":"8706c019e06d6f07de25dd1dfc23730e2380e6593f969c5ebe142c4d145cee37","Gateway":"172.17.0.1","IPAddress":"172.17.0.3","IPPrefixLen":16,"IPv6Gateway":"","GlobalIPv6Address":"","GlobalIPv6PrefixLen":0,"MacAddress":"02:42:ac:11:00:03"}}},"Mounts":[{"Name":"9f12f2e1afc82c6568bab126f533dc70074381befcd6a02f47f3f2ca0cc46b11","Source":"/var/lib/docker/volumes/9f12f2e1afc82c6568bab126f533dc70074381befcd6a02f47f3f2ca0cc46b11/_data","Destination":"/var/lib/cattle","Driver":"local","Mode":"","RW":true,"Propagation":""},{"Name":"b2728f249bd40443786723133a405b6bf6eeafa239fd2e3f80bbee4a14bbe007","Source":"/var/lib/docker/volumes/b2728f249bd40443786723133a405b6bf6eeafa239fd2e3f80bbee4a14bbe007/_data","Destination":"/var/lib/mysql","Driver":"local","Mode":"","RW":true,"Propagation":""},{"Name":"e12b76faac196b43d2703466bfead844c1a2e379c9540c4c07d8cd3b00f5772b","Source":"/var/lib/docker/volumes/e12b76faac196b43d2703466bfead844c1a2e379c9540c4c07d8cd3b00f5772b/_data","Destination":"/var/log/mysql","Driver":"local","Mode":"","RW":true,"Propagation":""}]},{"Id":"ff80f1a5d264ae013d8be5a736a6474f78024813eaee0808e59e5fb3d610a302","Names":["/registry"],"Image":"docker.io/registry:latest","ImageID":"sha256:c9bd19d022f6613fa0e3d73b2fe2b2cffe19ed629a426db9a652b597fccf07d4","Command":"/entrypoint.sh /etc/docker/registry/config.yml","Created":1575905767,"Ports":[{"IP":"0.0.0.0","PrivatePort":5000,"PublicPort":5000,"Type":"tcp"}],"Labels":{},"State":"running","Status":"Up 3 minutes","HostConfig":{"NetworkMode":"default"},"NetworkSettings":{"Networks":{"bridge":{"IPAMConfig":null,"Links":null,"Aliases":null,"NetworkID":"6aecde8865e5d53f09381b3c332cc29836190233f3d1a80969aa6a3d1edab23c","EndpointID":"f9a17c4715861211073d91b53ba0c78e51211fcf6d190c42ab94335ade27a091","Gateway":"172.17.0.1","IPAddress":"172.17.0.2","IPPrefixLen":16,"IPv6Gateway":"","GlobalIPv6Address":"","GlobalIPv6PrefixLen":0,"MacAddress":"02:42:ac:11:00:02"}}},"Mounts":[{"Name":"8c301b409524ea5ea93b4e95d105ba45728c887d8802cf46f6b70d1a85aeeb0c","Source":"/var/lib/docker/volumes/8c301b409524ea5ea93b4e95d105ba45728c887d8802cf46f6b70d1a85aeeb0c/_data","Destination":"/var/lib/registry","Driver":"local","Mode":"","RW":true,"Propagation":""}]}]
```

##### 58

在 server 节点通过 docker api 查询 docker 内所有镜像，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server web]# curl -L 192.168.100.110:2375/images/json
```

##### 59

在 server 节点通过 docker api 相关命令查询 rancher/server 镜像的具体信息，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server web]# curl 192.168.100.110:2375/images/f89070da7581/json
```

##### 60

根据提供的 tomcat 镜像，创建容器，使用该镜像，并创建/root/www1 目录，在www1 目录下编写index.jsp 文件，容器的默认项目地址连接到创建的www1 目录，要求访问 tomcat 的时候输出一句话为 this is Tomcat1，最后启动容器，并启动 tomcat 服务，使用 curl 命令查询 tomcat 的首页，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server web]# mkdir /root/www1
[root@server web]# echo "this is Tomcat1" >/root/www1/index.jsp
[root@server ~]# docker run -itd -P -v /root/www1/:/usr/local/tomcat/webapps/ROOT tomcat /bin/bash
02940bc2feac6f0c474a12edf97654725115485ac6861f1716bc613750d3e96a

[root@server ~]# docker exec -it 02940b /bin/bash
root@02940bc2feac:/usr/local/tomcat# ./bin/startup.sh 
Tomcat started.

root@02940bc2feac:/usr/local/tomcat# exit
[root@server ~]# docker ps -a |grep tomcat
02940bc2feac        tomcat                      "/bin/bash"              4 minutes ago       Up 4 minutes        0.0.0.0:32769->8080/tcp            nostalgic_bartik

[root@server ~]# curl 192.168.100.110:32769
this is Tomcat1
```

##### 61

在 server 节点，使用 docker 命令查看最近创建的 2 个容器的 id，将以上操作命令及检查结果以文本形式填入答题框。

```
[root@server ~]# docker ps -n=2 -q
02940bc2feac
30468c72c0c8

```

##### 62

在 server 节点，创建容器，然后将容器的卷空间值扩容（不要求扩容文件系统），最后查看容器的卷空间值。

```

```

