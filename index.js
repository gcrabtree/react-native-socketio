'use strict';

import { DeviceEventEmitter, NativeModules, Platform } from 'react-native';
import Chance from 'chance';
let SocketIO = NativeModules.SocketIO;

const chance = new Chance();

class Socket {
  constructor(host, config) {
    this.socketID = chance.guid();
    if (typeof host === 'undefined')
      throw 'Hello there! Could you please give socket a host, please.';
    if (typeof config === 'undefined')
      config = {};

    this.sockets = SocketIO;
    this.isConnected = false;
    this.handlers = {};
    this.onAnyHandler = null;

    this.deviceEventSubscription = DeviceEventEmitter.addListener(
      'socketEvent', this._handleEvent.bind(this)
    );

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
    this.sockets.initialize(host, config, this.socketID);
  }

  _handleEvent(event) {
    if (event.socketID !== this.socketID) {
      return;
    }
    if (this.handlers.hasOwnProperty(event.name)) {
      // event parameters are emitted as an array
      // we spread them to keep the same interface
      // as the JS socketio client
      this.handlers[event.name].map(handler =>
        handler(...(event.hasOwnProperty("items") ? event.items : [null]))
      );
    }
    if (this.defaultHandlers.hasOwnProperty(event.name)) {
      this.defaultHandlers[event.name]();
    }

    if (this.onAnyHandler) this.onAnyHandler(event);
  }

  connect () {
    this.sockets.connect(this.socketID);
  }

  on (event, handler) {
    this.handlers[event] = this.handlers[event] || [];
    this.handlers[event].push(handler);
    if (Platform.OS === 'android') {
      this.sockets.on(event, this.socketID);
    }
  }

  onAny (handler) {
    this.onAnyHandler = handler;
  }

  emit (event, data) {
    this.sockets.emit(event, data, this.socketID);
  }

  joinNamespace (namespace) {
    this.sockets.joinNamespace(namespace);
  }

  leaveNamespace () {
    this.sockets.leaveNamespace();
  }

  close(...args) {
    this.disconnect(...args);
  }

  disconnect () {
    this.sockets.disconnect(this.socketID);
  }

  reconnect () {
    this.sockets.reconnect(this.socketID);
  }
}

module.exports = Socket;
