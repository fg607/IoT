package com.hardwork.fg607.iot.view.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hardwork.fg607.iot.R;
import com.hardwork.fg607.iot.adapter.DevicesGridAdapter;
import com.hardwork.fg607.iot.model.Device;
import com.hardwork.fg607.iot.model.TimeTask;
import com.hardwork.fg607.iot.presenter.TaskSetPresenter;
import com.hardwork.fg607.iot.utils.IoTUtils;
import com.hardwork.fg607.iot.view.TaskSetView;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskSetFragment extends Fragment implements View.OnClickListener,
        DevicesGridAdapter.OnDeviceChoosedListener,TaskSetView,OnDateSetListener {

    private static final String TAG = "TaskSetFragment";

    @Bind(R.id.cancel) TextView mCancelBtn;
    @Bind(R.id.ok) TextView mOkBtn;
    @Bind(R.id.devices) GridView mDevicesGrid;
    @Bind(R.id.tv_time) TextView mTimeTextView;
    @Bind(R.id.monday) ToggleButton mMondayBtn;
    @Bind(R.id.tuesday)ToggleButton mTuesdayBtn;
    @Bind(R.id.wednesday) ToggleButton mWeddayBtn;
    @Bind(R.id.thursday)ToggleButton mThudayBtn;
    @Bind(R.id.friday) ToggleButton mFridayBtn;
    @Bind(R.id.saturday)ToggleButton mSatdayBtn;
    @Bind(R.id.sunday) ToggleButton mSundayBtn;
    @Bind(R.id.checkbox_on) CheckBox mOnCheckBox;
    @Bind(R.id.checkbox_off) CheckBox mOffCheckBox;

    private DevicesGridAdapter mDeviceGridAdapter;
    private TimeTask mTimeTask;
    private boolean mIsNewTask;
    private TimePickerDialog mTimePicker;

    private Activity mContext;

    private TaskSetPresenter mPresenter;

    private int mActivatedDays=0;

    private FragmentManager mFragmentManager;

    public TaskSetFragment() {
        // Required empty public constructor
    }

    public void setTimeTask(TimeTask timeTask){

        mTimeTask = timeTask;

        if(mTimeTask.getDeviceId()==-1){
            mIsNewTask = true;
        }else {
            mIsNewTask = false;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.task_setting_layout,container,false);

        init(view);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;

        mFragmentManager = ((FragmentActivity)activity).getSupportFragmentManager();
    }

    private void init(View view) {

        ButterKnife.bind(this,view);

        mCancelBtn.setOnClickListener(this);
        mOkBtn.setOnClickListener(this);
        mTimeTextView.setOnClickListener(this);
        mOnCheckBox.setOnClickListener(this);
        mOffCheckBox.setOnClickListener(this);



        mDeviceGridAdapter = new DevicesGridAdapter(getActivity(),mTimeTask.getDeviceId());
        mDeviceGridAdapter.setOnDeviceChoosedListener(this);
        mDevicesGrid.setAdapter(mDeviceGridAdapter);


        initTime();

        setTodayOfWeek();

        initSwitchState();

        mPresenter = new TaskSetPresenter();
        mPresenter.attachView(this);
        mPresenter.getDevices();


    }

    private void initSwitchState() {
        
        if(mTimeTask.getSwitchState()){

            mOnCheckBox.setChecked(true);

        }else {

            mOffCheckBox.setChecked(true);
        }
    }

    private void initTime() {

        if (mIsNewTask) {

            Date date = new Date(System.currentTimeMillis());

            mTimeTask.setHour(date.getHours());
            mTimeTask.setMinute(date.getMinutes());
        }

        mTimeTextView.setText(mTimeTask.getTime());
    }

    private void setTodayOfWeek() {

        if(mIsNewTask){

            switch (IoTUtils.getDayOfWeek()){

                case 1:
                    mSundayBtn.setChecked(true);
                    break;
                case 2:
                    mMondayBtn.setChecked(true);
                    break;
                case 3:
                    mTuesdayBtn.setChecked(true);
                    break;
                case 4:
                    mWeddayBtn.setChecked(true);
                    break;
                case 5:
                    mThudayBtn.setChecked(true);
                    break;
                case 6:
                    mFridayBtn.setChecked(true);
                    break;
                case 7:
                    mSatdayBtn.setChecked(true);
                    break;
                default:
                    break;
            }

        }else {

            byte[] daybyte = IoTUtils.intToByteArray(mTimeTask.getActivatedDays());

            if((daybyte[3] & IoTUtils.MONDAY)>0){

                mMondayBtn.setChecked(true);
            }

            if((daybyte[3] & IoTUtils.TUESDAY)>0){

                mTuesdayBtn.setChecked(true);
            }

            if((daybyte[3] & IoTUtils.WEDNESDAY)>0){

                mWeddayBtn.setChecked(true);
            }

            if((daybyte[3] & IoTUtils.THURSDAY)>0){

                mThudayBtn.setChecked(true);
            }

            if((daybyte[3] & IoTUtils.FRIDAY)>0){

                mFridayBtn.setChecked(true);
            }

            if((daybyte[3] & IoTUtils.SATURDAY)>0){

                mSatdayBtn.setChecked(true);
            }

            if((daybyte[3] & IoTUtils.SUNDAY)>0){

                mSundayBtn.setChecked(true);
            }
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.cancel:
                cancel();
                break;
            case R.id.ok:
                ok();
                break;
            case R.id.tv_time:
                showTimePickDlg();
                break;
            case R.id.checkbox_on:
                mOffCheckBox.setChecked(!mOnCheckBox.isChecked());
                mTimeTask.setSwitchState(mOnCheckBox.isChecked());
                break;
            case R.id.checkbox_off:
                mOnCheckBox.setChecked(!mOffCheckBox.isChecked());
                mTimeTask.setSwitchState(mOnCheckBox.isChecked());
                break;
            default:
                break;
        }
    }

    private void showTimePickDlg() {

        mTimePicker = new TimePickerDialog.Builder()
                .setType(Type.HOURS_MINS)
                .setTitleStringId(mContext.getString(R.string.time_picker))
                .setThemeColor(getResources().getColor(R.color.colorPrimary))
                .setCallBack(this)
                .build();

        mTimePicker.show(((FragmentActivity)mContext).getSupportFragmentManager(), "hour_minute");
    }

    private void ok() {

        Fragment fragment = mFragmentManager.findFragmentByTag("MainFragment");

        MainFragment mainFragment = null;

        if(fragment!=null) {

            mainFragment = (MainFragment) fragment;
        }

        if(mainFragment!=null){

            //wifi不可用时,无法更新旧的任务
            if(!mainFragment.checkWifiConnected() && !mIsNewTask && mTimeTask.isActivated()){

                Toast.makeText(mContext,mContext.getString(R.string.update_task_failed),Toast.LENGTH_SHORT).show();

                cancel();

                return;
            }
        }

        saveTimeTask();

        setTimer(mTimeTask);

        cancel();
    }

    private void setTimer(final TimeTask timeTask) {

        Fragment fragment = mFragmentManager.findFragmentByTag("MainFragment");

        if(fragment!=null){

            final MainFragment mainFragment = (MainFragment) fragment;

            if(mainFragment.checkWifiConnected()){

                if(!mIsNewTask && mTimeTask.isActivated()){

                   //取消旧的定时任务
                    mainFragment.getPresenter().cancelTimer(timeTask);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //休眠500ms,防止http频繁访问造成服务器堵塞
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mainFragment.getPresenter().setTimer(timeTask);
                    }
                }).start();


            }else {

                timeTask.setActivated(false);
                timeTask.update();
                mainFragment.getPresenter().loadTimeTask();
            }

        }

    }


    private void saveTimeTask() {

        if(mMondayBtn.isChecked()){

            mActivatedDays+= IoTUtils.MONDAY;
        }

        if(mTuesdayBtn.isChecked()){

            mActivatedDays+= IoTUtils.TUESDAY;
        }

        if(mWeddayBtn.isChecked()){

            mActivatedDays+= IoTUtils.WEDNESDAY;
        }

        if(mThudayBtn.isChecked()){

            mActivatedDays+= IoTUtils.THURSDAY;
        }

        if(mFridayBtn.isChecked()){

            mActivatedDays+= IoTUtils.FRIDAY;
        }

        if(mSatdayBtn.isChecked()){

            mActivatedDays+= IoTUtils.SATURDAY;
        }

        if(mSundayBtn.isChecked()){

            mActivatedDays+= IoTUtils.SUNDAY;
        }

        if(mActivatedDays==0){

            switch (IoTUtils.getDayOfWeek()){

                case 1:
                    mActivatedDays = IoTUtils.SUNDAY;
                    break;
                case 2:
                    mActivatedDays = IoTUtils.MONDAY;
                    break;
                case 3:
                    mActivatedDays = IoTUtils.TUESDAY;
                    break;
                case 4:
                    mActivatedDays = IoTUtils.WEDNESDAY;
                    break;
                case 5:
                    mActivatedDays = IoTUtils.THURSDAY;
                    break;
                case 6:
                    mActivatedDays = IoTUtils.FRIDAY;
                    break;
                case 7:
                    mActivatedDays = IoTUtils.SATURDAY;
                    break;
            }
        }

        mTimeTask.setActivatedDays(mActivatedDays);

        if(mIsNewTask){

            mTimeTask.save();

        }else {

            mTimeTask.update();

        }
    }

    private void cancel() {


        Fragment fragment = mFragmentManager.findFragmentByTag("MainFragment");

        mFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .remove(this)
                .show(fragment).commitAllowingStateLoss();
    }



    @Override
    public void deviceChoosed(Device device) {

        mTimeTask.setDeviceId(device.getDeviceId());
        mTimeTask.setDeviceName(device.getDeviceName());
    }

    @Override
    public void loadDevices(List<Device> deviceList) {

        if(mIsNewTask){

            Device device = deviceList.get(0);

            mTimeTask.setDeviceId(device.getDeviceId());
            mTimeTask.setDeviceName(device.getDeviceName());

            mDeviceGridAdapter.setChoosedDevice(device.getDeviceId());
        }

        mDeviceGridAdapter.setDeviceList(deviceList);
        mDeviceGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {

        Date date = new Date(millseconds);
        mTimeTask.setHour(date.getHours());
        mTimeTask.setMinute(date.getMinutes());
        mTimeTask.setSecond(date.getSeconds());

        mTimeTextView.setText(mTimeTask.getTime());
    }
}
