package zyot.shyn.healthcareapp.ui.fragment.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.ui.activity.SignInActivity;
import zyot.shyn.healthcareapp.model.User;
import zyot.shyn.healthcareapp.utils.MyDateTimeUtils;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE_OPEN_IMAGE = 1001;

    private SettingViewModel settingViewModel;

    private TextView startTimeNightSleepTxt;
    private TextView endTimeNightSleepTxt;
    private TextView startTimeNoonSleepTxt;
    private TextView endTimeNoonSleepTxt;
    private TextView maxTimeSittingTxt;

    private CircleImageView userAvatar;

    private Button logoutBtn;

    private SharedPreferences sp;

    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbReference;
    private StorageReference stReference;
    private UploadTask uploadTask;

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
        userAvatar = view.findViewById(R.id.user_image);
        logoutBtn = view.findViewById(R.id.logout_btn);

        startTimeNightSleepTxt.setOnClickListener(this);
        endTimeNightSleepTxt.setOnClickListener(this);
        startTimeNoonSleepTxt.setOnClickListener(this);
        endTimeNoonSleepTxt.setOnClickListener(this);
        maxTimeSittingTxt.setOnClickListener(this);
        userAvatar.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
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

        stReference = FirebaseStorage.getInstance().getReference("user_avatar");
        // get user ID from main
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        // get info
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.getAvatar().equals("default"))
                        userAvatar.setImageResource(R.mipmap.ic_launcher_round);
                    else Glide.with(getContext()).load(user.getAvatar()).into(userAvatar);
                } else {
                    userAvatar.setImageResource(R.mipmap.ic_launcher_round);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    private void openImageFolder() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_OPEN_IMAGE);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference storageReference = stReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = storageReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // get link uri
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        // change imageUrl in Users
                        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("avatar", "" + mUri);
                        dbReference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_OPEN_IMAGE) {
                // get uri
                if (data != null && data.getData() != null) {
                    imageUri = data.getData();
                }

                // upload
                if (uploadTask != null && uploadTask.isInProgress())
                    Toast.makeText(getContext(), "Upload in progress!", Toast.LENGTH_SHORT).show();
                else uploadImage();
            }
        }
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
            case R.id.user_image: {
                openImageFolder();
                break;
            }
            case R.id.logout_btn: {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), SignInActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
                break;
            }
        }
    }
}