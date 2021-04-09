package com.zt.navigation.oldlyg.util.navigation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.esri.core.tasks.na.RouteDirection;
import com.zt.navigation.oldlyg.MyApplication;

import java.util.List;

/**
 * 管理 @class Timing 和 location 两个线程
 * 负责任务调度 与view层交互
 */
public class ThreadManager {
    //播报状态对象 同步加锁
    protected static Boolean isRead = new Boolean(true);


    protected TaskHandle taskHandle;

    private Thread t_Location;
    private Thread t_Timing;


    private LocationThread locationThread = null;
    private TimingThread timingThread = null;

    private OnNavigateListen onNavigateListen = null;

    public ThreadManager(List<RouteDirection> routes, OnNavigateListen onNavigateListen) {
        this.taskHandle = new TaskHandle(Looper.getMainLooper());
        this.onNavigateListen = onNavigateListen;
        locationThread = new LocationThread(MyApplication.startPoint);
        timingThread = new TimingThread(taskHandle, routes);
        t_Location = new Thread(locationThread);
        t_Timing = new Thread(timingThread);
    }


    public void start() {
        t_Location.start();
        t_Timing.start();
    }

    public void exit() {
        locationThread.stopJob();
        timingThread.stopJob();
        t_Location.interrupt();
        t_Timing.interrupt();
        locationThread = null;
        timingThread = null;
        isRead =true;
    }

    public class TaskHandle extends Handler {

        public TaskHandle(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            post(new Runnable() {
                @Override
                public void run() {
                    onNavigateListen.speak(msg.obj.toString());
                }
            });
        }
    }


    public interface OnNavigateListen {
        public void speak(String msg);
    }
}
