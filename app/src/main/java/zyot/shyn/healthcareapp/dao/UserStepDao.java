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
import zyot.shyn.healthcareapp.entity.UserStepEntity;

@Dao
public interface UserStepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(UserStepEntity userStepEntity);

    @Update
    Completable update(UserStepEntity userStepEntity);

    @Delete
    Completable delete(UserStepEntity userStepEntity);

    @Query("SELECT * FROM user_step WHERE uid = :uid AND timestamp = :time")
    Maybe<UserStepEntity> getUserStepInfo(String uid, long time);

    @Query("SELECT * FROM user_step WHERE uid = :uid AND timestamp >= :startTime AND timestamp <= :endTime")
    Maybe<List<UserStepEntity>> getUserStepInfoBetween(String uid, long startTime, long endTime);
}
