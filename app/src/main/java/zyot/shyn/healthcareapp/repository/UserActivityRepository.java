package zyot.shyn.healthcareapp.repository;

import android.app.Application;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import zyot.shyn.healthcareapp.dao.UserActivityDao;
import zyot.shyn.healthcareapp.dao.UserHeightDao;
import zyot.shyn.healthcareapp.dao.UserStepDao;
import zyot.shyn.healthcareapp.dao.UserWeightDao;
import zyot.shyn.healthcareapp.database.AppDatabase;
import zyot.shyn.healthcareapp.entity.UserActivityEntity;
import zyot.shyn.healthcareapp.entity.UserHeightEntity;
import zyot.shyn.healthcareapp.entity.UserStepEntity;
import zyot.shyn.healthcareapp.entity.UserWeightEntity;
import zyot.shyn.healthcareapp.pojo.ActivityDurationPOJO;
import zyot.shyn.healthcareapp.utils.MyDateTimeUtils;

public class UserActivityRepository {
    private static volatile UserActivityRepository instance;

    private final UserActivityDao userActivityDao;
    private final UserStepDao userStepDao;
    private final UserWeightDao userWeightDao;
    private final UserHeightDao userHeightDao;

    private UserActivityRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userActivityDao = db.userActivityDao();
        userStepDao = db.userStepDao();
        userWeightDao = db.userWeightDao();
        userHeightDao = db.userHeightDao();
    }

    public static UserActivityRepository getInstance(final Application application) {
        if (instance == null) {
            synchronized (UserActivityRepository.class) {
                if (instance == null)
                    instance = new UserActivityRepository(application);
            }
        }
        return instance;
    }

    public Completable saveUserActivity(UserActivityEntity userActivityEntity) {
        return userActivityDao.insert(userActivityEntity);
    }

    public Maybe<List<UserActivityEntity>> getUserActivityDataInDay(String uid, int year, int month, int date) {
        long startTime = MyDateTimeUtils.getStartTimeOfDate(year, month, date);
        long endTime = startTime + MyDateTimeUtils.MILLISECONDS_PER_DAY - 1;
        return userActivityDao.getUserActivityDataBetween(uid, startTime, endTime);
    }

    public Maybe<List<UserActivityEntity>> getUserActivityDataInDay(String uid, long timestamp) {
        long startTime = MyDateTimeUtils.getStartTimeOfDate(timestamp);
        long endTime = startTime + MyDateTimeUtils.MILLISECONDS_PER_DAY - 1;
        return userActivityDao.getUserActivityDataBetween(uid, startTime, endTime);
    }

    public Maybe<List<ActivityDurationPOJO>> getUserActivityDurationInMonth(String uid, int year, int month) {
        long startTime = MyDateTimeUtils.getStartTimeOfDate(year, month, 1);
        long endTime = MyDateTimeUtils.getStartTimeOfDate(year, month + 1, 1) - 1;
        return userActivityDao.queryTotalTimeOfEachActivityBetween(uid, startTime, endTime);
    }

    public Completable saveUserStep(UserStepEntity userStepEntity) {
        return userStepDao.insert(userStepEntity);
    }

    public Maybe<UserStepEntity> getUserStepDataInDay(String uid, long startTimeOfDay) {
        return userStepDao.getUserStepInfo(uid, startTimeOfDay);
    }

    public Maybe<List<UserStepEntity>> getUserStepDataInMonth(String uid, int year, int month) {
        long startTime = MyDateTimeUtils.getStartTimeOfDate(year, month, 1);
        long endTime = MyDateTimeUtils.getStartTimeOfDate(year, month + 1, 1) - 1;
        return userStepDao.getUserStepInfoBetween(uid, startTime, endTime);
    }

    public Completable saveUserWeight(UserWeightEntity userWeightEntity) {
        return userWeightDao.insert(userWeightEntity);
    }

    public Maybe<UserWeightEntity> getUserWeightDataInDay(String uid, long startTimeOfDay) {
        return userWeightDao.getUserWeightInfo(uid, startTimeOfDay);
    }

    public Maybe<List<UserWeightEntity>> getUserWeightDataInMonth(String uid, int year, int month) {
        long startTime = MyDateTimeUtils.getStartTimeOfDate(year, month, 1);
        long endTime = MyDateTimeUtils.getStartTimeOfDate(year, month + 1, 1) - 1;
        return userWeightDao.getUserWeightInfoBetween(uid, startTime, endTime);
    }

    public Completable saveUserHeight(UserHeightEntity userHeightEntity) {
        return userHeightDao.insert(userHeightEntity);
    }

    public Maybe<UserHeightEntity> getUserHeightDataInDay(String uid, long startTimeOfDay) {
        return userHeightDao.getUserHeightInfo(uid, startTimeOfDay);
    }

    public Maybe<List<UserHeightEntity>> getUserHeightDataInMonth(String uid, int year, int month) {
        long startTime = MyDateTimeUtils.getStartTimeOfDate(year, month, 1);
        long endTime = MyDateTimeUtils.getStartTimeOfDate(year, month + 1, 1) - 1;
        return userHeightDao.getUserHeightInfoBetween(uid, startTime, endTime);
    }

    public Maybe<UserHeightEntity> getUserHeightRecent(String uid) {
        return userHeightDao.getRecentUserHeightInfo(uid);
    }

    public Maybe<UserWeightEntity> getUserWeightRecent(String uid) {
        return userWeightDao.getRecentUserWeightInfo(uid);
    }
}
