package com.ramymokako.plugin.ussd.android;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.ramymokako.plugin.ussd.android.USSDController;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class VoIpUSSD extends CordovaPlugin {
    private static final int REQUEST_PERMISSION_REQ_CODE = 1;
    private static final int SEND_SMS_REQ_CODE = 0;
    // public static String TAG = "USSDService";
    public final String ACTION_HAS_PERMISSION = "has_permission";
    public final String ACTION_REQUEST_PERMISSION = "request_permission";
    public final String ACTION_SEND_SMS = "show";
    private JSONArray _args;
    CallbackContext callbackContext;
    private Context context;
    private HashMap<String, HashSet<String>> map;
    public String[] res = {""};
    private String result;
    private USSDApi ussdApi;

    private static String TAG = "tesitoVOIPUSSD";


    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext2) throws JSONException {
		Log.d(TAG, "execute.");
		Log.d(TAG, "execute..");
		Log.d(TAG, "execute...");
		Log.d(TAG, "execute....");
		Log.d(TAG, "execute.....");

        HashMap<String, HashSet<String>> hashMap = new HashMap<>();
        this.map = hashMap;
        hashMap.put("KEY_LOGIN", new HashSet(Arrays.asList(new String[]{"espere", "waiting", "loading", "esperando"})));
        this.map.put("KEY_ERROR", new HashSet(Arrays.asList(new String[]{"problema", "problem", "error", "null"})));
        Activity activity = this.cordova.getActivity();
        this.context = activity;
        this.callbackContext = callbackContext2;
        this._args = jSONArray;
        this.ussdApi = USSDController.getInstance(activity);
        this.result = "";
        if (str.equals("show")) {
            try {
                String string = jSONArray.getJSONObject(0).getString("ussdCode");
                if (hasPermission()) {
		            // Log.d(TAG, "hasPermission()");
                    executeSimpleUssd(string, callbackContext2);
                    new PluginResult(PluginResult.Status.NO_RESULT).setKeepCallback(true);
                    return true;
                }
		        Log.d(TAG, "requestPermission(0);");
                requestPermission(0);
                return false;
            } catch (JSONException e) {
                callbackContext2.error("Error encountered: " + e.getMessage());
                return false;
            }
        } else if (str.equals("has_permission")) {
            callbackContext2.sendPluginResult(new PluginResult(PluginResult.Status.OK, hasPermission()));
            return false;
        } else {
            if (str.equals("request_permission")) {
                requestPermission(1);
            }
            return false;
        }
    }

	public void doUSSD(String[] ussdCommands, int index, PluginResult[] pluginResult){
        if(index < ussdCommands.length){
            Log.d(TAG, "###doUSSD### Sending #### " + index + ": " + ussdCommands[index]);
            ussdApi.send(ussdCommands[index], new USSDController.CallbackMessage() {
                @Override
                public void responseMessage(String message) {
                    if(index == ussdCommands.length - 1){
                        message = message + "DONE ";
			            ussdApi.cancel();
                    }
			
			// message = message + "DONEENOD" + index + " vvvvvvvvvvv" + ussdCommands.length;
			
                    Log.d(TAG, "###doUSSD### Received ### " + index + ": " + message);
                    pluginResult[index]  = new PluginResult(PluginResult.Status.OK, ussdCommands[index] + ": " + message);
                    pluginResult[index].setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult[index]); 
                    final int ussdIndex = index + 1;
                    // ussdIndex = ussdIndex + 1;
                    doUSSD(ussdCommands, ussdIndex, pluginResult);
                }
            });
        }
	}

    private void executeSimpleUssd(String incoming, CallbackContext callbackContext){
		// String example 1,*777#,1,1,2 on sim 1 dial *777# and enter 1, then 1, then 2 in menus
		String [] temp = incoming.split(",");
		int simSlot = Integer.parseInt( temp[0]);
        String phoneNumber = temp[1];
        PluginResult[] pluginResult = new PluginResult[temp.length];
        final int cmdIndex = 2;
        final int noCmdIndex = 0;
		
        Log.d(TAG, "before executeSimpleUssd/callUSSD: " + phoneNumber);
        ussdApi.callUSSDInvoke(phoneNumber, simSlot, map, new USSDController.CallbackInvoke() {
            @Override
            public void responseInvoke(String message) {
				Log.d(TAG, "after executeSimpleUssd/responseInvoke:" + phoneNumber + ":" + message);
				pluginResult[cmdIndex]  = new PluginResult(PluginResult.Status.OK, phoneNumber + ": " + message);
				pluginResult[cmdIndex].setKeepCallback(true);
				callbackContext.sendPluginResult(pluginResult[cmdIndex]); 
				doUSSD(temp, cmdIndex, pluginResult);
            }

            @Override
            public void over(String message) {
				// message = message + "DONEENOD";
		        Log.d(TAG, "after executeSimpleUssd/over:" + message);
				pluginResult[noCmdIndex]  = new PluginResult(PluginResult.Status.OK, message + " THE_END");
				pluginResult[noCmdIndex].setKeepCallback(true);
				callbackContext.sendPluginResult(pluginResult[noCmdIndex]);
            }
        });
    }


    private boolean hasPermission() {
		            // Log.d(TAG, "CALL_PHONE." + this.cordova.hasPermission("android.permission.CALL_PHONE"));
		            // Log.d(TAG, "READ_PHONE_STATE." + this.cordova.hasPermission("android.permission.READ_PHONE_STATE"));
        return this.cordova.hasPermission("android.permission.CALL_PHONE") && this.cordova.hasPermission("android.permission.READ_PHONE_STATE");
    }

    private void requestPermission(int i) {
        this.cordova.requestPermissions(this, i, new String[]{"android.permission.READ_PHONE_STATE", "android.permission.CALL_PHONE"});
    }

    public void onRequestPermissionResult(int i, String[] strArr, int[] iArr) throws JSONException {
        for (int i2 : iArr) {
            if (i2 == -1) {
                this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "User has denied permission"));
                return;
            }
        }
        if (i == 0) {
            try {
                executeSimpleUssd(this._args.getJSONObject(0).getString("ussdCode"), this.callbackContext);
                new PluginResult(PluginResult.Status.NO_RESULT).setKeepCallback(true);
            } catch (JSONException e) {
                this.callbackContext.error("Error encountered: " + e.getMessage());
            }
        } else {
            this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
        }
    }
}
