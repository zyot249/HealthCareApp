package zyot.shyn.healthcareapp.ui.setting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingViewModel extends ViewModel {

    private MutableLiveData<String> startTimeNightSleep;
    private MutableLiveData<String> endTimeNightSleep;
    private MutableLiveData<String> startTimeNoonSleep;
    private MutableLiveData<String> endTimeNoonSleep;
    private MutableLiveData<String> maxTimeSitting;

    public SettingViewModel() {
        startTimeNightSleep = new MutableLiveData<>();
        startTimeNightSleep.setValue("00:00");
        endTimeNightSleep = new MutableLiveData<>();
        endTimeNightSleep.setValue("07:00");
        startTimeNoonSleep = new MutableLiveData<>();
        startTimeNoonSleep.setValue("11:30");
        endTimeNoonSleep = new MutableLiveData<>();
        endTimeNoonSleep.setValue("13:30");
        maxTimeSitting = new MutableLiveData<>();
        maxTimeSitting.setValue("01:30");
    }

    public LiveData<String> getStartTimeNightSleep() {
        return startTimeNightSleep;
    }

    public void setStartTimeNightSleep(String startTimeNightSleep) {
        this.startTimeNightSleep.setValue(startTimeNightSleep);
    }

    public LiveData<String> getEndTimeNightSleep() {
        return endTimeNightSleep;
    }

    public void setEndTimeNightSleep(String endTimeNightSleep) {
        this.endTimeNightSleep.setValue(endTimeNightSleep);
    }

    public LiveData<String> getStartTimeNoonSleep() {
        return startTimeNoonSleep;
    }

    public void setStartTimeNoonSleep(String startTimeNoonSleep) {
        this.startTimeNoonSleep.setValue(startTimeNoonSleep);
    }

    public LiveData<String> getEndTimeNoonSleep() {
        return endTimeNoonSleep;
    }

    public void setEndTimeNoonSleep(String endTimeNoonSleep) {
        this.endTimeNoonSleep.setValue(endTimeNoonSleep);
    }

    public LiveData<String> getMaxTimeSitting() {
        return maxTimeSitting;
    }

    public void setMaxTimeSitting(String endTimeNoonSleep) {
        this.maxTimeSitting.setValue(endTimeNoonSleep);
    }
}