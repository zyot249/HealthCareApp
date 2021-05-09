package zyot.shyn.healthcareapp.ui.report.month;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import zyot.shyn.HumanActivity;
import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.pojo.ActivityDurationPOJO;
import zyot.shyn.healthcareapp.repository.UserActivityRepository;
import zyot.shyn.healthcareapp.utils.MyDateTimeUtils;

public class MonthReportFragment extends Fragment {
    private static final String TAG = MonthReportFragment.class.getSimpleName();

    private MonthReportViewModel mViewModel;

    private TextView monthChosenTxt;
    private PieChart activityPieChart;

    int mYear;
    int mMonth;
    int mDay;

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
        userActivityRepository = UserActivityRepository.getInstance(getActivity().getApplication());
        mViewModel = new ViewModelProvider(this).get(MonthReportViewModel.class);
        mViewModel.getChosenMonth().observe(getViewLifecycleOwner(), s -> {
            monthChosenTxt.setText(s);
            Calendar calendar = MyDateTimeUtils.getCalendarOfTimestamp(MyDateTimeUtils.getTimeFromDateStringWithoutDay(s));
            loadActivityData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        });
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        monthChosenTxt = view.findViewById(R.id.month_chosen_txt);
        activityPieChart = view.findViewById(R.id.month_activity_pie_chart);
        configureActivityPieChart();

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

    private void configureActivityPieChart() {
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
    }

    private void loadActivityData(int year, int month) {
        userActivityRepository.getUserActivityDurationInMonth(year, month)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    Log.d(TAG, "size " + data.size());
                    HashMap<Integer, Float> userActivityData = new HashMap<>();
                    for (ActivityDurationPOJO activityDuration : data) {
                        if (activityDuration.getActivity() >= 0)
                            userActivityData.put(activityDuration.getActivity(), (float) activityDuration.getTotalduration() / 1000);
                    }
                    mViewModel.setActivityData(userActivityData);
                }, err -> Log.d(TAG, "error: " + err.getMessage()));
    }
}