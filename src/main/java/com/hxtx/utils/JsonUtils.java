package com.hxtx.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by liyongjun on 15/1/14.
 */
public class JsonUtils {

    public static final Gson gson = new Gson();

    public JsonUtils() {
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static String toJson(Object src, Type objType) {
        return gson.toJson(src, objType);
    }

    public static String callBackJson(Object src, String callBack) {
        String json = toJson(src);
        if (StringUtils.isBlank(callBack)) {
            return json;
        } else {
            return callBack + "( " + json + " )";
        }
    }

    public static <T> T fromJsonElement(JsonElement je, Class<T> t){
        return gson.fromJson(je, t);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, Class<T> t) {
        return gson.fromJson(json, t);
    }

    public static <T> T fromJsonWithJackson(String json, Class<T> t) {

        ObjectMapper jsonObjectMapper = new ObjectMapper();
        try {
            return jsonObjectMapper.readValue(json, t);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("json exception", e);
        }

    }

    public static <T> List<T> toList(String json, Class<T> t) {

        ObjectMapper jsonObjectMapper = new ObjectMapper();
        try {
            return jsonObjectMapper.readValue(json,
                    TypeFactory.defaultInstance().constructCollectionType(List.class, t));

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("json exception", e);
        }

    }



    public static String resultToJson(Object src, String callBack) {

        String json = JsonUtils.toJson(src);
        if (StringUtils.isBlank(callBack)) {
            return json;
        } else {
            return callBack + "( " + json + " )";
        }
    }

    public static String resultToJson(String src, String callBack) {


        if (StringUtils.isBlank(callBack)) {
            return src;
        } else {
            return callBack + "( " + src + " )";
        }
    }
}
