package com.zt.navigation.oldlyg.model.webbean;

import java.util.Date;

public class CarListBean {
    private int ID;//派车单ID
    private String Taskno;//委托号
    private String OPERATETYPE;//作业类型
    private String VEHICLE;//车辆
    private String DEPARTMENT;
    private Date AUDITTIME;//审核时间

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public String getVEHICLE() {
        return VEHICLE;
    }

    public void setVEHICLE(String VEHICLE) {
        this.VEHICLE = VEHICLE;
    }

    public Date getAUDITTIME() {
        return AUDITTIME;
    }

    public void setAUDITTIME(Date AUDITTIME) {
        this.AUDITTIME = AUDITTIME;
    }

    public String getDEPARTMENT() {
        return DEPARTMENT;
    }

    public void setDEPARTMENT(String DEPARTMENT) {
        this.DEPARTMENT = DEPARTMENT;
    }
}
