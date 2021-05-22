package zyot.shyn.healthcareapp.ui.practice;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.adapter.ExerciseAdapter;
import zyot.shyn.healthcareapp.divider.SampleDivider;
import zyot.shyn.healthcareapp.model.Exercise;
import zyot.shyn.healthcareapp.utils.MyNumberUtils;

public class PracticeFragment extends Fragment {
    private PracticeViewModel practiceViewModel;

    private RecyclerView recyclerView;
    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exercises;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        practiceViewModel = new ViewModelProvider(this).get(PracticeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_practice, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        SampleDivider divider = new SampleDivider(recyclerView.getContext(), R.drawable.divider_recyclerview);
        recyclerView.addItemDecoration(divider);
        exercises = new ArrayList<>();
        exercises.add(new Exercise("Plank", "It’s the best exercise for rock hard abs. A simple exercise for beginners for stronger abs and to strengthen your shoulder, arms and back. It can be done in many variations.\n" +
                "\n" +
                "How to do: get into pushup position with elbows bent in 90 degree keeping your body weight on your forearms. Ensure your body forms a straight line from your head to feet. Hold on to the position as long as you can.\n" +
                "\n", R.mipmap.plank));
        exercises.add(new Exercise("Cross Crunches", "It’s more effective and easy exercise for abs and the oblique muscles. It strengthens the core and strengthens your abdominal muscles.\n" +
                "\n" +
                "How to do: Lie flat on your back. Bend your knees with feet flat on the floor. Place both the hands loosely behind your head. Now, bring your right shoulder and elbow across your body and at the same time bring up your left knee towards your left shoulder. Try to touch your knee with elbow. Go back to the original position and repeat the same with the left elbow.",
                R.mipmap.cross_crunches));
        exercises.add(new Exercise("Side Plank", "It strengthens oblique and helps your build stronger abs.\n" +
                "\n" +
                "How to do: Start on your side with feet together while keeping forearm below your shoulder. Now slowly raise your hip until your body form straight line from head to feet. Hold on to the position and repeat on the other side too!",
                R.mipmap.side_plank));
        exercises.add(new Exercise("Squats", "It helps you get in better shape when included in daily workout plan. Strengthens lower body muscles for men. It can be done in many variations.\n" +
                "\n" +
                "How to do: Start with the hips back with back straight, chest and shoulders up. Bend your knees and squat down keeping them in line with your feet. Start with 25 squats a day and then increase.",
                R.mipmap.squats));
        exercises.add(new Exercise("Lunges", "A very good work out on the core that helps you strengthen your lower body and mobility in your hips.\n" +
                "\n" +
                "How to do: Lower your hip until both keens bent in 90 degree angle. Step forward with one leg while keeping your upper body straight and relaxed. Come back to original position keeping the weight in your heels.",
                R.mipmap.lunges));
        exercises.add(new Exercise("Push-ups", "Basically, push-ups are effective exercise for strengthening chest and arm muscles.\n" +
                "\n" +
                "How to do: Get down on the ground setting your hands shoulder-width apart. Ensure your body forms a straight line. Begin to lower your body keeping elbows close to your body. Push back to starting high plank position.",
                R.mipmap.push_ups));
        exerciseAdapter = new ExerciseAdapter(getContext(), exercises);
        recyclerView.setAdapter(exerciseAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String key = bundle.getString("key");
            if (key != null && !key.equals("")) {
                int randomExercise = MyNumberUtils.randomIntegerBetween(1, exercises.size());
                Exercise exercise = exercises.get(randomExercise - 1);
                exerciseAdapter.getTutorialDialog(exercise.getName(), exercise.getDescription(), exercise.getDemoImg()).show();
            }
        }
    }
}
