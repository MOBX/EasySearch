/*
 * Copyright 2015-2020 msun.com All right reserved.
 */
package com.mob.easySearch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;

import com.lamfire.json.JSON;
import com.mob.easySearch.support.ElasticsearchHelper;

/**
 * @author zxc Jun 13, 2016 5:51:09 PM
 */
public class ElasticsearchHelperTest {

    static ElasticsearchHelper es = new ElasticsearchHelper("sdk.sms.mob", "192.168.180.155", 9300);

    public static void main(String[] args) {
        // schema();
        // getSchema();
        // index();
        // analyzer();
        query();
    }

    // 创建schema
    static void schema() {
        es.dropIndex("mob_news");
        if (!es.existsIndex("mob_news")) {
            es.createIndex("mob_news");
        }
        Map<String, Map<String, Object>> fields = new HashMap<String, Map<String, Object>>();
        fields.put("content", new HashMap<String, Object>());
        fields.put("title", new HashMap<String, Object>());
        fields.put("isTask", new HashMap<String, Object>());
        fields.put("newsDate", new HashMap<String, Object>());
        fields.put("keyWords", new HashMap<String, Object>());
        es.createMapping("mob_news", "shanghai", fields);
        // es.clearIndex("news");
    }

    static void getSchema() {
        GetMappingsResponse mappingsRes = es.getMapping("mob_news", "shanghai");
        try {
            Map<String, Object> sourceMap = mappingsRes.mappings().get("mob_news").get("shanghai").getSourceAsMap();
            System.out.println(JSON.toJSONString(sourceMap.get("properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 创建index
    static void index() {
        Map<String, Object> source = new HashMap<String, Object>();
        source.put("content", "zxc hello world!");
        source.put("title", "zxc hello world!");
        source.put("isTask", "zxc hello world!");
        source.put("content", "zxc hello world!");
        source.put("content", "zxc hello world!");
        es.index("mob_news", "shanghai", "1", source);
    }

    // 查询query
    static void query() {

    }

    // 分词analyzer
    static void analyzer() {
        AnalyzeResponse res = es.analyzer("mob_news", "测试Elasticsearch分词器,我是huawei is 192.168.10.10", "ik");
        System.out.println(JSON.toJSONString(res.getTokens()));
    }
}
