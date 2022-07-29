package com.ensightplus.faas.model.tracking_history;

import java.util.List;

public class TrackingHistory {
    public int deviceId;
    public int vehicleId;
    public String firstIgnition;
    public String lastIgnition;
    public double distance;
    public double workingTime;
    public double idleTime;
    public double stoppedTime;
    public double maxSpeed;
    public double avgSpeed;
    public double startFuelLevel;
    public double endFuelLevel;
    public double startPosition;
    public double endPosition;
    public List<Detail> details;
    public String polyLine;
    public List<Object> events;
    public List<Alarm> alarms;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getFirstIgnition() {
        return firstIgnition;
    }

    public void setFirstIgnition(String firstIgnition) {
        this.firstIgnition = firstIgnition;
    }

    public String getLastIgnition() {
        return lastIgnition;
    }

    public void setLastIgnition(String lastIgnition) {
        this.lastIgnition = lastIgnition;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(double workingTime) {
        this.workingTime = workingTime;
    }

    public double getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(double idleTime) {
        this.idleTime = idleTime;
    }

    public double getStoppedTime() {
        return stoppedTime;
    }

    public void setStoppedTime(double stoppedTime) {
        this.stoppedTime = stoppedTime;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public double getStartFuelLevel() {
        return startFuelLevel;
    }

    public void setStartFuelLevel(double startFuelLevel) {
        this.startFuelLevel = startFuelLevel;
    }

    public double getEndFuelLevel() {
        return endFuelLevel;
    }

    public void setEndFuelLevel(double endFuelLevel) {
        this.endFuelLevel = endFuelLevel;
    }

    public double getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(double startPosition) {
        this.startPosition = startPosition;
    }

    public double getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(double endPosition) {
        this.endPosition = endPosition;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    public String getPolyLine() {
        return polyLine;
    }

    public void setPolyLine(String polyLine) {
        this.polyLine = polyLine;
    }

    public List<Object> getEvents() {
        return events;
    }

    public void setEvents(List<Object> events) {
        this.events = events;
    }

    public List<Alarm> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
    }
}


