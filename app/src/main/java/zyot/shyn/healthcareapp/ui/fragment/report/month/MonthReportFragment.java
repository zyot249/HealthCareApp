package zyot.shyn.healthcareapp.ui.fragment.report.month;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.Resources;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import zyot.shyn.HumanActivity;
import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.entity.UserStepEntity;
import zyot.shyn.healthcareapp.pojo.ActivityDurationPOJO;
import zyot.shyn.healthcareapp.repository.UserActivityRepository;
import zyot.shyn.healthcareapp.utils.MyDateTimeUtils;

public class MonthReportFragment extends Fragment {
    private static final String TAG = MonthReportFragment.class.getSimpleName();

    private MonthReportViewModel mViewModel;

    private TextView monthChosenTxt;
    private TextView footStepsTxt;
    private TextView kcalTxt;
    private TextView distanceTxt;
    private PieChart activityPieChart;
    private LineChart stepLineChart;

    int mYear, mMonth, mDay;

    private FirebaseUser firebaseUser;

    private UserActivityRepository userActivityRepository;

    public static MonthReportFragment newInstance() {
        return new MonthReportFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        return inflater.inflate(R.layout.fragment_month_report, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userActivityRepository = UserActivityRepository.getInstance(getActivity().getApplication());
        mViewModel = new ViewModelProvider(this).get(MonthReportViewModel.class);
        mViewModel.getChosenMonth().observe(getViewLifecycleOwner(), s -> {
            monthChosenTxt.setText(s);
            Calendar calendar = MyDateTimeUtils.getCalendarOfTimestamp(MyDateTimeUtils.getTimeFromDateStringWithoutDay(s));
            loadActivityData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
            loadStepData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        });
        mViewModel.getSteps().observe(getViewLifecycleOwner(), s -> footStepsTxt.setText(s));
        mViewModel.getCalo().observe(getViewLifecycleOwner(), s -> kcalTxt.setText(s));
        mViewModel.getDistance().observe(getViewLifecycleOwner(), s -> distanceTxt.setText(s));
        mViewModel.getActivityData().observe(getViewLifecycleOwner(), data -> {
            if (data.size() > 0) {
                ArrayList<PieEntry> entries = new ArrayList<>();
                for (Map.Entry<Integer, Float> entry : data.entrySet())
                    entries.add(new PieEntry(entry.getValue(), HumanActivity.getHumanActivity(entry.getKey()).toString()));
                PieDataSet dataSet = new PieDataSet(entries, "");
                dataSet.setDrawIcons(false);
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);
                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

                PieData pieData = new PieData(dataSet);
                pieData.setValueFormatter(new PercentFormatter());
                pieData.setValueTextSize(11f);
                pieData.setValueTextColor(Color.WHITE);

                activityPieChart.setData(pieData);
            } else {
                activityPieChart.setData(null);
            }
            activityPieChart.highlightValues(null);
            activityPieChart.invalidate();
        });
        mViewModel.getStepData().observe(getViewLifecycleOwner(), data -> {
            LineDataSet stepSet, caloSet, distanceSet;
            ArrayList<Entry> stepList = new ArrayList<>();
            ArrayList<Entry> caloList = new ArrayList<>();
            ArrayList<Entry> distanceList = new ArrayList<>();
            Set<Integer> daysHadData = new HashSet<>();

            float steps, calo, distance;
            steps = calo = distance = 0;
            if (data.size() > 0) {
                for (UserStepEntity userStepEntity : data) {
                    int dayOfMonth = MyDateTimeUtils.getDayOfMonth(userStepEntity.getTimestamp());
                    daysHadData.add(dayOfMonth);
                    steps += userStepEntity.getAmountOfSteps();
                    calo += userStepEntity.getTotalCaloriesBurned();
                    distance += userStepEntity.getDistance();

                    stepList.add(new Entry(dayOfMonth, userStepEntity.getAmountOfSteps()));
                    caloList.add(new Entry(dayOfMonth, userStepEntity.getTotalCaloriesBurned()));
                    distanceList.add(new Entry(dayOfMonth, userStepEntity.getDistance()));
                }
                Calendar calendar = MyDateTimeUtils.getCalendarOfTime(mYear, mMonth, mDay);
                int numOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int day = 1; day <= numOfDays; day++)
                    if (!daysHadData.contains(day)) {
                        stepList.add(new Entry(day, 0));
                        caloList.add(new Entry(day, 0));
                        distanceList.add(new Entry(day, 0));
                    }
                Collections.sort(stepList, (o1, o2) -> Float.compare(o1.getX(), o2.getX()));
                Collections.sort(caloList, (o1, o2) -> Float.compare(o1.getX(), o2.getX()));
                Collections.sort(distanceList, (o1, o2) -> Float.compare(o1.getX(), o2.getX()));
            }
            if (stepLineChart.getData() != null &&
                    stepLineChart.getData().getDataSetCount() > 0) {
                stepSet = (LineDataSet) stepLineChart.getData().getDataSetByIndex(0);
                caloSet = (LineDataSet) stepLineChart.getData().getDataSetByIndex(1);
                distanceSet = (LineDataSet) stepLineChart.getData().getDataSetByIndex(2);
                stepSet.setValues(stepList);
                caloSet.setValues(caloList);
                distanceSet.setValues(distanceList);
                stepLineChart.getData().notifyDataChanged();
                stepLineChart.notifyDataSetChanged();
                stepLineChart.invalidate();
            }
            mViewModel.setSteps(String.format("%.0f", steps));
            mViewModel.setDistance(String.format("%.2f", distance));
            mViewModel.setCalo(String.format("%.2f", calo));
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        monthChosenTxt = view.findViewById(R.id.month_chosen_txt);
        footStepsTxt = view.findViewById(R.id.footstep_txt);
        kcalTxt = view.findViewById(R.id.calo_txt);
        distanceTxt = view.findViewById(R.id.distance_txt);
        activityPieChart = view.findViewById(R.id.month_activity_pie_chart);
        stepLineChart = view.findViewById(R.id.month_footsteps_line_chart);
        configureActivityPieChart();
        configureStepLineChart();

        monthChosenTxt.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_DARK,
                    (view1, year, monthOfYear, dayOfMonth) -> {
                        mDay = dayOfMonth;
                        mMonth = monthOfYear;
                        mYear = year;
                        mViewModel.setChosenMonth(MyDateTimeUtils.getDateStringWithoutDay(year, monthOfYear + 1, dayOfMonth));
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
            datePickerDialog.show();
        });
    }

