using ReactNative.Bridge;
using Quobject.SocketIoClientDotNet.Client;
using Quobject.EngineIoClientDotNet.ComponentEmitter;
using System;
using System.Collections.Generic;
using Newtonsoft.Json.Linq;
using ReactNative.Modules.Core;

namespace ReactNativeSocketIO
{
    public class SocketIOModule : ReactContextNativeModuleBase, ILifecycleEventListener
    {
        /**
         * Tag name.
         */
        private static string TAG = "RCTSocketIoModule";

        /**
         * Wrapped socket connection.
         */
        private Socket _socket;

        /**
         * React context.
         */
        private ReactContext _reactContext;

        /**
         * Socket.io events constants.
         * Used to allow access in Javascript.
         */
        private const string EVENT_CONNECT = "EVENT_CONNECT";
        private const string EVENT_CONNECT_ERROR = "EVENT_CONNECT_ERROR";
        private const string EVENT_CONNECT_TIMEOUT = "EVENT_CONNECT_TIMEOUT";
        private const string EVENT_CONNECTING = "EVENT_CONNECTING";
        private const string EVENT_DISCONNECT = "EVENT_DISCONNECT";
        private const string EVENT_ERROR = "EVENT_ERROR";
        private const string EVENT_MESSAGE = "EVENT_MESSAGE";
        private const string EVENT_PING = "EVENT_PING";
        private const string EVENT_PONG = "EVENT_PONG";
        private const string EVENT_RECONNECT = "EVENT_RECONNECT";
        private const string EVENT_RECONNECT_ATTEMPT = "EVENT_RECONNECT_ATTEMPT";
        private const string EVENT_RECONNECT_ERROR = "EVENT_RECONNECT_ERROR";
        private const string EVENT_RECONNECT_FAILED = "EVENT_RECONNECT_FAILED";
        private const string EVENT_RECONNECTING = "EVENT_RECONNECTING";

        public SocketIOModule(ReactContext reactContext) : base(reactContext)
        {
            _reactContext = reactContext;
        }

        public override string Name
        {
            get
            {
                return "SocketIO";
            }
        }

        public override IReadOnlyDictionary<string, object> Constants
        {
            get
            {
                return new Dictionary<string, object>
                {
                    { EVENT_ERROR, Socket.EVENT_ERROR },
                    { EVENT_RECONNECT_FAILED, Socket.EVENT_RECONNECT_FAILED },
                    { EVENT_RECONNECTING, Socket.EVENT_RECONNECTING },
                    { EVENT_RECONNECT, Socket.EVENT_RECONNECT },
                    { EVENT_PONG, EVENT_PONG },
                    { EVENT_PING, EVENT_PING },
                    { EVENT_RECONNECT_ERROR, Socket.EVENT_RECONNECT_ERROR },
                    { EVENT_DISCONNECT, Socket.EVENT_DISCONNECT },
                    { EVENT_CONNECT_TIMEOUT, Socket.EVENT_CONNECT_TIMEOUT },
                    { EVENT_CONNECT_ERROR, Socket.EVENT_CONNECT_ERROR },
                    { EVENT_CONNECTING, EVENT_CONNECTING },
                    { EVENT_CONNECT, Socket.EVENT_CONNECT },
                };
            }
        }

        /**
         * Initialize and configure socket
         * @param connection Url string to connect to.
         * @param options Configuration options.
         */
        [ReactMethod]
        public void initialize(String connection, JObject options)
        {
            try
            {
                _socket = IO.Socket(connection, SocketIOUtils.JObjectToOptions(options));
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        /**
         * Close the socket
         */
        [ReactMethod]
        public void close()
        {
            if (_socket != null)
            {
                _socket.Close();
            }
            else
            {
                throw new Exception("Cannot execute close. mSocket is null. Initialize Socket first.");
            }
        }

        /**
         * Disconnect from socket.
         */
        [ReactMethod]
        public void disconnect()
        {
            if (_socket != null)
            {
                _socket.Disconnect();
            }
            else
            {
                throw new Exception("Cannot execute disconnect. mSocket is null. Initialize Socket first");
            }
        }

        /**
         * Emit event to server
         * @param event The name of the event.
         * @param items The data to pass through the SocketIo engine to the server endpoint.
         * @param Ack callback
         */
        [ReactMethod]
        public void emit(string e, JObject items, ICallback ack)
        {
            Dictionary<string, object> map = SocketIOUtils.ToDictionnary(items);
            if (_socket != null)
            {
                _socket.Emit(e, new SocketIOAckCallback(ack), items);
            }
            else
            {
                throw new Exception("Cannot execute emit. mSocket is null. Initialize Socket first.");
            }
        }

        /**
         * Generates a Listener that handles an event. We've made it generic so that all response
         * data will be packed into the items hash. Data is sent to the ReactNative JS layer.
         * @return an Emitter.Listener that has a callback that will emit the coupled event name and response data
         * to the ReactNative JS layer.
         */
        private IListener _onAnyEventHandler(string e)
        {
            return new SocketIOEmitterListener(e, ref _reactContext);
        }

        /**
         * Add an eventhandler for any socket event via the Socket.IO function 'on'.
         * Handlers are also being added on the JS layer.
         * @param event The name of the event.
         */
        [ReactMethod]
        public void on(string e)
        {
            if (_socket != null)
            {
                _socket.On(e, _onAnyEventHandler(e));
            }
            else
            {
                throw new Exception("Cannot execute on. mSocket is null. Initialize socket first!!!");
            }
        }

        /**
         * Connect to socket
         */
        [ReactMethod]
        public void connect()
        {
            if (_socket != null)
            {
                _socket.Connect();
            }
            else
            {
                throw new Exception("Cannot execute connect. mSocket is null. Initialize socket first!!!");
            }
        }

        /**
        * Connect to socket
        */
        [ReactMethod]
        public void open()
        {
            if (_socket != null)
            {
                _socket.Open();
            }
            else
            {
                throw new Exception("Cannot execute open. mSocket is null. Initialize socket first!!!");
            }
        }

        /**
         * Reconnect to socket
         */
        [ReactMethod]
        public void reconnect()
        {
            throw new Exception("reconnect not implemented in SocketIO-Java client. Set reconnect boolean in "
                    + "options passed in with the initialize function");
        }

        /**
         * Manually join the namespace
         */
        [ReactMethod]
        public void joinNamespace(string n)
        {
            throw new Exception("joinNamespace not implemented in SocketIO-Java client.");
        }

        /**
         * Leave namespace back to '/'
         */
        [ReactMethod]
        public void leaveNamespace()
        {
            throw new Exception("leaveNamespace not implemented in SocketIO-Java client.");
        }

        /**
         * Exposed but not currently used
         */
        [ReactMethod]
        public void addHandlers()
        {
            throw new Exception("addHandlers not implemented in this wrapper.");
        }

        public void OnSuspend()
        {
            // Nothing
        }

        public void OnResume()
        {
            // Nothing
        }

        public void OnDestroy()
        {
            close();
        }
    }
}
