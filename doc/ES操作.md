## 启动步骤：
1. 启动ES
2. 启动Kibana
3. 启动Logstash（修改配置文件，需要同步的数据库地址和ES地址）
4. 构建“创建ES表结构”
5. 查看同步数据：GET post_v1/_search

## ES初始化结构
  1、启动ES、Kibana \
  2、初始化结构：

  ```
  #删除信息
  DELETE /post
  DELETE /post_v1
  #获取数据
  GET post/_search
  GET post_v1/_search
  #初始化结构
  PUT post_v1
  {
    "aliases": {
      "post": {}
    },
    "mappings": {
      "properties": {
        "title": {
          "type": "text",
          "analyzer": "ik_max_word",
          "search_analyzer": "ik_smart",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        },
        "content": {
          "type": "text",
          "analyzer": "ik_max_word",
          "search_analyzer": "ik_smart",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        },
        "tags": {
          "type": "keyword"
        },
        "userId": {
          "type": "keyword"
        },
        "createTime": {
          "type": "date"
        },
        "updateTime": {
          "type": "date"
        },
        "isDelete": {
          "type": "keyword"
        }
      }
    }
  }
  ```
  3、配置好‘logstash’启动同步数据

## 相关操作

1. 查看结构信息：GET /post_v1
2. 检查索引、别名是否已存在
   GET /_cat/indices GET /_cat/aliases

3. 删除索引、别名
   DELETE /post
   DELETE /post_v1

## logstash同步程序

#### 配置文件【注意是同步win的还是linux的ES】

1. linux

```
input {
  jdbc {
    jdbc_driver_library => "D:\data\service\ElasticSearch\mysql-connector-java-8.0.29.jar"
    jdbc_driver_class => "com.mysql.jdbc.Driver"
    jdbc_connection_string => "jdbc:mysql://118.178.231.233:3306/ry-vue"
    jdbc_user => "root"
	jdbc_password => "Admin_ia_sql"
  	statement => "SELECT * from post where updateTime > :sql_last_value and updateTime < now() order by updateTime desc"
  	tracking_column => "updateTime"
  	tracking_column_type => "timestamp"
    use_column_value => true
    parameters => { "favorite_artist" => "Beethoven" }
    schedule => "*/5 * * * * *"
    jdbc_default_timezone => "Asia/Shanghai"
  }
}

filter {
	mutate {
		rename => {
			"updatetime" => "updateTime"
			"userid" => "userId"
			"createtime" => "createTime"
			"isdelete" => "isDelete"
		}
		remove_field => ["thumbnum", "favournum"]
	}
}

output {
  stdout { codec => rubydebug }
  elasticsearch {
	hosts => "http://118.178.231.233:9200"
	index => "post_v1"
	document_id => "%{id}"
  }
}
```

2. Windows

```
input {
  jdbc {
    jdbc_driver_library => "D:\data\service\ElasticSearch\mysql-connector-java-8.0.29.jar"
    jdbc_driver_class => "com.mysql.jdbc.Driver"
    jdbc_connection_string => "jdbc:mysql://localhost:3306/ry-vue"
    jdbc_user => "root"
	  jdbc_password => "abc123"
  	statement => "SELECT * from post where updateTime > :sql_last_value and updateTime < now() order by updateTime desc"
  	tracking_column => "updateTime"
  	tracking_column_type => "timestamp"
    use_column_value => true
    parameters => { "favorite_artist" => "Beethoven" }
    schedule => "*/5 * * * * *"
    jdbc_default_timezone => "Asia/Shanghai"
  }
}

filter {
	mutate {
		rename => {
			"updatetime" => "updateTime"
			"userid" => "userId"
			"createtime" => "createTime"
			"isdelete" => "isDelete"
		}
		remove_field => ["thumbnum", "favournum"]
	}
}

output {
  stdout { codec => rubydebug }
  elasticsearch {
	hosts => "http://localhost:9200"
	index => "post_v1"
	document_id => "%{id}"
  }
}
```



#### 启动程序

```bash
logstash.bat -f ..\config\mytask.conf
```