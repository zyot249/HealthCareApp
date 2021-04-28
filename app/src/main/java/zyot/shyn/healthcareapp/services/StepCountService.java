package zyot.shyn.healthcareapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zyot.shyn.HARClassifier;
import zyot.shyn.HumanActivity;
import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.activities.MainActivity;
import zyot.shyn.healthcareapp.base.Constants;

public class StepCountService extends Service implements SensorEventListener {
    private static final String TAG = StepCountService.class.getSimpleName();
    //Sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mLinearAcceleration;
    private Sensor mStepCounter;
    private Sensor mStepDetectorSensor;

    private long startTime = 0;
    long timeInMilliseconds = 0;
    long elapsedTime = 0;
    long updatedTime = 0;
    private String timeString;
    private String elapsedString;

    //data sensor
    private static List<Float> ax;
    private static List<Float> ay;
    private static List<Float> az;

    private static List<Float> lx;
    private static List<Float> ly;
    private static List<Float> lz;

    private static List<Float> gx;
    private static List<Float> gy;
    private static List<Float> gz;

    private long stepCount = 0;
    private long lastSteps = 0;
    private double lastDistance = 0;
    private int prevStepCount = 0;
    private long stepTimestamp = 0;
    private int speed = 0;
    private double distance = 0;

    private long lastTimeActPrediction = 0;
    private long activeTime = 0;
    private long relaxTime = 0;

    HARClassifier classifier;

    private boolean isActive = false;

    private Handler handler = new Handler();
    String CHANNEL_ID = "healthcareapp_stepcountservice";
    int notification_id = 1711101;

    private IBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        createNotificationChannel();

        ax = new ArrayList<>(); ay = new ArrayList<>(); az = new ArrayList<>();
        lx = new ArrayList<>(); ly = new ArrayList<>(); lz = new ArrayList<>();
        gx = new ArrayList<>(); gy = new ArrayList<>(); gz = new ArrayList<>();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        lastTimeActPrediction = System.currentTimeMillis();

