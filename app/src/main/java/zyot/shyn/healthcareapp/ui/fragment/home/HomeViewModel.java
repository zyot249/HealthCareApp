package zyot.shyn.healthcareapp.ui.fragment.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<String> weight;
    private MutableLiveData<String> height;

    private MutableLiveData<String> steps;
    private MutableLiveData<String> calo;
    private MutableLiveData<String> distance;

    private MutableLiveData<String> spo2;
    private MutableLiveData<String> heartRate;

    private MutableLiveData<String> curState;
    private MutableLiveData<HashMap<Float, Integer>> activityData;

    public HomeViewModel() {
        weight = new MutableLiveData<>();
        weight.setValue("0");
        height = new MutableLiveData<>();
        height.setValue("0");
        steps = new MutableLiveData<>();
        steps.setValue("0");
        calo = new MutableLiveData<>();
        calo.setValue("0");
        distance = new MutableLiveData<>();
        distance.setValue("0");
        spo2 = new MutableLiveData<>();
        spo2.setValue("0");
        heartRate = new MutableLiveData<>();
        heartRate.setValue("0");
        curState = new MutableLiveData<>();
        curState.setValue("");
        activityData = new MutableLiveData<>();
        activityData.setValue(new HashMap<>());
    }

    public LiveData<String> getWeight() {
        return weight;
    }

    public LiveData<String> getHeight() {
        return height;
    }

    public LiveData<String> getSteps() {
        return steps;
    }

    public LiveData<String> getCalo() {
        return calo;
    }

    public LiveData<String> getDistance() {
        return distance;
    }

    public LiveData<String> getSpo2() {
        return spo2;
    }

    public LiveData<String> getHeartRate() {
        return heartRate;
    }

    public LiveData<HashMap<Float, Integer>> getActivityData() {
        return activityData;
    }

    public LiveData<String> getCurState() {
        return curState;
    }

    public void setWeight(String w) {
        weight.setValue(w);
    }

    public void setHeight(String h) {
        height.setValue(h);
    }

    public void setSteps(String steps) {
        this.steps.setValue(steps);
    }

    public void setCalo(String calo) {
        this.calo.setValue(calo);
    }

    public void setDistance(String distance) {
        this.distance.setValue(distance);
    }

    public void setSpo2(String spo2) {
        this.spo2.setValue(spo2);
    }

    public void setHeartRate(String heartRate) {
        this.heartRate.setValue(heartRate);
    }

    public void setActivityData(HashMap<Float, Integer> data) {
        this.activityData.setValue(data);
    }

    public void setCurState(String state) {
        this.curState.setValue(state);
    }
}