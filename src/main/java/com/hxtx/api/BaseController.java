package com.hxtx.api;

import com.hxtx.entity.HttpResult;
import com.hxtx.exception.ApiException;
import com.hxtx.utils.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 统一处理Exception
 * Created by dongchen on 15/7/7.
 */
@Controller
public class BaseController {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handleBusinessException(HttpServletRequest req, HttpServletResponse resp, ApiException e) throws IOException {
        Object obj = HttpResult.errorResult(520, e.getMessage());
        String result = JsonUtils.toJson(obj);
        resp.setCharacterEncoding("utf8");
        resp.getOutputStream().write(result.getBytes());
        resp.getOutputStream().flush();

//        logger.error("controller businessException handler", e);
        e.printStackTrace();

        return null;
    }

    @ExceptionHandler(Exception.class)
    public void handleError(HttpServletRequest req, HttpServletResponse resp, Exception e) throws IOException {
        Object obj = HttpResult.errorResult(500, e.getMessage());
        String result = JsonUtils.toJson(obj);
        resp.setCharacterEncoding("utf8");
        resp.getOutputStream().write(result.getBytes());
        resp.getOutputStream().flush();
//        logger.error("controller exception handler", e);
        e.printStackTrace();
    }
}
