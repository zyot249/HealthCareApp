package zyot.shyn.healthcareapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "user_weight", primaryKeys = {"timestamp", "uid"})
public class UserWeightEntity {
    private long timestamp;
    @NonNull
    private String uid;
    private float weight;

    public UserWeightEntity() {
        uid = "";
    }

    @Ignore
    public UserWeightEntity(long timestamp, @NotNull String uid, float weight) {
        this.timestamp = timestamp;
        this.uid = uid;
        this.weight = weight;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @NotNull
    public String getUid() {
        return uid;
    }

    public float getWeight() {
        return weight;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), timestamp, uid, weight);
    }
}
