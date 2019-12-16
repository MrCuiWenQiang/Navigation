package com.zt.navigation.oldlyg.model.webbean;

import java.util.Date;

public class CarListBean {
    private String id;//派车单ID
    private String Taskno;//委托号
    private String OPERATETYPE;//作业类型
    private Date time;//码头放行时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskno() {
        return Taskno;
    }

    public void setTaskno(String taskno) {
        Taskno = taskno;
    }

    public String getOPERATETYPE() {
        return OPERATETYPE;
    }

    public void setOPERATETYPE(String OPERATETYPE) {
        this.OPERATETYPE = OPERATETYPE;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
