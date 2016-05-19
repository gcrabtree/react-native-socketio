package com.gcrabtree.rctsocketio;

import android.util.Log;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by Greg Crabtree on 5/18/16.
 */
public class ReactNativeEventUtil {
    private static final String TAG = "ReactNativeEventUtil";

    /**
     * Send the event back so that our javascript code can listen using the DeviceEventEmitter
     * https://facebook.github.io/react-native/docs/native-modules-android.html#content
     * @param reactContext
     * @param eventName
     * @param params
     */
    public static void sendEvent(ReactContext reactContext, String eventName, Object params) {
        if (reactContext != null)
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        else
            Log.e(TAG, "Could not submit event for a null context...");
    }
}
