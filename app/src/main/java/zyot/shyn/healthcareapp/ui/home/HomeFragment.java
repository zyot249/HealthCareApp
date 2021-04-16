package zyot.shyn.healthcareapp.ui.home;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.utils.MyString;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;

    private MaterialCardView weightView;
    private MaterialCardView heightView;
    private TextView weightTxt;
    private TextView heightTxt;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        weightView = root.findViewById(R.id.weight_info_view);
        heightView = root.findViewById(R.id.height_info_view);
        weightTxt = root.findViewById(R.id.weight_txt);
        heightTxt = root.findViewById(R.id.height_txt);

        homeViewModel.getWeight().observe(getViewLifecycleOwner(), s -> weightTxt.setText(s));
        homeViewModel.getHeight().observe(getViewLifecycleOwner(), s -> heightTxt.setText(s));

        heightView.setOnClickListener(this);
        weightView.setOnClickListener(this);

        return root;
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
}