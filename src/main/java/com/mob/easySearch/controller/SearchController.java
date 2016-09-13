/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.easySearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.elasticsearch.common.collect.Maps;
import org.springframework.web.bind.annotation.*;

import com.lamfire.json.JSON;
import com.lamfire.utils.Sets;
import com.lamfire.utils.StringUtils;
import com.mob.easySearch.support.JsonResult;

/**
 * @author zxc Jun 8, 2016 5:52:05 PM
 */
@RestController
@RequestMapping("/v1/api")
@Api(value = "search", description = "搜索服务")
public class SearchController extends BaseController {

    @ResponseBody
    @ApiOperation(value = "do search", httpMethod = "GET", response = JsonResult.class, notes = "搜索接口")
    @RequestMapping(value = "/{indexName}/{indexType}/search", produces = { "application/json" }, method = RequestMethod.GET)
    JSON search(@ApiParam(required = true, name = "indexName", value = "索引名称命名空间") @PathVariable("indexName") String indexName,
                @ApiParam(required = true, name = "indexType", value = "文档名称") @PathVariable("indexType") String indexType,
                @ApiParam(required = false, name = "pageno", value = "分页页码") @RequestParam(value = "pageno", defaultValue = "1") Integer pageno,
                @ApiParam(required = false, name = "pagesize", value = "每页数量") @RequestParam(value = "pagesize", defaultValue = "30") Integer pagesize,
                @ApiParam(required = true, name = "keywords", value = "关键词") @RequestParam("keywords") String keywords) {
        access.info("[SearchController parameterMap]:" + JSON.toJSONString(request.getParameterMap()));
        if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(indexType)) return fail();
        Set<String> field = Sets.newHashSet();
        Set<String> aggregation = Sets.newHashSet();
        if (request.getParameterValues("field") != null) {
            field = Sets.newHashSet(request.getParameterValues("field"));
        }
        if (request.getParameterValues("distinct") != null) {
            aggregation = Sets.newHashSet(request.getParameterValues("distinct"));
        }
        Map<String, Object[]> filter = Maps.newHashMap();
        for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            if (entry != null && !StringUtils.equalsIgnoreCase(entry.getKey(), "pageno")
                && !StringUtils.equalsIgnoreCase(entry.getKey(), "pagesize")
                && !StringUtils.equalsIgnoreCase(entry.getKey(), "keywords")
                && !StringUtils.equalsIgnoreCase(entry.getKey(), "distinct")
                && !StringUtils.equalsIgnoreCase(entry.getKey(), "field")) {
                filter.put(entry.getKey(), entry.getValue());
            }
        }
        try {
            access.info("[SearchController]: filter=" + JSON.toJSONString(filter) + ",field=" + field);
            Map<String, Object> result = Maps.newHashMap();
            if (aggregation.size() == 0) {
                result = es.queryString(indexName, indexType, pageno, pagesize, keywords, filter, field);
            } else {
                result = es.aggregation(indexName, indexType, pageno, pagesize, keywords, filter, field, aggregation);
            }
            return success(result);
        } catch (Exception e) {
            _.error("es.queryString error!", e);
        }
        return success();
    }
}
