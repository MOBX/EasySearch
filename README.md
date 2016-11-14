#通用搜索服务


##约定说明
* indexName 	索引名称命名空间
* indexType 	文档名称
* schema 		索引键值对
* data   		索引内容
* version 		索引版本
* fields 		输出键值对
* keywords  	全文检索关键词
* filter 		过滤规则
* sort   		排序规则,默认文本相似度排序
* distinct      聚合字段
* field         完全匹配字段


###接口设计

###索引定义

####1. schema定义创建

```
curl -XPOST 'http://127.0.0.1:8080/indexName/indexType/schema' -d ' 
{
    "content": {
        "type": "string"
    }, 
    "webSectionName": {
        "type": "string"
    }, 
    "title": {
        "type": "string"
    }, 
    "type": {
        "type": "string", 
        "analyzed": false,
        "copy_to": "type_and_tagName"
    }, 
    "tagName": {
        "type": "string", 
        "index_analyzer": "whitespace",
        "copy_to": "type_and_tagName"
    }, 
    "keyWords": {
        "type": "string", 
        "index_analyzer": "ik"
    }, 
    "isTask": {
        "type": "long"
    }, 
    "newsDate": {
        "type": "long"
    }, 
    "crawlDate": {
        "type": "long"
    }, 
    "webName": {
        "type": "string"
    }, 
    "crawl_id": {
        "type": "long"
    }, 
    "url": {
        "type": "string", 
        "analyzed": false
    }, 
    "website_id": {
        "type": "string", 
        "index_analyzer": "whitespace"
    }, 
    "type_and_tagName": {
        "type": "string", 
        "analyzed": false
    }
}'
#return
{"status":200}
```


####2. schema定义查询
```
curl -XGET 'http://127.0.0.1:8080/indexName/indexType/schema'  
```

#####3. 查询全部schema定义
```
curl -XGET 'http://127.0.0.1:8080/schemas'  
```

###索引创建

####1. 创建索引

```
curl -XPOST 'http://127.0.0.1:8080/indexName/indexType/index/{version}' -d ' 
[
	{
	    "content": "news", 
	    "webSectionName": "news", 
	    "title": "news", 
	    "p_url": "news", 
	    "tagName": "news", 
	    "keyWords": "news", 
	    "isTask": "news", 
	    "newsDate": "news", 
	    "crawlDate": "news", 
	    "webName": "news", 
	    "crawl_id": "news", 
	    "url": "news", 
	    "website_id": "news"
	}
]'
#return
{"status":200}
```

####2. 批量提交创建索引

```
curl -XPOST 'http://127.0.0.1:8080/bulk' --data-binary "index.json"
#return
{"status":200}
```
index.json是存在当前目录下的一个json文件,里面存储了一个josn数组,你可以让它存储任何结构相同的数组.


####3. alias 索引文档别名,同义词

```
curl -XPOST 'http://127.0.0.1:8080/indexName/indexType/alias' -d '  
{
    "alias": "news_index"
}' 
#return 
{"status":200}
```


###搜索服务

####1. 关键词搜索
```
#普通的全文检索,keywords为空按照得分全部查询(支持分页)
curl -XGET 'http://127.0.0.1:8080/indexName/indexType/search?keywords=keywords'
```

```
#指定索引字段的全文检索(支持分页)
curl -XGET 'http://127.0.0.1:8080/indexName/indexType/search?keywords=keywords&field=field'
```

```
#指定某些字段完全匹配的全文检索(支持分页)
curl -XGET 'http://127.0.0.1:8080/indexName/indexType/search?keywords=keywords&xxx=yyy'
```

```
#指定字段区间范围查询的全文检索(在正常字段后面加上下划线 gt:大于,lt:小于,gte:大于或等于,lte:小于或等于;支持分页)
curl -XGET 'http://127.0.0.1:8080/indexName/indexType/search?keywords=keywords&xxx_gt=yyy&xxx_gt=yyy&zzz_lt=rrr'
```

```
#指定字段的聚合查询全文检索(其中distinct为索引中任意字段;distinct字段为聚合字段,可以支持多个同时聚合;支持分页)
curl -XGET 'http://127.0.0.1:8080/indexName/indexType/search?keywords=keywords&distinct=xxxx&topOnly=true'
```

```
#指定多字段的聚合查询全文检索,并显示聚合桶类集合(其中distinct为索引中任意字段;distinct字段为聚合字段注意多个字段的排序,可以支持多个同时聚合;topOnly是否true仅显示顶部1个,false显示多个,默认false;支持分页)
#多字段聚合说明(使用脚本合并字段;copy_to方法合并两个字段,创建出一个新的字段,对新字段执行单个字段的聚合;建议使用第二个,本接口已支持,需要在schema定义中指明)
curl -XGET 'http://127.0.0.1:8080/indexName/indexType/search?keywords=keywords&distinct=xxx1&distinct=xxx2&topOnly=false'
```
上述几种基础查询,可以任意组合来实现多种业务查询场景.

###分词服务
* text 	   文本内容
* analyzer   分词规则

####1. 文本分词
```
#对text文本进行分词,使用默认分词器;如需其他分词器,可带上&analyzer=jcseg等
curl -XGET 'http://127.0.0.1:8080/analyzer?text=text'

#return
{
  "status": 200,
  "data": [
    {
      "term": "hello",
      "startOffset": 0,
      "endOffset": 5,
      "position": 1,
      "type": "ENGLISH"
    },
    {
      "term": "world",
      "startOffset": 6,
      "endOffset": 11,
      "position": 2,
      "type": "ENGLISH"
    }
  ]
}
```

####2. 分词器查询
```
#获取服务目前支持的全部分词器
curl -XGET 'http://127.0.0.1:8080/analyzers' 
```

![image](https://github.com/MOBX/EasySearch/blob/master/src/main/resources/static/images/demo.png)

## 配置文件说明

####application.properties
```
#es节点名称
es.cluster.name=easy_search
#es传输通信端口
es.nodes=192.168.180.155:9300,192.168.180.156:9300
```



## install & depoly
### 安裝本地依赖包
`mvn clean`，然后`mvn -B -DskipTests clean dependency:list install`

### 下载依赖 & 测试
`mvn clean install`

### 打包
`mvn clean package -DskipTests`

### 运行
```
nohup java -cp "/usr/lib/jvm/java-7-openjdk-amd64/lib:/usr/lib/jvm/java-7-openjdk-amd64/jre/lib:/home/zxc/workspace/easySearch/conf/:/home/zxc/workspace/easySearch/lib/*:" com.mob.easySearch.EasySearchBoot > /dev/null  2>&1 &
```