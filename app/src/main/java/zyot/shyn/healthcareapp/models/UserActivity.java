package zyot.shyn.healthcareapp.models;

public class UserActivity {
    private long timestamp;
    private int activity;
    private long duration;

    public UserActivity() {
    }

    public UserActivity(long timestamp, int activity, long duration) {
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
}
