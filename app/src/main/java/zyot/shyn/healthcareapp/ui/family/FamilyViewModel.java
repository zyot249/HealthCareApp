package zyot.shyn.healthcareapp.ui.family;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FamilyViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public FamilyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
