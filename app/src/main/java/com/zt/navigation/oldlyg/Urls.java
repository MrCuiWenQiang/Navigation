package com.zt.navigation.oldlyg;

public class Urls {
    private static final String baseUrl = "http://192.168.1.8:6080/";

//    public static final String mapUrl = baseUrl+"arcgis/rest/services/path/MapServer";//在线地图地址与搜索同地址
    public static final String mapUrl = "http://192.168.1.13:6080/arcgis/rest/services/baseMap2/MapServer";//JN


//    public static final String searchUrl=baseUrl+"arcgis/rest/services/path/MapServer/8";
    public static final String searchUrl="http://192.168.1.13:6080/arcgis/rest/services/baseMap2/MapServer/7";//JN


//    public static final String mapNaviUrl =  baseUrl+"/arcgis/rest/services/path/NAServer/路径";//在线算路服务
//    public static final String mapNaviUrl ="http://192.168.1.13:6080/arcgis/rest/services/path/NAServer/路径";//JN
    public static final String mapNaviUrl ="http://119.3.165.194:6080/arcgis/rest/services/path/NAServer/路径";//JN


    private static final String URL = "http://www.boea.cn/DhApi/api";
    public static final String CARLIST = URL+"";
    public static final String CARINFO = URL+"";
    public static final String FEEDBACK = URL+"/Idea";
    public static final String LOGIN = URL+"/Logoin";
}
