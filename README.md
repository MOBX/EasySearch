# 通用搜索服务

![image](https://github.com/MOBX/EasySearch/blob/master/src/main/resources/static/images/demo.png)

## Next

* suggest支持
* solr支持
* 纯 lucene的索引库支持
* 自定义分词器支持
* 更多分析接口

## 约定说明

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


### 接口设计

### 索引定义

#### 1. schema定义创建

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


#### 2. schema定义查询
```
curl -XGET 'http://127.0.0.1:8080/indexName/indexType/schema'  
```

##### 3. 查询全部schema定义
```
curl -XGET 'http://127.0.0.1:8080/schemas'  
```

### 索引创建

#### 1. 创建索引

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

#### 2. 批量提交创建索引

```
curl -XPOST 'http://127.0.0.1:8080/bulk' --data-binary "index.json"
#return
{"status":200}
```
index.json是存在当前目录下的一个json文件,里面存储了一个josn数组,你可以让它存储任何结构相同的数组.


#### 3. alias 索引文档别名,同义词

```
curl -XPOST 'http://127.0.0.1:8080/indexName/indexType/alias' -d '  
{
    "alias": "news_index"
}' 
#return 
{"status":200}
```


### 搜索服务

#### 1. 关键词搜索
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

### 分词服务

* text 	   文本内容
* analyzer   分词规则

#### 1. 文本分词
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

#### 2. 分词器查询
```
#获取服务目前支持的全部分词器
curl -XGET 'http://127.0.0.1:8080/analyzers' 
```

## 配置文件说明

#### application.properties
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



## Elasticsearch优化

### Filter Cache
 
Filter cache是用来缓存使用过的filter的结果集的，需要注意的是这个缓存也是常驻heap，无法GC的。默认的10% heap size设置工作得够好了，如果实际使用中heap没什么压力的情况下，才考虑加大这个设置。
 
### Field Data cache
 
对 搜索结果做排序或者聚合操作，需要将倒排索引里的数据进行解析，然后进行一次倒排。在有大量排序、数据聚合的应用场景，可以说field data cache是性能和稳定性的杀手。这个过程非常耗费时间，因此ES 2.0以前的版本主要依赖这个cache缓存已经计算过的数据，提升性能。但是由于heap空间有限，当遇到用户对海量数据做计算的时候，就很容易导致 heap吃紧，集群频繁GC，根本无法完成计算过程。 ES2.0以后，正式默认启用Doc Values特性(1.x需要手动更改mapping开启)，将field data在indexing time构建在磁盘上，经过一系列优化，可以达到比之前采用field data cache机制更好的性能。因此需要限制对field data cache的使用，最好是完全不用，可以极大释放heap压力。这里需要注意的是，排序、聚合字段必须为not analyzed。 设想如果有一个字段是analyzed过的，排序的实际对象其实是词典，在数据量很大情况下这种情况非常致命。
 
### Bulk Queue
 
Bulk Queue是做什么用的？当所有的bulk thread都在忙，无法响应新的bulk request的时候，将request在内存里排列起来，然后慢慢清掉。一般来说，Bulk queue不会消耗很多的heap，但是见过一些用户为了提高bulk的速度，客户端设置了很大的并发量，并且将bulk Queue设置到不可思议的大，比如好几千。这在应对短暂的请求爆发的时候有用，但是如果集群本身索引速度一直跟不上，设置的好几千的queue都满了会 是什么状况呢？ 取决于一个bulk的数据量大小，乘上queue的大小，heap很有可能就不够用，内存溢出了。一般来说官方默认的thread pool设置已经能很好的工作了，建议不要随意去“调优”相关的设置，很多时候都是适得其反的效果。
 
### Indexing Buffer
 
Indexing Buffer是用来缓存新数据，当其满了或者refresh/flush interval到了，就会以segment file的形式写入到磁盘。 这个参数的默认值是10% heap size。根据经验，这个默认值也能够很好的工作，应对很大的索引吞吐量。 但有些用户认为这个buffer越大吞吐量越高，因此见过有用户将其设置为40%的。到了极端的情况，写入速度很高的时候，40%都被占用，导致OOM。
 
### Cluster State Buffer
 
ES 被设计成每个Node都可以响应用户的api请求，因此每个Node的内存里都包含有一份集群状态的拷贝。这个Cluster state包含诸如集群有多少个Node，多少个index，每个index的mapping是什么？有少shard，每个shard的分配情况等等 (ES有各类stats api获取这类数据)。 在一个规模很大的集群，这个状态信息可能会非常大的，耗用的内存空间就不可忽视了。并且在ES2.0之前的版本，state的更新是由Master Node做完以后全量散播到其他结点的。 频繁的状态更新都有可能给heap带来压力。 在超大规模集群的情况下，可以考虑分集群并通过tribe node连接做到对用户api的透明，这样可以保证每个集群里的state信息不会膨胀得过大。
 
### 超大搜索聚合结果集的fetch
 
ES 是分布式搜索引擎，搜索和聚合计算除了在各个data node并行计算以外，还需要将结果返回给汇总节点进行汇总和排序后再返回。无论是搜索，还是聚合，如果返回结果的size设置过大，都会给heap造成 很大的压力，特别是数据汇聚节点。超大的size多数情况下都是用户用例不对，比如本来是想计算cardinality，却用了terms aggregation + size:0这样的方式; 对大结果集做深度分页；一次性拉取全量数据等等。
 
### 在开发与维护过程中我们总结出以下优化建议:
 
* 尽量运行在Sun/Oracle JDK1.7以上环境中，低版本的jdk容易出现莫名的bug，ES性能体现在在分布式计算中，一个节点是不足以测试出其性能，一个生产系统至少在三个节点以上。
* 倒排词典的索引需要常驻内存，无法GC，需要监控data node上segment memory增长趋势。
* 根据机器数，磁盘数，索引大小等硬件环境，根据测试结果，设置最优的分片数和备份数，单个分片最好不超过10GB，定期删除不用的索引，做好冷数据的迁移。
* 保守配置内存限制参数，尽量使用doc value存储以减少内存消耗，查询时限制size、from参数。
* 如果不使用_all字段最好关闭这个属性，否则在创建索引和增大索引大小的时候会使用额外更多的CPU，如果你不受限CPU计算能力可以选择压缩文档的_source。这实际上就是整行日志，所以开启压缩可以减小索引大小。
* 避免返回大量结果集的搜索与聚合。缺失需要大量拉取数据可以采用scan & scroll api来实现。
* 熟悉各类缓存作用，如field cache, filter cache, indexing cache, bulk queue等等，要设置合理的大小，并且要应该根据最坏的情况来看heap是否够用。
* 必须结合实际应用场景，并对集群使用情况做持续的监控。
