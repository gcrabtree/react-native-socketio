package com.gcrabtree.rctsocketio;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Greg Crabtree on 5/19/16.
 *
 * A note from Greg... This is ugly. I am totally open to doing something else.
 * Basically, the SocketIo-Java client gives us back a varargs of Objects on an event callback.
 * This is an array of JSONObjects.
 * We need to take those, put them into a WritableArray, then emit that to React Native.
 * ReactNative then converts that WritableArray to JSON internally, then converts that JSON back
 * into Javascript objects.  Not very efficient, but other than rewriting a bunch of the
 * SocketIo-Java client, I don't know what to do.
 */
public class SocketIoJSONUtil {
    private static final String TAG = "SocketIoJSONUtil";

    public static WritableArray objectsFromJSON(Object... args) {
        if (args != null && args.length > 0) {
            WritableArray items = Arguments.createArray();
            for (Object object : args) {
                if (object == null) {
                    items.pushNull();
                } else if (object instanceof JSONObject) {
                    items.pushMap(jsonObjectToWritableMap((JSONObject) object));
                } else if (object instanceof JSONArray) {
                    items.pushArray(jsonArrayToWritableArray((JSONArray) object));
                } else {
                    Log.e(TAG, "Cannot identify JSONObject. Pushing null to the array. " +
                            "Original unidentfied object = " + object);
                    items.pushNull();
                }
            }
            return items;
        }
        return null;
    }

    public static WritableMap jsonObjectToWritableMap(JSONObject jsonObject) {
        WritableMap items = Arguments.createMap();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                Object object = jsonObject.get(key);
                if (object == null) {
                    items.putNull(key);
                } else if (object instanceof Boolean) {
                    items.putBoolean(key, ((Boolean) object));
                } else if (object instanceof Integer) {
                    items.putInt(key, ((Integer) object));
                } else if (object instanceof Double) {
                    items.putDouble(key, ((Double) object));
                } else if (object instanceof Float) {
                    items.putDouble(key, ((Float) object).doubleValue());
                } else if (object instanceof Long) {
                    items.putDouble(key, ((Long) object).doubleValue());
                } else if (object instanceof String) {
                    items.putString(key, object.toString());
                } else if (object instanceof JSONObject) {
                    items.putMap(key, jsonObjectToWritableMap((JSONObject) object));
                } else if (object instanceof JSONArray) {
                    items.putArray(key, jsonArrayToWritableArray((JSONArray) object));
                } else {
                    Log.e(TAG, "Cannot identify JSONObject. Inserting Null object for key "
                            + key + " unidentfied object = " + object);
                    items.putNull(key);
                }
            } catch (JSONException error) {
                Log.e(TAG, "objectsFromJSON JSONException = " + error);
            }
        }
        return items;
    }

    public static WritableArray jsonArrayToWritableArray(JSONArray jsonArray) {
        WritableArray items = Arguments.createArray();
        for (int i=0; i < jsonArray.length(); i++) {
            try {
                Object object = jsonArray.get(i);
                if (object == null) {
                    items.pushNull();
                } else if (object instanceof Boolean) {
                    items.pushBoolean((Boolean) object);
                } else if (object instanceof Integer) {
                    items.pushInt((Integer) object);
                } else if (object instanceof Double) {
                    items.pushDouble((Double) object);
                } else if (object instanceof Float) {
                    items.pushDouble(((Float) object).doubleValue());
                } else if (object instanceof Long) {
                    items.pushDouble(((Long) object).doubleValue());
                } else if (object instanceof String) {
                    items.pushString(object.toString());
                } else if (object instanceof JSONObject) {
                    items.pushMap(jsonObjectToWritableMap((JSONObject) object));
                } else if (object instanceof JSONArray) {
                    items.pushArray(jsonArrayToWritableArray((JSONArray) object));
                } else {
                    Log.e(TAG, "Cannot identify JSONObject. Inserting Null object. " +
                            "Original unidentfied object = " + object);
                    items.pushNull();
                }
            } catch (JSONException error) {
                Log.e(TAG, "objectsFromJSON JSONException = " + error);
            }
        }
        return items;
    }
}
