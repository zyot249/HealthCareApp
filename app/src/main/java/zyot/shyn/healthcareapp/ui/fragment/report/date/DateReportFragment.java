package zyot.shyn.healthcareapp.ui.fragment.report.date;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import zyot.shyn.HumanActivity;
import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.entity.UserActivityEntity;
import zyot.shyn.healthcareapp.repository.UserActivityRepository;
import zyot.shyn.healthcareapp.utils.MyDateTimeUtils;

public class DateReportFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = DateReportFragment.class.getSimpleName();

    private DateReportViewModel mViewModel;
    private UserActivityRepository userActivityRepository;

    private TextView footStepsTxt;
    private TextView kcalTxt;
    private TextView distanceTxt;
    private TextView dateChosenTxt;
    private LineChart activityLineChart;

    private MaterialDatePicker datePicker;

    private FirebaseUser firebaseUser;

    public static DateReportFragment newInstance() {
        return new DateReportFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date_report, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userActivityRepository = UserActivityRepository.getInstance(getActivity().getApplication());
        mViewModel = new ViewModelProvider(this).get(DateReportViewModel.class);
        mViewModel.getChosenDate().observe(getViewLifecycleOwner(), s -> {
            dateChosenTxt.setText(s);
            long time = MyDateTimeUtils.getTimeFromDateStringMedium(s);
            Log.d(TAG, "Chosen Time: " + time);
            loadActivityData(time);
            loadStepData(MyDateTimeUtils.getStartTimeOfDate(time));
        });
        mViewModel.getActivityData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
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
            } else
                activityLineChart.setData(null);

            activityLineChart.moveViewToX(activityLineChart.getXChartMax());
            activityLineChart.invalidate();
        });
        mViewModel.getSteps().observe(getViewLifecycleOwner(), s -> footStepsTxt.setText(s));
        mViewModel.getCalo().observe(getViewLifecycleOwner(), s -> kcalTxt.setText(s));
        mViewModel.getDistance().observe(getViewLifecycleOwner(), s -> distanceTxt.setText(s));
        loadActivityData(MyDateTimeUtils.getCurrentTimestamp());
        loadStepData(MyDateTimeUtils.getStartTimeOfCurrentDate());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateChosenTxt = view.findViewById(R.id.date_chosen_txt);
        footStepsTxt = view.findViewById(R.id.foot_step_txt);
        kcalTxt = view.findViewById(R.id.calo_txt);
        distanceTxt = view.findViewById(R.id.distance_txt);
        activityLineChart = view.findViewById(R.id.date_line_chart);

        MaterialDatePicker.Builder<Long> dateBuilder = MaterialDatePicker.Builder.datePicker();
        dateBuilder.setTitleText("Select a date");
        datePicker = dateBuilder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> mViewModel.setChosenDate(MyDateTimeUtils.getDateStringMedium((Long) selection)));

        dateChosenTxt.setOnClickListener(this);
        configureLineChart();
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
                long startTimeOfDate = MyDateTimeUtils.getStartTimeOfCurrentDate();
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
        activityLineChart.setNoDataText("No Data");

        activityLineChart.invalidate();
    }

    private void loadActivityData(long timestamp) {
        userActivityRepository.getUserActivityDataInDay(firebaseUser.getUid(), timestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                   if (data != null && data.size() > 0) {
                       Log.d(TAG, " activity size " + data.size());
                       long startTimeOfDate = MyDateTimeUtils.getStartTimeOfDate(timestamp);
                       HashMap<Float, Integer> userActivityData = new HashMap<>();
                       for (UserActivityEntity activityEntity : data) {
                           float timePointInDayOfState = (float) (activityEntity.getTimestamp() - startTimeOfDate) / 1000;
                           userActivityData.put(timePointInDayOfState, activityEntity.getActivity());
                       }
                       mViewModel.setActivityData(userActivityData);
                   } else {
                       mViewModel.setActivityData(null);
                   }
                }, err -> Log.e(TAG, "error: " + err.getMessage()));
    }

    private void loadStepData(long startTimeOfDate) {
        userActivityRepository.getUserStepDataInDay(firebaseUser.getUid(), startTimeOfDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (data != null) {
                        mViewModel.setSteps(String.valueOf(data.getAmountOfSteps()));
                        mViewModel.setDistance(String.format("%.2f", data.getDistance()));
                        mViewModel.setCalo(String.format("%.2f", data.getTotalCaloriesBurned()));
                    }
                }, err -> Log.e(TAG, "loadStepData error: " + err.getMessage()), () -> {
                        mViewModel.setSteps("0");
                        mViewModel.setCalo("0");
                        mViewModel.setDistance("0");
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_chosen_txt:
                datePicker.show(getChildFragmentManager(), "MATERIAL_DATE_PICKER");
                break;
        }
    }
}