package com.gcrabtree.rctsocketio;

import android.util.Log;

import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableNativeMap;

import java.util.HashMap;

import io.socket.client.IO;

/**
 * Created by Greg Crabtree on 5/17/16.
 */

public class SocketIoReadableNativeMap extends ReadableNativeMap {
    private static final String TAG = "SIOReadableNativeMap";
    /**
     * Note: This will only be necessary until RN version 0.26 goes live
     * It will be deprecated from the project, as this is just included in that version of RN.
     *
     * This converts the SocketIoReadableNativeMap to a Java usable HashMap.
     * @return converted HashMap.
     */
    public static HashMap<String, Object> toHashMap(ReadableNativeMap map) {
        ReadableMapKeySetIterator iterator = map.keySetIterator();
        HashMap<String, Object> hashMap = new HashMap<>();

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            switch (map.getType(key)) {
                case Null:
                    hashMap.put(key, null);
                    break;
                case Boolean:
                    hashMap.put(key, map.getBoolean(key));
                    break;
                case Number:
                    hashMap.put(key, map.getDouble(key));
                    break;
                case String:
                    hashMap.put(key, map.getString(key));
                    break;
                case Map:
                    hashMap.put(key, toHashMap(map.getMap(key)));
                    break;
                case Array:
                    hashMap.put(key, ((SocketIoReadableNativeArray) map.getArray(key)).toArrayList());
                    break;
                default:
                    throw new IllegalArgumentException("Could not convert object with key: " + key + ".");
            }
        }
        return hashMap;
    }

    /**
     * This converts a SocketIoReadableNativeMap to a SocketIO Option object.
     * @param options ReadableNativeMap that is a JS bridged hash of options.
     * @return IO.Options object that has been populated. Currently incomplete. PRs welcome.
     */
    public static IO.Options mapToOptions(ReadableNativeMap options) {
        ReadableMapKeySetIterator iterator = options.keySetIterator();
        IO.Options opts = new IO.Options();

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey().toLowerCase();
            switch (key) {
                case "force new connection":
                case "forcenew":
                    opts.forceNew = options.getBoolean(key);
                    break;
                case "multiplex":
                    opts.multiplex = options.getBoolean(key);
                    break;
                case "reconnection":
                    opts.reconnection = options.getBoolean(key);
                    break;
                case "connect_timeout":
                    opts.timeout = options.getInt(key);
                    break;
                default:
                    Log.e(TAG, "Could not convert object with key: " + key + ".");
            }
        }
        return opts;
    }
}
