/*
 * Copyright 2015-2020 msun.com All right reserved.
 */
package com.mob.easySearch;

import com.lamfire.utils.StringUtils;
import com.mob.easySearch.support.ElasticsearchHelper;

/**
 * @author zxc Jun 17, 2016 7:23:39 PM
 */
public class Test {

    public static void main(String[] args) {
        System.out.println("java.library.path: ");
        System.out.println(System.getProperty("java.library.path"));

        System.out.println(ElasticsearchHelper.customMatches("(.*)_(lt|gt|lte|gte)$", "app_category_id_gte"));
        System.out.println(StringUtils.substringBeforeLast("app_category_id_gte", "_"));
        System.out.println(StringUtils.substringAfterLast("app_category_id_gte", "_"));
    }
}
