package com.hxtx.listener;

import com.hxtx.entity.CacheResult;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理号码缓存, 初始化和持久化
 * Created by dongchen on 16/5/3.
 */
public class CacheCenter {
    private static String cacheFilePath = "./mobiles.cache";
    /**
     * 电话号码 = 上次查询时间
     */
    public static Map<String, Long> timeMap = new HashMap<String, Long>();
    /**
     * 电话号码 = 上传查询结果
     */
    public static Map<String, CacheResult> resultMap = new HashMap<String, CacheResult>();

    public static void init(){
        System.out.println("cache init--------------------");
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(cacheFilePath)));
            resultMap = (Map<String, CacheResult>)ois.readObject();
            timeMap = (Map<String, Long>)ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        System.out.println("cache save--------------------");
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(cacheFilePath)));
            oos.writeObject(resultMap);
            oos.writeObject(timeMap);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
