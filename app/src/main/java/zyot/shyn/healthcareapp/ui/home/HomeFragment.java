package zyot.shyn.healthcareapp.ui.home;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.services.StepCountService;
import zyot.shyn.healthcareapp.utils.MyString;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;

    private MaterialCardView weightView;
    private MaterialCardView heightView;

    private TextView weightTxt;
    private TextView heightTxt;
    private TextView footStepsTxt;
    private TextView kcalTxt;
    private TextView timeTxt;
    private TextView spo2Txt;
    private TextView heartRateTxt;

    private Handler handler = new Handler();
    private StepCountService service = null;
    private boolean isBound;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weightView = view.findViewById(R.id.weight_info_view);
        heightView = view.findViewById(R.id.height_info_view);
        weightTxt = view.findViewById(R.id.weight_txt);
        heightTxt = view.findViewById(R.id.height_txt);
        footStepsTxt = view.findViewById(R.id.foot_step_txt);
        kcalTxt = view.findViewById(R.id.calo_txt);
        timeTxt = view.findViewById(R.id.time_txt);
        spo2Txt = view.findViewById(R.id.spo2_txt);
        heartRateTxt = view.findViewById(R.id.heart_rate_txt);

        homeViewModel.getWeight().observe(getViewLifecycleOwner(), s -> weightTxt.setText(s));
        homeViewModel.getHeight().observe(getViewLifecycleOwner(), s -> heightTxt.setText(s));
        homeViewModel.getSteps().observe(getViewLifecycleOwner(), s -> footStepsTxt.setText(s));
        homeViewModel.getCalo().observe(getViewLifecycleOwner(), s -> kcalTxt.setText(s));
        homeViewModel.getTime().observe(getViewLifecycleOwner(), s -> timeTxt.setText(s));
        homeViewModel.getSpo2().observe(getViewLifecycleOwner(), s -> spo2Txt.setText(s));
        homeViewModel.getHeartRate().observe(getViewLifecycleOwner(), s -> heartRateTxt.setText(s));

        heightView.setOnClickListener(this);
        weightView.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getActivity(), StepCountService.class);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        handler.postDelayed(timerRunnable, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(mServiceConnection);
        handler.removeCallbacks(timerRunnable);
    }

    private boolean checkSensors() {
        SensorManager sensorManager;
        Sensor stepDetectorSensor;
        Sensor stepCounter;

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCounter != null) {
            return true;
        } else if (stepDetectorSensor != null) {
            return true;
        } else {
//            notices.setText(" Step Counter and Step Detector Sensor not available \n cannot calculate Steps , Sorry . ");
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weight_info_view:
                getDialogWithInput("Weight", InputType.TYPE_CLASS_NUMBER)
                        .setPositiveButton("OK", (dialog, which) -> {
                            Dialog dialogObj = (Dialog) dialog;
                            EditText weightEt = dialogObj.findViewById(R.id.dialog_et);
                            String weight = weightEt.getText().toString();
                            if (MyString.isNotEmpty(weight)) {
                                homeViewModel.setWeight(weight);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {

                        }).show();
                break;

            case R.id.height_info_view:
                getDialogWithInput("Height", InputType.TYPE_CLASS_NUMBER)
                        .setPositiveButton("OK", (dialog, which) -> {
                            Dialog dialogObj = (Dialog) dialog;
                            EditText weightEt = dialogObj.findViewById(R.id.dialog_et);
                            String height = weightEt.getText().toString();
                            if (MyString.isNotEmpty(height)) {
                                homeViewModel.setHeight(height);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {

                        }).show();
                break;
        }
    }

    public MaterialAlertDialogBuilder getDialogWithInput(String title, int inputType) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        LayoutInflater li = LayoutInflater.from(getContext());
        View dialogLayout = li.inflate(R.layout.dialog_with_et, null);
        TextInputEditText dialogEt = dialogLayout.findViewById(R.id.dialog_et);
        dialogEt.setHint(title);
        dialogEt.setInputType(inputType);

        dialogBuilder.setView(dialogLayout);
        return dialogBuilder;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder_service) {
            StepCountService.MyBinder myBinder = (StepCountService.MyBinder) binder_service;
            service = myBinder.getService();
            isBound = true;
            if (checkSensors())
                service.startForegroundService();
        }
    };

    //Runnable that calculates the elapsed time since the user presses the "start" button
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> data;
            if (isBound) {
                if (isAdded()) {
                    data = service.getData();

                    homeViewModel.setSteps(data.get("steps"));
                    homeViewModel.setTime(data.get("duration"));
                    homeViewModel.setCalo(data.get("caloBurned"));
                    homeViewModel.setSpo2(data.get("relaxTime"));
                    homeViewModel.setHeartRate(data.get("activeTime"));
                }
            }
            handler.postDelayed(this, 1000);
        }
    };
}