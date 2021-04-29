package zyot.shyn.healthcareapp.services;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import zyot.shyn.healthcareapp.models.AccelerationData;

public class StepDetector {

    private static final int WALKING_THRESHOLD = 17;
    private static final int JOGGING_THRESHOLD = 24;
    private static final int RUNNING_THRESHOLD = 30;

    private StepListener stepListener;

    private ArrayList<AccelerationData> newAccelerationDataList;
    private ArrayList<AccelerationData> calculatedList;

    public StepDetector() {
        newAccelerationDataList = new ArrayList<>();
        calculatedList = new ArrayList<>();
    }

    public void registerStepListener(StepListener pStepListener) {
        stepListener = pStepListener;
    }

    public void addAccelerationData(AccelerationData pNewAccelerationData) {
        newAccelerationDataList.add(pNewAccelerationData);

        if (newAccelerationDataList.size() >= 25) {
            handleAccelerationData();
        }
    }

    private void handleAccelerationData() {

        for (int i = 0; i < newAccelerationDataList.size(); i++) {
            AccelerationData accelerationData = newAccelerationDataList.get(i);
            accelerationData = calculateValueAndTime(accelerationData);
            calculatedList.add(accelerationData);
        }

        ArrayList<AccelerationData> highPointList = findHighPoints();
        highPointList = removeNearHighPoints(highPointList);
        examineStepTypeAndSendResponse(highPointList);

        calculatedList.clear();
        newAccelerationDataList.clear();
    }

    private AccelerationData calculateValueAndTime(AccelerationData pAccelerationData) {
        float x = pAccelerationData.getX();
        float y = pAccelerationData.getY();
        float z = pAccelerationData.getZ();

        double vectorLength = Math.sqrt(x * x + y * y + z * z);
        pAccelerationData.setValue(vectorLength);

        long time = pAccelerationData.getTime();
        long timeOffsetToUnix = System.currentTimeMillis() - SystemClock.elapsedRealtime();
        long unixTimestamp = (time / 1000000L) + timeOffsetToUnix;
        pAccelerationData.setTime(unixTimestamp);

        return pAccelerationData;
    }

    private ArrayList<AccelerationData> findHighPoints() {
        ArrayList<AccelerationData> highPointList = new ArrayList<>();
        ArrayList<AccelerationData> aboveWalkingThresholdList = new ArrayList<>();
        boolean wasAboveThreshold = true;
        for (int i = 0; i < calculatedList.size(); i++) {

            AccelerationData calculatedDataSet = calculatedList.get(i);
            if (calculatedDataSet.getValue() > WALKING_THRESHOLD) {
                aboveWalkingThresholdList.add(calculatedDataSet);
                wasAboveThreshold = true;
            } else {
                if (wasAboveThreshold && aboveWalkingThresholdList.size() > 0) {
                    Collections.sort(aboveWalkingThresholdList, new AccelerationDataSorter());
                    highPointList.add(aboveWalkingThresholdList.get(aboveWalkingThresholdList.size() - 1));
                    aboveWalkingThresholdList.clear();
                }
                wasAboveThreshold = false;
            }
        }
        return highPointList;
    }

    private ArrayList<AccelerationData> removeNearHighPoints(ArrayList<AccelerationData> pAccelerationDataList) {
        ArrayList<Integer> wrongHighPointIndexes = new ArrayList<>();
        for (int i = 0; i < pAccelerationDataList.size() - 1; i++) {
            if ((pAccelerationDataList.get(i + 1).getTime() - pAccelerationDataList.get(i).getTime()) < 400) {
                if (pAccelerationDataList.get(i + 1).getValue() < pAccelerationDataList.get(i).getValue()) {
                    wrongHighPointIndexes.add(i + 1);
                } else {
                    wrongHighPointIndexes.add(i);
                }
            }
        }
        for (int i = wrongHighPointIndexes.size() - 1; i >= 0; i--) {
            System.out.println(i);
            pAccelerationDataList.remove(i);
        }
        return pAccelerationDataList;
    }

    private void examineStepTypeAndSendResponse(ArrayList<AccelerationData> pAccelerationDataList) {
        for (int i = 0; i < pAccelerationDataList.size(); i++) {
            AccelerationData highPoint = pAccelerationDataList.get(i);
            if (highPoint.getValue() > RUNNING_THRESHOLD) {
                stepListener.step(highPoint, StepType.RUNNING);
            } else if (highPoint.getValue() > JOGGING_THRESHOLD) {
                stepListener.step(highPoint, StepType.JOGGING);
            } else {
                stepListener.step(highPoint, StepType.WALKING);
            }
        }
    }

    public class AccelerationDataSorter implements Comparator<AccelerationData> {
        @Override
        public int compare(AccelerationData data1, AccelerationData data2) {
            int returnValue = 0;
            if (data1.getValue() < data2.getValue()) {
                returnValue = -1;
            } else if (data1.getValue() > data2.getValue()) {
                returnValue = 1;
            }
            return returnValue;
        }
    }

}


