package com.zt.navigation.oldlyg.util;

import com.esri.core.geometry.Point;

import java.util.ArrayList;
import java.util.List;

public class MapUtil {
    private static List<Point> list;
    static {
        list = new ArrayList<>();
        list.add(new Point(36.7011954400,117.1071429400));
        list.add(new Point(36.7057995400,117.1763160800));
        list.add(new Point(36.6797010200,117.2865896600));
        list.add(new Point(36.6509314500,117.1031788300));
        list.add(new Point(36.6536223200,117.1875628600));
    }
    // TODO: 2019/12/30 区域范围判断总是不在范围内 
    /**
     * 判断坐标点是否落在指定的多边形区域内
     * @param x
     * @param y
     * @return
     */
    public static boolean isHave(double x,double y){
        int isum, icount, index;
        double dLon1 = 0, dLon2 = 0, dLat1 = 0, dLat2 = 0, dLon;

        if (list.size() < 3) {
            return false;
        }

        isum = 0;
        icount = list.size();

        for (index = 0; index < icount - 1; index++) {
            if (index == icount - 1) {
                dLon1 = list.get(index).getX();
                dLat1 = list.get(index).getY();
                dLon2 = list.get(0).getX();
                dLat2 = list.get(0).getY();
            } else {
                dLon1 = list.get(index).getX();
                dLat1 = list.get(index).getY();
                dLon2 = list.get(index + 1).getX();
                dLat2 = list.get(index + 1).getY();
            }

            // 判断指定点的 纬度是否在 相邻两个点(不为同一点)的纬度之间
            if (((y >= dLat1) && (y < dLat2)) || ((y >= dLat2) && (y < dLat1))) {
                if (Math.abs(dLat1 - dLat2) > 0) {
                    dLon = dLon1 - ((dLon1 - dLon2) * (dLat1 - y)) / (dLat1 - dLat2);
                    if (dLon < x){
                        isum++;
                    }
                }
            }
        }

        if ((isum % 2) != 0) {
            return true;
        } else {
            return false;
        }
    }



}
