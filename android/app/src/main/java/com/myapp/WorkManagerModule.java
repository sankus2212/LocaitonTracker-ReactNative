package com.myapp;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class WorkManagerModule extends ReactContextBaseJavaModule {
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "WorkManagerModule";
    private LocationCallback mLocationCallback;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private Location mLocation;
    @NonNull
    @Override
    public String getName() {
        return "WorkManagerModule";
    }

    private Context mContext;
    public WorkManagerModule(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @ReactMethod
    public void startWorkManager(Callback successCallBack, Callback errorCallback) {
        try {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                }
            };
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mFusedLocationClient
                    .getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLocation = task.getResult();
                            SharedPrefManager.getInstance(mContext).set(SharedPrefManager.Key.PLACE,
                                    mLocation.getLatitude() + ", " + mLocation.getLongitude());
                            mFusedLocationClient.removeLocationUpdates(mLocationCallback);


                        } else {
                            Log.w(TAG, "Failed to get location.");
                        }
                    });

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, null);
            Constraints constraints;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .setRequiresBatteryNotLow(false)
                        .setRequiresCharging(false)
                        .setRequiresDeviceIdle(false)
                        .setRequiresStorageNotLow(false)
                        .setTriggerContentMaxDelay(Duration.ZERO)
                        .build();
            } else {
                constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .setRequiresBatteryNotLow(false)
                        .setRequiresCharging(false)
                        .setRequiresDeviceIdle(false)
                        .setRequiresStorageNotLow(false)
                        .build();
            }
            WorkManager mWorkManager = WorkManager.getInstance(mContext);
            PeriodicWorkRequest mRequest = new PeriodicWorkRequest.Builder(MyWorker.class,
                    15, TimeUnit.MINUTES)
                    .build();
            mWorkManager.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, mRequest);
            successCallBack.invoke("WORKER_IS_RUNNING");
        }catch (Exception e){
            errorCallback.invoke(e.getMessage());
        }
    }
}
