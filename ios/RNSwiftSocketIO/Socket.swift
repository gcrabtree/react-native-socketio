//
//  Socket.swift
//  ReactSockets
//
//  Created by Henry Kirkness on 10/05/2015.
//  Copyright (c) 2015 Facebook. All rights reserved.
//

import Foundation

@objc(SocketIO)
class SocketIO : RCTEventEmitter {

  var socket: SocketIOClient!
  var connectionSocket: NSURL!

  /**
  * Construct and expose RCTBridge to module
  */

  @objc(initWithBridge:)
  func initWithBridge(_bridge: RCTBridge) {
    self.bridge = _bridge
  }

  /**
  * Initialize and configure socket
  */
  
  @objc(initialize:config:)
  func initialize(connection: NSString, config: NSDictionary) -> Void {
    connectionSocket = NSURL(string: String(connection));
    // Connect to socket with config
    self.socket = SocketIOClient(
      socketURL: self.connectionSocket!,
      config: config as NSDictionary?
    )
    
    // Initialize onAny events
    self.onAnyEvent()
  }

  /**
  * Manually join the namespace
  */

  @objc(joinNamespace:)
  func joinNamespace(namespace: String)  -> Void {
    self.socket.joinNamespace(namespace);
  }

  /**
  * Leave namespace back to '/'
  */

  @objc(leaveNamespace)
  func leaveNamespace() {
    self.socket.leaveNamespace();
  }

  /**
  * Exposed but not currently used
  * add NSDictionary of handler events
  */

  @objc(addHandlers:)
  func addHandlers(handlers: NSDictionary) -> Void {
    for handler in handlers {
      self.socket.on(handler.value as! String) { data, ack in
        self.sendEvent(withName: "socketEvent", body: handler.value as! String)
      }
    }
  }

  /**
  * Emit event to server
  */
  
  @objc(emit:items:ack:)
  func emit(event: String, items: AnyObject, ack: RCTResponseSenderBlock?) -> Void {
    if let ack = ack {
      self.socket.emitWithAck(event, items as! SocketData).timingOut(after: 1) { data in
        ack(data)
      }
    } else {
      self.socket.emit(event, items as! SocketData)
    } 
  }

  /**
  * PRIVATE: handler called on any event
  */

  private func onAnyEventHandler (sock: SocketAnyEvent) -> Void {
    if let items = sock.items {
      self.sendEvent(withName: "socketEvent", body: ["name": sock.event, "items": items])
    } else {
      self.sendEvent(withName: "socketEvent", body: ["name": sock.event])
    }
  }

  /**
  * Trigger the event above on any event
  * Currently adding handlers to event on the JS layer
  */

  @objc(onAnyEvent)
  func onAnyEvent() -> Void {
    self.socket.onAny(self.onAnyEventHandler)
  }

  // Connect to socket
  @objc(connect)
  func connect() -> Void {
    self.socket.connect()
  }

  // Reconnect to socket
  @objc(reconnect)
  func reconnect() -> Void {
    self.socket.reconnect()
  }

  // Disconnect from socket
  @objc(disconnect)
  func disconnect() -> Void {
    self.socket.disconnect()
  }

  override func supportedEvents() -> [String]! {
    return ["socketEvent"]
  }
}
