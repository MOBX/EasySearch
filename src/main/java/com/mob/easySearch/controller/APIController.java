/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.easySearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

/**
 * @author zxc Jun 8, 2016 4:06:36 PM
 */
@RestController
@RequestMapping("/v1/test")
@Api(value = "test", description = "Hello World测试")
public class APIController extends BaseController {

    @ApiIgnore
    @ApiOperation(value = "Hello World", httpMethod = "POST", response = String.class, notes = "requires noting")
    @RequestMapping(value = "/hello", produces = { "application/json" }, method = RequestMethod.POST)
    String getDemo() {
        return "Hello World \n" + "mob.com!";
    }
}
