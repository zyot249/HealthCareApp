package zyot.shyn.healthcareapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import zyot.shyn.healthcareapp.entity.UserActivityEntity;
import zyot.shyn.healthcareapp.pojo.ActivityDurationPOJO;

@Dao
public interface UserActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(UserActivityEntity userActivityEntity);

    @Query("SELECT * FROM user_activity WHERE uid = :uid AND timestamp >= :startTime AND timestamp <= :endTime")
    Maybe<List<UserActivityEntity>> getUserActivityDataBetween(String uid, long startTime, long endTime);

    @Query("SELECT activity, sum(duration) as totalduration FROM user_activity WHERE uid = :uid AND timestamp >= :startTime AND timestamp <= :endTime GROUP BY activity")
    Maybe<List<ActivityDurationPOJO>> queryTotalTimeOfEachActivityBetween(String uid, long startTime, long endTime);
}
