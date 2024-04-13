package com.github.sparkzxl.core.util;

import cn.hutool.core.convert.Convert;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * description: 集合工具类
 *
 * @author zhouxinlei
 */
@Slf4j
public class ListUtils {

    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    /**
     * 判断list是否为空
     *
     * @param list list集合
     * @return boolean
     */
    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 判断list是否不为空
     *
     * @param list list集合
     * @return boolean
     */
    public static <T> boolean isNotEmpty(List<T> list) {
        return !isEmpty(list);
    }


    public static <T> List<T> single(T value) {
        return Lists.newArrayList(value);
    }

    /**
     * set转list
     *
     * @param set set集合
     * @return List<T>
     */
    public static <T> List<T> setToList(Set<T> set) {
        return Lists.newArrayList(set);
    }

    /**
     * list转set
     *
     * @param list list集合
     * @return Set<T>
     */
    public static <T> Set<T> listToSet(List<T> list) {
        return Sets.newHashSet(list);
    }

    /**
     * 数组转list
     *
     * @param ts array数组
     * @return List<T>
     */
    public static <T> List<T> arrayToList(T[] ts) {
        if (ArrayUtils.isEmpty(ts)) {
            return emptyList();
        }
        return Arrays.stream(ts).collect(Collectors.toList());
    }

    /**
     * 数组转set
     *
     * @param ts array数组
     * @return List<T>
     */
    public static <T> Set<T> arrayToSet(T[] ts) {
        if (ArrayUtils.isEmpty(ts)) {
            return SetUtils.emptySet();
        }
        return Arrays.stream(ts).collect(Collectors.toSet());

    }

    /**
     * list转String
     *
     * @param list list
     * @return List<T>
     */
    public static String listToString(List<String> list) {
        String str = "";
        if (isNotEmpty(list)) {
            str = StringUtils.join(list, ",");
        }
        return str;
    }

    /**
     * list转String
     *
     * @param list list
     * @return List<T>
     */
    public static String arrayToString(String[] list) {
        String str = "";
        if (ArrayUtils.isNotEmpty(list)) {
            str = StringUtils.join(list, ",");
        }
        return str;
    }

    /**
     * String转list
     *
     * @param data 字符串
     * @return List<T>
     */
    public static List<String> stringToList(String data) {
        if (StringUtils.isNotEmpty(data)) {
            String[] str = StringUtils.split(data, ",");
            return Arrays.asList(str);
        } else {
            return emptyList();
        }
    }

    public static List<Long> stringToLongList(String data) {
        if (StringUtils.isNotEmpty(data)) {
            String[] str = StringUtils.split(data, ",");
            Long[] strArrNum = Convert.toLongArray(str);
            return Arrays.asList(strArrNum);
        } else {
            return emptyList();
        }
    }

    public static List<Integer> stringToIntegerList(String data) {
        if (StringUtils.isNotEmpty(data)) {
            String[] str = StringUtils.split(data, ",");
            Integer[] strArrNum = Convert.toIntArray(str);
            return Arrays.asList(strArrNum);
        } else {
            return emptyList();
        }
    }

    public static String[] stringToArray(String data) {
        if (StringUtils.isNotEmpty(data)) {
            return StringUtils.split(data, ",");
        } else {
            return new String[0];
        }
    }


    public static String[] listToArray(List<String> data) {
        if (isNotEmpty(data)) {
            return data.stream().map(String::valueOf).toArray(String[]::new);
        } else {
            return new String[0];
        }
    }

    /**
     * 交集（扣除）
     *
     * @param a 参数a
     * @param b 参数b
     * @return List<T>
     */
    public static <T> List<T> intersection(List<T> a, List<T> b) {
        return a.stream().filter(x -> !b.contains(x)).collect(Collectors.toList());
    }

    /**
     * 取并集
     *
     * @param a 参数a
     * @param b 参数b
     * @return List<T>
     */
    public static <T> List<T> unionList(List<T> a, List<T> b) {
        return Lists.newArrayList(CollectionUtils.union(a, b));
    }


    /**
     * list集合正向排序
     *
     * @param list         集合
     * @param keyExtractor 排序字典
     * @param <T>          对象属性
     * @param <U>          字段
     * @return List
     */
    public static <T, U extends Comparable<? super U>> List<T> sort(List<T> list, Function<? super T, ? extends U> keyExtractor) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        return list.stream().sorted(Comparator.comparing(keyExtractor)).collect(Collectors.toList());
    }

    /**
     * list集合逆向排序
     *
     * @param list         集合
     * @param keyExtractor 排序字典
     * @param <T>          对象属性
     * @param <U>          字段
     * @return List
     */
    public static <T, U extends Comparable<? super U>> List<T> reverse(List<T> list, Function<? super T, ? extends U> keyExtractor) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        return list.stream().sorted(Comparator.comparing(keyExtractor).reversed()).collect(Collectors.toList());
    }

    /**
     * 差集（扣除）
     *
     * @param a 参数a
     * @param b 参数b
     * @return List<T>
     */
    public static <T> List<T> differenceList(List<T> a, List<T> b) {
        return a.stream().filter(x -> !b.contains(x)).collect(Collectors.toList());
    }
}
