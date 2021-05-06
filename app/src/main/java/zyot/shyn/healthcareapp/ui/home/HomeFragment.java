package zyot.shyn.healthcareapp.ui.home;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import zyot.shyn.HumanActivity;
import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.service.SuperviseHumanActivityService;
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
    private TextView curStateTxt;

    private Button loadBtn;

    private LineChart activityLineChart;

    private Handler handler = new Handler();
    private SuperviseHumanActivityService service = null;
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
        curStateTxt = view.findViewById(R.id.cur_state_txt);
        activityLineChart = view.findViewById(R.id.activity_line_chart);
        configureLineChart();

        loadBtn = view.findViewById(R.id.load_btn);

        homeViewModel.getWeight().observe(getViewLifecycleOwner(), s -> weightTxt.setText(s));
        homeViewModel.getHeight().observe(getViewLifecycleOwner(), s -> heightTxt.setText(s));
        homeViewModel.getSteps().observe(getViewLifecycleOwner(), s -> footStepsTxt.setText(s));
        homeViewModel.getCalo().observe(getViewLifecycleOwner(), s -> kcalTxt.setText(s));
        homeViewModel.getTime().observe(getViewLifecycleOwner(), s -> timeTxt.setText(s));
        homeViewModel.getSpo2().observe(getViewLifecycleOwner(), s -> spo2Txt.setText(s));
        homeViewModel.getHeartRate().observe(getViewLifecycleOwner(), s -> heartRateTxt.setText(s));
        homeViewModel.getCurState().observe(getViewLifecycleOwner(), s -> curStateTxt.setText(s));
        homeViewModel.getActivityData().observe(getViewLifecycleOwner(), data -> {
            List<Entry> dataList = new ArrayList<>();
            data.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> dataList.add(new Entry(entry.getKey(), entry.getValue())));
            LineDataSet dataSet = new LineDataSet(dataList, "Activity");
            dataSet.setLineWidth(1);
            dataSet.setDrawFilled(true);
            dataSet.setDrawValues(false);
            dataSet.setMode(LineDataSet.Mode.STEPPED);
            LineData lineData = new LineData(dataSet);
            activityLineChart.setData(lineData);
            activityLineChart.moveViewToX(activityLineChart.getXChartMax());
            activityLineChart.invalidate();
        });

        heightView.setOnClickListener(this);
        weightView.setOnClickListener(this);
        loadBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getActivity(), SuperviseHumanActivityService.class);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        handler.postDelayed(timerRunnable, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(mServiceConnection);
        handler.removeCallbacks(timerRunnable);
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
            case R.id.load_btn:
                service.loadData();
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

    private void configureLineChart() {
        XAxis xAxis = activityLineChart.getXAxis();
        xAxis.setLabelCount(6, true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setSpaceMax(5);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");

            @Override
            public String getFormattedValue(float value) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.HOUR, 0);
                long startTimeOfDate = calendar.getTimeInMillis();
                long millis = startTimeOfDate + (long) value * 1000L;
                return mFormat.format(new Date(millis));
            }
        });

        YAxis yAxisLeft = activityLineChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setAxisMaximum(6);
        yAxisLeft.setTextColor(Color.WHITE);
        yAxisLeft.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return HumanActivity.getHumanActivity((int) value).toString();
            }
        });
        activityLineChart.getLegend().setTextColor(Color.WHITE);
        activityLineChart.getDescription().setEnabled(false);
        activityLineChart.getAxisRight().setEnabled(false);
        activityLineChart.getAxisRight().setDrawGridLines(false);
        activityLineChart.enableScroll();
        activityLineChart.setScaleYEnabled(false);

        activityLineChart.invalidate();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder_service) {
            SuperviseHumanActivityService.MyBinder myBinder = (SuperviseHumanActivityService.MyBinder) binder_service;
            service = myBinder.getService();
            isBound = true;
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
                    homeViewModel.setCurState(data.get("curState"));
                    homeViewModel.setActivityData(service.getUserActivityData());
                }
            }
            handler.postDelayed(this, 1000);
        }
    };
}