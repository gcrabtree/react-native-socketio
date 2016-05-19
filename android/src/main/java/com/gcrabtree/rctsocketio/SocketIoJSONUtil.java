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
                Class objectClass = object.getClass();
                if (objectClass == JSONObject.class) {
                    items.pushMap(jsonObjectToWritableMap((JSONObject) object));
                } else if (objectClass == JSONArray.class) {
                    items.pushArray(jsonArrayToWritableArray((JSONArray) object));
                } else {
                    Log.e(TAG, "Cannot identify JSONObject. Casting to string and hoping for " +
                            "best. Object.string = " + object.toString() + "classType = " +
                            objectClass.toString());
                    items.pushString(object.toString());
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
                Class objectClass = object.getClass();
                if (objectClass == Boolean.class) {
                    items.putBoolean(key, ((Boolean) object));
                } else if (objectClass == Integer.class) {
                    items.putInt(key, ((Integer) object));
                } else if (objectClass == Double.class) {
                    items.putDouble(key, ((Double) object));
                } else if (objectClass == Float.class) {
                    items.putDouble(key, ((Float) object).doubleValue());
                } else if (objectClass == Long.class) {
                    items.putDouble(key, ((Long) object).doubleValue());
                } else if (objectClass == String.class) {
                    items.putString(key, object.toString());
                } else if (objectClass == JSONObject.class) {
                    items.putMap(key, jsonObjectToWritableMap((JSONObject) object));
                } else if (objectClass == JSONArray.class) {
                    items.putArray(key, jsonArrayToWritableArray((JSONArray) object));
                } else {
                    Log.e(TAG, "Cannot identify JSONObject. Casting to string and hoping for best. Key = "
                            + key + " string = " + object.toString() + "classType = " + objectClass.toString());
                    items.putString(key, object.toString());
                }
            } catch (JSONException error) {
                Log.e(TAG, "objectsFromJSON JSONException = " + error);
            }
        }
        return items;
    }

    public static WritableArray jsonArrayToWritableArray(JSONArray jsonArray) {
        WritableArray items = Arguments.createArray();
        for (int i=0;i<jsonArray.length();i++){
            try {
                Object object = jsonArray.get(i);
                Class objectClass = object.getClass();
                if (objectClass == Boolean.class) {
                    items.pushBoolean((Boolean) object);
                } else if (objectClass == Integer.class) {
                    items.pushInt((Integer) object);
                } else if (objectClass == Double.class) {
                    items.pushDouble((Double) object);
                } else if (objectClass == Float.class) {
                    items.pushDouble(((Float) object).doubleValue());
                } else if (objectClass == Long.class) {
                    items.pushDouble(((Long) object).doubleValue());
                } else if (objectClass == String.class) {
                    items.pushString(object.toString());
                } else if (objectClass == JSONObject.class) {
                    items.pushMap(jsonObjectToWritableMap((JSONObject) object));
                } else if (objectClass == JSONArray.class) {
                    items.pushArray(jsonArrayToWritableArray((JSONArray) object));
                } else {
                    Log.e(TAG, "Cannot identify JSONObject. Casting to string and hoping for best. Element # = "
                            + i + " string = " + object.toString() + "classType = " + objectClass.toString());
                    items.pushString(object.toString());
                }
            } catch (JSONException error) {
                Log.e(TAG, "objectsFromJSON JSONException = " + error);
            }
        }
        return items;
    }
}
