/*
 * Copyright 2015-2020 msun.com All right reserved.
 */
package com.mob.easySearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lamfire.json.JSON;
import com.lamfire.utils.StringUtils;
import com.mob.easySearch.support.JsonResult;

/**
 * @author zxc Jun 8, 2016 5:54:15 PM
 */
@RestController
@RequestMapping("/v1/api")
@Api(value = "analyzer", description = "分词服务")
public class AnalyzerController extends BaseController {

    @ApiOperation(value = "analyzer text", httpMethod = "GET", response = JsonResult.class, notes = "分词接口")
    @RequestMapping(value = "/analyzer", produces = { "application/json" }, method = RequestMethod.GET)
    JSON analyzer(@ApiParam(required = true, name = "text", value = "文本", defaultValue = "hello world!") @RequestParam("text") String text,
                  @ApiParam(required = false, name = "analyzer", value = "分词器", defaultValue = "ik") @RequestParam("analyzer") String analyzer) {
        if (StringUtils.isEmpty(text)) return fail("参数错误");

        if (!es.existsIndex("easy_search")) {
            es.createIndex("easy_search");
            es.createMapping("easy_search", "easy_search", new HashMap<String, Map<String, Object>>());
        }
        AnalyzeResponse res = es.analyzer("easy_search", text, analyzer);
        return ok(res.getTokens());
    }

    @ApiOperation(value = "list all analyzer", httpMethod = "GET", response = JsonResult.class, notes = "支持的全部分词器")
    @RequestMapping(value = "/analyzers", produces = { "application/json" }, method = RequestMethod.GET)
    JSON analyzers() {
        return ok(new Object[][] { { "analyzer", new String[] { "IKAnalyzer", "StandardAnalyzer", "SmartChineseAnalyzer", "SimpleAnalyzer", "StopAnalyzer", "WhitespaceAnalyzer" } } });
    }
}
