package com.hxtx.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by dongchen on 16/5/3.
 */
@Service
public class CacheManager implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {

        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContextEvent.getServletContext());
        CacheManageTask cacheManagerTask = context.getBean("cacheManageTask", CacheManageTask.class);

        Thread workerThread = new Thread(cacheManagerTask);
        //守护线程可以在tomcat关闭时自动结束
        workerThread.setDaemon(true);
        workerThread.start();

    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("缓存管理线程停止");
    }
}
