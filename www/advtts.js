

/* global exports */

var argscheck = require('cordova/argscheck'),
    channel = require('cordova/channel'),
    utils = require('cordova/utils'),
    exec = require('cordova/exec'),
    cordova = require('cordova');

var isSpeaking=false;
var callback;
var setSpeaking=function(newVal) {
    if(newVal===isSpeaking) return;
    isSpeaking=newVal;
    if(callback) callback();
};
channel.onCordovaReady.subscribe(function() {
    exec(function(data){
        console.log("speaking change",data);
        if(data==='startSpeak') {
            setSpeaking(true);
        } else if(data==='stopSpeak') {
            setSpeaking(false);
        }
    },function(err){
        console.log("Internal plugin error",err);
        
    }, "Advtts", "registerCallback", []);
});

exports.onChange=function(cb) {
    callback=cb;
};
exports.resume=function() { 
    exec(function(){},function(err){},"Advtts","resume",[]);
};
exports.pause=function() { 
    exec(function(){},function(err){},"Advtts","pause",[]);
};
exports.speaking=function() {
    return isSpeaking;
};
    // Speed: 0.5 slow, 1 normal, 2 double fast
    // Lang: it/eng
exports.setProp=function(lang, speed) { 
    if(!speed) speed=1;
    exec(function(){
        console.log("Language changed");
    },function(err){
        console.log("Error during language setting",err);
    },"Advtts","setProp",[lang,speed]);
};

exports.speak = function(testo) {
    exec(function(){},function(){}, "Advtts", "speak", [testo]);
};