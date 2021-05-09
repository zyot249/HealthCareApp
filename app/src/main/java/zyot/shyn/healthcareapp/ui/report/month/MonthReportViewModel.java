package zyot.shyn.healthcareapp.ui.report.month;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

import zyot.shyn.healthcareapp.utils.MyDateTimeUtils;

public class MonthReportViewModel extends ViewModel {
    private MutableLiveData<HashMap<Integer, Float>> activityData;

    private MutableLiveData<String> chosenMonth;

    public MonthReportViewModel() {
        activityData = new MutableLiveData<>();
        activityData.setValue(new HashMap<>());
        chosenMonth = new MutableLiveData<>();
        chosenMonth.setValue(MyDateTimeUtils.getDateStringWithoutDayCurrentDay());
    }

    public LiveData<HashMap<Integer, Float>> getActivityData() {
        return activityData;
    }

    public LiveData<String> getChosenMonth() {
        return chosenMonth;
    }

    public void setActivityData(HashMap<Integer, Float> data) {
        this.activityData.setValue(data);
    }

    public void setChosenMonth(String month) {
        this.chosenMonth.setValue(month);
    }
}