package com.hardwork.fg607.iot.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by fg607 on 16-7-26.
 */
public class SocketUtils {

    private static WifiManager sWifiManager= null;

    //获取设备ip地址
    public static String getLocalHostIP(Context context){

        if(sWifiManager==null){

            sWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        }

        WifiInfo wifiInfo = sWifiManager.getConnectionInfo();

        int ipAddress = wifiInfo.getIpAddress();

        return intToIp(ipAddress);


    }


    /**
     * 检查端口是否开放
     */
    public static boolean isPortOpened(final String ip,final int port)
    {

        Socket tempSocket = null;
        boolean isOpend = false;
        try {
            tempSocket = new Socket(ip, port);

            isOpend = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        finally{
            if(tempSocket!=null)
            {
                try {
                    tempSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isOpend;
    }

    /**
     * 将获取的ＩＰ转化为字符串
     * @param i
     * @return
     */
    public static String intToIp(int i){

        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." +((i >> 16) & 0xFF) + "." +((i >> 24) & 0xFF);

    }


    /**
     *  扫描局域网指定ip范围端口开放
     * @param context
     * @param port 扫描的端口
     * @param range 相对与当前ip扫描的范围
     * @return
     */
    public static String scanPortOpendHost(Context context,int port,int range){

        String localIp = null;

        localIp = SocketUtils.getLocalHostIP(context);

        if(localIp == null){
            return null;
        }

        //获取主机所在网段
        int index = localIp.lastIndexOf(".");

        String ipPeroid = localIp.substring(0, index+1);
        String lastPeroid = localIp.substring(index+1);

        int i_lastPeroid = Integer.parseInt(lastPeroid);

        //扫描主机前后10位ip

        for(int i = 1;i<=range;i++) {

            int temp = i_lastPeroid + i;
            int temp1 = i_lastPeroid - i;

            if(SocketUtils.isPortOpened(ipPeroid + temp,port)) {
                return ipPeroid + temp;

            }else if(temp1>1){

                if(SocketUtils.isPortOpened(ipPeroid + temp1,port)){

                    return ipPeroid + temp1;
                }
            }
        }

        return null;
    }

}
