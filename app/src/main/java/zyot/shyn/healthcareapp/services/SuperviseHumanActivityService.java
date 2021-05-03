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
import java.util.Locale;

import zyot.shyn.HARClassifier;
import zyot.shyn.HumanActivity;
import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.activities.MainActivity;
import zyot.shyn.healthcareapp.base.Constants;
import zyot.shyn.healthcareapp.models.AccelerationData;

public class SuperviseHumanActivityService extends Service implements SensorEventListener, StepListener {
    private static final String TAG = SuperviseHumanActivityService.class.getSimpleName();
    //Sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope, mLinearAcceleration, mStepCounter, mStepDetectorSensor;

    private long startTime = 0;
    long timeInMilliseconds = 0;
    long elapsedTime = 0;
    long updatedTime = 0;

    //data sensor
    private static List<Float> ax, ay, az;
    private static List<Float> lx, ly, lz;
    private static List<Float> gx, gy, gz;

    private StepDetector stepDetector;
    HARClassifier classifier;

    private int amountOfSteps;
    private int walkingSteps, joggingSteps, downstairsSteps, upstairsSteps;
    private float totalCaloriesBurned = 0, totalDuration = 0, totalDistance = 0;

    private long lastTimeActPrediction = 0;
    private HumanActivity curState = HumanActivity.UNKNOWN;
    private long activeTime = 0, relaxTime = 0;

    private boolean isActive = false;

    private Handler handler = new Handler();
    String CHANNEL_ID = "healthcareapp_supervisorservice";
    int notification_id = 1711101;

    private IBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        createNotificationChannel();

        ax = ay = az = new ArrayList<>();
        lx = ly = lz = new ArrayList<>();
        gx = gy = gz = new ArrayList<>();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        lastTimeActPrediction = System.currentTimeMillis();

        classifier = new HARClassifier(getApplicationContext());

        stepDetector = new StepDetector();
        stepDetector.registerStepListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action) {

            case Constants.START_FOREGROUND:
                Log.d(TAG, "starting service");
                break;

            case Constants.RESET_COUNT:
                resetData();
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
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                ax.add(event.values[0]);
                ay.add(event.values[1]);
                az.add(event.values[2]);

                AccelerationData newAccelerationData = new AccelerationData();
                newAccelerationData.setX(event.values[0]);
                newAccelerationData.setY(event.values[1]);
                newAccelerationData.setZ(event.values[2]);
                newAccelerationData.setTime(event.timestamp);
                stepDetector.addAccelerationData(newAccelerationData);
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
        }
    }

    private void activityPrediction() {
        int index = classifier.predictHumanActivity(ax, ay, az, lx, ly, lz, gx, gy, gz);
        if (index != -1) {
            long now = System.currentTimeMillis();
            if (index == 3 || index == 4) {
                relaxTime += (now - lastTimeActPrediction);
            } else {
                activeTime += (now - lastTimeActPrediction);
            }
            curState = HumanActivity.getHumanActivity(index);
            lastTimeActPrediction = now;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void registerSensors() {
        if (mLinearAcceleration != null)
            mSensorManager.registerListener(SuperviseHumanActivityService.this, mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);

        if (mAccelerometer != null)
            mSensorManager.registerListener(SuperviseHumanActivityService.this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        if (mGyroscope != null)
            mSensorManager.registerListener(SuperviseHumanActivityService.this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);

        if (mStepCounter != null)
            mSensorManager.registerListener(SuperviseHumanActivityService.this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);

        if (mStepDetectorSensor != null)
            mSensorManager.registerListener(SuperviseHumanActivityService.this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSensors() {
        if (mLinearAcceleration != null)
            mSensorManager.unregisterListener(SuperviseHumanActivityService.this, mLinearAcceleration);

        if (mAccelerometer != null)
            mSensorManager.unregisterListener(SuperviseHumanActivityService.this, mAccelerometer);

        if (mGyroscope != null)
            mSensorManager.unregisterListener(SuperviseHumanActivityService.this, mGyroscope);

        if (mStepCounter != null)
            mSensorManager.unregisterListener(SuperviseHumanActivityService.this, mStepCounter);

        if (mStepDetectorSensor != null)
            mSensorManager.unregisterListener(SuperviseHumanActivityService.this, mStepDetectorSensor);
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

    public void resetData() {
        amountOfSteps = 0;
        walkingSteps = joggingSteps = downstairsSteps = upstairsSteps = 0;
        totalCaloriesBurned = 0;
        totalDistance = 0;
        totalDuration = 0;

        activeTime = relaxTime = 0;

        startTime = SystemClock.uptimeMillis();
        updatedTime = elapsedTime;
    }

    private Notification getNotification(String title, String body) {
        Intent resetIntent = new Intent(this, SuperviseHumanActivityService.class);
        resetIntent.setAction(Constants.RESET_COUNT);
        PendingIntent resetPendingIntent = PendingIntent.getService(this, 0, resetIntent, 0);

        Intent stopIntent = new Intent(this, SuperviseHumanActivityService.class);
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

        float hours = totalDuration / 3600;
        float minutes = (totalDuration % 3600) / 60;
        float seconds = totalDuration % 60;
        String duration = String.format(Locale.ENGLISH, "%.0f", hours) + "h " +
                String.format(Locale.ENGLISH, "%.0f", minutes) + "min " +
                String.format(Locale.ENGLISH, "%.0f", seconds) + "s";

        data.put("steps", String.valueOf(amountOfSteps));
        data.put("distance", String.format(getString(R.string.distance), totalDistance));
        data.put("duration", duration);
        data.put("relaxTime", String.valueOf(relaxTime));
        data.put("activeTime", String.valueOf(activeTime));
        data.put("caloBurned", String.format(Locale.ENGLISH, "%.0f", totalCaloriesBurned));
        return data;
    }

    private void calculateResults() {
        totalDistance = walkingSteps * 0.5f  + joggingSteps * 1.5f + (upstairsSteps + downstairsSteps) * 0.2f;
        totalDuration = walkingSteps * 1.0f + joggingSteps * 0.5f + (upstairsSteps + downstairsSteps) * 1.0f;
        totalCaloriesBurned = walkingSteps + 0.05f + joggingSteps * 0.2f + upstairsSteps * 0.1f + downstairsSteps * 0.05f;
    }

    @Override
    public void step(AccelerationData accelerationData, StepType stepType) {
        if (curState == HumanActivity.WALKING) {
            walkingSteps++;
            amountOfSteps++;
        } else if (curState == HumanActivity.JOGGING) {
            joggingSteps++;
            amountOfSteps++;
        } else if (curState == HumanActivity.UPSTAIRS) {
            upstairsSteps++;
            amountOfSteps++;
        } else if (curState == HumanActivity.DOWNSTAIRS) {
            downstairsSteps++;
            amountOfSteps++;
        }
    }

    public class MyBinder extends Binder {
        private SuperviseHumanActivityService service;

        public MyBinder() {
            this.service = SuperviseHumanActivityService.this;
        }

        public SuperviseHumanActivityService getService() {
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
            activityPrediction();
            calculateResults();
            handler.postDelayed(this, 1000);
        }
    };
}
