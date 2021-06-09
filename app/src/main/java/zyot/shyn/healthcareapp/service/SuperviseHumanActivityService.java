package zyot.shyn.healthcareapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.schedulers.Schedulers;
import zyot.shyn.HARClassifier;
import zyot.shyn.HumanActivity;
import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.ui.activity.MainActivity;
import zyot.shyn.healthcareapp.base.Constants;
import zyot.shyn.healthcareapp.entity.UserActivityEntity;
import zyot.shyn.healthcareapp.entity.UserStepEntity;
import zyot.shyn.healthcareapp.model.AccelerationData;
import zyot.shyn.healthcareapp.repository.UserActivityRepository;
import zyot.shyn.healthcareapp.utils.MyDateTimeUtils;

public class SuperviseHumanActivityService extends Service implements SensorEventListener, StepListener {
    private static final String TAG = SuperviseHumanActivityService.class.getSimpleName();
    //Sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope, mLinearAcceleration;

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

    private long lastTimeActPrediction = 0, startTimeOfCurState = 0;
    private long prevActivityDuration = 0;
    private HumanActivity curState = HumanActivity.UNKNOWN;
    private HashMap<Float, Integer> userActivityData;

    private long activityDuration = 0;
    private boolean isWarn = false;

    private boolean isActive = false;

    private SharedPreferences sp;
    private Handler handler = new Handler();
    String CHANNEL_ID = "healthcareapp_supervisorservice";
    int notification_id = 1711101;
    int warning_notify_id = 1711102;

    private IBinder mBinder = new MyBinder();

    // repository
    private UserActivityRepository userActivityRepository;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        createNotificationChannel();

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        ax = new ArrayList<>(); ay = new ArrayList<>(); az = new ArrayList<>();
        lx = new ArrayList<>(); ly = new ArrayList<>(); lz = new ArrayList<>();
        gx = new ArrayList<>(); gy = new ArrayList<>(); gz = new ArrayList<>();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        lastTimeActPrediction = startTimeOfCurState = MyDateTimeUtils.getCurrentTimestamp();

        classifier = new HARClassifier(getApplicationContext());

        stepDetector = new StepDetector();
        stepDetector.registerStepListener(this);

