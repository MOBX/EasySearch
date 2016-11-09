/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.easySearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;

import com.lamfire.json.JSON;
import com.lamfire.utils.Lists;
import com.lamfire.utils.PropertiesUtils;
import com.lamfire.utils.StringUtils;
import com.mob.easySearch.support.ElasticsearchHelper;

/**
 * 导出ES指定Index的所有数据
 * 
 * @author zxc Nov 8, 2016 6:24:22 PM
 */
public class ExportAndImportData {

    private static Properties          pro;
    private static String              clusterName;
    private static String              nodes;
    private static ElasticsearchHelper es;

    private static long                keepAlive = 600000;

    static {
        pro = PropertiesUtils.load("application.properties", ExportAndImportData.class);
        clusterName = pro.getProperty("es.cluster.name");
        nodes = pro.getProperty("es.nodes");
        String[] nodeArray = StringUtils.split(nodes, ",");
        es = new ElasticsearchHelper(clusterName, nodeArray);
    }

    public static void main(String[] args) {
        // long total = new ExportAndImportData()//
        // .exportData("/data/es/appgo.json", "appgo", "app_publisher_search_test_2");
        // System.out.println("---------------------> 共导出数据[" + total + "]条 <------------------------");

        long _total = new ExportAndImportData()//
        .importData("/data/es/appgo.json", "appgo", "app_publisher_search_test_1", 1000);
        System.out.println("---------------------> 共导入数据[" + _total + "]条 <------------------------");
    }

    public long exportData(String filePath, String index, String indexType) {
        long total = 0;
        int size = 100;
        try {
            Client client = es.getClient();
            TimeValue timeValue = new TimeValue(keepAlive);
            SearchResponse response = client.prepareSearch(index)//
            .setTypes(indexType)//
            .setQuery(QueryBuilders.matchAllQuery())//
            .setSize(size)//
            .setScroll(timeValue)//
            .setSearchType(SearchType.SCAN).execute().actionGet();
            String scrollId = response.getScrollId();
            List<String> data = Lists.newArrayList(size);
            long _count = response.getHits().getTotalHits();
            for (; total < _count;) {
                SearchResponse scrollResponse = client.prepareSearchScroll(scrollId) //
                .setScroll(timeValue).execute().actionGet();
                SearchHits hits = scrollResponse.getHits();
                int length = hits.getHits().length;
                total += length;
                for (int i = 0; i < length; i++) {
                    String result = hits.getHits()[i].getSourceAsString();
                    if (StringUtils.isBlank(result)) continue;
                    data.add(result);
                }
                System.out.println("总量" + _count + " 已经查到" + total);
            }
            FileUtils.writeLines(new File(filePath), "utf-8", data, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public long importData(String dataFile, String index, String indexType, int batchSize) {
        long count = 0;
        try {
            Client client = es.getClient();
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            BufferedReader reader = new BufferedReader(new FileReader(dataFile));
            String line = null;

            while ((line = reader.readLine()) != null) {
                JSON temp = JSON.fromJSONString(line);
                IndexRequestBuilder indexBuilder = client.prepareIndex(index, indexType);
                if (temp.containsKey("id")) indexBuilder.setId(temp.getString("id"));
                indexBuilder.setSource(line);
                bulkRequest.add(indexBuilder);
                if (count % batchSize == 0) {
                    bulkRequest.execute().actionGet();
                    System.out.println("提交了[" + count + "]条.");
                }
                count++;
            }
            bulkRequest.execute().actionGet();
            System.out.println("导入完毕，共导入数据[" + count + "]条");
            reader.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
