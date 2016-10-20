/*
 * Copyright 2015-2020 msun.com All right reserved.
 */
package com.mob.easySearch.support;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author zxc Apr 28, 2016 1:40:13 PM
 */
@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { RuntimeException.class, Throwable.class })
    @ResponseBody
    ResponseEntity<Object> handleControllerException(HttpServletRequest req, Throwable ex) {
        JsonResult result = JsonResult.fail(exceptionDetail(ex));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/plain; charset=UTF-8"));
        return new ResponseEntity<Object>(result.toString(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                   HttpStatus status, WebRequest request) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("path", request.getContextPath());
        responseBody.put("message", "The URL you have reached is not in service at this time (404).");
        return new ResponseEntity<Object>(responseBody, HttpStatus.NOT_FOUND);
    }

    public String exceptionDetail(Throwable ex) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        ex.printStackTrace(pout);
        String ret = out.toString();
        pout.close();
        try {
            out.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ret;
    }
}
