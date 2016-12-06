/*
 * Copyright 2015-2020 msun.com All right reserved.
 */
package com.mob.easySearch;

import com.lamfire.utils.StringUtils;
import com.mob.easySearch.cons.Definition;

/**
 * @author zxc Jun 17, 2016 7:23:39 PM
 */
public class Test implements Definition {

    public static void main(String[] args) {
        // System.out.println("java.library.path: ");
        // System.out.println(System.getProperty("java.library.path"));
        // System.out.println(ElasticsearchHelper.customMatches("(.*)_(lt|gt|lte|gte)$", "app_category_id_gte"));
        // System.out.println(StringUtils.substringBeforeLast("app_category_id_gte", "_"));
        // System.out.println(StringUtils.substringAfterLast("app_category_id_gte", "_"));
        // String[] keys = org.apache.commons.lang3.StringUtils.split(word, AGGR_SPLIT);

        String word = "ali.alhadidi.gif!@~^%&(*$()$#)_&_facebook^_^-1---^_^_---2";
        System.out.println(split(word));
    }

    private static String[] split(String word) {
        if (StringUtils.isEmpty(word)) return new String[] {};
        int index = org.apache.commons.lang3.StringUtils.indexOf(word, AGGR_SPLIT);
        String word1 = org.apache.commons.lang3.StringUtils.substring(word, 0, index);
        String _word = org.apache.commons.lang3.StringUtils.substring(word, index + 3, word.length());

        int _index = org.apache.commons.lang3.StringUtils.indexOf(_word, AGGR_SPLIT);
        String word2 = org.apache.commons.lang3.StringUtils.substring(_word, 0, _index);
        String word3 = org.apache.commons.lang3.StringUtils.substring(_word, _index + 3, _word.length());
        return new String[] { word1, word2, word3 };
    }
}
