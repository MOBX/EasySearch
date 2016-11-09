/*
 * Copyright 2015-2020 msun.com All right reserved.
 */
package com.mob.easySearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import com.lamfire.json.JSON;
import com.mob.easySearch.support.JsonResult;

/**
 * @author zxc Aug 18, 2016 11:23:35 AM
 */
@RestController
@RequestMapping("/v1/test")
@Api(value = "test", description = "数据测试")
public class TestController extends BaseController {

    @ApiIgnore
    @ApiOperation(value = "Hello World", httpMethod = "POST", response = String.class, notes = "Hello World测试")
    @RequestMapping(value = "/hello", produces = { "application/json" }, method = RequestMethod.POST)
    String hello() {
        return "Hello World \n" + "mob.com!";
    }

    @ResponseBody
    @ApiOperation(value = "POST TEST", httpMethod = "POST", response = JsonResult.class, notes = "测试提交数据")
    @RequestMapping(produces = { "application/json" }, method = RequestMethod.POST)
    JSON schema(@RequestBody Object... data) {
        return ok(data);
    }
}
