package zyot.shyn.healthcareapp.event;

import java.util.HashMap;

public class UpdateUIEvent {
    private HashMap<String, String> data;

    public UpdateUIEvent(HashMap<String, String> data) {
        if (data != null)
            this.data = data;
        else this.data = new HashMap<>();
    }

    public HashMap<String, String> getData() {
        return data;
    }
}
