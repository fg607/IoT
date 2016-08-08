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
public class DevicePanelAdapter extends BaseAdapter implements View.OnClickListener{

    private List<Device> mDeviceList;
    private Context mContext;
    private LayoutInflater mInflater;
    private OnDeviceItemClickListener mItemClickListener;

    public DevicePanelAdapter(Context context, OnDeviceItemClickListener listener){

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mItemClickListener = listener;
    }

    public void setDeviceList(List<Device> list){

        mDeviceList = list;
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

           convertView = mInflater.inflate(R.layout.device_main_item,parent,false);

            holder = new DeviceHolder(convertView);

            convertView.setTag(holder);

        }else {

            holder = (DeviceHolder) convertView.getTag();
        }

        Device device = mDeviceList.get(position);

        if(device.getDeviceName()!=null){

            holder.deviceName.setText(device.getDeviceName());

        }else {

            holder.deviceName.setText(mContext.getString(R.string.device) + device.getDeviceId());
        }

        holder.deviceName.setTag(device);

        holder.deviceName.setOnClickListener(this);

        convertView.setTag(R.id.device,device);

        convertView.setOnClickListener(this);


        if(device.getDeviceState()){

            holder.light.setImageDrawable(mContext.getResources().getDrawable(R.drawable.light_green));
            holder.deviceName.setTextColor(mContext.getResources().getColor(R.color.green));

        }else {

            holder.light.setImageDrawable(mContext.getResources().getDrawable(R.drawable.light_gray));
            holder.deviceName.setTextColor(mContext.getResources().getColor(R.color.black));

        }



        return convertView;
    }

    @Override
    public void onClick(View v) {

        if(mItemClickListener!=null){

            mItemClickListener.deviceItemClick(v);
        }
    }

    public class DeviceHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.device_name) TextView deviceName;
        @Bind(R.id.light)
        ImageView light;

        public DeviceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnDeviceItemClickListener{

        public void deviceItemClick(View view);
    }
}
