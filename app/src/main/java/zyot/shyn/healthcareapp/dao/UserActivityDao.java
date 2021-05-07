package zyot.shyn.healthcareapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import zyot.shyn.healthcareapp.entity.UserActivityEntity;

@Dao
public interface UserActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(UserActivityEntity userActivityEntity);

    @Query("SELECT * FROM user_activity WHERE timestamp >= :startTime AND timestamp <= :endTime")
    Maybe<List<UserActivityEntity>> getUserActivityDataBetween(long startTime, long endTime);
}
