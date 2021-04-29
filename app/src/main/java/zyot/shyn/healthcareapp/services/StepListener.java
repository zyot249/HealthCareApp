package zyot.shyn.healthcareapp.services;

import zyot.shyn.healthcareapp.models.AccelerationData;

public interface StepListener {

    void step(AccelerationData accelerationData, StepType stepType);

}
