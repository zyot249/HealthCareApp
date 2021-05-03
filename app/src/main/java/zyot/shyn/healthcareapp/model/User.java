package zyot.shyn.healthcareapp.model;

public class User {
    private String id;
    private String displayName;
    private int age;
    private String gender;

    public User() {
    }

    public User(String id, String displayName, int age, String gender) {
        this.id = id;
        this.displayName = displayName;
        this.age = age;
        this.gender = gender;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
