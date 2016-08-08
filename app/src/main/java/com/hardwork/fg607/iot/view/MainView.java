package com.hardwork.fg607.iot.view;

import com.hardwork.fg607.iot.model.Device;
import com.hardwork.fg607.iot.model.TimeTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fg607 on 16-7-27.
 */
public interface MainView {

    public void showSearching();
    public void hideSearching();
    public void showPanel(List<Device> deviceList);
    public void updateDeviceState(List<Device> deviceList);
    public void showError();
    public void hideError();
    public void showTimeTasks(List<TimeTask> taskList);
    public void showNoTasks();

}
