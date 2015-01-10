package com.eugene.donttouchme;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import java.util.Locale;


public class MainActivity extends ActionBarActivity implements SensorEventListener, TextToSpeech.OnInitListener {

    private static final float NEAR_THRESHOLD = 5; //in cm

    private View mView;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextToSpeech mTextToSpeech;
    private boolean isSpeechAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mView = findViewById(R.id.view);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (mSensor == null) {
            new AlertDialog.Builder(this)
                    .setMessage("Что за хрень ты держишь в руках? Я с ЭТИМ работать не буду.")
                    .setPositiveButton("OK", null)
                    .show();
        }

        mTextToSpeech = new TextToSpeech(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mSensor != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float proximity = event.values[0];

        Log.d("Sensor", "Value = " + proximity);

//        if (proximity <= NEAR_THRESHOLD) {
//            Random random = new Random();
//            mView.setBackgroundColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
//        }

        if (mTextToSpeech != null && isSpeechAvailable) {
            mTextToSpeech.speak("Не трогай меня", TextToSpeech.QUEUE_FLUSH, null, "");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTextToSpeech.setLanguage(new Locale("ru"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("Speech", "Language isn't supported");
            } else {
                isSpeechAvailable = true;
            }
        } else {
            Log.e("Speech", "Speech init failed");
        }
    }
}
