/*
 * Copyright 2015-2020 msun.com All right reserved.
 */
package com.mob.easySearch;

import java.util.List;

import com.lamfire.utils.Lists;
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
        // String[] keys = org.apache.commons.lang3.StringUtils.split(word, "$_$");
        // f40da3306847508cfa72551b5068a858f6e40346ef2657636ac0dd3050980a9e^_^1
        String word = "ali.alhadidi.gif!@~^%&(*$()$#)_&_facebook^_^-1---^_^asdas^_^(asdas)^_^asdas";

        List<String> list = Lists.newLinkedList();
        split(word, list);
        System.out.println(list);
    }

    private static void split(String word, List<String> list) {
        if (StringUtils.isEmpty(word)) return;
        int index = org.apache.commons.lang3.StringUtils.indexOf(word, AGGR_SPLIT);
        String split_word = org.apache.commons.lang3.StringUtils.substring(word, 0, index);
        list.add(split_word);

        String _word = org.apache.commons.lang3.StringUtils.substring(word, index + 3, word.length());
        int _index = org.apache.commons.lang3.StringUtils.indexOf(_word, AGGR_SPLIT);
        if (_index > 0) {
            split(_word, list);
        } else {
            list.add(_word);
        }
        return;
    }
}
