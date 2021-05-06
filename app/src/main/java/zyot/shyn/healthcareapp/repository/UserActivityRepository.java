package zyot.shyn.healthcareapp.repository;

import android.app.Application;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import zyot.shyn.healthcareapp.dao.UserActivityDao;
import zyot.shyn.healthcareapp.database.AppDatabase;
import zyot.shyn.healthcareapp.entity.UserActivityEntity;

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

    public Flowable<List<UserActivityEntity>> getUserActivityDataInDay(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        long startTime = calendar.getTimeInMillis();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        long endTime = calendar.getTimeInMillis();
        return userActivityDao.getUserActivityDataBetween(startTime, endTime);
    }
}
