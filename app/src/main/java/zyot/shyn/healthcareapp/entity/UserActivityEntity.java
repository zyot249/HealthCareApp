package zyot.shyn.healthcareapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;


@Entity(tableName = "user_activity", primaryKeys = {"timestamp", "uid"})
public class UserActivityEntity implements Serializable {
    private long timestamp;
    @NonNull
    private String uid;
    private int activity;
    private long duration;

    public UserActivityEntity() {
        uid = "";
    }

    @Ignore
    public UserActivityEntity(long timestamp, @NotNull String uid, int activity, long duration) {
        this.timestamp = timestamp;
        this.uid = uid;
        this.activity = activity;
        this.duration = duration;
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
