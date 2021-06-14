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
import zyot.shyn.healthcareapp.entity.UserHeightEntity;

@Dao
public interface UserHeightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(UserHeightEntity userHeightEntity);

    @Update
    Completable update(UserHeightEntity userHeightEntity);

    @Delete
    Completable delete(UserHeightEntity userHeightEntity);

    @Query("SELECT * FROM user_height WHERE uid = :uid AND timestamp = :time")
    Maybe<UserHeightEntity> getUserHeightInfo(String uid, long time);

    @Query("SELECT * FROM user_height WHERE uid = :uid AND timestamp >= :startTime AND timestamp <= :endTime")
    Maybe<List<UserHeightEntity>> getUserHeightInfoBetween(String uid, long startTime, long endTime);

    @Query("SELECT * FROM user_height WHERE uid = :uid ORDER BY timestamp DESC LIMIT 1")
    Maybe<UserHeightEntity> getRecentUserHeightInfo(String uid);
}
