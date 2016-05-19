package com.gcrabtree.rctsocketio;

import com.facebook.jni.HybridData;
import com.facebook.react.bridge.ReadableNativeArray;

import java.util.ArrayList;

/**
 * Created by Greg Crabtree on 5/17/16.
 *
 * Note: This will only be necessary until RN version 0.26 goes live.
 * It will be deprecated from the project, as this is just included in that version of RN.
 */

public class SocketIoReadableNativeArray extends ReadableNativeArray {
    public SocketIoReadableNativeArray(HybridData hybridData) {
        super(hybridData);
    }

    public ArrayList<Object> toArrayList() {
        ArrayList<Object> arrayList = new ArrayList<>();

        for (int i = 0; i < this.size(); i++) {
            switch (getType(i)) {
                case Null:
                    arrayList.add(null);
                    break;
                case Boolean:
                    arrayList.add(getBoolean(i));
                    break;
                case Number:
                    arrayList.add(getDouble(i));
                    break;
                case String:
                    arrayList.add(getString(i));
                    break;
                case Map:
                    arrayList.add(SocketIoReadableNativeMap.toHashMap(getMap(i)));
                    break;
                case Array:
                    arrayList.add(((SocketIoReadableNativeArray) getArray(i)).toArrayList());
                    break;
                default:
                    throw new IllegalArgumentException("Could not convert object at index: " + i + ".");
            }
        }
        return arrayList;
    }
}
