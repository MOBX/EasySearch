/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.easySearch.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mob.easySearch.support.JsonResult;

/**
 * @author zxc Jun 7, 2016 2:47:04 PM
 */
@Controller
public class HomeController {

    @RequestMapping(value = { "/", "/home" })
    public String index(HttpServletRequest request) {
        return "home";
    }

    @RequestMapping("/api")
    public String home() {
        return "redirect:index.html";
    }

    @RequestMapping(value = { "json" }, produces = "application/json")
    @ResponseBody
    public JsonResult json(HttpServletRequest request) {
        return JsonResult.successMsg("hello,now time " + System.currentTimeMillis());
    }
}
