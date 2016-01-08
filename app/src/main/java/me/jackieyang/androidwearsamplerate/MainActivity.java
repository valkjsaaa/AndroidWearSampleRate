package me.jackieyang.androidwearsamplerate;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements SensorEventListener {

    private Context mContext;
    private TextView mTextView;
    private SensorManager mSensorManager;
    private int mLastGyroCount = 0;
    private int mGyroCount = 0;
    private int mLastAccelCount = 0;
    private int mAccelCount = 0;
    private Timer mTimerUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        mContext = this;

        mSensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        
        mTimerUpdate = new Timer();
        mTimerUpdate.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int AccelSampleRate = mAccelCount - mLastAccelCount;
                mLastAccelCount = mAccelCount;
                int GyroSampleRate = mGyroCount - mLastGyroCount;
                mLastGyroCount = mGyroCount;
                String text = "";
                text += "Accelerometer: " + AccelSampleRate + " samples/second";
                text += "\n";
                text += "Gyroscope: " + GyroSampleRate + " samples/second";
                final String outputText = text;

                new Handler(mContext.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(outputText);
                    }
                });
            }
        }, 1000, 1000);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelCount++;
                break;
            case Sensor.TYPE_GYROSCOPE:
                mGyroCount++;
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
