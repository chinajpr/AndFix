package com.jpr.andfix;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alipay.euler.andfix.annotation.MethodReplace;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

/**
 * 类描述:
 * 创建日期:2018/2/24 on 11:28
 * 作者:JiaoPeiRong
 */

public class AndFixManager {
    private Context context;
    private File optFile;

    public AndFixManager(Context context) {
        this.context = context;
        Andfix.init(Build.VERSION.SDK_INT);

    }

    public void fix(File file, final ClassLoader classLoader, List<String> list) {
        optFile = new File(context.getFilesDir(), file.getName());
        if (optFile.exists()) {
            optFile.delete();
        }

        try {
            final DexFile dexFile = DexFile.loadDex(file.getAbsolutePath(), optFile.getAbsolutePath(), Context.MODE_PRIVATE);
            ClassLoader classLoader1 = new ClassLoader() {
                @Override
                protected Class<?> findClass(String className) throws ClassNotFoundException {
                    Class clazz = dexFile.loadClass(className, this);
                    if (clazz == null) {
                        clazz = Class.forName(className);
                    }
                    return clazz;
                }
            };


            Enumeration<String> entry = dexFile.entries();
            while (entry.hasMoreElements()) {
                String key = entry.nextElement();
                if (!list.contains(key)) {
                    continue;
                }
                Class realClazz = dexFile.loadClass(key, classLoader1);
                if (realClazz != null) {

                    fixClass(realClazz, classLoader);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void fixClass(Class realClazz, ClassLoader classLoader) {
        Method[] methods = realClazz.getMethods();
        for (Method needMethod : methods) {
            MethodReplace methodReplace = needMethod.getAnnotation(MethodReplace.class);
            if (methodReplace == null) {
                continue;
            }
            String clazz = methodReplace.clazz();
            String methodName = methodReplace.method();
            replaceMehod(classLoader, clazz, methodName, realClazz, needMethod);

        }

    }

    private void replaceMehod(ClassLoader classLoader, String clazz, String methodName, Class realClazz, Method method) {

        try {
            //拿到会崩溃的class
            Class srcClazz = Class.forName(clazz);
            if (srcClazz != null) {
                Method src = srcClazz.getDeclaredMethod(methodName, method.getParameterTypes());
                //src:会崩溃的方法;method:已经修复好的方法
                Andfix.replaceMethod(src, method);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
