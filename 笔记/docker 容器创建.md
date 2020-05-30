### docker 容器创建

###### redis

```

```

###### mysql

[修改默认编码为 utf8](https://blog.csdn.net/jiegemena/article/details/80062653)

```shell
# 拉取镜像
docker pull mysql:5.7

# 运行mysql
sudo docker run -d -p 3306:3306 --restart always --name mysql01 -e MYSQL_ROOT_PASSWORD=123456 -d -v /media/docker-V/mysql/data:/var/lib/mysql  mysql:5.7 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci


# 进入docker本地连接mysql客户端
$ sudo docker exec -it mysql bash
$ mysql -uroot -p123456
```



###### Nginx

```shell
# 创建nginx
docker run -d --name nginx01 -p 80:80  nginx 

# 进入nginx
docker exec -it 64ab6 /bin/bash
```

###### elasticsearch+kibana

```shell
docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.6.2
```

###### Portainer

```shell
docker run -d -p 9000:9000 --restart=always -v /var/run/docker.sock:/var/run/docker.sock --name prtainer  portainer/portainer
```

