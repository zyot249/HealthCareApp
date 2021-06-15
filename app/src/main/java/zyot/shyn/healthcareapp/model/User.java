package zyot.shyn.healthcareapp.model;

import androidx.annotation.Nullable;

public class User {
    private String id;
    private String email;
    private String displayName;
    private int birthYear;
    private String gender;
    private String avatar;

    public User() {
    }

    public User(String id, String email, String displayName, int birthYear, String gender, String avatar) {
        this.id = id;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof User)) {
            return false;
        }
        User user = (User) obj;
        return (this.id.equals(user.getId())
        && this.email.equals(user.getEmail())
        && this.avatar.equals(user.getAvatar())
        && this.birthYear == user.getBirthYear()
        && this.displayName.equals(user.getDisplayName())
        && this.gender.equals(user.getGender()));
    }
}
