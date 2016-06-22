/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.easySearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.lamfire.code.UUIDGen;
import com.lamfire.json.JSON;
import com.lamfire.utils.StringUtils;
import com.mob.easySearch.support.JsonResult;

/**
 * @author zxc Jun 8, 2016 5:49:38 PM
 */
@RestController
@RequestMapping("/v1/api")
@Api(value = "index", description = "文档服务")
public class IndexConroller extends BaseController {

    @ResponseBody
    @ApiOperation(value = "POST index", httpMethod = "POST", response = JsonResult.class, notes = "创建文档内容索引")
    @RequestMapping(value = "/{indexName}/{indexType}/index", method = RequestMethod.POST)
    JSON index(@ApiParam(required = true, name = "indexName", value = "索引名称命名空间") @PathVariable("indexName") String indexName,
               @ApiParam(required = true, name = "indexType", value = "文档名称") @PathVariable("indexType") String indexType,
               List<Map<String, Object>> data) {
        if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(indexType)) return fail();
        if (data == null || data.size() == 0) return success();

        for (Map<String, Object> source : data) {
            String id = UUIDGen.uuid();
            if (source.containsKey("id")) {
                id = source.get(id) + "";
            }
            es.index(indexName, indexType, id, source);
        }
        return success();
    }

    @ResponseBody
    @ApiOperation(value = "POST bulk", httpMethod = "POST", response = JsonResult.class, notes = "bulk批量索引文档")
    @RequestMapping(value = "/bulk ", method = RequestMethod.POST)
    JSON bulk() {
        return success();
    }

    @ResponseBody
    @ApiOperation(value = "SET alias", httpMethod = "POST", response = JsonResult.class, notes = "alias 索引文档别名,同义词")
    @RequestMapping(value = "/{indexName}/{indexType}/alias ", method = RequestMethod.POST)
    JSON alias(@ApiParam(required = true, name = "indexName", value = "索引名称命名空间") @PathVariable("indexName") String indexName,
               @ApiParam(required = true, name = "indexType", value = "文档名称") @PathVariable("indexType") String indexType) {
        if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(indexType)) return fail();

        return success();
    }
}
