package zyot.shyn.healthcareapp.ui.fragment.report.date;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

import zyot.shyn.healthcareapp.utils.MyDateTimeUtils;

public class DateReportViewModel extends ViewModel {
    private MutableLiveData<String> chosenDate;

    private MutableLiveData<String> steps;
    private MutableLiveData<String> calo;
    private MutableLiveData<String> distance;

    private MutableLiveData<HashMap<Float, Integer>> activityData;

    public DateReportViewModel() {
        chosenDate = new MutableLiveData<>();
        chosenDate.setValue(MyDateTimeUtils.getDateStringMediumCurrentDay());

        steps = new MutableLiveData<>();
        steps.setValue("0");
        calo = new MutableLiveData<>();
        calo.setValue("0");
        distance = new MutableLiveData<>();
        distance.setValue("0");
        activityData = new MutableLiveData<>();
        activityData.setValue(new HashMap<>());
    }

    public LiveData<String> getChosenDate() {
        return chosenDate;
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

    public LiveData<HashMap<Float, Integer>> getActivityData() {
        return activityData;
    }

    public void setChosenDate(String date) {
        this.chosenDate.setValue(date);
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

    public void setActivityData(HashMap<Float, Integer> data) {
        this.activityData.setValue(data);
    }
}