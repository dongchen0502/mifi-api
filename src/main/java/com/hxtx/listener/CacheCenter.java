package com.hxtx.listener;

import com.hxtx.entity.CacheResult;
import com.rits.cloning.Cloner;
import org.apache.oro.util.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理号码缓存, 初始化和持久化
 * Created by dongchen on 16/5/3.
 */
public class CacheCenter {
    private static Logger logger = LoggerFactory.getLogger(CacheCenter.class);

    private static String cacheFilePath = "./mobiles.cache";
    private static String tmpCacheFilePath = "./mobiles.cache.tmp";
    private static String bakCacheFilePath = "./mobiles.cache.bak";
    private static Cloner cloner = new Cloner();
    /**
     * 电话号码 = 上次查询时间
     */
    public static Map<String, Long> timeMap = new HashMap<String, Long>();
    /**
     * 电话号码 = 上传查询结果
     */
    public static Map<String, CacheResult> resultMap = new HashMap<String, CacheResult>();

    public static void init(){
        logger.info("-----------> cacheMap init start <-----------");
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(cacheFilePath)));
            resultMap = (Map<String, CacheResult>)ois.readObject();
            timeMap = (Map<String, Long>)ois.readObject();
            ois.close();
            logger.info("-----------> cacheMap init complated, resultMap.size() = " + resultMap.size()
                    + "| timeMap.size() = " + timeMap.size() + "<-----------");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void save() {
        logger.info("-----------> cacheMap save start <-----------");
        try {
            File newFile = new File(tmpCacheFilePath);
            File bakFile = new File(bakCacheFilePath);
            File cacheFile = new File(cacheFilePath);

            Map<String, CacheResult> rMap = cloner.deepClone(resultMap);
            Map<String, Long> tMap = cloner.deepClone(timeMap);

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(newFile));
            oos.writeObject(rMap);
            oos.writeObject(tMap);
            oos.flush();
            oos.close();

            cacheFile.renameTo(bakFile);
            newFile.renameTo(cacheFile);
            logger.info("-----------> cacheMap save complated, resultMap.size() = " + resultMap.size()
                    + "| timeMap.size() = " + timeMap.size() + "<-----------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
