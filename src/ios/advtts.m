//
//  advtts.m
//  MEbook eXtra
//
//  Created by Lorenzo Chellini on 13/10/15.
//
//

#import "advtts.h"

@implementation advtts


- (void)pluginInitialize
{
    if( !synthesizer ){
        synthesizer = [AVSpeechSynthesizer new];
        synthesizer.delegate = self;
    }
}


- (void)registerCallback:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT];
    [ result setKeepCallbackAsBool:YES ];
    [self.commandDelegate sendPluginResult:result callbackId:CDVCommandStatus_NO_RESULT];
    callbackId = command.callbackId;
}


- (void)speechSynthesizer:(AVSpeechSynthesizer*)synthesizer didFinishSpeechUtterance:(AVSpeechUtterance*)utterance {
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    if( callbackId ){
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
        callbackId = nil;
    }
}


- (void)speak:(CDVInvokedUrlCommand*)command
{
    [synthesizer stopSpeakingAtBoundary:AVSpeechBoundaryImmediate];
    
    NSDictionary* options = [command.arguments objectAtIndex:0];
    
    NSString* text = [options objectForKey:@"text"];
    NSString* locale = [options objectForKey:@"locale"];
    double rate = [[options objectForKey:@"rate"] doubleValue];
    
    if (!locale || (id)locale == [NSNull null]) {
        locale = @"it-IT";
    }
    
    if (!rate) {
        rate = 1.0;
    }
    
    AVSpeechUtterance* utterance = [[AVSpeechUtterance new] initWithString:text];
    utterance.voice = [AVSpeechSynthesisVoice voiceWithLanguage:locale];
    utterance.rate = (AVSpeechUtteranceMinimumSpeechRate * 1.5 + AVSpeechUtteranceDefaultSpeechRate) / 2.5 * rate * rate;
    utterance.pitchMultiplier = 1.2;
    [synthesizer speakUtterance:utterance];
    
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [ result setKeepCallbackAsBool:YES ];
    
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId ];
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"startSpeak"] callbackId:callbackId];
}


- (void)pause:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [ result setKeepCallbackAsBool:YES ];
    
    [synthesizer pauseSpeakingAtBoundary:AVSpeechBoundaryImmediate];
    
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId ];
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"stopSpeak"] callbackId:callbackId];
}


- (void)resume:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [ result setKeepCallbackAsBool:YES ];
    
    [synthesizer continueSpeaking];
    
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId ];
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"startSpeak"] callbackId:callbackId];
}

@end
