
package it.gruppometa.cordova;


import java.util.HashMap;
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
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;


public class Advtts extends CordovaPlugin {
    public static final String TAG = "Advtts";
    private static HashMap<String, String> utteranceId;
    private String[] text;
    private int position=0;
    private boolean mustStop=false;
    private TextToSpeech ttsEngine;
    private CallbackContext jsListener;

    /**
     * Constructor.
     */
    public Advtts() {
    	utteranceId = new HashMap<String, String>();
    	utteranceId.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,TAG);
    }
    
    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    public void initialize(final CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        ttsEngine=new TextToSpeech(cordova.getActivity().getApplication(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                   if(status != TextToSpeech.ERROR) {
			   if(ttsEngine == null) return;
                	   ttsEngine.setOnUtteranceProgressListener(new UtteranceProgressListener() {
               			
               			@Override
               			public void onStart(String utteranceId) {
               				Log.d(TAG, "Utterance listener: start");
               			}
               			
               			@Override
               			@Deprecated
               			public void onError(String utteranceId) {
               				Log.e(TAG, "Utterance listener: error");
               			}
               			
               			@SuppressLint("NewApi") @Override
               			public void onDone(String utteranceId) {
               				Log.d(TAG, "Utterance listener: done utterance "+position+", mustStop: "+mustStop);
               				if(!mustStop && position<text.length-1){
               					position++;
               					Log.d(TAG, "Now speacking sentence "+position+" in "+text.length+": \""+text[position]);
               					if(Build.VERSION.SDK_INT>=21) ttsEngine.speak(text[position], TextToSpeech.QUEUE_FLUSH, null, TAG);
               					else ttsEngine.speak(text[position], TextToSpeech.QUEUE_FLUSH, Advtts.utteranceId);
               				} else if(position>=text.length-1) {
               					position=0;
               					mustStop=false;
               				}
               			}
               		});
                   }
                }
             });
    }
//    private void sendError(String val) {
//        if(jsListener!=null) {
//            PluginResult r=new PluginResult(PluginResult.Status.ERROR,val);
//            r.setKeepCallback(true);
//            jsListener.sendPluginResult(r);
//        }
//    }
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
        	mustStop=true;
            ttsEngine.stop();
            position=0;
            send("stopSpeak");
            callbackContext.success();
            return true;
        }
        if(action.equals("pause")) {
        	Log.d(TAG, "Pause");
        	mustStop=true;
            ttsEngine.stop();
            send("stopSpeak");
            callbackContext.success();
            return true;
        }
        if(action.equals("resume")) {
        	Log.d(TAG,"resume: mustStop="+mustStop+" position="+position);
            try {
            	mustStop=false;
            	if(Build.VERSION.SDK_INT>=21) ttsEngine.speak(text[position], TextToSpeech.QUEUE_FLUSH, null, TAG);
				else ttsEngine.speak(text[position], TextToSpeech.QUEUE_FLUSH, utteranceId);
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
            Log.d(TAG,"setProp:"+language+" rate:"+rate);
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
            send("startSpeak");
            ttsEngine.stop();
            mustStop=false;
            Log.d(TAG, args.getString(0));
//          Splitta la stringa ad ogni segno di punteggiatura, in modo da offrire una migliore granularità per il tasto Pause
            text=args.getString(0).split("[,.:;!?\\n]");
            Log.d(TAG, text.length+" sentences. First one is \""+text[0]+"\"");
            Log.d(TAG, "Start Syntesizing.");
            position=0;
            if(Build.VERSION.SDK_INT>=21) ttsEngine.speak(text[position], TextToSpeech.QUEUE_FLUSH, null, TAG);
			else ttsEngine.speak(text[position], TextToSpeech.QUEUE_FLUSH, utteranceId);
            JSONObject r = new JSONObject();
            callbackContext.success(r);
            return true;
        }

        return false;

    }


}




