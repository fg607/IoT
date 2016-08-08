package com.hardwork.fg607.iot.presenter;

import com.hardwork.fg607.iot.model.Device;
import com.hardwork.fg607.iot.view.TaskSetView;

import java.util.List;

/**
 * Created by fg607 on 16-7-29.
 */
public class TaskSetPresenter implements Presenter<TaskSetView> {

    private static final String TAG = "TaskSetPresenter";

    private TaskSetView mTaskSetView;

    @Override
    public void attachView(TaskSetView view) {

        mTaskSetView = view;

    }


    public void getDevices(){

        List<Device> deviceList = Device.listAll(Device.class);

        if (deviceList != null && mTaskSetView != null) {

            mTaskSetView.loadDevices(deviceList);
        }
    }


}
