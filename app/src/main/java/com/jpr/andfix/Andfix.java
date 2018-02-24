package com.jpr.andfix;

import java.lang.reflect.Method;

/**
 * 类描述:
 * 创建日期:2018/2/24 on 10:46
 * 作者:JiaoPeiRong
 */

public class Andfix {
    static {
        System.loadLibrary("native-lib");
    }

    public static  native  void init(int api);

    public  static  native  void replaceMethod(Method src, Method dest);
}