    private void configureStepLineChart() {
        stepLineChart.getDescription().setEnabled(false);
        stepLineChart.setTouchEnabled(true);
        stepLineChart.setDragDecelerationFrictionCoef(0.9f);
        stepLineChart.setDragEnabled(true);
        stepLineChart.setScaleEnabled(true);
        stepLineChart.setDrawGridBackground(false);
        stepLineChart.setHighlightPerDragEnabled(true);
        stepLineChart.setPinchZoom(false);
        stepLineChart.setDoubleTapToZoomEnabled(false);

        CustomMarkerView mv = new CustomMarkerView (this.getContext(), R.layout.marker_view);
        stepLineChart.setMarker(mv);

        // get the legend
        Legend l = stepLineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis = stepLineChart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = stepLineChart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);

        stepLineChart.getAxisRight().setEnabled(false);

        LineDataSet stepSet, caloSet, distanceSet;
        stepSet = new LineDataSet(new ArrayList<>(), "Footstep");
        stepSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        stepSet.setColor(ColorTemplate.getHoloBlue());
        stepSet.setCircleColor(Color.WHITE);
        stepSet.setLineWidth(2f);
        stepSet.setCircleRadius(3f);
        stepSet.setFillAlpha(65);
        stepSet.setFillColor(ColorTemplate.getHoloBlue());
        stepSet.setHighLightColor(Color.rgb(244, 117, 117));
        stepSet.setDrawCircleHole(false);
        stepSet.setDrawValues(false);

        caloSet = new LineDataSet(new ArrayList<>(), "Calorie");
        caloSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        caloSet.setColor(Color.RED);
        caloSet.setCircleColor(Color.WHITE);
        caloSet.setLineWidth(2f);
        caloSet.setCircleRadius(3f);
        caloSet.setFillAlpha(65);
        caloSet.setFillColor(Color.RED);
        caloSet.setDrawCircleHole(false);
        caloSet.setHighLightColor(Color.rgb(244, 117, 117));
        caloSet.setDrawValues(false);

        distanceSet = new LineDataSet(new ArrayList<>(), "Distance");
        distanceSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        distanceSet.setColor(Color.YELLOW);
        distanceSet.setCircleColor(Color.WHITE);
        distanceSet.setLineWidth(2f);
        distanceSet.setCircleRadius(3f);
        distanceSet.setFillAlpha(65);
        distanceSet.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
        distanceSet.setDrawCircleHole(false);
        distanceSet.setHighLightColor(Color.rgb(244, 117, 117));
        distanceSet.setDrawValues(false);

        LineData data = new LineData(stepSet, caloSet, distanceSet);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.2f", value);
            }
        });

        // set data
        stepLineChart.setData(data);
        stepLineChart.invalidate();
    }

    private void configureActivityPieChart() {
        // get the legend
        Legend l = activityPieChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        activityPieChart.setNoDataText("No data");
        activityPieChart.setNoDataTextColor(Color.WHITE);
        activityPieChart.setUsePercentValues(true);
        activityPieChart.setDrawHoleEnabled(false);
        activityPieChart.setTransparentCircleRadius(61f);
        activityPieChart.setDrawCenterText(false);
        activityPieChart.setRotationAngle(0);
        activityPieChart.setRotationEnabled(true);
        activityPieChart.setHighlightPerTapEnabled(true);
        activityPieChart.setDrawEntryLabels(false);
        activityPieChart.getDescription().setEnabled(false);
        activityPieChart.setRotationEnabled(false);
    }

    private void loadActivityData(int year, int month) {
        userActivityRepository.getUserActivityDurationInMonth(firebaseUser.getUid(), year, month)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    Log.d(TAG, "Activity data size " + data.size());
                    HashMap<Integer, Float> userActivityData = new HashMap<>();
                    for (ActivityDurationPOJO activityDuration : data) {
                        if (activityDuration.getActivity() >= 0)
                            userActivityData.put(activityDuration.getActivity(), (float) activityDuration.getTotalduration() / 1000);
                    }
                    mViewModel.setActivityData(userActivityData);
                }, err -> Log.d(TAG, "error: " + err.getMessage()));
    }

    private void loadStepData(int year, int month) {
        userActivityRepository.getUserStepDataInMonth(firebaseUser.getUid(), year, month)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    Log.d(TAG, "Step data size " + data.size());
                    mViewModel.setStepData(new ArrayList<>(data));
                }, err -> Log.d(TAG, "error: " + err.getMessage()));
    }
}