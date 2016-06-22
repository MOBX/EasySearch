/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.easySearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.lamfire.json.JSON;
import com.lamfire.utils.StringUtils;
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
    @RequestMapping(value = "/{indexName}/{indexType}/schema", method = RequestMethod.POST)
    JSON schema(@ApiParam(required = true, name = "indexName", value = "索引名称命名空间") @PathVariable("indexName") String indexName,
                @ApiParam(required = true, name = "indexType", value = "文档名称") @PathVariable("indexType") String indexType,
                @ApiParam(required = true, name = "fields", value = "索引shcema") Map<String, Map<String, Object>> fields) {
        if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(indexType)) return fail();

        try {
            es.dropIndex(indexName);
            if (!es.existsIndex(indexName)) es.createIndex(indexName);
            es.createMapping(indexName, indexType, fields);
            return success();
        } catch (Exception e) {
            _.error("create schema Exception!", e);
        }
        return fail();
    }

    @ResponseBody
    @ApiOperation(value = "GET Schema", httpMethod = "GET", response = JsonResult.class, notes = "获取Schema定义")
    @RequestMapping(value = "/{indexName}/{indexType}/schema", method = RequestMethod.GET)
    JSON getSchema(@ApiParam(required = true, name = "indexName", value = "索引名称命名空间") @PathVariable("indexName") String indexName,
                   @ApiParam(required = true, name = "indexType", value = "文档名称") @PathVariable("indexType") String indexType) {
        if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(indexType)) return fail();

        try {
            GetMappingsResponse mappingsRes = es.getMapping(indexName, indexType);
            Map<String, Object> sourceMap = mappingsRes.mappings().get(indexName).get(indexType).getSourceAsMap();
            return success(sourceMap.get("properties"));
        } catch (IOException e) {
            _.error("getSchema IOException!", e);
        }
        return fail();
    }
}
