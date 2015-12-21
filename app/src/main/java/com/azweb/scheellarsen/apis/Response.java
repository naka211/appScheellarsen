package com.azweb.scheellarsen.apis;

/**
 * Created by Dinh Huynh on 10/5/2015.
 */
public class Response {
    private boolean isSuccess;
    private String message;
    private Object object;
    private String tag;

    public Response(boolean isSuccess, String message, Object object, String tag) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.object = object;
        this.tag = tag;
    }



    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


}
