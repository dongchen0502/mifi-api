package com.hxtx.listener;

import com.hxtx.entity.CacheResult;
import com.hxtx.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * 缓存控制线程,负责自动更新号码信息
 * Created by dongchen on 16/5/3.
 */
@Service
public class CacheManageTask implements Runnable {
    @Autowired
    private ExchangeService exchange;

    private SimpleDateFormat sdf = new SimpleDateFormat("YYYYMM");
    private boolean keepOnRunning = true;
    private final int updateInterval = 1000 * 60 * 60;
    /**
     * 1.执行初始化缓存的操作
     * 2.循环执行查询操作,更新缓存
     * 2.1查询新信息
     * 2.2保存到硬盘
     */
    public void run() {
        CacheCenter.init();

        while (keepOnRunning){
            int counter = 0;
            Set<String> mobiles = CacheCenter.resultMap.keySet();

            innerFor:
            for(String key : mobiles){
                Long lastTime = CacheCenter.timeMap.get(key);
                Long curTime = System.currentTimeMillis();
                if(lastTime != null && (curTime - lastTime) < updateInterval){
                    continue innerFor;
                }

                CacheResult cache = CacheCenter.resultMap.get(key);
                String curMonth = sdf.format(curTime);
                try{
                    //完成一次更新
                    String balance = exchange.balance(key, 0);
                    cache.setBalance(balance);

                    String flowset = exchange.flowSet(key, curMonth);
                    cache.setFlowset(curMonth, flowset);

                    String payment = exchange.chargeInfo(key, curMonth);
                    cache.setPayment(curMonth, payment);

                    //一轮完成后更新时间
                    CacheCenter.timeMap.put(key, System.currentTimeMillis());
                    counter++;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(counter > 0){
                System.out.println("------------->完成一轮更新, 更新数量:" + counter);
                CacheCenter.save();
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        this.keepOnRunning = false;
        Thread.currentThread().interrupt();
    }
}
