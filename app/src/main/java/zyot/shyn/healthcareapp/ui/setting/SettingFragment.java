package zyot.shyn.healthcareapp.ui.setting;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.ui.report.date.DateReportViewModel;
import zyot.shyn.healthcareapp.utils.MyDateTimeUtils;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private SettingViewModel settingViewModel;

    private TextView startTimeNightSleepTxt;
    private TextView endTimeNightSleepTxt;
    private TextView startTimeNoonSleepTxt;
    private TextView endTimeNoonSleepTxt;
    private TextView maxTimeSittingTxt;

    private SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                new ViewModelProvider(this).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startTimeNightSleepTxt = view.findViewById(R.id.start_time_night_sleep);
        endTimeNightSleepTxt = view.findViewById(R.id.end_time_night_sleep);
        startTimeNoonSleepTxt = view.findViewById(R.id.start_time_noon_sleep);
        endTimeNoonSleepTxt = view.findViewById(R.id.end_time_noon_sleep);
        maxTimeSittingTxt = view.findViewById(R.id.max_time_sitting);

        startTimeNightSleepTxt.setOnClickListener(this);
        endTimeNightSleepTxt.setOnClickListener(this);
        startTimeNoonSleepTxt.setOnClickListener(this);
        endTimeNoonSleepTxt.setOnClickListener(this);
        maxTimeSittingTxt.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String startTimeNightSleep = sp.getString("startTimeNightSleep", "00:00");
        String endTimeNightSleep = sp.getString("endTimeNightSleep", "07:00");
        String startTimeNoonSleep = sp.getString("startTimeNoonSleep", "11:30");
        String endTimeNoonSleep = sp.getString("endTimeNoonSleep", "13:30");
        long maxTimeForSitOrStand = sp.getLong("maxTimeSitOrStand", 1000);
        String maxTimeForSitOrStandString = MyDateTimeUtils.getTimeStringDuration(maxTimeForSitOrStand);

        settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        settingViewModel.setMaxTimeSitting(maxTimeForSitOrStandString);
        settingViewModel.setStartTimeNightSleep(startTimeNightSleep);
        settingViewModel.setEndTimeNightSleep(endTimeNightSleep);
        settingViewModel.setStartTimeNoonSleep(startTimeNoonSleep);
        settingViewModel.setEndTimeNoonSleep(endTimeNoonSleep);

        settingViewModel.getStartTimeNightSleep().observe(getViewLifecycleOwner(), s -> {
            startTimeNightSleepTxt.setText(s);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("startTimeNightSleep", s);
            editor.apply();
        });
        settingViewModel.getEndTimeNightSleep().observe(getViewLifecycleOwner(), s -> {
            endTimeNightSleepTxt.setText(s);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("endTimeNightSleep", s);
            editor.apply();
        });
        settingViewModel.getStartTimeNoonSleep().observe(getViewLifecycleOwner(), s -> {
            startTimeNoonSleepTxt.setText(s);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("startTimeNoonSleep", s);
            editor.apply();
        });
        settingViewModel.getEndTimeNoonSleep().observe(getViewLifecycleOwner(), s -> {
            endTimeNoonSleepTxt.setText(s);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("endTimeNoonSleep", s);
            editor.apply();
        });
        settingViewModel.getMaxTimeSitting().observe(getViewLifecycleOwner(), s -> {
            maxTimeSittingTxt.setText(s);
            Date date = MyDateTimeUtils.getDateFromTimeStringDefault(s);
            if (date != null) {
                SharedPreferences.Editor editor = sp.edit();
                long duration = MyDateTimeUtils.getDuration(date.getHours(), date.getMinutes());
                editor.putLong("maxTimeSitOrStand", duration);
                editor.apply();
            }
        });
    }

    private MaterialTimePicker getTimePicker(String title, String curTime) {
        String[] timeSplit = curTime.split(":");
        return new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTitleText(title)
                .setHour(Integer.parseInt(timeSplit[0]))
                .setMinute(Integer.parseInt(timeSplit[1]))
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_time_night_sleep: {
                MaterialTimePicker timePicker = getTimePicker("Select time", startTimeNightSleepTxt.getText().toString());
                timePicker.addOnPositiveButtonClickListener(view -> {
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    String timeString = MyDateTimeUtils.getTimeStringDefault(hour, minute);
                    settingViewModel.setStartTimeNightSleep(timeString);
                });
                timePicker.show(getChildFragmentManager(), "MATERIAL_TIME_PICKER");
                break;
            }
            case R.id.end_time_night_sleep: {
                MaterialTimePicker timePicker = getTimePicker("Select time", endTimeNightSleepTxt.getText().toString());
                timePicker.addOnPositiveButtonClickListener(view -> {
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    String timeString = MyDateTimeUtils.getTimeStringDefault(hour, minute);
                    settingViewModel.setEndTimeNightSleep(timeString);
                });
                timePicker.show(getChildFragmentManager(), "MATERIAL_TIME_PICKER");
                break;
            }
            case R.id.start_time_noon_sleep: {
                MaterialTimePicker timePicker = getTimePicker("Select time", startTimeNoonSleepTxt.getText().toString());
                timePicker.addOnPositiveButtonClickListener(view -> {
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    String timeString = MyDateTimeUtils.getTimeStringDefault(hour, minute);
                    settingViewModel.setStartTimeNoonSleep(timeString);
                });
                timePicker.show(getChildFragmentManager(), "MATERIAL_TIME_PICKER");
                break;
            }
            case R.id.end_time_noon_sleep: {
                MaterialTimePicker timePicker = getTimePicker("Select time", endTimeNoonSleepTxt.getText().toString());
                timePicker.addOnPositiveButtonClickListener(view -> {
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();
                    String timeString = MyDateTimeUtils.getTimeStringDefault(hour, minute);
                    settingViewModel.setEndTimeNoonSleep(timeString);
                });
                timePicker.show(getChildFragmentManager(), "MATERIAL_TIME_PICKER");
                break;
            }
            case R.id.max_time_sitting: {
                String curTime = maxTimeSittingTxt.getText().toString();
                String[] timeSplit = curTime.split(":");
                int hours = Integer.parseInt(timeSplit[0]);
                int minutes = Integer.parseInt(timeSplit[1]);
                TimePickerDialog.OnTimeSetListener myTimeListener = (view, hourOfDay, minute) -> {
                    if (view.isShown()) {
                        String timeString = MyDateTimeUtils.getTimeStringDefault(hourOfDay, minute);
                        settingViewModel.setMaxTimeSitting(timeString);
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hours, minutes, true);
                timePickerDialog.setTitle("Choose hour:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
                break;
            }
        }
    }
}