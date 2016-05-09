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
    private final int updateInterval = 1000 * 60 * 60 * 12;

    private final String[] taskArr = {"payment", "flowset", "balance"};
    /**
     * 1.执行初始化缓存的操作
     * 2.循环执行查询操作,更新缓存
     * 2.1查询新信息
     * 2.2保存到硬盘
     */
    public void run() {
        CacheCenter.init();
        Cloner cloner = new Cloner();

        while (keepOnRunning){
            int counter = 0;
            Set<String> mobiles = CacheCenter.resultMap.keySet();
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

                        taskResult = exchangeHandler(taskName, key, curMonth);

                        code = ExchangeUtils.parseCode(taskResult);
                        logger.info("update cache : mobile = " + key + " | type = " + taskName + " | code : " + code);
                        complated &= ExchangeUtils.isSuccCode(code);
                        if(!complated){
                            continue outerFor;
                        }

                        resultHandler(taskName, cache, taskResult, curMonth);
                    }

                    //一轮完成后更新时间
                    if(complated){
                        CacheCenter.timeMap.put(key, System.currentTimeMillis());
                        counter++;
                    }
                    if(counter >= 10){
                        break outerFor;
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

    private String exchangeHandler(String taskName, String mobile, String month){
        String taskResult = "";
        switch(taskName){
            case "payment" : {
                taskResult = exchange.chargeInfo(mobile, month);break;
            }
            case "flowset" : {
                taskResult = exchange.flowSet(mobile, month);break;
            }
            case "balance" : {
                taskResult = exchange.balance(mobile, 0); break;
            }
        }
        return taskResult;
    }

    private void resultHandler(String taskName, CacheResult cache, String xml, String month){
        switch(taskName){
            case "payment" : {
                cache.setPayment(month, xml);
                break;
            }
            case "flowset" : {
                cache.setFlowset(month, xml);
                break;
            }
            case "balance" : {
                cache.setBalance(xml);
                break;
            }
        }
    }

    public void stop(){
        this.keepOnRunning = false;
        Thread.currentThread().interrupt();
    }
}
