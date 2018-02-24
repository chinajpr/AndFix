package com.jpr.andfix;

import android.content.Context;

import java.io.File;
import java.util.List;

/**
 * 类描述:
 * 创建日期:2018/2/24 on 11:28
 * 作者:JiaoPeiRong
 */

public class PatchManager {
    private Context context;
    private AndFixManager andManager;
    private File file;

    public PatchManager(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        andManager = new AndFixManager(context);

    }

    public void loadPatch(String path){
        file = new File(path);
        Patch patch = new Patch(context , file);
        loadPatch(patch);
    }

    private void loadPatch(Patch patch){
        //反射加载到内存中
        ClassLoader classLoader = context.getClassLoader();
        List<String> list = null;
        for (String name : patch.mClassMap.keySet()){
            list = patch.mClassMap.get(name);
            andManager.fix(file , classLoader,list);
        }

    }
}
