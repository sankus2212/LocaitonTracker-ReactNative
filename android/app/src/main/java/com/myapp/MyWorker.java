package com.myapp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.BATTERY_SERVICE;

public class MyWorker extends Worker implements Workable<GPSPoint> {

    private static final String TAG = "MyWorker";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * The current location.
     */
    private Location mLocation;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    private Context mContext;
    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        if(isGPSOn()){
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                }
            };

            final LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            mFusedLocationClient
                    .getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLocation = task.getResult();
                            work(new GPSPoint(mLocation.getLatitude(), mLocation.getLongitude()));
                            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                        } else {
                            work(new GPSPoint(0.0,0.0));
                        }
                    });

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, null);

        }else{
            work(new GPSPoint(0.0,0.0));
        }
        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        stopLocationUpdates();
        Log.e(TAG, "GPSPoint : Stopped");
    }

    private void stopLocationUpdates() {
        Log.i(TAG, "stop() Stopping location tracking");
        this.mFusedLocationClient.removeLocationUpdates(this.mLocationCallback);
    }

    @Override
    public void work(GPSPoint gpsPoint) {
        Log.e(TAG, "GPSPoint :" + gpsPoint.getLatitude() + ", " +
                gpsPoint.getLongitude());

        String location = SharedPrefManager.getInstance(mContext)
                .get(SharedPrefManager.Key.PLACE, null);
        if (location != null) {
            double lat = Double.parseDouble(location.split(",")[0]);
            double lng = Double.parseDouble(location.split(",")[1]);

            Location location1 = new Location("");
            location1.setLatitude(gpsPoint.getLatitude());
            location1.setLongitude(gpsPoint.getLongitude());

            Location location2 = new Location("");
            location2.setLatitude(lat);
            location2.setLongitude(lng);
            float distanceInMeters = location1.distanceTo(location2);

            createEntry(distanceInMeters, lat, lng);
        }

    }

    private void createEntry(float distanceInMeters, double lat, double lng) {
        String s = SharedPrefManager.getInstance(mContext).
                get(SharedPrefManager.Key.SINGLE_FENCE, null);
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MMM/yyyy hh:mm aa", Locale.getDefault());
        String date = dateFormat.format(new Date(System.currentTimeMillis()));
        String isGpsOn;
        String isNetworkConnected;

        if (isNetworkAvailable()) isNetworkConnected = "Network : Connected";
        else isNetworkConnected = "Network : Not Connected";

        if (isGPSOn()) isGpsOn = "GPS : On";
        else isGpsOn = "GPS : Off";

        String batteryPercentage = "Battery " + getBatteryPercentage(mContext) + "%";

        LocationEntry entry = new LocationEntry(distanceInMeters,
                lat, lng, date,
                batteryPercentage,
                isInBatteryOptimizationMode(),
                isNetworkConnected,
                isGpsOn, getDeviceName());

        List<LocationEntry> entries = new ArrayList<>();
        if (s == null) {
            entries.add(entry);
            String list = new Gson().toJson(entries);
            SharedPrefManager.getInstance(mContext).
                    set(SharedPrefManager.Key.SINGLE_FENCE, list);

        } else {
            entries.addAll(Arrays.asList(new Gson().fromJson(s, LocationEntry[].class)));
            entries.add(entry);
            String list = new Gson().toJson(entries);
            SharedPrefManager.getInstance(mContext).
                    set(SharedPrefManager.Key.SINGLE_FENCE, list);
        }
    }


    private static int getBatteryPercentage(Context context) {

        if (Build.VERSION.SDK_INT >= 21) {

            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        } else {

            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            double batteryPct = level / (double) scale;

            return (int) (batteryPct * 100);
        }
    }


    private boolean isGPSOn() {
        LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    private String isInBatteryOptimizationMode() {
        PowerManager manager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (manager.isIgnoringBatteryOptimizations(mContext.getPackageName())) {
                return "Battery : Not Optimized";
            } else {
                return "Battery : Optimized";
            }
        }
        return "Battery : Unknown";
    }
}
