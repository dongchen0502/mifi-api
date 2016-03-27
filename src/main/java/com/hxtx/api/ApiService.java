package com.hxtx.api;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

/**
 * 实现api服务的具体业务逻辑
 * Created by dongchen on 16/3/27.
 */
@Service
public class ApiService {
    private String DstSysID ="6090010003";
    private String SrcOrgID = "609002";
    private String SrcSysID =  "6090010002";
    private String DstOrgID = "609001";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private static  SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMM");
    private static  SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static  SimpleDateFormat sdf5 = new SimpleDateFormat("MMddHHmmss");


}
