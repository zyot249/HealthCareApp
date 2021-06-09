package zyot.shyn.healthcareapp.ui.fragment.report.month;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;

import zyot.shyn.healthcareapp.entity.UserStepEntity;
import zyot.shyn.healthcareapp.utils.MyDateTimeUtils;

public class MonthReportViewModel extends ViewModel {
    private MutableLiveData<String> chosenMonth;

    private MutableLiveData<String> steps;
    private MutableLiveData<String> calo;
    private MutableLiveData<String> distance;

    private MutableLiveData<HashMap<Integer, Float>> activityData;

    private MutableLiveData<ArrayList<UserStepEntity>> stepData;

    public MonthReportViewModel() {
        activityData = new MutableLiveData<>();
        activityData.setValue(new HashMap<>());
        stepData = new MutableLiveData<>();
        stepData.setValue(new ArrayList<>());
        steps = new MutableLiveData<>();
        steps.setValue("0");
        calo = new MutableLiveData<>();
        calo.setValue("0");
        distance = new MutableLiveData<>();
        distance.setValue("0");
        chosenMonth = new MutableLiveData<>();
        chosenMonth.setValue(MyDateTimeUtils.getDateStringWithoutDayCurrentDay());
    }

    public LiveData<HashMap<Integer, Float>> getActivityData() {
        return activityData;
    }

    public LiveData<String> getChosenMonth() {
        return chosenMonth;
    }

    public LiveData<ArrayList<UserStepEntity>> getStepData() {
        return stepData;
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

    public void setActivityData(HashMap<Integer, Float> data) {
        this.activityData.setValue(data);
    }

    public void setChosenMonth(String month) {
        this.chosenMonth.setValue(month);
    }

    public void setStepData(ArrayList<UserStepEntity> data) {
        this.stepData.setValue(data);
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
}