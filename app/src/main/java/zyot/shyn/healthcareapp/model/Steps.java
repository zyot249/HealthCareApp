package zyot.shyn.healthcareapp.model;

public class Steps {
    private String date;
    private long totalSteps;
    private long downstairsSteps;
    private long upstairsSteps;
    private long walkingSteps;
    private long joggingSteps;
    private long duration;
    private long distance;

    public Steps() {
    }

    public Steps(String date, long totalSteps, long downstairsSteps, long upstairsSteps, long walkingSteps, long joggingSteps, long duration, long distance) {
        this.date = date;
        this.totalSteps = totalSteps;
        this.downstairsSteps = downstairsSteps;
        this.upstairsSteps = upstairsSteps;
        this.walkingSteps = walkingSteps;
        this.joggingSteps = joggingSteps;
        this.duration = duration;
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(long totalSteps) {
        this.totalSteps = totalSteps;
    }

    public long getDownstairsSteps() {
        return downstairsSteps;
    }

    public void setDownstairsSteps(long downstairsSteps) {
        this.downstairsSteps = downstairsSteps;
    }

    public long getUpstairsSteps() {
        return upstairsSteps;
    }

    public void setUpstairsSteps(long upstairsSteps) {
        this.upstairsSteps = upstairsSteps;
    }

    public long getWalkingSteps() {
        return walkingSteps;
    }

    public void setWalkingSteps(long walkingSteps) {
        this.walkingSteps = walkingSteps;
    }

    public long getJoggingSteps() {
        return joggingSteps;
    }

    public void setJoggingSteps(long joggingSteps) {
        this.joggingSteps = joggingSteps;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }
}
