
package it.gruppometa.cordova;


import java.io.File;
import java.util.Locale;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;


public class Advtts extends CordovaPlugin {
    public static final String TAG = "Advtts";


    /**
     * Constructor.
     */
    public Advtts() {
        
      
    }
    TextToSpeech ttsEngine;
    boolean supportsPause = Build.VERSION.SDK_INT>=21;
    private CallbackContext jsListener;
    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private File dest;
    public void initialize(final CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        File local=cordova.getActivity().getDir("audio", Context.MODE_WORLD_WRITEABLE);
        dest=new File(local,"tempaudio.mp3");
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
              send("stopSpeak");
            }
        });
        ttsEngine=new TextToSpeech(cordova.getActivity().getApplication(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                   if(status != TextToSpeech.ERROR) {
                   }
                }
             });
        
        ttsEngine.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String string) {
                
                
            }

            @Override
            public void onDone(String string) {
            	Log.d(TAG, "Syntesizing completed");
                try {
                    mediaPlayer.setDataSource(cordova.getActivity(),Uri.fromFile(dest));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    send("startSpeak");
                } catch (Exception e) {
                    Log.e(TAG,"Error",e);
                    sendError(e.getMessage());
                    send("stopSpeak");
                }
            }

            @Override
            public void onError(String string) {
            	Log.e(TAG, "Syntesizing aborted: "+string);
                sendError(string);
            }
        });
    }
    private void sendError(String val) {
        if(jsListener!=null) {
            PluginResult r=new PluginResult(PluginResult.Status.ERROR,val);
            r.setKeepCallback(true);
            jsListener.sendPluginResult(r);
        }
    }
    private void send(String val) {
        if(jsListener!=null) {
            PluginResult r=new PluginResult(PluginResult.Status.OK,val);
            r.setKeepCallback(true);
            jsListener.sendPluginResult(r);
        }
    }
    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */

    @SuppressLint("NewApi") @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if(action.equals("registerCallback")) {
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            jsListener=callbackContext;
            return true;
        }
        if(action.equals("stop")) {
        	Log.d(TAG, "Stop");
            if(supportsPause) mediaPlayer.stop();
            else ttsEngine.stop();
            send("stopSpeak");
            callbackContext.success();
            return true;
        }
        if(action.equals("pause")) {
        	Log.d(TAG, "Pause");
            if(supportsPause && mediaPlayer.isPlaying())  mediaPlayer.pause();
            else ttsEngine.stop();
            send("stopSpeak");
            callbackContext.success();
            return true;
        }
        if(action.equals("resume")) {
            try {
                mediaPlayer.start();
                send("startSpeak");
                PluginResult result = new PluginResult(PluginResult.Status.OK);
                callbackContext.sendPluginResult(result);
            } catch(Throwable t) {
                PluginResult result = new PluginResult(PluginResult.Status.ERROR);
                callbackContext.sendPluginResult(result);
            }
            return true;
        }
        if (action.equals("setProp")) {
            
            String language=args.getString(0);
            double rate=args.getDouble(1);
            Log.e(TAG,"setProp:"+language+" rate:"+rate);
            Locale l = new Locale(language);
            int res=ttsEngine.setLanguage(l);
            ttsEngine.setSpeechRate((float)rate);
            if(TextToSpeech.LANG_NOT_SUPPORTED==res) {
                Log.e(TAG,"LANG_NOT_SUPPORTED "+((l!=null) ? l.toString() :"Null language"));
                callbackContext.error("LANG_NOT_SUPPORTED");
            } else {
                callbackContext.success();
            }
            return true;
        }
        if (action.equals("speak")) {
            send("stopSpeak");
            mediaPlayer.reset();
            String testo=args.getString(0);
            ttsEngine.stop();
            Log.d(TAG, "Syntesizing: "+testo);
            if(supportsPause) ttsEngine.synthesizeToFile(testo, null, dest,"1");
//            else ttsEngine.synthesizeToFile(testo, null, dest.getPath());
            else ttsEngine.speak(testo, TextToSpeech.QUEUE_FLUSH, null);
            JSONObject r = new JSONObject();
            callbackContext.success(r);
            return true;
        }

        return false;

    }


}


