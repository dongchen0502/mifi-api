package com.hxtx.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 为网关服务的启动提供控制
 * Created by dongchen on 16/3/27.
 */
@Controller
public class StartController {

    @RequestMapping(value = "/demo", method = {RequestMethod.GET})
    public String getHelloWorld(){
        return "demo";
    }
}
