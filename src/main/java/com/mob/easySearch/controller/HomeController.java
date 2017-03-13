/*
 * Copyright 2015-2020 msun.com All right reserved.
 */
package com.mob.easySearch.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lamfire.json.JSON;
import com.lamfire.utils.DateFormatUtils;
import com.mob.easySearch.support.JsonResult;

/**
 * @author zxc Jun 7, 2016 2:47:04 PM
 */
@Controller
public class HomeController {

    @RequestMapping("/api")
    public String home() {
        return "redirect:index.html";
    }

    @RequestMapping(value = { "json" }, produces = "application/json")
    @ResponseBody
    public JsonResult json(HttpServletRequest request) {
        return JsonResult.successMsg("hello,now time " + System.currentTimeMillis());
    }

    // 健康检查
    @Bean
    public ServletRegistrationBean healthAction() {
        return new ServletRegistrationBean(new HttpServlet() {

            private static final long serialVersionUID = -33776623249934740L;

            public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                JSON json = new JSON();
                json.put("status", 200);
                json.put("time", DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss:SSS"));
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.println(json.toJSONString());
                out.flush();
                out.close();
            }

            public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                doGet(request, response);
            }
        }, "/health");
    }
}
