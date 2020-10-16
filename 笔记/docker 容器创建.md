### docker 容器创建

###### redis

```

```

###### mysql

[修改默认编码为 utf8](https://blog.csdn.net/jiegemena/article/details/80062653)

```shell
# 拉取镜像
$ docker pull mysql:5.7

# 运行mysql
$ sudo docker run -d -p 3306:3306 --restart always --name mysql01 -e MYSQL_ROOT_PASSWORD=123456 -d -v /media/docker-V/mysql/data:/var/lib/mysql  mysql:5.7 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci


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

###### RabbitMQ

```shell
$ docker run --name rabbitmq -d -p 15672:15672 -p 5672:5672 --restart always rabbitmq:3.7.7-management
# 访问管理界面的地址就是 http://[宿主机IP]:15672
```

###### Zookeeper

```shell
$ docker run -d -p 2181:2181 --name some-zookeeper --restart always zookeeper:3.4.9
```

###### Consul

```shell
$ docker run -d -p 8500:8500 -v /data/consul:/consul/data -e CONSUL_BIND_INTERFACE='eth0' --name=consul_server_1 consul:1.6.1 agent -server -bootstrap -ui -node=1 -client='0.0.0.0' 

$ docker run -d -p 8500:8500 -v /data/consul:/consul/data --name=dev-consul -e CONSUL_BIND_INTERFACE=eth0 --restart always consul:1.6.1
```

