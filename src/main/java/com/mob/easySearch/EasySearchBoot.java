/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package com.mob.easySearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.google.common.collect.Sets;

/**
 * @author zxc Aug 28, 2015 11:46:11 AM
 */
@Configuration
@EnableSwagger2
@SpringBootApplication
public class EasySearchBoot extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        new SpringApplication(EasySearchBoot.class).run(args);
    }

    // @Bean
    // public EmbeddedServletContainerFactory servletContainer() {
    // return new NettyEmbeddedServletContainerFactory();
    // }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EasySearchBoot.class);
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return new EmbeddedServletContainerCustomizer() {

            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {

                ErrorPage page401 = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.html");
                ErrorPage page404 = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
                ErrorPage page500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
                container.addErrorPages(page401, page404, page500);
            }
        };
    }

    @Bean
    public Docket restApi() {
        return new Docket(DocumentationType.SWAGGER_2)//
        .produces(Sets.newHashSet("application/json; charset=UTF-8"))//
        .consumes(Sets.newHashSet("application/json; charset=UTF-8"))//
        .protocols(Sets.newHashSet("http"))//
        .forCodeGeneration(true)//
        .select().apis(RequestHandlerSelectors.basePackage("com.mob.easySearch.controller"))//
        .paths(PathSelectors.regex("/v1/.*")).build()//

        .apiInfo(new ApiInfoBuilder()//
        .contact("zhangxiongcai337@gmail.com")//
        .title("EasySearch")//
        .description("MOB通用搜索服务.")//
        .termsOfServiceUrl("http://mob.com")//
        .license("Apache License Version 2.0")//
        .licenseUrl("https://github.com/MOBX/EasySearch/blob/master/LICENSE")//
        .version("1.0").build());
    }
}
