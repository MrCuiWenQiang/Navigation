package com.zt.navigation.oldlyg;

public class Urls {
    private static final String baseUrl = "http://192.168.1.8:6080/";

//    public static final String mapUrl = baseUrl+"arcgis/rest/services/path/MapServer";//在线地图地址与搜索同地址
//    public static final String mapUrl = "http://192.168.1.13:6080/arcgis/rest/services/baseMap2/MapServer";//JN
//    public static final String mapUrl = "http://192.168.1.13:6080/arcgis/rest/services/路网服务/MapServer";//LYG测试
    public static final String mapUrl = "http://192.168.1.13:6080/arcgis/rest/services/lyg0310/MapServer";//LYG测试


//    public static final String searchUrl=baseUrl+"arcgis/rest/services/path/MapServer/8";
//    public static final String searchUrl="http://192.168.1.13:6080/arcgis/rest/services/baseMap2/MapServer/7";//JN
//    public static final String searchUrl="http://192.168.1.13:6080/arcgis/rest/services/路网服务/MapServer/3";//JN
    public static final String searchUrl="http://192.168.1.13:6080/arcgis/rest/services/lyg0310/MapServer";//LYG测试

    /**
     * 障碍点数据 用于导航的时候加入
     */
    public static final String searchHinderUrl = "http://192.168.1.13:6080/arcgis/rest/services/LYG_ZAD/FeatureServer/0";

//    public static final String mapNaviUrl =  baseUrl+"/arcgis/rest/services/path/NAServer/路径";//在线算路服务
//    public static final String mapNaviUrl ="http://192.168.1.13:6080/arcgis/rest/services/path/NAServer/路径";//JN
//    public static final String mapNaviUrl ="http://119.3.165.194:6080/arcgis/rest/services/path/NAServer/路径";//JN
//    public static final String mapNaviUrl ="http://192.168.1.13:6080/arcgis/rest/services/LYGNetworkService0220/NAServer/路径";//连云港路径
//    public static final String mapNaviUrl ="http://192.168.1.13:6080/arcgis/rest/services/路网服务/NAServer/路径";//连云港路径
    public static final String mapNaviUrl ="http://192.168.1.13:6080/arcgis/rest/services/连云港0323/NAServer/路径";//连云港路径

    private static final String URL = "http://www.boea.cn/DhApi/api";
    public static final String CARLIST = URL+"/DispatchList";
    public static final String UPDATELOCATION = URL+"/LocationUpload";
    public static final String CARINFO = URL+"/DispatchInfo";
    public static final String FEEDBACK = URL+"/Idea";
    public static final String LOGIN = URL+"/Logoin";
    public static final String ARRIVE = URL+"/Arrive";
}
