//
//  SocketBridge.m
//  RNSwiftSocketIO
//
//  Created by Henry Kirkness on 10/05/2015.
//  Copyright (c) 2015 Facebook. All rights reserved.
//

#import "RCTBridge.h"

@interface RCT_EXTERN_MODULE(SocketIO, NSObject)

RCT_EXTERN_METHOD(initialize:(NSString*)connection config:(NSDictionary*)config)
RCT_EXTERN_METHOD(addHandlers:(NSDictionary*)handlers)
RCT_EXTERN_METHOD(connect)
RCT_EXTERN_METHOD(disconnect)
RCT_EXTERN_METHOD(reconnect)
RCT_EXTERN_METHOD(emit:(NSString*)event items:(id)items)
RCT_EXTERN_METHOD(joinNamespace:(NSString *)namespace)
RCT_EXTERN_METHOD(leaveNamespace)

@end
