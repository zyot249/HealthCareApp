package zyot.shyn.healthcareapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.model.Exercise;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private Context mContext;
    private List<Exercise> exercises;

    public ExerciseAdapter(Context mContext, List<Exercise> exercises) {
        this.mContext = mContext;
        this.exercises = exercises;
    }

    @NonNull
    @NotNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View item = inflater.inflate(R.layout.list_item_exercise, parent, false);
        ExerciseViewHolder exerciseViewHolder = new ExerciseViewHolder(item);
        return exerciseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExerciseViewHolder holder, int position) {
        final Exercise exercise = exercises.get(position);
        holder.titleTxt.setText(exercise.getName());
        holder.exerciseImg.setImageResource(exercise.getDemoImg());
        holder.itemView.setOnClickListener(v -> {
            AlertDialog alertDialog = getTutorialDialog(exercise.getName(), exercise.getDescription(), exercise.getDemoImg()).show();
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            alertDialog.getWindow().setAttributes(layoutParams);
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTxt;
        private ImageView exerciseImg;

        public ExerciseViewHolder(View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.title_txt);
            exerciseImg = itemView.findViewById(R.id.exercise_img);
        }
    }

    public MaterialAlertDialogBuilder getTutorialDialog(String name, String des, int img) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(mContext);
        LayoutInflater li = LayoutInflater.from(mContext);
        View dialogLayout = li.inflate(R.layout.dialog_exercise_tutorial, null);
        TextView exerciseNameTxt = dialogLayout.findViewById(R.id.exercise_name_txt);
        TextView exerciseDesTxt = dialogLayout.findViewById(R.id.exercise_des_txt);
        ImageView exerciseImg = dialogLayout.findViewById(R.id.exercise_img);

        exerciseNameTxt.setText(name);
        exerciseDesTxt.setText(des);
        exerciseImg.setImageResource(img);
        dialogBuilder.setView(dialogLayout);
        return dialogBuilder;
    }
}
