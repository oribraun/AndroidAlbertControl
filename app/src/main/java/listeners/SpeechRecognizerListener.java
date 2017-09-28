package listeners;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import fragments.BluetoothDialogFragment;

/**
 * Created by ori on 9/24/2017.
 */

public class SpeechRecognizerListener implements RecognitionListener
{
    private static final String TAG = "speech Recognizer";
    private boolean _speechReady = false;
    private TextView _speechText;
    private BluetoothDialogFragment _bluetoothFragment;
    private String [] _albertCommands = {
            "Albert Begin Scan Male",
            "Albert Begin Scan Female",
            "Albert Begin Scan Again",
            "Albert No Socks Start",
            "Albert Black Socks Start",
            "Albert Brown Socks Start",
            "Albert Navy Socks Start",
            "Albert White Socks Start",
            "Albert Continue",
            "Albert Start",
            "Albert Send Email",
            "Albert Go Home",
            "Albert exit"
    };

    public SpeechRecognizerListener(TextView tv) {
        _speechText = tv;
    }
    public SpeechRecognizerListener(TextView tv, BluetoothDialogFragment bluetoothFragment) {
        _speechText = tv;
        _bluetoothFragment = bluetoothFragment;
    }

    public void onReadyForSpeech(Bundle params)
    {
        Log.d(TAG, "onReadyForSpeech");
        _speechText.setText("");
        _speechReady = true;
    }
    public void onBeginningOfSpeech()
    {
        Log.d(TAG, "onBeginningOfSpeech");
    }
    public void onRmsChanged(float rmsdB)
    {
        Log.d(TAG, "onRmsChanged");
    }
    public void onBufferReceived(byte[] buffer)
    {
        Log.d(TAG, "onBufferReceived");
    }
    public void onEndOfSpeech()
    {
        Log.d(TAG, "onEndofSpeech");
    }
    public void onError(int error)
    {
        Log.d(TAG,  "error " +  error);
        if(_speechReady) {
//                _speechText.setText("error " + error);
            _speechText.setText("not found");
        }
    }
    public void onResults(Bundle results)
    {
        if(_speechReady) {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            boolean foundCommand = false;
            String foundText = "";
            for (int i = 0; i < data.size() && !foundCommand; i++) {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i).toString();
                for(int j = 0; j < _albertCommands.length; j++) {
                    if(_albertCommands[j].toLowerCase().equals(data.get(i).toString().toLowerCase())) {
                        Log.i("results",_albertCommands[j]);
                        foundText = _albertCommands[j];
                        foundCommand = true;
                    }
                }
            }
            if(foundCommand) {
                _speechText.setText("results: " + foundText);
                _bluetoothFragment.sendBluetoothMessage(foundText);
            } else {
                _speechText.setText("not found");
            }
        }
    }
    public void onPartialResults(Bundle partialResults)
    {
        Log.d(TAG, "onPartialResults");
    }
    public void onEvent(int eventType, Bundle params)
    {
        Log.d(TAG, "onEvent " + eventType);
    }
}
