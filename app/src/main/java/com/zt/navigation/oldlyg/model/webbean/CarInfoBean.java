package com.zt.navigation.oldlyg.model.webbean;

import java.util.List;

public class CarInfoBean {
    private String id;
    private String taskno;
    private String vehicle;
    private String department;
    private String permitno;
    private String vessel;
    private String voyage;
    private String cargo;
    private String mark;
    private String weight;
    private List<Address> address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskno() {
        return taskno;
    }

    public void setTaskno(String taskno) {
        this.taskno = taskno;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPermitno() {
        return permitno;
    }

    public void setPermitno(String permitno) {
        this.permitno = permitno;
    }

    public String getVessel() {
        return vessel;
    }

    public void setVessel(String vessel) {
        this.vessel = vessel;
    }

    public String getVoyage() {
        return voyage;
    }

    public void setVoyage(String voyage) {
        this.voyage = voyage;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public class Address{
        private String STORAGE;
        private String CODE_DEPARTMENT;

        public String getSTORAGE() {
            return STORAGE;
        }

        public void setSTORAGE(String STORAGE) {
            this.STORAGE = STORAGE;
        }

        public String getCODE_DEPARTMENT() {
            return CODE_DEPARTMENT;
        }

        public void setCODE_DEPARTMENT(String CODE_DEPARTMENT) {
            this.CODE_DEPARTMENT = CODE_DEPARTMENT;
        }
    }
}
