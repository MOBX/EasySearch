/*
 * Copyright 2015-2020 msun.com All right reserved.
 */
package com.mob.easySearch.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lamfire.json.JSON;
import com.lamfire.utils.StringUtils;
import com.mob.easySearch.cons.Definition;
import com.mob.easySearch.support.ElasticsearchHelper;

/**
 * @author zxc Jun 14, 2016 3:43:28 PM
 */
@Component
public class BaseController implements Definition {

    @Autowired
    protected HttpServletRequest         request;

    @Value("${es.cluster.name}")
    private String                       clusterName;
    @Value("${es.nodes}")
    private String                       nodes;

    protected static ElasticsearchHelper es;

    @PostConstruct
    private void init() {
        String[] nodeArray = StringUtils.split(nodes, ",");
        es = new ElasticsearchHelper(clusterName, nodeArray);
    }

    public static void split(String word, List<String> list) {
        if (StringUtils.isEmpty(word)) return;
        int index = org.apache.commons.lang3.StringUtils.indexOf(word, AGGR_SPLIT);
        String split_word = org.apache.commons.lang3.StringUtils.substring(word, 0, index);
        list.add(split_word);

        String _word = org.apache.commons.lang3.StringUtils.substring(word, index + 3, word.length());
        int _index = org.apache.commons.lang3.StringUtils.indexOf(_word, AGGR_SPLIT);
        if (_index > 0) {
            split(_word, list);
        } else {
            list.add(_word);
        }
        return;
    }

    public static JSON fail(String msg) {
        return result(900, null, msg);
    }

    public static JSON ok() {
        return result(200, null, "");
    }

    public static JSON ok(Object data) {
        return result(200, data, "");
    }

    public JSON ok(Object[][] keyValues) {
        JSON data = new JSON();
        for (int i = 0; i < keyValues.length; i++) {
            data.put((String) keyValues[i][0], keyValues[i][1]);
        }
        return result(200, data, "");
    }

    public static JSON result(int status, Object children, String msg) {
        JSON json = new JSON();
        json.put("status", status);
        if (StringUtils.isNotEmpty(msg)) json.put("msg", msg);
        if (children != null) json.put("data", children);
        return json;
    }
}
