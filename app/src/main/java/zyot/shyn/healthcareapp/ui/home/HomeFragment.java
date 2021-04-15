package zyot.shyn.healthcareapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import zyot.shyn.healthcareapp.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private TextView textView;
    private AppCompatButton btnClickToMe;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        btnClickToMe = root.findViewById(R.id.btnClickToMe);
        btnClickToMe.setOnClickListener(v -> {
             homeViewModel.setText("Hello my friends");
        });
        return root;
    }
}