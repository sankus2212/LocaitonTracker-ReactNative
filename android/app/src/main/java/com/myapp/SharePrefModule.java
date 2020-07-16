package com.myapp;

import android.content.Context;
import android.telecom.Call;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.IllegalViewOperationException;

import androidx.annotation.NonNull;

public class SharePrefModule extends ReactContextBaseJavaModule {

    private Context mContext;

    public SharePrefModule(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "SharePrefModule";
    }

    @ReactMethod
    public void getLocationData(Callback successCallBack,Callback errorCallback){
        try{
            SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(mContext);
            String s = sharedPrefManager.get(SharedPrefManager.Key.SINGLE_FENCE,null);
            successCallBack.invoke(s);
        }catch (Exception e){
            errorCallback.invoke(e.getMessage());
        }
    }
}
