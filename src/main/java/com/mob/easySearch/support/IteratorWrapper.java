/*
 * Copyright 2015-2020 msun.com All right reserved.
 */
package com.mob.easySearch.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.lamfire.logger.Logger;
import com.lamfire.logger.LoggerFactory;
import com.lamfire.utils.Lists;

/**
 * @author zxc Nov 9, 2016 4:12:18 PM
 */
public class IteratorWrapper<T> {

    private static final Logger _ = LoggerFactory.getLogger(IteratorWrapper.class);

    private List<T>             data;
    private int                 pageSize;

    public static int getPageCount(int size, int pageSize) {
        int pageCount = size / pageSize;
        if (size % pageSize != 0) pageCount++;
        return pageCount;
    }

    /**
     * pageSize为每页的大小，data为待分页数据List
     * 
     * @param data
     * @param pageSize
     * @return
     */
    public static <T extends Object> IteratorWrapper<T> pagination(Collection<T> data, int pageSize) {
        IteratorWrapper<T> it = new IteratorWrapper<T>();
        if (data == null) return it;
        if (data instanceof List) {
            it.data = (List<T>) data;
        } else {
            it.data = new ArrayList<T>(data);
        }
        it.pageSize = pageSize;
        return it;
    }

    /**
     * 具体的迭代接口，params为传入的参数
     * 
     * @param handler
     * @param params
     */
    public void iterator(IteratorHandler<T> handler, Object... params) {
        if (data == null || data.isEmpty()) return;
        int total = data.size(), pageCount = getPageCount(total, pageSize);
        for (int pageNum = 0; pageNum < pageCount; pageNum++) {
            int start = Math.min(pageNum * pageSize, total), end = Math.min((pageNum + 1) * pageSize, total);
            if (start >= end) break;
            List<T> subList = null;
            try {
                subList = data.subList(start, end);
                if (!handler.handle(pageNum, subList, params)) return;
            } catch (Exception e) {
                if (handler.onException(e, pageNum, data, subList, params) == false) {
                    return;
                } else {
                    _.error("iterator error!", e);
                }
            }
        }
    }

    public static abstract class IteratorHandler<T> {

        private static final Logger _ = LoggerFactory.getLogger(IteratorHandler.class);

        /**
         * return false 则迭代不再继续
         * 
         * @param pageNum
         * @param subData
         * @param params
         * @return
         */
        public abstract boolean handle(int pageNum, List<T> subData, Object... params);

        /**
         * 执行某次迭代发生异常
         * 
         * @param e
         * @param pageNum
         * @param subData
         * @param params
         * @return true则迭代继续，否则迭代退出
         */
        public boolean onException(Throwable e, int pageNum, List<T> data, Collection<T> subData, Object... params) {
            _.error("data.size:" + data.size() + ";" + e.getMessage(), e);
            e.printStackTrace();
            return true;
        }
    }

    public static void main(String[] args) {
        Integer[] data = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 19, 20, 25 };
        IteratorWrapper.pagination(Lists.newArrayList(data), 3).iterator(new IteratorHandler<Integer>() {

            @Override
            public boolean handle(int pageNum, List<Integer> subData, Object... params) {
                System.out.println("pageNum" + pageNum + "subData" + subData);
                System.out.println("第" + pageNum + "页" + subData);
                return true;
            }
        });

        System.out.println();
        IteratorWrapper.pagination(Lists.newArrayList(data), 4).iterator(new IteratorHandler<Integer>() {

            @Override
            public boolean handle(int pageNum, List<Integer> subData, Object... params) {
                if (pageNum + 1 == ((Integer) params[0]).intValue()) {
                    System.out.println("pageNum" + pageNum + "subData" + subData);
                    System.out.println("第" + (pageNum + 1) + "页" + subData);
                    return false;
                }
                return true;
            }
        }, 2);
    }
}
