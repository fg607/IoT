package com.hardwork.fg607.iot.model;

import com.hardwork.fg607.iot.utils.IoTUtils;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by fg607 on 16-7-28.
 */
public class TimeTask extends SugarRecord{

    @Unique
    long taskId;
    int deviceId;
    int timerId;
    String deviceName;
    int hour;
    int minute;
    int second;
    int activatedDays;
    boolean switchState;
    boolean activated;

    public TimeTask(){

    }

    public TimeTask(boolean activated){

        //将字段taskID设置为字段id的值（id为自增长）
        if(TimeTask.count(TimeTask.class)>0){

            this.taskId = TimeTask.last(TimeTask.class).getId()+1;
        }else {

            this.taskId = 1;
        }

        this.deviceId = -1;
        this.deviceName = null;
        this.hour = -1;
        this.minute = -1;
        this.second = -1;
        this.activatedDays = 0;
        this.switchState = true;
        this.activated = activated;
    }


    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public int getDeviceId(){

        return deviceId;
    }

    public void setDeviceId(int deviceId){

        this.deviceId = deviceId;
    }

    public int getTimerId() {
        return timerId;
    }

    public void setTimerId(int timerId) {
        this.timerId = timerId;
    }

    public String getDeviceName(){

        return deviceName;
    }

    public void setDeviceName(String deviceName){

        this.deviceName = deviceName;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getActivatedDays() {
        return activatedDays;
    }

    public void setActivatedDays(int activatedDays) {
        this.activatedDays = activatedDays;
    }

    public boolean getSwitchState() {
        return switchState;
    }

    public void setSwitchState(boolean switchState) {
        this.switchState = switchState;
    }

    public boolean isActivated(){
        return activated;
    }

    public void setActivated(boolean activated){

        this.activated = activated;
    }

    public String getTime(){

        String HH = hour>9?""+hour:"0"+hour;
        String mm = minute>9?""+minute:"0"+minute;
        return HH+":"+mm;
    }

    public long getInterval(){

        long interval = 0;

        Date date = new Date(System.currentTimeMillis());

        long now = date.getHours()*60*60*1000+date.getMinutes()*60*1000;

        long alarmTime = hour*60*60*1000+minute*60*1000;

        if(alarmTime>now){

            interval = alarmTime - now;

        }else {
            //如果设置的时间比当前时间早，时间间隔加上24小时
           interval = 24*60*60*1000+alarmTime-now;
        }

        return interval;
    }

    public String getActivatedDayStr(){

        byte[] daybyte = IoTUtils.intToByteArray(activatedDays);

        StringBuilder builder = new StringBuilder();

        for(int i = 6;i>=0; i--){

            //daybyte[3]为int的低４位
            if((daybyte[3]&1)>0){

                builder.append((i+1)+",");

            }

            daybyte[3] = (byte) (daybyte[3] >> 1);
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return "TimeTask{" +
                "deviceId=" + deviceId +
                "deviceName=" + deviceName +
                "hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                ", activatedDays=" + activatedDays +
                ", switchState=" + switchState +
                ", activated=" + activated +
                '}';
    }
}
