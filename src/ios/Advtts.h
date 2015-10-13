//
//  advtts.h
//  MEbook eXtra
//
//  Created by Lorenzo Chellini on 13/10/15.
//
//

#import <Cordova/CDV.h>
#import <AVFoundation/AVFoundation.h>

@interface Advtts : CDVPlugin <AVSpeechSynthesizerDelegate> {
    AVSpeechSynthesizer* synthesizer;
    NSString* regCallbackId;
    NSString* locale;
    double rate;
}


/*
- (void)setProp:(CDVInvokedUrlCommand*)command;
- (void)registerCallback:(CDVInvokedUrlCommand*)command;
- (void)speak:(CDVInvokedUrlCommand*)command;
- (void)pause:(CDVInvokedUrlCommand*)command;
- (void)resume:(CDVInvokedUrlCommand*)command;
*/

@end
