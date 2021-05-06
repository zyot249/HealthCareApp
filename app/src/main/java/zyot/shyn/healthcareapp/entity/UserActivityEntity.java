package zyot.shyn.healthcareapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "user_activity")
public class UserActivityEntity implements Serializable {
    @PrimaryKey
    private long timestamp;
    private int activity;
    private long duration;

    public UserActivityEntity() {
    }

    public UserActivityEntity(long timestamp, int activity, long duration) {
        this.timestamp = timestamp;
        this.activity = activity;
        this.duration = duration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timestamp, activity, duration);
    }
}
