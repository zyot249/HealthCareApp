package zyot.shyn.healthcareapp.ui.fragment.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.ui.fragment.report.date.DateReportFragment;
import zyot.shyn.healthcareapp.ui.fragment.report.month.MonthReportFragment;

public class ReportFragment extends Fragment {

    private ReportViewModel reportViewModel;
    private BottomNavigationView bottomNavigationView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        reportViewModel =
                new ViewModelProvider(this).get(ReportViewModel.class);
        View root = inflater.inflate(R.layout.fragment_report, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.date_page:
                    fragment = DateReportFragment.newInstance();
                    loadFragment(fragment);
                    return true;
                case R.id.month_page:
                    fragment = MonthReportFragment.newInstance();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        });
        loadFragment(new DateReportFragment());
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
}