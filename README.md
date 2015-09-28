# cordova-plugin-adv-tts
Alternative Advanced Cordova TTS plugin.

Support pause and resume.

## Installation

    cordova plugin add https://github.com/danieleguiducci/cordova-plugin-adv-tts.git


## Supported Platforms

- Android
- iOS


## Usage

```javascript
    // Se language and speed
    // Speed 0.5 slow, 1.0 normal, 2.0 double 
    advtts.setProp('it',1);
    // Register a call
    advtts.onChange(function(){
        console.log("is Speaking? ",advtts.speaking());
    });
    advtts.pause();
    advtts.resume();
    advtts.speak(text); // Will stop past speaking

```