package zyot.shyn.healthcareapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "user_height", primaryKeys = {"timestamp", "uid"})
public class UserHeightEntity {
    private long timestamp;
    @NonNull
    private String uid;
    private float height;

    public UserHeightEntity() {
        uid = "";
    }

    @Ignore
    public UserHeightEntity(long timestamp, @NotNull String uid, float height) {
        this.timestamp = timestamp;
        this.uid = uid;
        this.height = height;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @NotNull
    public String getUid() {
        return uid;
    }

    public float getHeight() {
        return height;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timestamp, uid, height);
    }
}
