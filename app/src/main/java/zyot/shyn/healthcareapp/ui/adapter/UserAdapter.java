package zyot.shyn.healthcareapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.model.User;
import zyot.shyn.healthcareapp.ui.fragment.family.FamilyViewModel;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context mContext;
    private List<User> users;
    private FamilyViewModel familyViewModel;
    private FirebaseUser fuser;

    public UserAdapter(Context mContext, List<User> users, FamilyViewModel familyViewModel) {
        this.mContext = mContext;
        this.users = users;
        this.familyViewModel = familyViewModel;
        fuser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View item = inflater.inflate(R.layout.list_item_user, parent, false);
        UserViewHolder userViewHolder = new UserViewHolder(item);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final User user = users.get(position);
        holder.tvUsername.setText(user.getDisplayName());
        if (user.getAvatar().equals("default"))
            holder.userImage.setImageResource(R.mipmap.ic_launcher_round);
        else Glide.with(mContext).load(user.getAvatar()).into(holder.userImage);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(mContext);
                dialogBuilder
                        .setTitle("Remove member:")
                        .setMessage("Are you sure to remove this member?")
                        .setPositiveButton("OK", (dialog, which) -> {
                            familyViewModel.removeMemberId(user.getId());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("memberIds", familyViewModel.getIds());
                            FirebaseDatabase.getInstance().getReference("Family").child(fuser.getUid()).updateChildren(map);
                        }).setNegativeButton("Cancel", (dialog, which) -> {

                }).show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private CircleImageView userImage;

        public UserViewHolder(View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            userImage = itemView.findViewById(R.id.userImage);
        }
    }
}
