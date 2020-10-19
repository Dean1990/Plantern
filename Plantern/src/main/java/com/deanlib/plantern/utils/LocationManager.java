package com.deanlib.plantern.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.deanlib.plantern.Plantern;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationManager {

    private Timer timer1;
    private android.location.LocationManager lm;
    private LocationResult locationResult;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    boolean passive_enabled = false;

    /**
     * android.permission.ACCESS_COARSE_LOCATION
     * @param context
     * @param result
     * @return
     */
    public boolean getLocation(Context context, LocationResult result) {
        // I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult = result;
        if (lm == null)
            lm = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //是否可用
        try {
            gps_enabled = lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            passive_enabled = lm.isProviderEnabled(android.location.LocationManager.PASSIVE_PROVIDER);
        } catch (Exception ex) {
        }

        //如果gps和网络还有wifi不行就不开监听
        if (!gps_enabled && !network_enabled && !passive_enabled)
            return false;

        //压制警告
        if (ActivityCompat.checkSelfPermission(Plantern.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Plantern.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (gps_enabled)
            lm.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if (network_enabled)
            lm.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        if (passive_enabled)
            lm.requestLocationUpdates(android.location.LocationManager.PASSIVE_PROVIDER, 0, 0, locationListenerpassive);

        timer1 = new Timer();
        timer1.schedule(new GetLastLocation(), 20000);
        return true;
    }

    //关闭定位
    public void cancel() {
        timer1.cancel();
        lm.removeUpdates(locationListenerGps);
        lm.removeUpdates(locationListenerNetwork);
        lm.removeUpdates(locationListenerpassive);
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.getLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
            lm.removeUpdates(locationListenerpassive);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.getLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerpassive);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerpassive = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.getLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);
            lm.removeUpdates(locationListenerpassive);

            Location net_loc = null, gps_loc = null, passive_loc = null;

            //压制警告
            if (ActivityCompat.checkSelfPermission(Plantern.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(Plantern.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (gps_enabled)
                gps_loc = lm.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
            if (network_enabled)
                net_loc = lm.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
            if (passive_enabled)
                passive_loc = lm.getLastKnownLocation(android.location.LocationManager.PASSIVE_PROVIDER);

            //使用最近的一条
            if (gps_loc != null && net_loc != null && passive_loc != null) {
                if (gps_loc.getTime() > net_loc.getTime() && gps_loc.getTime() > passive_loc.getTime()) {
                    locationResult.getLocation(gps_loc);
                } else if (net_loc.getTime() > gps_loc.getTime() && net_loc.getTime() > passive_loc.getTime()) {
                    locationResult.getLocation(net_loc);
                } else {
                    locationResult.getLocation(passive_loc);
                }
                return;
            }

            if (gps_loc != null) {
                locationResult.getLocation(gps_loc);
                return;
            }
            if (net_loc != null) {
                locationResult.getLocation(net_loc);
                return;
            }
            if (passive_loc != null) {
                locationResult.getLocation(passive_loc);
                return;
            }
            locationResult.getLocation(null);
        }
    }

    public static abstract class LocationResult {
        public abstract void getLocation(Location location);
    }

    public String getFeatureLocation(Context context, Location location) {
        String featureName = "";
        if (location != null) {

            Geocoder geocoder = new Geocoder(context);
            List<Address> addList = null;// 解析经纬度
            try {
                addList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addList != null && addList.size() > 0) {
//                for (int i = 0; i < addList.size(); i++) {
//                    Address add = addList.get(i);
//                    featureName = add.getFeatureName();
//                }
                if (addList.get(0).getMaxAddressLineIndex() >= 0) { //不是size 是 index
                    featureName = addList.get(0).getAddressLine(0);
                }
            }
        }

        return featureName;

    }
}
