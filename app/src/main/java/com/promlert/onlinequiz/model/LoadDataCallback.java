package com.promlert.onlinequiz.model;

/**
 * Created by Promlert on 3/23/2016.
 */
public interface LoadDataCallback {

    void onFailure(String errMessage);
    void onSuccess();

}
