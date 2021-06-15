package zyot.shyn.healthcareapp.ui.fragment.family;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.model.User;
import zyot.shyn.healthcareapp.model.UserFamily;
import zyot.shyn.healthcareapp.ui.adapter.UserAdapter;
import zyot.shyn.healthcareapp.ui.divider.SampleDivider;
import zyot.shyn.healthcareapp.utils.MyStringUtils;

public class FamilyFragment extends Fragment {
    private static final String TAG = FamilyFragment.class.getSimpleName();

    private FamilyViewModel familyViewModel;
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton addMemberBtn;

    private FirebaseUser fuser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_family, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addMemberBtn = view.findViewById(R.id.add_member_btn);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        SampleDivider divider = new SampleDivider(recyclerView.getContext(), R.drawable.divider_recyclerview);
        recyclerView.addItemDecoration(divider);

        addMemberBtn.setOnClickListener(v -> {
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
            LayoutInflater li = LayoutInflater.from(getContext());
            View dialogLayout = li.inflate(R.layout.dialog_with_et, null);
            TextInputLayout etLayout = dialogLayout.findViewById(R.id.et_layout);
            etLayout.setHint("Email");
            dialogBuilder.setView(dialogLayout);
            dialogBuilder.setTitle("Add a member:")
                    .setPositiveButton("OK", (dialog, which) -> {
                        Dialog dialogObj = (Dialog) dialog;
                        EditText emailEt = dialogObj.findViewById(R.id.dialog_et);
                        String email = emailEt.getText().toString();
                        if (MyStringUtils.isValidEmail(email) && !fuser.getEmail().equals(email)) {
                            Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(email);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        User user = snapshot1.getValue(User.class);
                                        if (user != null) {
                                            familyViewModel.addMemberId(user.getId());
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("memberIds", familyViewModel.getIds());
                                            FirebaseDatabase.getInstance().getReference("Family").child(fuser.getUid()).updateChildren(map);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                        } else {
                            Snackbar.make(addMemberBtn, "Email is not correct", Snackbar.LENGTH_SHORT).show();
                        }

                    }).setNegativeButton("Cancel", (dialog, which) -> {

            }).show();
        });
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        familyViewModel = new ViewModelProvider(this).get(FamilyViewModel.class);
        familyViewModel.getMemberIds().observe(getViewLifecycleOwner(), this::readUsers);
        familyViewModel.getMembers().observe(getViewLifecycleOwner(), users -> {
            userAdapter = new UserAdapter(getContext(), users, familyViewModel);
            recyclerView.setAdapter(userAdapter);
        });
        loadMembers();
    }

    private void readUsers(List<String> ids) {
        if (ids != null && ids.size() > 0) {
            familyViewModel.getUsers().clear();
            for (String memUid : ids) {
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(memUid);
                userReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        User member = snapshot.getValue(User.class);
                        if (member != null)
                            familyViewModel.addMember(member);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void loadMembers() {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference familyReference = FirebaseDatabase.getInstance().getReference("Family").child(fuser.getUid());
        familyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserFamily userFamily = dataSnapshot.getValue(UserFamily.class);
                if (userFamily != null) {
                    familyViewModel.setMemberIds(userFamily.getMemberIds());
                } else {
                    userFamily = new UserFamily(fuser.getUid(), new ArrayList<>());
                    dataSnapshot.getRef().setValue(userFamily);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

