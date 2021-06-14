package zyot.shyn.healthcareapp.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "user_step", primaryKeys = {"timestamp", "uid"})
public class UserStepEntity implements Serializable {
    private long timestamp;
    @NonNull
    private String uid;
    @ColumnInfo(name = "amount_step")
    private int amountOfSteps;
    @ColumnInfo(name = "walking_step")
    private int walkingSteps;
    @ColumnInfo(name = "jogging_step")
    private int joggingSteps;
    @ColumnInfo(name = "downstairs_step")
    private int downstairsSteps;
    @ColumnInfo(name = "upstairs_step")
    private int upstairsSteps;
    @ColumnInfo(name = "calo_burned")
    private float totalCaloriesBurned;
    private float distance;

    public UserStepEntity() {
        uid = "";
    }

    @Ignore
    public UserStepEntity(long timestamp, @NotNull String uid, int amountOfSteps, int walkingSteps, int joggingSteps, int downstairsSteps, int upstairsSteps, float totalCaloriesBurned, float distance) {
        this.timestamp = timestamp;
        this.uid = uid;
        this.amountOfSteps = amountOfSteps;
        this.walkingSteps = walkingSteps;
        this.joggingSteps = joggingSteps;
        this.downstairsSteps = downstairsSteps;
        this.upstairsSteps = upstairsSteps;
        this.totalCaloriesBurned = totalCaloriesBurned;
        this.distance = distance;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @NotNull
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getAmountOfSteps() {
        return amountOfSteps;
    }

    public void setAmountOfSteps(int amountOfSteps) {
        this.amountOfSteps = amountOfSteps;
    }

    public int getWalkingSteps() {
        return walkingSteps;
    }

    public void setWalkingSteps(int walkingSteps) {
        this.walkingSteps = walkingSteps;
    }

    public int getJoggingSteps() {
        return joggingSteps;
    }

    public void setJoggingSteps(int joggingSteps) {
        this.joggingSteps = joggingSteps;
    }

    public int getDownstairsSteps() {
        return downstairsSteps;
    }

    public void setDownstairsSteps(int downstairsSteps) {
        this.downstairsSteps = downstairsSteps;
    }

    public int getUpstairsSteps() {
        return upstairsSteps;
    }

    public void setUpstairsSteps(int upstairsSteps) {
        this.upstairsSteps = upstairsSteps;
    }

    public float getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(float totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
