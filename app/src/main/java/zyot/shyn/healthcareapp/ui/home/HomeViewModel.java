package zyot.shyn.healthcareapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> weight;
    private MutableLiveData<String> height;

    public HomeViewModel() {
        weight = new MutableLiveData<>();
        weight.setValue("0");
        height = new MutableLiveData<>();
        height.setValue("0");
    }

    public LiveData<String> getWeight() {
        return weight;
    }

    public LiveData<String> getHeight() {
        return height;
    }

    public void setWeight(String w) {
        weight.setValue(w);
    }

    public void setHeight(String h) {
        height.setValue(h);
    }
}