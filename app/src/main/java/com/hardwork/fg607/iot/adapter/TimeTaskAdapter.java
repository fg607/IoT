package com.hardwork.fg607.iot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hardwork.fg607.iot.R;
import com.hardwork.fg607.iot.model.TimeTask;
import com.hardwork.fg607.iot.utils.IoTUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fg607 on 16-7-28.
 */
public class TimeTaskAdapter extends RecyclerView.Adapter<TimeTaskAdapter.TaskViewHolder>
        implements View.OnClickListener,View.OnLongClickListener {

    private List<TimeTask> mTaskList;
    private Context mContext;
    private LayoutInflater mInflater;
    private StringBuilder mStringBuilder;
    private OnTaskClickListener mTaskClickListener;

    public TimeTaskAdapter(Context context,List<TimeTask> taskList) {

        mContext = context;
        mTaskList = taskList;
        mInflater = LayoutInflater.from(context);
    }

    public void setTaskList(List<TimeTask> taskList){

        mTaskList = taskList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnTaskClickListener taskClickListener){

        mTaskClickListener = taskClickListener;
    }

    @Override
    public TimeTaskAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = mInflater.inflate(R.layout.time_task_item,parent,false);

        return new TaskViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TimeTaskAdapter.TaskViewHolder holder, int position) {

        TimeTask timeTask = mTaskList.get(position);

        String deviceName = timeTask.getDeviceName();

        if (deviceName != null) {

            holder.deviceName.setText(deviceName);

        } else {

            holder.deviceName.setText(mContext.getString(R.string.device) + timeTask.getDeviceId());
        }


        holder.tv_time.setText(timeTask.getTime());

        int days = timeTask.getActivatedDays();

        byte[] daybyte = IoTUtils.intToByteArray(days);

        mStringBuilder = new StringBuilder();

        for(int i = 6;i>=0; i--){

            //daybyte[3]为int的低４位
            if((daybyte[3]&1)>0){

                mStringBuilder.append(IoTUtils.WEEKDAYS[i]);
            }

            daybyte[3] = (byte) (daybyte[3] >> 1);
        }

        holder.tv_days.setText(mStringBuilder.reverse().toString());

        if(timeTask.getSwitchState()){

            holder.tv_state.setText(mContext.getString(R.string.on));
        }else {

            holder.tv_state.setText(mContext.getString(R.string.off));
        }


        holder.img_switch.setTag(timeTask);

        holder.img_switch.setOnClickListener(this);

        holder.itemView.setTag(timeTask);
        holder.itemView.setTag(R.id.position,position);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);


        if(timeTask.isActivated()){

            holder.deviceName.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.dark));
            holder.tv_days.setTextColor(mContext.getResources().getColor(R.color.green));
            holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.img_switch.setImageDrawable(mContext.getResources().getDrawable(R.drawable.clock_green));
        }else {

            holder.deviceName.setTextColor(mContext.getResources().getColor(R.color.gray));
            holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.gray));
            holder.tv_days.setTextColor(mContext.getResources().getColor(R.color.gray));
            holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.gray));
            holder.img_switch.setImageDrawable(mContext.getResources().getDrawable(R.drawable.clock_gray));

        }

    }


    @Override
    public int getItemCount() {
        return mTaskList!=null?mTaskList.size():0;
    }

    @Override
    public void onClick(View v) {

        if(mTaskClickListener!=null){

            mTaskClickListener.taskClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {

        if(mTaskClickListener!=null){

            mTaskClickListener.taskLongClick(v);

            return true;
        }

        return false;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.device_name)TextView deviceName;
        @Bind(R.id.time)TextView tv_time;
        @Bind(R.id.valiad_day)TextView tv_days;
        @Bind(R.id.state)TextView tv_state;
        @Bind(R.id.switcher)ImageView img_switch;

        View itemView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnTaskClickListener{

        public void taskClick(View view);

        public void taskLongClick(View view);
    }
}
