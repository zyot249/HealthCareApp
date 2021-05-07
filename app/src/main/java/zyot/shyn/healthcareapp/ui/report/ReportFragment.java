package zyot.shyn.healthcareapp.ui.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import zyot.shyn.healthcareapp.R;

public class ReportFragment extends Fragment {

    private ReportViewModel reportViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        reportViewModel =
                new ViewModelProvider(this).get(ReportViewModel.class);
        View root = inflater.inflate(R.layout.fragment_report, container, false);
        return root;
    }
}