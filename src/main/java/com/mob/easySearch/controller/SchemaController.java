/*
 * Copyright 2015-2020 msun.com All right reserved.
 */
package com.mob.easySearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.lamfire.json.JSON;
import com.mob.easySearch.support.JsonResult;

/**
 * @author zxc Jun 8, 2016 5:46:43 PM
 */
@RestController
@EnableSwagger2
@RequestMapping("/v1/api")
@Api(value = "schema", description = "索引定义")
public class SchemaController extends BaseController {

    @ResponseBody
    @ApiOperation(value = "POST Schema", httpMethod = "POST", response = JsonResult.class, notes = "提交Schema定义")
    @RequestMapping(value = "/{indexName}/{indexType}/schema", produces = { "application/json" }, method = RequestMethod.POST)
    JSON schema(@ApiParam(required = true, name = "indexName", value = "索引名称命名空间") @PathVariable("indexName") String indexName,
                @ApiParam(required = true, name = "indexType", value = "文档名称") @PathVariable("indexType") String indexType,
                @ApiParam(required = true, name = "fields", value = "索引shcema") @RequestBody Map<String, Map<String, Object>> fields) {
        if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(indexType)) return fail("参数错误");
        if (fields == null || fields.size() == 0) return fail("索引shcema为空");

        try {
            if (es.existsIndex(indexName)) es.deleteMapping(indexName, indexType);
            if (!es.existsIndex(indexName)) es.createIndex(indexName);
            es.createMapping(indexName, indexType, fields);
            return ok();
        } catch (Exception e) {
            _.error("create schema Exception!", e);
        }
        return fail("参数错误");
    }

    @ResponseBody
    @ApiOperation(value = "GET Schema", httpMethod = "GET", response = JsonResult.class, notes = "获取Schema定义")
    @RequestMapping(value = "/{indexName}/{indexType}/schema", produces = { "application/json" }, method = RequestMethod.GET)
    JSON getSchema(@ApiParam(required = true, name = "indexName", value = "索引名称命名空间") @PathVariable("indexName") String indexName,
                   @ApiParam(required = true, name = "indexType", value = "文档名称") @PathVariable("indexType") String indexType) {
        if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(indexType)) return fail("参数错误");
        if (!es.existsIndex(indexName)) return fail("索引未定义");

        try {
            GetMappingsResponse mappingsRes = es.getMapping(indexName, indexType);
            Map<String, Object> sourceMap = mappingsRes.mappings().get(indexName).get(indexType).getSourceAsMap();
            return ok(sourceMap.get("properties"));
        } catch (Exception e) {
            _.error("getSchema Exception!", e);
        }
        return fail("参数错误");
    }

    @ResponseBody
    @ApiOperation(value = "DEL Schema", httpMethod = "DELETE", response = JsonResult.class, notes = "删除Schema定义")
    @RequestMapping(value = "/{indexName}/{indexType}/schema", produces = { "application/json" }, method = RequestMethod.DELETE)
    JSON delSchema(@ApiParam(required = true, name = "indexName", value = "索引名称命名空间") @PathVariable("indexName") String indexName,
                   @ApiParam(required = true, name = "indexType", value = "文档名称") @PathVariable("indexType") String indexType) {
        if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(indexType)) return fail("参数错误");

        try {
            if (es.existsIndex(indexName)) es.deleteMapping(indexName, indexType);
            return ok();
        } catch (Exception e) {
            _.error("create schema Exception!", e);
        }
        return fail("参数错误");
    }

    @ResponseBody
    @ApiOperation(value = "GET all Schema", httpMethod = "GET", response = JsonResult.class, notes = "全部Schema定义")
    @RequestMapping(value = "/schemas", produces = { "application/json" }, method = RequestMethod.GET)
    JSON allSchema() {
        try {
            Map<String, Object> sourceMap = es.allMapping();
            return ok(sourceMap);
        } catch (Exception e) {
            _.error("allSchema Exception!", e);
        }
        return fail("参数错误");
    }
}
