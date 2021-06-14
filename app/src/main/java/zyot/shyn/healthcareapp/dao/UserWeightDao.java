package zyot.shyn.healthcareapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import zyot.shyn.healthcareapp.entity.UserWeightEntity;

@Dao
public interface UserWeightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(UserWeightEntity userWeightEntity);

    @Update
    Completable update(UserWeightEntity userWeightEntity);

    @Delete
    Completable delete(UserWeightEntity userWeightEntity);

    @Query("SELECT * FROM user_weight WHERE uid = :uid AND timestamp = :time")
    Maybe<UserWeightEntity> getUserWeightInfo(String uid, long time);

    @Query("SELECT * FROM user_weight WHERE uid = :uid AND timestamp >= :startTime AND timestamp <= :endTime")
    Maybe<List<UserWeightEntity>> getUserWeightInfoBetween(String uid, long startTime, long endTime);

    @Query("SELECT * FROM user_weight WHERE uid = :uid ORDER BY timestamp DESC LIMIT 1")
    Maybe<UserWeightEntity> getRecentUserWeightInfo(String uid);
}
