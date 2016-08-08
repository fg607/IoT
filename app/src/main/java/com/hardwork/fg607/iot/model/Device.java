package com.hardwork.fg607.iot.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by fg607 on 16-7-29.
 */
public class Device extends SugarRecord {

    @Unique
    int deviceId;
    String deviceName;
    boolean deviceState;

    public Device(){

    }

    public Device(int deviceId, String deviceName, boolean deviceState) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceState = deviceState;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(boolean deviceState) {
        this.deviceState = deviceState;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceId=" + deviceId +
                ", deviceName='" + deviceName + '\'' +
                ", deviceState=" + deviceState +
                '}';
    }
}
