package zyot.shyn.healthcareapp.model;

public class Exercise {
    private String name;
    private String description;
    private int demoImg;

    public Exercise() {
    }

    public Exercise(String name, String description, int demoImg) {
        this.name = name;
        this.description = description;
        this.demoImg = demoImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDemoImg() {
        return demoImg;
    }

    public void setDemoImg(int demoImg) {
        this.demoImg = demoImg;
    }
}
