package com.showlocationservicesdialogbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.text.Html;
import com.facebook.react.bridge.*;

class LocationServicesDialogBoxModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    private Promise promiseCallback;
    private ReadableMap map;
    private Activity currentActivity;
    private static final int ENABLE_LOCATION_SERVICES = 1009;

    LocationServicesDialogBoxModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
    }

    @Override
    public String getName() {
        return "LocationServicesDialogBox";
    }

    @ReactMethod
    public void locationServicesIsEnable(Promise promise)  {
        promiseCallback = promise;
        currentActivity = getCurrentActivity();
        LocationManager locationManager = (LocationManager) currentActivity.getSystemService(Context.LOCATION_SERVICE);
        Boolean isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    
        if (isEnabled ){
             promiseCallback.resolve("enabled");
        }else{                
          
             promiseCallback.reject("disabled");
            
        }

    }


     @ReactMethod
     public void openLocationSetting() {
          currentActivity = getCurrentActivity();
          final String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
          currentActivity.startActivityForResult(new Intent(action), ENABLE_LOCATION_SERVICES);
    }


    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if(requestCode == ENABLE_LOCATION_SERVICES) {
            currentActivity = activity;
        }
    }
}
