package com.zt.navigation.oldlyg.util.navigation;

import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.zt.navigation.oldlyg.MyApplication;

import cn.faker.repaymodel.util.error.ErrorUtil;

/**
 * 位置线程 用于导航过程中每1秒查询当前位置
 * 决定1.是否播报内容
 * 2.当前路径变化后是否立即播报
 */
public class LocationThread implements Runnable {

    private SpatialReference sr = SpatialReference.create(4490);

    private Point mpoint = null;
    private volatile boolean istop = true;

    private final int intervalValue = 3;

    private final long sleepValue = 1*1000;

    public LocationThread( Point mpoint) {
        this.mpoint = mpoint;
    }

    @Override
    public void run() {
       while (istop){
           try {
               if (!istop){
                   return;
               }
               Thread.sleep(sleepValue);
           } catch (InterruptedException e) {
               e.printStackTrace();
               ErrorUtil.showError(e);
               break;
           }
           if (!istop){
               return;
           }
           Point nowPoint = (Point) MyApplication.startPoint.copy();
           double distance = GeometryEngine.distance(mpoint, nowPoint, sr);
           distance = DistanceUtil.sumTo(distance);
           synchronized (ThreadManager.isRead) {
               if (distance >= intervalValue) {
                   ThreadManager.isRead = true;
               } else {
                   ThreadManager.isRead = false;
               }
           }
           mpoint = nowPoint;
       }
    }

    public void stopJob(){
        istop = false;
    }
}