        userActivityData = new HashMap<>();
        userActivityRepository = UserActivityRepository.getInstance(getApplication());
        loadDataToday();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action) {

            case Constants.START_FOREGROUND:
                Log.d(TAG, "starting service");
                break;

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
        saveLastDataBeforeReset();
        Log.d(TAG, "service destroy");
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void registerSensors() {
        if (mLinearAcceleration != null)
            mSensorManager.registerListener(SuperviseHumanActivityService.this, mLinearAcceleration, 20000);

        if (mAccelerometer != null)
            mSensorManager.registerListener(SuperviseHumanActivityService.this, mAccelerometer, 20000);

        if (mGyroscope != null)
            mSensorManager.registerListener(SuperviseHumanActivityService.this, mGyroscope, 20000);
    }

    private void unregisterSensors() {
        if (mLinearAcceleration != null)
            mSensorManager.unregisterListener(SuperviseHumanActivityService.this, mLinearAcceleration);

        if (mAccelerometer != null)
            mSensorManager.unregisterListener(SuperviseHumanActivityService.this, mAccelerometer);

        if (mGyroscope != null)
            mSensorManager.unregisterListener(SuperviseHumanActivityService.this, mGyroscope);
    }

    public void startForegroundService() {
        registerSensors();
        startTime = SystemClock.uptimeMillis() + 1000;
        startForeground(notification_id, getNotification("Starting Step Counter Service", ""));
        handler.postDelayed(timerRunnable, 1000);
        isActive = true;
    }

    public void stopForegroundService(boolean persist) {
        saveLastDataBeforeReset();
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

        userActivityData.clear();

        startTime = SystemClock.uptimeMillis();
        updatedTime = elapsedTime;
    }

    private Notification getNotification(String title, String body) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 9, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
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
        Notification notification = getNotification(curState.toString(), "");

        return notification;
    }

    private void showNoti(String textTitle, String textContent) {
        Bundle bundle = new Bundle();
        bundle.putString("key", "Hihihihihi");
        PendingIntent resultPendingIntent = new NavDeepLinkBuilder(this)
                .setComponentName(MainActivity.class)
                .setGraph(R.navigation.mobile_navigation)
                .setDestination(R.id.nav_practice)
                .setArguments(bundle)
                .createPendingIntent();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setContentIntent(resultPendingIntent)
                .setOngoing(true)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(warning_notify_id, builder.build());
    }

    public boolean isActive() {
        return isActive;
    }

    private void activityPrediction() {
        Log.d(TAG, "size: " + ax.size() + " " + ay.size() + " " + az.size() + " " + lx.size() + " " + ly.size() + " " + lz.size() + " " + gx.size() + " " + gy.size() + " " + gz.size());
        int index = classifier.predictHumanActivity(ax, ay, az, lx, ly, lz, gx, gy, gz);
        if (index != -1) {
            long now = MyDateTimeUtils.getCurrentTimestamp();
            if (MyDateTimeUtils.getDiffDays(now, lastTimeActPrediction) > 0) {
                saveLastDataBeforeReset();
                resetData();
            }
            HumanActivity state = HumanActivity.getHumanActivity(index);
            prevActivityDuration = now - startTimeOfCurState;
            if (state != curState) {
                UserActivityEntity userActivityEntity = new UserActivityEntity(startTimeOfCurState, curState.getIndex(), prevActivityDuration);
                userActivityRepository.saveUserActivity(userActivityEntity)
                        .subscribeOn(Schedulers.io())
                        .subscribe(() -> {}, err -> Log.e(TAG, "Error: " + err.getMessage()));

                startTimeOfCurState = lastTimeActPrediction;
                curState = state;
                long startTimeOfDate = MyDateTimeUtils.getStartTimeOfDate(now);
                float timePointInDayOfState = (float) (startTimeOfCurState - startTimeOfDate) / 1000;

                userActivityData.put(timePointInDayOfState, curState.getIndex());
                isWarn = false;
            }

            long startTimeNightSleep = MyDateTimeUtils.getDateFromTimeStringDefault(sp.getString("startTimeNightSleep", "00:00")).getTime();
            long endTimeNightSleep = MyDateTimeUtils.getDateFromTimeStringDefault(sp.getString("endTimeNightSleep", "07:00")).getTime();
            long startTimeNoonSleep = MyDateTimeUtils.getDateFromTimeStringDefault(sp.getString("startTimeNoonSleep", "11:30")).getTime();
            long endTimeNoonSleep = MyDateTimeUtils.getDateFromTimeStringDefault(sp.getString("endTimeNoonSleep", "13:30")).getTime();
            long maxTimeForSitOrStand = sp.getLong("maxTimeSitOrStand", 1000);

            activityDuration = now - startTimeOfCurState;
            if ((now < startTimeNightSleep || now > endTimeNightSleep) &&
                    (now < startTimeNoonSleep || now > endTimeNoonSleep) &&
                    (curState == HumanActivity.SITTING || curState == HumanActivity.STANDING) &&
                    activityDuration > maxTimeForSitOrStand && !isWarn) {
                showNoti("Warning", "You need to do exercise now!!!");
                isWarn = true;
            }

            lastTimeActPrediction = now;
        }
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
        data.put("caloBurned", String.format(Locale.ENGLISH, "%.0f", totalCaloriesBurned));
        data.put("curState", curState.toString());
        return data;
    }

    public HashMap<Float, Integer> getUserActivityData() {
        return userActivityData;
    }

    public void loadDataToday() {
        final long now = MyDateTimeUtils.getCurrentTimestamp();
        userActivityRepository.getUserActivityDataInDay(now)
                .subscribeOn(Schedulers.io())
                .subscribe(data -> {
                    Log.d(TAG, "size " + data.size());
                    long startTimeOfDate = MyDateTimeUtils.getStartTimeOfDate(now);
                    for (UserActivityEntity activityEntity : data) {
                        float timePointInDayOfState = (float) (activityEntity.getTimestamp() - startTimeOfDate) / 1000;
                        userActivityData.put(timePointInDayOfState, activityEntity.getActivity());
                    }
                }, err -> Log.d(TAG, "error: " + err.getMessage()));
    }

    private void saveLastDataBeforeReset() {
        UserActivityEntity userActivityEntity = new UserActivityEntity(startTimeOfCurState, curState.getIndex(), prevActivityDuration);
        userActivityRepository.saveUserActivity(userActivityEntity)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {}, err -> Log.e(TAG, "Error: " + err.getMessage()));
        UserStepEntity userStepEntity = new UserStepEntity(MyDateTimeUtils.getStartTimeOfDate(lastTimeActPrediction),
                amountOfSteps,
                walkingSteps, joggingSteps, downstairsSteps, upstairsSteps,
                totalCaloriesBurned, totalDistance
        );
        userActivityRepository.saveUserStep(userStepEntity)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {}, err -> Log.e(TAG, "Error: " + err.getMessage()));
    }

    private void calculateResults() {
        totalDistance = walkingSteps * 0.5f + joggingSteps * 1.5f + (upstairsSteps + downstairsSteps) * 0.2f;
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
            handler.postDelayed(this, 2000);
        }
    };
}
