package zyot.shyn.healthcareapp.repository;

import android.app.Application;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import zyot.shyn.healthcareapp.dao.UserActivityDao;
import zyot.shyn.healthcareapp.database.AppDatabase;
import zyot.shyn.healthcareapp.entity.UserActivityEntity;
import zyot.shyn.healthcareapp.pojo.ActivityDurationPOJO;
import zyot.shyn.healthcareapp.utils.MyDateTimeUtils;

public class UserActivityRepository {
    private static final String TAG = UserActivityRepository.class.getSimpleName();

    private static volatile UserActivityRepository instance;

    private final UserActivityDao userActivityDao;

    private UserActivityRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userActivityDao = db.userActivityDao();
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

    public Maybe<List<UserActivityEntity>> getUserActivityDataInDay(int year, int month, int date) {
        long startTime = MyDateTimeUtils.getStartTimeOfDate(year, month, date);
        long endTime = startTime + MyDateTimeUtils.MILLISECONDS_PER_DAY - 1;
        return userActivityDao.getUserActivityDataBetween(startTime, endTime);
    }

    public Maybe<List<UserActivityEntity>> getUserActivityDataInDay(long timestamp) {
        long startTime = MyDateTimeUtils.getStartTimeOfDate(timestamp);
        long endTime = startTime + MyDateTimeUtils.MILLISECONDS_PER_DAY - 1;
        return userActivityDao.getUserActivityDataBetween(startTime, endTime);
    }

    public Maybe<List<ActivityDurationPOJO>> getUserActivityDurationInMonth(int year, int month) {
        long startTime = MyDateTimeUtils.getStartTimeOfDate(year, month, 1);
        long endTime = MyDateTimeUtils.getStartTimeOfDate(year, month + 1, 1) - 1;
        return userActivityDao.queryTotalTimeOfEachActivityBetween(startTime, endTime);
    }
}
