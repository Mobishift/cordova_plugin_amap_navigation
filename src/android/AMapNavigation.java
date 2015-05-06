package com.mobishift.cordova.plugins.amapnavigation;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;

import android.util.Log;

import com.mobishift.cordova.plugins.navigationService.NavigationActivity;

/**
 * This class echoes a string called from JavaScript.
 */
public class AMapNavigation extends CordovaPlugin {
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("navigation")) {
            Log.i("result","Navigation");
            //String message = args.getString(0);
            this.callbackContext = callbackContext;
            Intent intent = new Intent();
            intent.setClass(this.cordova.getActivity().getApplicationContext(), NavigationActivity.class);
            Log.i("result",args.getString(0));
            Log.i("result",args.getString(1));
            Log.i("result",args.getString(2));
            Log.i("result",args.getString(3));
            intent.putExtra("NaviStartLng", args.getString(0));
            intent.putExtra("NaviStartLat", args.getString(1));
            intent.putExtra("NaviEndLng", args.getString(2));
            intent.putExtra("NaviEndLat", args.getString(3));
            Log.i("result","cordova");
            this.cordova.startActivityForResult((CordovaPlugin) this, intent, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == Activity.RESULT_OK){
            callbackContext.success("success");
        }else{
            callbackContext.error("error");
        }
    }
}
