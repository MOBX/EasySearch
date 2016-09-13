/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.easySearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lamfire.code.UUIDGen;
import com.lamfire.json.JSON;
import com.lamfire.utils.StringUtils;
import com.mob.easySearch.support.JsonResult;
import com.mob.easySearch.support.ThreadUtil;

/**
 * @author zxc Jun 8, 2016 5:49:38 PM
 */
@RestController
@RequestMapping("/v1/api")
@Api(value = "index", description = "文档服务")
public class IndexConroller extends BaseController {

    @ResponseBody
    @ApiOperation(value = "POST index", httpMethod = "POST", response = JsonResult.class, notes = "创建文档内容索引")
    @RequestMapping(value = "/{indexName}/{indexType}/index", produces = { "application/json" }, method = RequestMethod.POST)
    JSON index(@ApiParam(required = true, name = "indexName", value = "索引名称命名空间")
    @PathVariable("indexName")
    final String indexName, @ApiParam(required = true, name = "indexType", value = "文档名称")
    @PathVariable("indexType")
    final String indexType, @RequestBody Object... data) {
        if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(indexType)) return fail();
        if (data == null || data.length == 0) return success();

        for (Object source : data) {
            final Map<String, Object> _source = JSON.fromJavaObject(source);
            String id = UUIDGen.uuid();
            if (_source.containsKey("id")) {
                id = _source.get("id") + "";
            }
            final String _id = id;
            ThreadUtil.submitTask(new Runnable() {

                @Override
                public void run() {
                    es.index(indexName, indexType, _id, _source);
                }
            });
        }
        return success();
    }

    @ResponseBody
    @ApiOperation(value = "POST bulk", httpMethod = "POST", response = JsonResult.class, notes = "bulk批量索引文档")
    @RequestMapping(value = "/{indexName}/{indexType}/bulk ", produces = { "application/json" }, method = RequestMethod.POST)
    JSON bulk(@ApiParam(required = true, name = "indexName", value = "索引名称命名空间")
    @PathVariable("indexName")
    final String indexName, @ApiParam(required = true, name = "indexType", value = "文档名称")
    @PathVariable("indexType")
    final String indexType, @RequestParam() MultipartFile jsonfile) throws Exception {
        Integer count = es.bulk(indexName, indexType, convert(jsonfile));
        return success(count);
    }

    public File convert(MultipartFile file) throws Exception {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    @ResponseBody
    @ApiOperation(value = "SET alias", httpMethod = "POST", response = JsonResult.class, notes = "alias 索引文档别名,同义词")
    @RequestMapping(value = "/{indexName}/{indexType}/alias ", produces = { "application/json" }, method = RequestMethod.POST)
    JSON alias(@ApiParam(required = true, name = "indexName", value = "索引名称命名空间") @PathVariable("indexName") String indexName,
               @ApiParam(required = true, name = "indexType", value = "文档名称") @PathVariable("indexType") String indexType) {
        if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(indexType)) return fail();

        return success();
    }
}
