package zyot.shyn.healthcareapp.model;

public class User {
    private String id;
    private String displayName;
    private int birthYear;
    private String gender;
    private String avatar;

    public User() {
    }

    public User(String id, String displayName, int birthYear, String gender, String avatar) {
        this.id = id;
        this.displayName = displayName;
        this.birthYear = birthYear;
        this.gender = gender;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
