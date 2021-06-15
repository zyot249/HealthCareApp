package zyot.shyn.healthcareapp.ui.fragment.family;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import zyot.shyn.healthcareapp.model.User;

public class FamilyViewModel extends ViewModel {
    private ArrayList<String> ids;
    private MutableLiveData<List<String>> memberIds;

    private ArrayList<User> users;
    private MutableLiveData<List<User>> members;

    public FamilyViewModel() {
        ids = new ArrayList<>();
        memberIds = new MutableLiveData<>();
        memberIds.setValue(ids);

        users = new ArrayList<>();
        members = new MutableLiveData<>();
        members.setValue(users);
    }

    public LiveData<List<String>> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        if (memberIds != null) {
            ids.clear();
            ids.addAll(memberIds);
            this.memberIds.setValue(ids);
        }
    }

    public void addMemberId(String id) {
        if (ids.contains(id))
            return;
        ids.add(id);
        memberIds.setValue(ids);
    }

    public void removeMemberId(String id) {
        if (!ids.contains(id))
            return;
        ids.remove(id);
        memberIds.setValue(ids);
    }

    public LiveData<List<User>> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        if (members != null) {
            users.clear();
            users.addAll(members);
            this.members.setValue(users);
        }
    }

    public void addMember(User user) {
        if (users.contains(user))
            return;
        users.add(user);
        members.setValue(users);
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
