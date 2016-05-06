package com.hxtx.listener;

import com.hxtx.entity.CacheResult;
import com.hxtx.service.ExchangeService;
import com.hxtx.utils.ExchangeUtils;
import com.rits.cloning.Cloner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * 缓存控制线程,负责自动更新号码信息
 * Created by dongchen on 16/5/3.
 */
@Service
public class CacheManageTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(CacheManageTask.class);
    @Autowired
    private ExchangeService exchange;

    private SimpleDateFormat sdf = new SimpleDateFormat("YYYYMM");
    private boolean keepOnRunning = true;
    private final int updateInterval = 1000 * 60 * 60;

    private final String[] taskArr = {"payment", "flowset", "balance"};
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
            Cloner cloner = new Cloner();
            mobiles = cloner.deepClone(mobiles);

            outerFor:
            for(String key : mobiles){
                Long lastTime = CacheCenter.timeMap.get(key);
                Long curTime = System.currentTimeMillis();
                if(lastTime != null && (curTime - lastTime) < updateInterval){
                    continue outerFor;
                }

                boolean complated = true;
                CacheResult cache = CacheCenter.resultMap.get(key);
                String curMonth = sdf.format(curTime);
                String code;
                String taskResult = "";
                try{
                    //完成一次更新
                    for(String taskName : taskArr){
                        switch(taskName){
                            case "payment" : {
                                taskResult = exchange.chargeInfo(key, curMonth);break;
                            }
                            case "flowset" : {
                                taskResult = exchange.flowSet(key, curMonth);break;
                            }
                            case "balance" : {
                                taskResult = exchange.balance(key, 0); break;
                            }
                        }
                        code = ExchangeUtils.parseCode(taskResult);
                        logger.info("update cache : mobile = " + key + " | type = " + taskName + " | code : " + code);
                        complated &= ExchangeUtils.isSuccCode(code);
                        if(!complated){
                            continue outerFor;
                        }

                        switch(taskName){
                            case "payment" : {
                                cache.setPayment(curMonth, taskResult);
                                break;
                            }
                            case "flowset" : {
                                cache.setFlowset(curMonth, taskResult);
                                break;
                            }
                            case "balance" : {
                                cache.setBalance(taskResult);
                                break;
                            }
                        }
                    }

                    //一轮完成后更新时间
                    if(complated){
                        CacheCenter.timeMap.put(key, System.currentTimeMillis());
                        counter++;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(counter > 0){
                logger.info("------------->完成一轮更新, 更新数量:" + counter);
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
