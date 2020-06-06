package com.zt.navigation.oldlyg;

import com.zt.navigation.oldlyg.util.UrlUtil;

public class Urls {
    public static final String baseMapUrl = "http://218.92.115.59:6080";


    public static final String mapUrl = UrlUtil.getGisUrl() + "/arcgis/rest/services/路网服务/MapServer";//LYG测试
//    public static final String mapUrl = "http://192.168.1.13:6080/arcgis/rest/services/lyg0508/MapServer";//LYG测试

    public static final String topMapUrl = UrlUtil.getGisUrl()+"/arcgis/rest/services/LYG_TDT0605/MapServer";//叠加图层 所有地图都要叠加
//        public static final String topMapUrl = UrlUtil.getGisUrl()+"/arcgis/rest/services/lianyungang_tdt2/MapServer";//叠加图层 所有地图都要叠加
//        public static final String topMapUrl = UrlUtil.getGisUrl()+"/arcgis/rest/services/lianyungang_tdt/MapServer";//叠加图层 所有地图都要叠加
//    public static final String topMapUrl = UrlUtil.getGisUrl() + "/arcgis/rest/services/lyg_tdt529/MapServer";//叠加图层 所有地图都要叠加

    public static final String searchUrl = UrlUtil.getGisUrl() + "/arcgis/rest/services/路网服务/MapServer";//LYG测试
//      public static final String searchUrl="http://192.168.1.13:6080/arcgis/rest/services/lyg0508/MapServer";//JN

    /**
     * 限速牌
     */
    public static final String xspmapUrl = UrlUtil.getGisUrl() + "/arcgis/rest/services/路网服务/MapServer/16";//LYG测试

    /**
     * 障碍点数据 用于导航的时候加入
     */
    public static final String searchHinderUrl = UrlUtil.getGisUrl() + "/arcgis/rest/services/路网服务/MapServer/1";
//    public static final String searchHinderUrl = "http://192.168.1.13:6080/arcgis/rest/services/lyg0508/MapServer/1";

    public static final String maxNaviUrl = UrlUtil.getGisUrl() + "/arcgis/rest/services/bclw/NAServer/%E8%B7%AF%E5%BE%84";//大车
    public static final String minNaviUrl = UrlUtil.getGisUrl() + "/arcgis/rest/services/路网服务/NAServer/%E8%B7%AF%E5%BE%84";//小车
//    public static final String mapNaviUrl ="http://192.168.1.13:6080/arcgis/rest/services/lyg0508/NAServer/%E8%B7%AF%E5%BE%84";//连云港路径

    public static final String URL = "http://www.boea.cn";
    public static final String CARLIST = UrlUtil.getNetUrl() + "/DhApi/api/DispatchList";
    public static final String UPDATELOCATION = UrlUtil.getNetUrl() + "/DhApi/api/LocationUpload";
    public static final String CARINFO = UrlUtil.getNetUrl() + "/DhApi/api/DispatchInfo";
    public static final String FEEDBACK = UrlUtil.getNetUrl() + "/DhApi/api/Idea";
    public static final String LOGIN = UrlUtil.getNetUrl() + "/DhApi/api/Logoin";
    public static final String ARRIVE = UrlUtil.getNetUrl() + "/DhApi/api/Arrive";
    public static final String MESSAGEHISTORY = UrlUtil.getNetUrl() + "/DhApi/api/MessageHistory";
    public static final String SELECT_TG = UrlUtil.getNetUrl() + "/DhApi/api/ReceiveMessage";
    public static final String LSERVICE = UrlUtil.getNetUrl() + "/DhApi/api/LService";
}
