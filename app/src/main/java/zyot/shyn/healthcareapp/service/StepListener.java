package zyot.shyn.healthcareapp.service;

import zyot.shyn.healthcareapp.model.AccelerationData;

public interface StepListener {

    void step(AccelerationData accelerationData, StepType stepType);

}
