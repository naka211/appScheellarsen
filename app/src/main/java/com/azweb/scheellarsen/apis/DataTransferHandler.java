package com.azweb.scheellarsen.apis;

public interface DataTransferHandler {
    public void onDataTransferResultHandler(Response response);
    public void onError(String messageError);
}
