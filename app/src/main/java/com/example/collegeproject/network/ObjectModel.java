package com.example.collegeproject.network;

public class ObjectModel {
    boolean status;
    Object obj;
    String errMsg;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getMessage() {
        return errMsg;
    }

    public void setMessage(String message) {
        this.errMsg = message;
    }

    public ObjectModel(boolean status, Object obj, String errMsg) {
        this.status = status;
        this.obj = obj;
        this.errMsg = errMsg;
    }
}
