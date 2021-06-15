package zyot.shyn.healthcareapp.model;

import java.util.List;

public class UserFamily {
    private String id;
    private List<String> memberIds;

    public UserFamily() {
    }

    public UserFamily(String id, List<String> memberIds) {
        this.id = id;
        this.memberIds = memberIds;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
