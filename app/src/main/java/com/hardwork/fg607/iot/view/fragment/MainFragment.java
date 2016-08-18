package com.hardwork.fg607.iot.view.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hardwork.fg607.iot.R;
import com.hardwork.fg607.iot.adapter.DevicePanelAdapter;
import com.hardwork.fg607.iot.adapter.DevicePanelAdapter.OnDeviceItemClickListener;
import com.hardwork.fg607.iot.adapter.TimeTaskAdapter;
import com.hardwork.fg607.iot.adapter.TimeTaskAdapter.OnTaskClickListener;
import com.hardwork.fg607.iot.model.Device;
import com.hardwork.fg607.iot.model.TimeTask;
import com.hardwork.fg607.iot.presenter.MainPresenter;
import com.hardwork.fg607.iot.view.MainView;
import com.jzxiang.pickerview.TimePickerDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements MainView,
       OnTaskClickListener,OnDeviceItemClickListener {

    private static final String TAG = "MainFragment";

    @Bind(R.id.tv_search) TextView mSearchTextView;
    @Bind(R.id.pb_search)
    ProgressBar mProgressBar;
    @Bind(R.id.tv_error) TextView mErrorTextView;
    @Bind(R.id.btn_retry) Button mRetryButton;
    @Bind(R.id.panel)
    LinearLayout mPanelLayout;
    @Bind(R.id.no_task_text) TextView mNoTaskTextView;
    @Bind(R.id.task_list)
    RecyclerView mTimeTaskView;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.devices)
    GridView mDevicesGrid;


    private Activity mContext;

    private MainPresenter mPresenter;
    private TimeTaskAdapter mTaskAdapter;
    private List<TimeTask> mTimeTaskList;

    private DevicePanelAdapter mDeviceAdapter;

    private NetworkInfo mNetworkInfo;
    private ConnectivityManager mConnectivityManager;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_layout,container,false);

        init(view);


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = activity;

        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mPresenter.updateDeviceState();
            }
        }).start();

    }

    //fragment调用hide/show方法不会触发onStop()和onResume()
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(hidden){

        }else {

            ((AppCompatActivity)mContext).getSupportActionBar().show();

            mPresenter.loadTimeTask();

        }
    }

    private void init(View view){

        ButterKnife.bind(this,view);

        mFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                TimeTask timeTask = new TimeTask(true);

                showTaskSetting(timeTask);

            }
        });

        mFab.hide();

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideError();
                mPresenter.searchDevice();

            }
        });

        mTimeTaskView.setLayoutManager(
                new LinearLayoutManager(mTimeTaskView.getContext()));


        mTimeTaskView.setItemAnimator(new DefaultItemAnimator());


        mTimeTaskView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy>0){

                    mFab.hide();

                }else {

                    mFab.show();
                }
            }
        });

        mDeviceAdapter = new DevicePanelAdapter(mContext,this);

        mPresenter = new MainPresenter(mContext);
        mPresenter.attachView(this);
        mPresenter.searchDevice();
        mPresenter.loadTimeTask();


    }

    public MainPresenter getPresenter(){

        return mPresenter;
    }

    private void showTaskSetting(TimeTask timeTask) {

        TaskSetFragment taskSetFragment = new TaskSetFragment();

        taskSetFragment.setTimeTask(timeTask);

        FragmentManager fragmentManager = ((FragmentActivity)mContext).getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .hide(this)
                .add(R.id.container,taskSetFragment,"TaskSetFragment")
                .commitAllowingStateLoss();

        ((AppCompatActivity)mContext).getSupportActionBar().hide();
    }


    @Override
    public void showSearching() {

        mProgressBar.setVisibility(View.VISIBLE);
        mSearchTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearching() {

        mProgressBar.setVisibility(View.GONE);
        mSearchTextView.setVisibility(View.GONE);
    }

    @Override
    public void showPanel(List<Device> deviceList) {

        mDeviceAdapter.setDeviceList(deviceList);

        mDevicesGrid.setAdapter(mDeviceAdapter);

        mPanelLayout.setVisibility(View.VISIBLE);

        mFab.show();

    }

    @Override
    public void updateDeviceState(List<Device> deviceList) {

        mDeviceAdapter.setDeviceList(deviceList);
        mDeviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {

        mErrorTextView.setVisibility(View.VISIBLE);
        mRetryButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideError() {

        mErrorTextView.setVisibility(View.GONE);
        mRetryButton.setVisibility(View.GONE);
    }


    @Override
    public void showTimeTasks(List<TimeTask> taskList) {

        mTimeTaskList = taskList;
        mNoTaskTextView.setText("");
        mTaskAdapter = new TimeTaskAdapter(mContext,taskList);
        mTaskAdapter.setOnItemClickListener(this);
        mTimeTaskView.setAdapter(mTaskAdapter);
    }

    @Override
    public void showNoTasks() {

        mNoTaskTextView.setText(getString(R.string.no_task));
    }

    @Override
    public void taskClick(View view) {

        if(view instanceof ImageView){

            if(checkWifiConnected()){

                TimeTask timeTask = (TimeTask) view.getTag();

                timeTask.setActivated(!timeTask.isActivated());

                timeTask.update();

                mTaskAdapter.notifyDataSetChanged();

                mPresenter.updateTimer(timeTask);

            }else {

                Snackbar.make(getView(),getString(R.string.check_newwork),Snackbar.LENGTH_SHORT).show();

            }



        }else {

            TimeTask timeTask = (TimeTask) view.getTag();

            showTaskSetting(timeTask);

        }
    }

    @Override
    public void taskLongClick(View view) {

        TimeTask timeTask = (TimeTask) view.getTag();

        int position = (int) view.getTag(R.id.position);

        deleteTask(timeTask,position);
    }

    private void deleteTask(final TimeTask timeTask, final int position) {

        SweetAlertDialog confirmDlg = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);

        confirmDlg.setCanceledOnTouchOutside(true);

        confirmDlg.setTitleText(getString(R.string.confirm_delete_task))
                .setConfirmText(getString(R.string.delete_ok))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        if(checkWifiConnected()){

                            //删除前取消激活的任务
                            if(timeTask.isActivated()){

                                mPresenter.cancelTimer(timeTask);
                            }

                            timeTask.delete();

                            mTimeTaskList.remove(position);

                            mTaskAdapter.notifyDataSetChanged();

                        }else {

                            Snackbar.make(MainFragment.this.getView(),getString(R.string.check_newwork),Snackbar.LENGTH_SHORT).show();

                        }


                    }
                })
                .show();

    }

    @Override
    public void deviceItemClick(View view) {

        //如果点击设备名称,进行修改
        if(view instanceof TextView){

            Device device = (Device) view.getTag();

            showEditNameDlg(device);

        }else{

            if(checkWifiConnected()){

                Device device = (Device) view.getTag(R.id.device);

                device.setDeviceState(!device.getDeviceState());

                device.save();

                mDeviceAdapter.notifyDataSetChanged();

                mPresenter.switchDevice(device);

            }else {

                Snackbar.make(getView(),getString(R.string.check_newwork),Snackbar.LENGTH_SHORT).show();

            }

        }
    }

    public boolean checkWifiConnected() {

        mNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mNetworkInfo.isConnected();
    }

    private void showEditNameDlg(final Device device) {

        final String deviceName = device.getDeviceName();

        String text = deviceName!=null?deviceName:getString(R.string.device)+device.getDeviceId();

        View editDlgView = LayoutInflater.from(mContext).inflate(R.layout.edit_dlg_layout,null);

        final EditText editText = (EditText) editDlgView.findViewById(R.id.edit_device_name);

        editText.setHint(text);



        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        AlertDialog editNameDlg = builder.setTitle(getString(R.string.alter_device_name))
                .setView(editDlgView)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String deviceName = editText.getText().toString().trim();

                        if(!TextUtils.isEmpty(deviceName)){

                            device.setDeviceName(deviceName);
                            device.save();
                            mDeviceAdapter.notifyDataSetChanged();

                            updateTimeTaskName(device);
                        }

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .create();

        editNameDlg.show();
    }

    private void updateTimeTaskName(Device device) {

        if(mTimeTaskList==null || mTimeTaskList.size()==0){

            return;
        }

        for(TimeTask task:mTimeTaskList){

            if(task.getDeviceId()==device.getDeviceId()){

                task.setDeviceName(device.getDeviceName());

                task.save();
            }
        }

        mTaskAdapter.notifyDataSetChanged();
    }
}
