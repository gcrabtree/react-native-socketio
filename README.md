# A React Native wrapper for the Socket.io Library

This project was forked from Kirkness' React Native Swift Socket.Io project
[found here](https://github.com/kirkness/react-native-swift-socketio)

Going forward, I will split from Kirkness' project so that it will
include the Java Swift Socket.Io project as well. The point is to have one entry point into
Socket.Io for both platforms.

* The wrapped 'Socket.IO-Client-Swift' can be [found here](https://github.com/socketio/socket.io-client-swift).

* The wrapped 'Socket.IO-Client-Java' can be [found here](https://github.com/socketio/socket.io-client-java).

### Example
I've also added a super simple example app to /examples, copy and paste to your index.ios.js.
``` js
/**
 * Pass in an optional config obj, this can include most of the
 * standard props supported by the swift library
 */
var socketConfig = { path: '/socket' };
var socket = new SocketIO('localhost:3000', socketConfig);

// Connect!
socket.connect();

// An event to be fired on connection to socket
socket.on('connect', () => {
    console.log('Wahey -> connected!');
});

// Event called when 'someEvent' it emitted by server
socket.on('someEvent', (data) => {
    console.log('Some event was called, check out this data: ', data);
});

// Called when anything is emitted by the server
socket.onAny((event) => {
    console.log(`${event.name} was called with data: `, event.items);
});

// Manually join namespace. Ex: namespace is now partyRoom
socket.joinNamespace('partyRoom')

// Leave namespace, back to '/'
socket.leaveNamespace()

// Emit an event to server
socket.emit('helloWorld', {some: 'data'});

//Disconnect from server
socket.disconnect();

// Reconnect to a closed socket
socket.reconnect();
```

### Constructor

Requires:
`host` - example: 'localhost:3000'

Optional:
`config` - JSON object comprising any of the options listed below.


### Config

- `connectParams: Any Object` - Any data to be sent with the connection.
- `reconnects: Boolean` Default is `true`
- `reconnectAttempts: Int` Default is `-1` (infinite tries)
- `reconnectWait: Number` Default is `10`
- `forcePolling: Boolean` Default is `false`. `true` forces the client to use xhr-polling.
- `forceWebsockets: Boolean` Default is `false`. `true` forces the client to use WebSockets.
- `nsp: String` Default is `"/"`. Connects to a namespace.
- `log: Bool` If `true` socket will log debug messages. Default is false.
- `path: String` - If the server uses a custom path. ex: `"/swift"`. Default is `""`

#### Not supported yet but easy enough to implement.

- ~~`cookies: [NSHTTPCookie]?` An array of NSHTTPCookies. Passed during the handshake. Default is nil.~~
- ~~`sessionDelegate: NSURLSessionDelegate` Sets an NSURLSessionDelegate for the underlying engine. Useful if you need to handle self-signed certs. Default is nil.~~

### Methods

- `connect` - Connect to socket
- `on` - Add event handler to socket
    - `@param` - String - event name
    - `@param` - Function - callback
- `onAny` - Add event handler to any event
    - `@param` - Function - callback
- `emit` - Emit an event to server
    - `@param` - String - Event name
    - `@param` - Anything - Data to be sent
- `close` - Close the connection
    - `@param` - Boolean - should close fast?
- `reconnect` - Reconnect to a closed connection
- `joinNamespace` - Manually join namespace
- `leaveNamespace` - Leave namespace, back to '/'

### Known issues

- Would rather this in an xcode framework but run into non-modular header issue.

### Install

- Run in your project:
```sh
$ npm install react-native-swift-socketio
```

- Open up your project in xcode and right click the package.
- Click **Add files to 'Your project name'**
- Navigate to **/node_modules/react-native-swift-socketio/RNSwiftSocketIO**
- Click 'Add'
- Click your project in the navigator on the left and go to **build settings**
- Search for **Objective-C Bridging Header**
- Double click on the empty column
- Enter **../node_modules/react-native-swift-socketio/RNSwiftSocketIO/SocketBridge.h**
- Search for **Header Search Paths**
- Double Click on the column (likely has other search paths in it already)
- Enter this text at the bottom of the column $(SRCROOT)/../node_modules/react-native-swift-socketio/RNSwiftSocketIO

... That should do it! Please let me know of any issues ...
