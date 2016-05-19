package com.gcrabtree.rctsocketio;

/**
 * Create by Greg Crabtree on 5/16/2016
 *
 */

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIoModule extends ReactContextBaseJavaModule {
    private static final String TAG = "RCTSocketIoModule";

    private Socket mSocket;
    private ReactApplicationContext mReactContext;

    public SocketIoModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return "SocketIO";
    }

    /**
     * Initialize and configure socket
     * @param connection Url string to connect to.
     * @param options Configuration options.
     */
    @ReactMethod
    public void initialize(String connection, ReadableMap options) {
        try {
            this.mSocket = IO.socket(
                    connection,
                    SocketIoReadableNativeMap.mapToOptions((ReadableNativeMap) options)
            );
        }
        catch(URISyntaxException exception) {
            Log.e(TAG, "Socket Initialization error: ", exception);
        }
    }

    /**
     * Emit event to server
     * @param event The name of the event.
     * @param items The data to pass through the SocketIo engine to the server endpoint.
     */
    @ReactMethod
    public void emit(String event, ReadableMap items) {
        HashMap<String, Object> map = SocketIoReadableNativeMap.toHashMap((ReadableNativeMap) items);
        if (mSocket != null) {
            mSocket.emit(event, new JSONObject(map));
        }
        else {
            Log.e(TAG, "Cannot execute emit. mSocket is null. Initialize socket first!!!");
        }
    }

    /**
     * Generates a Listener that handles an event. We've made it generic so that all response
     * data will be packed into the items hash. Data is sent to the ReactNative JS layer.
     * @return an Emitter.Listener that has a callback that will emit the coupled event name and response data
     * to the ReactNative JS layer.
     */
    private Emitter.Listener onAnyEventHandler (final String event) {
        return new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                WritableMap params = Arguments.createMap();
                params.putString("name", event);
                WritableArray items = SocketIoJSONUtil.objectsFromJSON(args);
                if (items != null) {
                    params.putArray("items", items);
                }
                ReactNativeEventUtil.sendEvent(mReactContext, "socketEvent", params);
            }
        };
    }

    /**
     * Add an eventhandler for any socket event via the Socket.IO function 'on'.
     * Handlers are also being added on the JS layer.
     * @param event The name of the event.
     */
    @ReactMethod
    public void on(String event) {
        if (mSocket != null) {
            mSocket.on(event, onAnyEventHandler(event));
        }
        else {
            Log.e(TAG, "Cannot execute on. mSocket is null. Initialize socket first!!!");
        }
    }

    /**
     * Connect to socket
     */
    @ReactMethod
    public void connect() {
        if (mSocket != null) {
            mSocket.connect();
        }
        else {
            Log.e(TAG, "Cannot execute connect. mSocket is null. Initialize socket first!!!");
        }
    }

    /**
     * Disconnect from socket
     */
    @ReactMethod
    public void disconnect() {
        if (mSocket != null) {
            mSocket.disconnect();
        }
        else {
            Log.e(TAG, "Cannot execute disconnect. mSocket is null. Initialize socket first!!!");
        }
    }

    // The following functions are not yet implemented but are here for JS side API completeness.
    /**
     * Reconnect to socket
     */
    @ReactMethod
    public void reconnect() {
        Log.d(TAG, "reconnect not implemented in SocketIO-Java client. Set reconnect boolean in " +
                "options passed in with the initialize function");
    }

    /**
     * Manually join the namespace
     */
    @ReactMethod
    public void joinNamespace(String namespace) {
        Log.d(TAG, "joinNamespace not implemented in SocketIO-Java client.");
    }

    /**
     * Leave namespace back to '/'
     */
    @ReactMethod
    public void leaveNamespace() {
        Log.d(TAG, "leaveNamespace not implemented in SocketIO-Java client.");
    }

    /**
     * Exposed but not currently used
     */
    @ReactMethod
    public void addHandlers() {
        Log.d(TAG, "addHandlers not implemented in this wrapper.");
    }
}
