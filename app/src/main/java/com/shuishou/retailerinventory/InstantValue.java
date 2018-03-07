package com.shuishou.retailerinventory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2017-12-06.
 */

public class InstantValue {
    public static final DateFormat DFYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final DateFormat DFHMS = new SimpleDateFormat("HH:mm:ss");
	public static final DateFormat DFYMD = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DFWEEK = new SimpleDateFormat("EEE");
	public static final DateFormat DFYMDHMS_2 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static final String DATE_PATTERN_YMD = "yyyy-MM-dd";
	public static final String DATE_PATTERN_YMDHMS = "yyyy-MM-dd HH:mm:ss";

    public static final String DOLLAR = "$";
    public static final String DOLLARSPACE = "$ ";
    public static final String NULLSTRING = "";
    public static final String SPACESTRING = " ";
    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_FAIL = "FAIL";

    public static final String FORMAT_DOUBLE_2DECIMAL = "%.2f";

    public static String URL_TOMCAT = null;
    public static final String LOCAL_CATALOG_ERRORLOG = "/data/data/com.shuishou.retailerinventory/errorlog/";
    public static final String FILE_SERVERURL = "/data/data/com.shuishou.retailerinventory/serverconfig";
    public static final String ERRORLOGPATH = "/data/data/com.shuishou.retailerinventory/errorlog/";

}
