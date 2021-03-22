package com.zt.navigation.oldlyg.util;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.zt.navigation.oldlyg.Urls;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.faker.repaymodel.net.okhttp3.HttpHelper;
import cn.faker.repaymodel.net.okhttp3.callback.HttpCallback;
import cn.faker.repaymodel.net.okhttp3.callback.HttpResponseCallback;
import cn.faker.repaymodel.util.LogUtil;
import cn.faker.repaymodel.util.db.DBThreadHelper;

public class MapUtil {
    private static List<Point> startPoint;

    static {
        startPoint = new ArrayList<>();

        startPoint.add(new Point(34.746959, 119.373045));
        startPoint.add(new Point(34.696852, 119.433791));
    }
    // TODO: 2019/12/30 区域范围判断总是不在范围内 



    /**
     * 判断坐标点是否落在指定的多边形区域内
     *
     * @param x
     * @param y
     * @return
     */
    static boolean isHave = false;
 /*  public static synchronized void isHave(double x, double y,HttpResponseCallback callback) {




       HashMap map = new HashMap<String, String>();
       map.put("x", x);
       map.put("y", y);
       HttpHelper.get(" http://218.92.115.59:7732/GISServer1._1/LService/?GetStatus_peopel", map, new HttpResponseCallback() {
           @Override
           public void onSuccess(String data) {
               isHave = Boolean.valueOf(data);
               callback.onSuccess(data);
           }

           @Override
           public void onFailed(int status, String message) {
               callback.onSuccess(message);
           }
       });
    }
*/
    static boolean isEND = true;
    public static  void isHave(double x, double y,HttpResponseCallback callback){
        if (!isEND){
            return;
        }else {
            isEND = false;
        }
        HashMap map = new HashMap<String, String>();
        map.put("x", ""+x);
        map.put("y", ""+y);
        HttpHelper.get(Urls.LSERVICE, map, new HttpCallback() {
            @Override
            public void onSuccess(String data) {
                isEND = true;
                callback.onSuccess(data);
            }

            @Override
            public void onFailed(int status, String message) {
                isEND = true;
                callback.onFailed(status,message);
            }
        });
/*        DBThreadHelper.startThreadInPool(new DBThreadHelper.ThreadCallback<String>() {
            @Override
            protected String jobContent() throws Exception {
                String WSDL_URI = "http://218.92.115.59:7732/GISServer1._1/LService/?wsdl";//wsdl 的uri
                String namespace = "http://tempuri.org/";//namespace
                String methodName = "GetStatus_peopel";//要调用的方法名称

                SoapObject request = new SoapObject(namespace, methodName);
                // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
                request.addProperty("x", String.valueOf(x));
                request.addProperty("y", String.valueOf(y));

                //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER12);
                envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
                envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true

                HttpTransportSE httpTransportSE = new HttpTransportSE(WSDL_URI);
                try {
                    httpTransportSE.call(null, envelope);//调用
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                // 获取返回的数据
                SoapObject object = (SoapObject) envelope.bodyIn;
                // 获取返回的结果
                String result = object.getProperty(0).toString();
                LogUtil.d("debug",result);
                return result;
            }

            @Override
            protected void jobEnd(String o) {
                isEND = true;
            }
        });*/

    }



    //之前到达港区就直接返回坐标
    public synchronized static Point movenPoint(Point point) {
        return point;
      /*  if (isHave) {
            return point;
        }
        double minLength = -1;
        Point minPoint = null;
        for (Point item : startPoint) {
            double w = Math.pow(item.getX() - point.getX(), 2);
            double h = Math.pow(item.getY() - point.getY(), 2);
            double l = w + h;
            if (minLength <= 0 || l < minLength) {
                minLength = l;
                minPoint = item;
            }

        }
        point.setXY(minPoint.getY(), minPoint.getX());
        return point;*/
    }


}
