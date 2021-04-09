package com.zt.navigation.oldlyg.util.navigation;

import android.os.Message;

import com.esri.core.tasks.na.RouteDirection;
import com.zt.navigation.oldlyg.MyApplication;

import java.util.List;

import cn.faker.repaymodel.util.error.ErrorUtil;

/**
 * 定时线程 用于导航过程中每5秒播报一次
 */
public class TimingThread implements Runnable {
    private volatile boolean istop = true;

    private RouteCore routeCore;
    private final long sleepValue = 4 * 1000;
    private ThreadManager.TaskHandle taskHandle;
    ;

    public TimingThread( ThreadManager.TaskHandle taskHandle, List<RouteDirection> routes) {
        this.taskHandle = taskHandle;
        routeCore = new RouteCore(routes);
    }

    @Override
    public void run() {
        while (istop) {
            if (!istop){
                return;
            }
            // TODO: 2021/3/25 判断在这里合适吗?
            if (ThreadManager.isRead) {

                String msg = routeCore.screenRoute(MyApplication.startPoint);
                Message message = new Message();
                message.obj = msg;
                taskHandle.handleMessage(message);
            }else{
                synchronized (ThreadManager.isRead){
                    ThreadManager.isRead = true;//兼容性
                }
            }
            if (!istop){
                return;
            }
            try {
                Thread.sleep(sleepValue);
            } catch (InterruptedException e) {
                e.printStackTrace();
                ErrorUtil.showError(e);
                break;
            }
        }
    }

    public void stopJob(){
        istop = false;
    }
}
