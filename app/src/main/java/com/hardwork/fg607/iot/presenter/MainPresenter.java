package com.hardwork.fg607.iot.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.hardwork.fg607.iot.model.Device;
import com.hardwork.fg607.iot.model.TimeTask;
import com.hardwork.fg607.iot.utils.HttpUtil;
import com.hardwork.fg607.iot.utils.IoTUtils;
import com.hardwork.fg607.iot.utils.SocketUtils;
import com.hardwork.fg607.iot.view.MainView;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by fg607 on 16-7-27.
 */
public class MainPresenter implements Presenter<MainView> {

    private static final String TAG = "MainPresenter";

    private static final String POWER_ON = "power/on";
    private static final String POWER_OFF = "power/off";

    private static final int SHOW_PANEL = 1;
    private static final int SHOW_ERROR = 2;
    private static final int UPDATE_DEVICE_STATE = 3;
    private static final int UPDATE_TIMER_TASK = 4;
    private static final int PORT = 2016;

    private MainView mView;
    private Context mContext;

    private SharedPreferences mSharedPreferences;

    private WifiManager mWifiManager;

    private Handler mHandler;

    private String mUrl = null;

    private List<Device> mDeviceList;

    private LinkedList<Runnable> mTaskQueue = new LinkedList<>();

    public MainPresenter(final Context context){

        mContext = context;

        mSharedPreferences = context.getSharedPreferences("config",context.MODE_PRIVATE);
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

    }

    public void startTaskLoops(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true){

                    synchronized (mTaskQueue) {
                        if (mTaskQueue.size() > 0) {

                            Runnable task = mTaskQueue.removeFirst();

                            if (task != null) {
                                task.run();
                            }
                        }
                    }
                    //避免频繁访问芯片服务器造成堵塞
                    IoTUtils.sleep(1000);
                }
            }
        }).start();
    }

    @Override
    public void attachView(MainView view) {

        mView = view;

        mHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {

                switch (msg.what){

                    case SHOW_PANEL:
                        mView.hideSearching();
                        mView.showPanel(mDeviceList);
                        break;
                    case SHOW_ERROR:
                        mView.hideSearching();
                        mView.showError();
                        break;
                    case UPDATE_DEVICE_STATE:
                        mView.updateDeviceState(mDeviceList);
                        break;
                    case UPDATE_TIMER_TASK:
                        loadTimeTask();
                        break;
                    default:
                        break;
                }
            }
        };

        startTaskLoops();
    }


    public void switchDevice(Device device){

        if(device.getDeviceState()){

            switchOn(device);


        }else {

            switchOff(device);
        }

    }

    public void switchOn(final Device device){

        Runnable task = new Runnable() {
            @Override
            public void run() {

                if(mUrl!=null){

                    HttpUtil.doPost(mUrl + POWER_ON, "deviceId="+device.getDeviceId()+"&");

                }
            }

        };

        synchronized (mTaskQueue) {

            mTaskQueue.addLast(task);
        }


    }

    public void switchOff(final Device device){

        Runnable task =  new Runnable() {
            @Override
            public void run() {

                if (mUrl != null) {

                    HttpUtil.doPost(mUrl + POWER_OFF, "deviceId="+device.getDeviceId()+"&");


                }
            }
        };

        synchronized (mTaskQueue) {

            mTaskQueue.addLast(task);
        }

    }


    public void searchDevice(){

        mView.showSearching();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String serverIP = scanDeviceServer();

                if(serverIP!=null){

                    mUrl = "http://"+serverIP+":"+PORT+"/";

                    IoTUtils.sleep(500);

                    //同步定时任务
                    initTask();

                    IoTUtils.sleep(500);

                    //更新设备状态
                    updateDeviceState();

                    mHandler.obtainMessage(SHOW_PANEL).sendToTarget();

                }else{

                    mHandler.obtainMessage(SHOW_ERROR).sendToTarget();
                }
            }
        }).start();
    }

    public void initTask(){

        StringBuilder builder = new StringBuilder();

        int week = IoTUtils.getDayOfWeek();
        Date date = new Date(System.currentTimeMillis());

        builder.append("week="+week+"&hour="+date.getHours()+"&minute="+
                date.getMinutes()+"&\n");

        List<TimeTask> tasks = TimeTask.listAll(TimeTask.class);

        if(tasks!=null && tasks.size()>0){

            for(TimeTask task : tasks){

                builder.append("taskId="+task.getTaskId()+"&deviceId="+task.getDeviceId()+
                        "&hour="+task.getHour()+"&minute="+task.getMinute()+"&state="+
                        task.getSwitchState()+"&days="+task.getActivatedDays()+"\n");

            }
        }

        String args = builder.toString();

        HttpUtil.doPost(mUrl+"initTask",args);
    }

    public String scanDeviceServer(){

        //使用上一次ip连接服务器
        String lastServerIp = mSharedPreferences.getString("lastServerIp",null);


        if(lastServerIp != null && SocketUtils.isPortOpened(lastServerIp,PORT)){

            return lastServerIp;
        }

        String serverIp = SocketUtils.scanPortOpendHost(mContext,PORT,10);

        mSharedPreferences.edit().putString("lastServerIp",serverIp).commit();

        return serverIp;

    }



    public  void loadTimeTask(){

        List<TimeTask> list = TimeTask.listAll(TimeTask.class);

        if(list!=null&&list.size()>0){


            mView.showTimeTasks(list);

        }else {

            mView.showNoTasks();
        }
    }

    public void updateTimer(TimeTask timeTask){

        if(timeTask.isActivated()){

            setTimer(timeTask);

        }else {

            cancelTimer(timeTask);

        }
    }

    public void cancelTimer(final TimeTask timeTask){

        Runnable task = new Runnable() {
            @Override
            public void run() {

                if (mUrl != null) {

                    HttpUtil.doPost(mUrl, "cancelTimer&taskId="+timeTask.getTaskId()+"&");

                }

            }
        };

        synchronized (mTaskQueue) {

            mTaskQueue.addLast(task);
        }

    }

    public void setTimer(final TimeTask timeTask){

       Runnable task = new Runnable() {
            @Override
            public void run() {

                if (mUrl != null) {

                    String response = HttpUtil.doPost(mUrl+"setTimer",timeTask.getPostString());

                    if(!TextUtils.isEmpty(response) && response.contains("setTimer=OK")){

                        mHandler.obtainMessage(UPDATE_TIMER_TASK).sendToTarget();
                    }

                }

            }
        };

        synchronized (mTaskQueue) {

            mTaskQueue.addLast(task);
        }

    }

    public void updateDeviceState() {

        String response = HttpUtil.doPost(mUrl + "listDevices", "");

        String[] devicesInfo;

        if (response.contains(";")) {

            devicesInfo = response.split(";");

            for (String info : devicesInfo) {

                int deviceID = Integer.parseInt(info.substring(0, info.indexOf(":")));
                String state = info.substring(info.indexOf(":") + 1);
                boolean deviceState = state.equals("0") ? true : false;

                List<Device> deviceList = Device.findWithQuery(Device.class, "select * from DEVICE where DEVICE_ID=" + deviceID);

                if (deviceList == null || deviceList.size() == 0) {

                    Device device = new Device(deviceID,
                            null, deviceState);
                    device.save();

                } else if (deviceList.size() > 0) {

                    Device device = deviceList.get(0);

                    device.setDeviceState(deviceState);

                    device.update();
                }

            }

        }

        mDeviceList = Device.listAll(Device.class);

        mHandler.obtainMessage(UPDATE_DEVICE_STATE).sendToTarget();


    }


    public String getUrl(){

        return mUrl;
    }


}
