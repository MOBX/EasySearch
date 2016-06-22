/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.easySearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.lamfire.json.JSON;
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
    @RequestMapping(value = "/{indexName}/{indexType}/search", method = RequestMethod.GET)
    JSON search(@ApiParam(required = true, name = "indexName", value = "索引名称命名空间") @PathVariable("indexName") String indexName,
                @ApiParam(required = true, name = "indexType", value = "文档名称") @PathVariable("indexType") String indexType,
                @ApiParam(required = true, name = "keywords", value = "关键词") @RequestParam("keywords") String keywords,
                @ApiParam(required = false, name = "pageno", value = "分页页码") @RequestParam(value = "pageno", defaultValue = "1") Integer pageno,
                @ApiParam(required = false, name = "pagesize", value = "每页数量") @RequestParam(value = "pagesize", defaultValue = "30") Integer pagesize) {
        if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(indexType)) return fail();

        Map<String, Object> result = es.queryString(indexName, indexType, pageno, pagesize, keywords);
        return success(result);
    }
}
