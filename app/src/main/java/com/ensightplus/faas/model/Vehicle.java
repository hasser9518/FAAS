package com.ensightplus.faas.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Vehicle implements Serializable {

    public int vehicleId;
    public int deviceId;
    public String gpsNumber;
    public String vin;
    public String vehicleLabel;
    public String status;
    public String lastTransmision;
    public String lastPositionDate;
    public int positionId;
    public double latitude;
    public double longitude;
    public double speed;
    public boolean ignition;
    public double fuelLevel;
    public double odometer;
    public double engineHours;
    public double course;
    public int driverId;
    public String driver;
    public List<Object> dtcErrors;
    public String label;

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getGpsNumber() {
        return gpsNumber;
    }

    public void setGpsNumber(String gpsNumber) {
        this.gpsNumber = gpsNumber;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getVehicleLabel() {
        return vehicleLabel;
    }

    public void setVehicleLabel(String vehicleLabel) {
        this.vehicleLabel = vehicleLabel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastTransmision() {
        return lastTransmision;
    }

    public void setLastTransmision(String lastTransmision) {
        this.lastTransmision = lastTransmision;
    }

    public String getLastPositionDate() {
        return lastPositionDate;
    }

    public void setLastPositionDate(String lastPositionDate) {
        this.lastPositionDate = lastPositionDate;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isIgnition() {
        return ignition;
    }

    public void setIgnition(boolean ignition) {
        this.ignition = ignition;
    }

    public double getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public double getOdometer() {
        return odometer;
    }

    public void setOdometer(double odometer) {
        this.odometer = odometer;
    }

    public double getEngineHours() {
        return engineHours;
    }

    public void setEngineHours(double engineHours) {
        this.engineHours = engineHours;
    }

    public double getCourse() {
        return course;
    }

    public void setCourse(double course) {
        this.course = course;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public List<Object> getDtcErrors() {
        return dtcErrors;
    }

    public void setDtcErrors(List<Object> dtcErrors) {
        this.dtcErrors = dtcErrors;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
