package zyot.shyn.healthcareapp.ui.fragment.home;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import zyot.shyn.HumanActivity;
import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.entity.UserHeightEntity;
import zyot.shyn.healthcareapp.entity.UserWeightEntity;
import zyot.shyn.healthcareapp.event.UpdateUIEvent;
import zyot.shyn.healthcareapp.repository.UserActivityRepository;
import zyot.shyn.healthcareapp.service.SuperviseHumanActivityService;
import zyot.shyn.healthcareapp.utils.MyDateTimeUtils;
import zyot.shyn.healthcareapp.utils.MyStringUtils;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private HomeViewModel homeViewModel;

    private MaterialCardView weightView;
    private MaterialCardView heightView;

    private TextView weightTxt;
    private TextView heightTxt;
    private TextView footStepsTxt;
    private TextView kcalTxt;
    private TextView distanceTxt;
    private TextView spo2Txt;
    private TextView heartRateTxt;

    private CircleImageView stateImg;

    private LineChart activityLineChart;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    private UserActivityRepository userActivityRepository;

    private SuperviseHumanActivityService service = null;
    private boolean isBound;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
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
        distanceTxt = view.findViewById(R.id.distance_txt);
        spo2Txt = view.findViewById(R.id.spo2_txt);
        heartRateTxt = view.findViewById(R.id.heart_rate_txt);
        stateImg = view.findViewById(R.id.state_img);
        activityLineChart = view.findViewById(R.id.activity_line_chart);
        configureLineChart();

        heightView.setOnClickListener(this);
        weightView.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userActivityRepository = UserActivityRepository.getInstance(getActivity().getApplication());

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getWeight().observe(getViewLifecycleOwner(), s -> weightTxt.setText(s));
        homeViewModel.getHeight().observe(getViewLifecycleOwner(), s -> heightTxt.setText(s));
        homeViewModel.getSteps().observe(getViewLifecycleOwner(), s -> footStepsTxt.setText(s));
        homeViewModel.getCalo().observe(getViewLifecycleOwner(), s -> kcalTxt.setText(s));
        homeViewModel.getDistance().observe(getViewLifecycleOwner(), s -> distanceTxt.setText(s));
        homeViewModel.getSpo2().observe(getViewLifecycleOwner(), s -> spo2Txt.setText(s));
        homeViewModel.getHeartRate().observe(getViewLifecycleOwner(), s -> heartRateTxt.setText(s));
        homeViewModel.getCurState().observe(getViewLifecycleOwner(), s -> {
            if (s.equals("UNKNOWN"))
                stateImg.setImageResource(R.mipmap.unknown);
            else if (s.equals("STANDING"))
                stateImg.setImageResource(R.mipmap.standing);
            else if (s.equals("BIKING"))
                stateImg.setImageResource(R.mipmap.biking);
            else if (s.equals("SITTING"))
                stateImg.setImageResource(R.mipmap.sitting);
            else if (s.equals("UPSTAIRS"))
                stateImg.setImageResource(R.mipmap.upstairs);
            else if (s.equals("DOWNSTAIRS"))
                stateImg.setImageResource(R.mipmap.downstairs);
            else if (s.equals("JOGGING"))
                stateImg.setImageResource(R.mipmap.jogging);
            else if (s.equals("WALKING"))
                stateImg.setImageResource(R.mipmap.walking);
        });
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
        loadDataInDay();
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getActivity(), SuperviseHumanActivityService.class);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(mServiceConnection);
        EventBus.getDefault().unregister(this);
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
                            float w = Float.parseFloat(weight);
                            if (MyStringUtils.isNotEmpty(weight)) {
                                homeViewModel.setWeight(String.format("%.1f", w));
                                UserWeightEntity userWeightEntity = new UserWeightEntity(
                                        MyDateTimeUtils.getStartTimeOfCurrentDate(),
                                        firebaseUser.getUid(), w
                                );
                                userActivityRepository.saveUserWeight(userWeightEntity)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {}, err -> Log.e(TAG, "Error: " + err.getMessage()));
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
                            float h = Float.parseFloat(height);
                            if (MyStringUtils.isNotEmpty(height)) {
                                homeViewModel.setHeight(String.format("%.1f", h));
                                UserHeightEntity userHeightEntity = new UserHeightEntity(
                                        MyDateTimeUtils.getStartTimeOfCurrentDate(),
                                        firebaseUser.getUid(), h
                                );
                                userActivityRepository.saveUserHeight(userHeightEntity)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {}, err -> Log.e(TAG, "Error: " + err.getMessage()));
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

    private void configureLineChart() {
        XAxis xAxis = activityLineChart.getXAxis();
        xAxis.setLabelCount(6, true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setSpaceMax(5);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");

            @Override
            public String getFormattedValue(float value) {
                long startTimeOfDate = MyDateTimeUtils.getStartTimeOfCurrentDate();
                long millis = startTimeOfDate + (long) value * 1000L;
                return mFormat.format(new Date(millis));
            }
        });

        YAxis yAxisLeft = activityLineChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setAxisMaximum(6);
        yAxisLeft.setGranularity(1);
        yAxisLeft.setGranularityEnabled(true);
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

    public void loadDataInDay() {
        userActivityRepository.getUserWeightRecent(firebaseUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((data) -> {
                    if (data != null) {
                        homeViewModel.setWeight(String.format("%.1f", data.getWeight()));
                    }
                }, err -> Log.e(TAG, "Error: " + err.getMessage()));
        userActivityRepository.getUserHeightRecent(firebaseUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((data) -> {
                    if (data != null) {
                        homeViewModel.setHeight(String.format("%.1f", data.getHeight()));
                    }
                }, err -> Log.e(TAG, "Error: " + err.getMessage()));
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

    @Subscribe
    public void onUpdateUIEvent(UpdateUIEvent event) {
        HashMap<String, String> data;
        if (isBound) {
            if (isAdded()) {
                data = event.getData();

                homeViewModel.setSteps(data.get("steps"));
                homeViewModel.setDistance(data.get("distance"));
                homeViewModel.setCalo(data.get("caloBurned"));
                homeViewModel.setSpo2(data.get("relaxTime"));
                homeViewModel.setHeartRate(data.get("activeTime"));
                homeViewModel.setCurState(data.get("curState"));
                homeViewModel.setActivityData(service.getUserActivityData());
            }
        }
    }
}