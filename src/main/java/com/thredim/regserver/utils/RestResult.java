package com.thredim.regserver.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestResult {
    private Object data;
    private String code;
    private String msg;
    private boolean status;

    public RestResult(){}

    public RestResult(String msg, boolean status){
        this("200", msg, status);
    }

    public RestResult(String code, String msg, boolean status){
        this.code = code;
        this.msg = msg;
        this.status = status;
    }

    public static RestResult getSuccess(){
        return new RestResult("", true);
    }

    public static RestResult getSuccess(String msg){
        return new RestResult(msg, true);
    }

    public static RestResult getFailed(){
        return new RestResult("", false);
    }

    public static RestResult getFailed(String msg){
        return new RestResult(msg, false);
    }

    public RestResult setObject(Object data){
        this.data = data;
        return this;
    }
}