        classifier = new HARClassifier(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action) {

            case Constants.START_FOREGROUND:
                Log.d(TAG, "starting service");
                break;

            case Constants.RESET_COUNT:
                resetCount();
                break;

            case Constants.STOP_SAVE_COUNT:
                stopForegroundService(true);

            case Constants.STOP_FOREGROUND:
                Log.d(TAG, "stopping service");
                stopForeground(true);
                unregisterSensors();
                handler.removeCallbacks(timerRunnable);
                stopSelf();
                break;
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        activityPrediction();

        if (event.sensor == mStepCounter) {
            if (prevStepCount < 1) {
                prevStepCount = (int) event.values[0];
            }
            calculateSpeed(event.timestamp, (int) (event.values[0] - prevStepCount - stepCount));
            countSteps((int) (event.values[0] - prevStepCount - stepCount));
            Log.d(TAG, "steps count: " + event.values[0]);
        }

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                ax.add(event.values[0]);
                ay.add(event.values[1]);
                az.add(event.values[2]);
                break;
            case Sensor.TYPE_GYROSCOPE:
                gx.add(event.values[0]);
                gy.add(event.values[1]);
                gz.add(event.values[2]);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                lx.add(event.values[0]);
                ly.add(event.values[1]);
                lz.add(event.values[2]);
                break;
            case Sensor.TYPE_STEP_COUNTER:
                if (prevStepCount < 1) {
                    prevStepCount = (int) event.values[0];
                }
                calculateSpeed(event.timestamp, (int) (event.values[0] - prevStepCount - stepCount));
                countSteps((int) (event.values[0] - prevStepCount - stepCount));
                Log.d(TAG, "steps count: " + event.values[0]);
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                if (mStepCounter == null) {
                    countSteps((int) event.values[0]);
                    calculateSpeed(event.timestamp, 1);
                }
                Log.d(TAG, "steps detector: " + event.values[0]);
                break;
        }
    }

    private void activityPrediction() {
        int index = classifier.predictHumanActivity(ax, ay, az, lx, ly, lz, gx, gy, gz);
        if (index != -1) {
            String activity = HumanActivity.getHumanActivity(index).toString();
            long now = System.currentTimeMillis();
            if (index == 3 || index == 4) {
                relaxTime += (now - lastTimeActPrediction);
            } else {
                activeTime += (now - lastTimeActPrediction);
            }
            lastTimeActPrediction = now;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void registerSensors() {
        if (mLinearAcceleration != null)
            mSensorManager.registerListener(StepCountService.this, mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);

        if (mAccelerometer != null)
            mSensorManager.registerListener(StepCountService.this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        if (mGyroscope != null)
            mSensorManager.registerListener(StepCountService.this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);

        if (mStepCounter != null)
            mSensorManager.registerListener(StepCountService.this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);

        if (mStepDetectorSensor != null)
            mSensorManager.registerListener(StepCountService.this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSensors() {
        if (mLinearAcceleration != null)
            mSensorManager.unregisterListener(StepCountService.this, mLinearAcceleration);

        if (mAccelerometer != null)
            mSensorManager.unregisterListener(StepCountService.this, mAccelerometer);

        if (mGyroscope != null)
            mSensorManager.unregisterListener(StepCountService.this, mGyroscope);

        if (mStepCounter != null)
            mSensorManager.unregisterListener(StepCountService.this, mStepCounter);

        if (mStepDetectorSensor != null)
            mSensorManager.unregisterListener(StepCountService.this, mStepDetectorSensor);
    }

    public void startForegroundService() {
        registerSensors();
        startTime = SystemClock.uptimeMillis() + 1000;
        startForeground(notification_id, getNotification("Starting Step Counter Service", ""));
        handler.postDelayed(timerRunnable, 1000);
        isActive = true;
    }

    public void stopForegroundService(boolean persist) {
        unregisterSensors();
        handler.removeCallbacks(timerRunnable);
        isActive = false;
        startForeground(notification_id, getNotification("Stopping Step Counter Service", ""));
        stopForeground(true);
        elapsedTime = elapsedTime + timeInMilliseconds;
    }

    public void resetCount() {
        stepCount = 0;
        distance = 0;
        startTime = SystemClock.uptimeMillis();
        updatedTime = elapsedTime;
    }

    private Notification getNotification(String title, String body) {

        Intent resetIntent = new Intent(this, StepCountService.class);
        resetIntent.setAction(Constants.RESET_COUNT);
        PendingIntent resetPendingIntent = PendingIntent.getService(this, 0, resetIntent, 0);

        Intent stopIntent = new Intent(this, StepCountService.class);
        resetIntent.setAction(Constants.STOP_SAVE_COUNT);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, 0);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 9, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo), 97, 128, false))
                .setContentIntent(resultPendingIntent)
                .addAction(R.drawable.reset, "reset", resetPendingIntent)
                .addAction(R.drawable.stop, "Stop", stopPendingIntent)
                .setOngoing(true)
                .build();

        return notification;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Health Care App",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification updateNotification() {
        String body = "";
        String title = "Step Counter ";
        HashMap<String, String> data = getData();

        body += data.get("distance") + "                ";
        body += data.get("duration");

        Notification notification = getNotification("STEPS TAKEN :  " + data.get("steps"), body);

        return notification;
    }

    public boolean isActive() {
        return isActive;
    }

    public HashMap<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        String distanceString = String.format("%.2f", lastDistance + distance);

        int seconds = (int) (updatedTime / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        timeString = String.format("%d:%s:%s", hours, String.format("%02d", minutes), String.format("%02d", seconds));

        data.put("steps", String.format(getString(R.string.steps), lastSteps + stepCount));
        data.put("distance", String.format(getString(R.string.distance), distanceString));
        data.put("duration", String.format(getString(R.string.time), timeString));
        data.put("speed", String.format(getResources().getString(R.string.speed), speed));
        data.put("relaxTime", String.valueOf(relaxTime));
        data.put("activeTime", String.valueOf(activeTime));
        return data;
    }

    //Calculates the number of steps and the other calculations related to them
    private void countSteps(int step) {
        //Step count
        stepCount += step;

        //Distance calculation
        distance = stepCount * 0.8; //Average step length in an average adult
    }

    //Calculated the amount of steps taken per minute at the current rate
    private void calculateSpeed(long eventTimeStamp, int steps) {

        long timestampDifference = eventTimeStamp - stepTimestamp;
        stepTimestamp = eventTimeStamp;
        double stepTime = timestampDifference / 1000000000.0;
        speed = (int) (60 / stepTime);
    }

    public class MyBinder extends Binder {
        private StepCountService service;

        public MyBinder() {
            this.service = StepCountService.this;
        }

        public StepCountService getService() {
            return service;
        }
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = elapsedTime + timeInMilliseconds;
            Notification notification = updateNotification();
            startForeground(notification_id, notification);
//            Log.d(TAG, timeString);
            activityPrediction();
            handler.postDelayed(this, 1000);
        }
    };
}
