package com.hardwork.fg607.iot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hardwork.fg607.iot.R;
import com.hardwork.fg607.iot.model.Device;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fg607 on 16-7-29.
 */
public class DevicesGridAdapter extends BaseAdapter implements View.OnClickListener{

    private List<Device> mDeviceList;
    private Context mContext;
    private LayoutInflater mInflater;
    private OnDeviceChoosedListener mDeviceChoosedListener;

    private int mchoosedDeviceId;

    public DevicesGridAdapter(Context context,int deviceId){

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mchoosedDeviceId = deviceId;
    }

    public void setDeviceList(List<Device> list){

        mDeviceList = list;
    }

    public void setChoosedDevice(int deviceId){

        mchoosedDeviceId = deviceId;
    }

    public void setOnDeviceChoosedListener(OnDeviceChoosedListener listener){

        mDeviceChoosedListener = listener;
    }

    @Override
    public int getCount() {
        return mDeviceList!=null?mDeviceList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return mDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DeviceHolder holder = null;

        if(convertView==null){

           convertView = mInflater.inflate(R.layout.device_grid_item,parent,false);

            holder = new DeviceHolder(convertView);

            convertView.setTag(holder);

        }else {

            holder = (DeviceHolder) convertView.getTag();
        }

        Device device = mDeviceList.get(position);

        if(device.getDeviceName()!=null){

            holder.device.setTextOn(device.getDeviceName());
            holder.device.setTextOff(device.getDeviceName());

        }else {

            holder.device.setTextOn(mContext.getString(R.string.device) + device.getDeviceId());
            holder.device.setTextOff(mContext.getString(R.string.device) + device.getDeviceId());
        }

        holder.device.setTag(device);

        holder.device.setOnClickListener(this);

        if(device.getDeviceId()==mchoosedDeviceId){

            holder.device.setChecked(true);
            holder.device.setTextColor(mContext.getResources().getColor(R.color.white));
        }else {

            holder.device.setChecked(false);
            holder.device.setTextColor(mContext.getResources().getColor(R.color.black));
        }



        return convertView;
    }

    @Override
    public void onClick(View v) {

        Device device = (Device) v.getTag();

        mchoosedDeviceId = device.getDeviceId();

        notifyDataSetChanged();


        if(mDeviceChoosedListener!=null){

            mDeviceChoosedListener.deviceChoosed(device);
        }
    }

    public class DeviceHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.device)
        ToggleButton device;

        public DeviceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnDeviceChoosedListener{

        public void deviceChoosed(Device device);
    }
}
