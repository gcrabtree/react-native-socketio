'use strict';

import { DeviceEventEmitter, NativeModules, Platform } from 'react-native';
let SocketIO = NativeModules.SocketIO;

class Socket {
  constructor (host, config) {

    if (typeof host === "undefined")
      throw "Cannot create a socket connection without a host.";
    if (typeof config === "undefined")
      config = {};

    this.sockets = SocketIO;
    this.isConnected = false;
    this.handlers = {};
    this.onAnyHandler = null;

    if(Platform.OS === "ios")
      this.sockets.addListener("socketEvent");

    this.deviceEventSubscription = DeviceEventEmitter.addListener("socketEvent", this._handleEvent.bind(this));

    // Set default handlers
    this.defaultHandlers = {
      connect: () => {
        this.isConnected = true;
      },

      disconnect: () => {
        this.isConnected = false;
      }
    };

    // Set initial configuration
    this.sockets.initialize(host, config);
  }

  _handleEvent (event) {
    if (this.handlers.hasOwnProperty(event.name)) {
      this.handlers[event.name].apply(this, (event.hasOwnProperty('items')) ? event.items : null);
    }
    if (this.defaultHandlers.hasOwnProperty(event.name)) {
      this.defaultHandlers[event.name]();
    }

    if (this.onAnyHandler) this.onAnyHandler(event);
  }

  connect () {
    this.sockets.connect();
  }

  on (event, handler) {
    this.handlers[event] = handler;
    if (Platform.OS === 'android') {
      this.sockets.on(event);
    }
  }

  onAny (handler) {
    this.onAnyHandler = handler;
  }

  emit (event, data, ack = () => console.log(`ACK ${event}`)) {
    this.sockets.emit(event, data, ack);
  }

  joinNamespace (namespace) {
    this.sockets.joinNamespace(namespace);
  }

  leaveNamespace () {
    this.sockets.leaveNamespace();
  }

  disconnect () {
    this.sockets.disconnect();
  }

  reconnect () {
    this.sockets.reconnect();
  }
}

module.exports = Socket;
