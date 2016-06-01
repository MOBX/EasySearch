#通用搜索服务


##索引建立
* indexName 	索引名称命名空间
* indexType 	文档名称
* schema 		索引键值对
* data   		索引内容
* version 	索引版本


###接口设计

1. schema定义 文档属性键值对

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
    "p_url": {
        "type": "string", 
        "index": "not_analyzed"
    }, 
    "tagName": {
        "type": "string", 
        "index_analyzer": "whitespace"
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
        "index": "not_analyzed"
    }, 
    "website_id": {
        "type": "string", 
        "index_analyzer": "whitespace"
    }
}'
{"status":200}
```


```
#索引库schema定义查询接口
curl -XGET 'http://127.0.0.1:8080/indexName/indexType/schema'  
```

2. index  文档内容

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
{"status":200}
```

3. bulk   批量索引文档

```
curl -XPOST 'http://127.0.0.1:8080/bulk' --data-binary "index.json"
{"status":200}
```
index.json是存在当前目录下的一个json文件,里面存储了一个josn数组,你可以让它存储任何结构相同的数组.


4. alias  索引文档别名,同义词

```
curl -XPOST 'http://127.0.0.1:8080/indexName/indexType/alias' -d '  
{
    "alias": "news_index"
}'  
{"status":200}
```


##搜索服务
* indexName 	索引命名空间
* indexType 	文档名称
* fields 		输出键值对
* query  		搜索内容(keywords,must,match,fuzzy)
* filter 		过滤规则
* sort   		排序规则,默认文本相似度排序


###接口设计

1. search 
```
curl -XGET 'http://127.0.0.1:8080/indexName/indexType/search?query.keywords='+keywords
```



##分词服务
* text 	   文本内容
* analyzer   分词规则


###接口设计

1. analyzer 
```
curl -XGET 'http://127.0.0.1:8080/analyzer?text='+text 
```
