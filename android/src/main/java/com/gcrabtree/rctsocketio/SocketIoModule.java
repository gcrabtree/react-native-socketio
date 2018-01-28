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
import com.facebook.react.bridge.Callback;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Socket.io module for React Native.
 * Wrapper of socket.io-client-java by Socket.io <https://github.com/socketio/socket.io-client-java>
 *
 * @author gcrabtree
 * @author Nana Axel <ax.lnana@outlook.com>
 */
public class SocketIoModule extends ReactContextBaseJavaModule {
    /**
     * Tag name.
     */
    private static final String TAG = "RCTSocketIoModule";

    /**
     * Wrapped socket connection.
     */
    private Socket mSocket;

    /**
     * React context.
     */
    private ReactApplicationContext mReactContext;

    /**
     * Socket.io events constants.
     * Used to allow access in Javascript.
     */
    public static final String EVENT_CONNECT = "EVENT_CONNECT";
    public static final String EVENT_CONNECT_ERROR = "EVENT_CONNECT_ERROR";
    public static final String EVENT_CONNECT_TIMEOUT = "EVENT_CONNECT_TIMEOUT";
    public static final String EVENT_CONNECTING = "EVENT_CONNECTING";
    public static final String EVENT_DISCONNECT = "EVENT_DISCONNECT";
    public static final String EVENT_ERROR = "EVENT_ERROR";
    public static final String EVENT_MESSAGE = "EVENT_MESSAGE";
    public static final String EVENT_PING = "EVENT_PING";
    public static final String EVENT_PONG = "EVENT_PONG";
    public static final String EVENT_RECONNECT = "EVENT_RECONNECT";
    public static final String EVENT_RECONNECT_ATTEMPT = "EVENT_RECONNECT_ATTEMPT";
    public static final String EVENT_RECONNECT_ERROR = "EVENT_RECONNECT_ERROR";
    public static final String EVENT_RECONNECT_FAILED = "EVENT_RECONNECT_FAILED";
    public static final String EVENT_RECONNECTING = "EVENT_RECONNECTING";

    public SocketIoModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return "SocketIO";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();

        constants.put(EVENT_ERROR, Socket.EVENT_ERROR);
        constants.put(EVENT_RECONNECT_FAILED, Socket.EVENT_RECONNECT_FAILED);
        constants.put(EVENT_RECONNECTING, Socket.EVENT_RECONNECTING);
        constants.put(EVENT_RECONNECT, Socket.EVENT_RECONNECT);
        constants.put(EVENT_PONG, Socket.EVENT_PONG);
        constants.put(EVENT_PING, Socket.EVENT_PING);
        constants.put(EVENT_RECONNECT_ERROR, Socket.EVENT_RECONNECT_ERROR);
        constants.put(EVENT_ERROR, Socket.EVENT_ERROR);
        constants.put(EVENT_DISCONNECT, Socket.EVENT_DISCONNECT);
        constants.put(EVENT_CONNECT_TIMEOUT, Socket.EVENT_CONNECT_TIMEOUT);
        constants.put(EVENT_CONNECT_ERROR, Socket.EVENT_CONNECT_ERROR);
        constants.put(EVENT_CONNECTING, Socket.EVENT_CONNECTING);
        constants.put(EVENT_CONNECT, Socket.EVENT_CONNECT);

        return constants;
    }

    /**
     * Initialize and configure socket
     * @param connection Url string to connect to.
     * @param options Configuration options.
     */
    @ReactMethod
    public void initialize(String connection, ReadableMap options) {
        try {
            this.mSocket = IO.socket(connection, SocketIoReadableNativeMap.mapToOptions((ReadableNativeMap) options));
        } catch (URISyntaxException exception) {
            Log.e(TAG, "Socket Initialization error: ", exception);
        }
    }

    /**
     * Close the socket
     */
    @ReactMethod
    public void close() {
        if (mSocket != null) {
            mSocket.close();
        } else {
            Log.e(TAG, "Cannot execute close. mSocket is null. Initialize Socket first.");
        }
    }

    /**
     * Disconnect from socket.
     */
    @ReactMethod
    public void disconnect() {
        if (mSocket != null) {
            mSocket.disconnect();
        } else {
            Log.e(TAG, "Cannot execute disconnect. mSocket is null. Initialize Socket first");
        }
    }

    /**
     * Emit event to server
     * @param event The name of the event.
     * @param items The data to pass through the SocketIo engine to the server endpoint.
     * @param Ack callback
     */
    @ReactMethod
    public void emit(String event, ReadableMap items, final Callback ack) {
        HashMap<String, Object> map = SocketIoReadableNativeMap.toHashMap((ReadableNativeMap) items);
        if (mSocket != null) {
            mSocket.emit(event, new JSONObject(map), new Ack() {
                @Override
                public void call(Object... args) {
                    ack.invoke(SocketIoJSONUtil.objectsFromJSON(args));
                }
            });
        } else {
            Log.e(TAG, "Cannot execute emit. mSocket is null. Initialize Socket first.");
        }
    }

    /**
     * Generates a Listener that handles an event. We've made it generic so that all response
     * data will be packed into the items hash. Data is sent to the ReactNative JS layer.
     * @return an Emitter.Listener that has a callback that will emit the coupled event name and response data
     * to the ReactNative JS layer.
     */
    private Emitter.Listener onAnyEventHandler(final String event) {
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
        } else {
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
        } else {
            Log.e(TAG, "Cannot execute connect. mSocket is null. Initialize Socket first.");
        }
    }

    /**
    * Connect to socket
    */
    @ReactMethod
    public void open() {
        if (mSocket != null) {
            mSocket.open();
        } else {
            Log.e(TAG, "Cannot execute open. mSocket is null. Initialize Socket first.");
        }
    }

    // The following functions are not yet implemented but are here for JS side API completeness.
    /**
     * Reconnect to socket
     */
    @ReactMethod
    public void reconnect() {
        Log.d(TAG, "reconnect not implemented in SocketIO-Java client. Set reconnect boolean in "
                + "options passed in with the initialize function");
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
