/*
 * Copyright 2015-2020 msun.com All right reserved.
 */
package com.mob.easySearch.controller;

import static com.mob.easySearch.support.ElasticsearchHelper.customMatches;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.*;
import java.util.Map.Entry;

import org.elasticsearch.common.collect.Maps;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.*;
import com.lamfire.json.JSON;
import com.lamfire.utils.StringUtils;
import com.mob.easySearch.support.IteratorWrapper;
import com.mob.easySearch.support.IteratorWrapper.IteratorHandler;
import com.mob.easySearch.support.JsonResult;

/**
 * @author zxc Jun 8, 2016 5:52:05 PM
 */
@RestController
@RequestMapping("/v1/api")
@Api(value = "search", description = "搜索服务")
public class SearchController extends BaseController {

    @SuppressWarnings("unchecked")
    @ResponseBody
    @ApiOperation(value = "do search", httpMethod = "GET", response = JsonResult.class, notes = "搜索接口")
    @RequestMapping(value = "/{indexName}/{indexType}/search", produces = { "application/json" }, method = RequestMethod.GET)
    JSON search(@ApiParam(required = true, name = "indexName", value = "索引名称命名空间") @PathVariable("indexName") String indexName,
                @ApiParam(required = true, name = "indexType", value = "文档名称") @PathVariable("indexType") String indexType,
                @ApiParam(required = false, name = "pageno", value = "分页页码") @RequestParam(value = "pageno", defaultValue = "1") Integer pageno,
                @ApiParam(required = false, name = "pagesize", value = "每页数量") @RequestParam(value = "pagesize", defaultValue = "30") Integer pagesize,
                @ApiParam(required = true, name = "keywords", value = "关键词") @RequestParam("keywords") String keywords) {
        access.info("[SearchController parameterMap start]:" + JSON.toJSONString(request.getParameterMap()));
        if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(indexType)) return fail("参数错误");
        // if (!es.existsIndex(indexName)) return fail("索引未定义");

        Set<String> field = Sets.newHashSet();
        Set<String> aggregation = Sets.newLinkedHashSet();
        Map<String, Object[]> filter = Maps.newLinkedHashMap();
        Table<String, String, Object> ranges = HashBasedTable.create();
        boolean topOnly = true;
        try {
            if (request.getParameterValues("field") != null) field = Sets.newHashSet(request.getParameterValues("field"));
            if (request.getParameterValues("distinct") != null) {
                for (String value : request.getParameterValues("distinct"))
                    aggregation.add(value);
            }
            for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                if (entry == null || StringUtils.isEmpty(entry.getKey()) || entry.getValue() == null) continue;
                if (!StringUtils.equalsIgnoreCase(entry.getKey(), "pageno")
                    && !StringUtils.equalsIgnoreCase(entry.getKey(), "pagesize")
                    && !StringUtils.equalsIgnoreCase(entry.getKey(), "keywords")
                    && !StringUtils.equalsIgnoreCase(entry.getKey(), "distinct")
                    && !StringUtils.equalsIgnoreCase(entry.getKey(), "field")
                    && !StringUtils.equalsIgnoreCase(entry.getKey(), "topOnly")
                    && !customMatches("(.*)_(lt|gt|lte|gte)$", entry.getKey())) {
                    filter.put(entry.getKey(), entry.getValue());
                }
                if (customMatches("(.*)_(lt|gt|lte|gte)$", entry.getKey())) {
                    String r = StringUtils.substringBeforeLast(entry.getKey(), "_");
                    String c = StringUtils.substringAfterLast(entry.getKey(), "_");
                    ranges.put(r, c, entry.getValue()[0]);
                }
                if (StringUtils.equalsIgnoreCase(entry.getKey(), "topOnly")) topOnly = Boolean.parseBoolean(entry.getValue()[0]);
            }
        } catch (Exception e) {
            _.error("es.queryString param error!", e);
            return fail("参数不支持");
        }
        Map<String, Object> result = Maps.newHashMap();
        try {
            if (aggregation.size() == 0) {
                result = es.query(indexName, indexType, pageno, pagesize, keywords, filter, field, ranges);
            } else {
                Map<String, Object> _result = es.aggr(indexName, indexType, keywords, filter, //
                                                      field, aggregation, ranges, topOnly);
                result.put("total", _result.get("total"));
                result.put("pageno", pageno);
                result.put("pagesize", pagesize);
                Set<Map<String, Object>> _list = (Set<Map<String, Object>>) _result.get("list");
                if (_list != null && _list.size() > 0) {
                    IteratorWrapper.pagination(_list, pagesize)//
                    .iterator(new IteratorHandler<Map<String, Object>>() {

                        @Override
                        public boolean handle(int pageNum, List<Map<String, Object>> subData, Object... params) {
                            int pageno = ((Integer) params[0]).intValue();
                            Map<String, Object> result = (Map<String, Object>) params[1];
                            if (pageNum + 1 == pageno) {
                                if (subData != null && subData.size() > 0) result.put("list", subData);
                                return false;
                            }
                            return true;
                        }
                    }, pageno, result);
                }else {
                    result.put("list", Lists.newArrayList());
                }
            }
        } catch (Exception e) {
            _.error("es.queryString search error!", e);
        }
        access.info("[SearchController parameterMap end]:" + JSON.toJSONString(request.getParameterMap()));
        return ok(result);
    }
}
