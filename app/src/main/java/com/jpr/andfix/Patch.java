package com.jpr.andfix;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * 类描述:Patch解析,patch是一个.jar文件.将patch文件加载进内存,并解析得到patch的一些信息,比如修改了哪几个类.
 * 创建日期:2018/2/24 on 10:46
 * 作者:JiaoPeiRong
 */

public class Patch {
    private Context context;
    /**
     * patch的路径
     */
    private File file;
    /**
     * patch中,修改的class
     */
    public Map<String, List<String>> mClassMap;

    public Patch(Context context, File file) {
        this.context = context;
        this.file = file;
        init();
    }

    public void init() {
        //加载jar包的类
        JarFile jarFile = null;
        InputStream inputStream = null;
        mClassMap = new HashMap<>();
        List<String> list = new ArrayList<>();

        try {
            jarFile = new JarFile(file);
            JarEntry jarEntry = jarFile.getJarEntry("META-INF/PATCH.MF");
            inputStream = jarFile.getInputStream(jarEntry);
            //跟DOM解析比较相似
            Manifest manifest = new Manifest(inputStream);
            //通过manifest 拿到MF文件里面的key-value
            Attributes attributes = manifest.getMainAttributes();

            Attributes.Name attrName;
            for (Iterator<?> ite = attributes.keySet().iterator(); ite.hasNext(); ) {
                attrName = (Attributes.Name) ite.next();
                if (attrName != null) {
                    String name = attrName.toString();
                    if (name.endsWith("Classes")) {
                        list = Arrays.asList(attributes.getValue(name).split(","));
                        if (name.equalsIgnoreCase("Patch-Classes")) {
                            mClassMap.put(name, list);
                        } else {
                            mClassMap.put(name.trim().substring(0, name.length() - 8), list);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                jarFile.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public List<String> getClasses(String name){
        return mClassMap.get(name);
    }
}
