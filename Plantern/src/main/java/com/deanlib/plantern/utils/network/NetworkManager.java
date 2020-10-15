package com.deanlib.plantern.utils.network;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.deanlib.plantern.Plantern;
import com.deanlib.plantern.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class NetworkManager {

	public static final int TYPE_NO_CONNECTION = 0x00;
	public static final int TYPE_WIFI = 0x01;
	public static final int TYPE_CMWAP = 0x02;
	public static final int TYPE_CMNET = 0x03;


	public interface NetworkListener{

		void onNetworkDisconnect();
		void onNetworkConnected(int type);

	}

	List<NetworkListener> listeners = new ArrayList<NetworkListener>();
	public void addOnNetworkListener(NetworkListener listener){
		if(!listeners.contains(listener))
			listeners.add(listener);
	}

	/**
	 * 移除网络监听
	 * @param listener
	 */
	public void removeNetworkListener(NetworkListener listener){
		if(listeners.contains(listener))
			listeners.remove(listener);
	}

	private static NetworkManager instance  = null;

	public static boolean enable = false;

	public static NetworkManager getInstance(){
		if(instance == null){
			instance = new NetworkManager();
		}
		return instance;
	}

	/**
	 * 初始化
	 */
	public void init(){
		if(isNetworkConn()&& getAPNType()!=0){
			enable = true;
		}
	}

	NetworkChangedReceiver ncr = new NetworkChangedReceiver();

	public void registerNetworkReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		Plantern.getAppContext().registerReceiver(ncr, filter);
	}

	public void unregisterNetworkReceiver(){
		Plantern.getAppContext().unregisterReceiver(ncr);
	}


	/**
	 * 网络状态接收器
	 * @author louis.lv
	 *
	 */
	class NetworkChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			LogUtils.d(action);
			if(getAPNType()==TYPE_NO_CONNECTION){
				NetworkManager.enable = false;
				LogUtils.d("No Network");
				for(NetworkListener lis:listeners){
					lis.onNetworkDisconnect();
				}
				return;
			}

			if(getAPNType()==TYPE_WIFI){
				wifiAction(intent, action);
			}else {
				wapAction(intent, action);
			}

		}

		public void wapAction(Intent intent, String action) {
			if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
				ConnectivityManager cm = (ConnectivityManager) Plantern.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				//如果是在开启wifi连接和有网络状态下
				if(NetworkInfo.State.CONNECTED==info.getState()){
					//连接状态
					LogUtils.e("Network connection");
					NetworkManager.enable = true;
					for(NetworkListener lis:listeners){
						lis.onNetworkConnected(getAPNType());
					}
				}else{
					LogUtils.e("No network connection");
					NetworkManager.enable = false;

					for(NetworkListener lis:listeners){
						lis.onNetworkDisconnect();
					}
				}
			}
		}

		public void wifiAction(Intent intent, String action) {
			if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
				NetworkInfo info =intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if(info!=null){
					LogUtils.v(info.getDetailedState().toString());
					if(info.getDetailedState()== DetailedState.CONNECTED){
						NetworkManager.enable = true;
						LogUtils.d("Network connected");

						//获取更新--网络断开后没有到达的消息通过这个方法获取

						//开启推送服务

						for(NetworkListener lis:listeners){
							lis.onNetworkConnected(getAPNType());
						}
					}
				}
			}else if(action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)){
				DetailedState state = WifiInfo.getDetailedStateOf((SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
				LogUtils.i(state.toString());
				if(state== DetailedState.DISCONNECTED){//切换网络
					NetworkManager.enable = false;
					LogUtils.d("Network disconnection");
					for(NetworkListener lis:listeners){
						lis.onNetworkDisconnect();
					}
				}
			}else if(action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){//开关wifi
				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
				if(wifiState== WifiManager.WIFI_STATE_DISABLED){
					NetworkManager.enable = false;
					LogUtils.d("Network close");
					//发送通知给activity 显示toast提示
					for(NetworkListener lis:listeners){
						lis.onNetworkDisconnect();
					}
				}
			}
		}

	}

	/**
	 * 获取当前的网络状态
	 * android.permission.ACCESS_WIFI_STATE
	 * @return
	 */
	public static int getAPNType() {
		//设置默认网路类型
		int netType = TYPE_NO_CONNECTION;
		//获取当前的网络管理器
		ConnectivityManager connManager = (ConnectivityManager) Plantern.getAppContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		//获取网络信息
		@SuppressLint("MissingPermission") NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		//得到网络类型
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			netType = networkInfo.getExtraInfo().toLowerCase().equals("cmnet") ? TYPE_CMNET : TYPE_CMWAP;
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = TYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 判断WiFi网络是否可用
	 * android.permission.ACCESS_WIFI_STATE
	 * @return
	 */
	public static boolean isWifiConn() {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) Plantern.getAppContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			@SuppressLint("MissingPermission") NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		return false;
	}

	/**
	 * 判断数据流量是否可用
	 * android.permission.ACCESS_WIFI_STATE
	 * @return
	 */
	public static boolean isMobileConn() {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) Plantern.getAppContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			@SuppressLint("MissingPermission") NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}

		return false;
	}

	/**
	 * 判断是否有网络
	 * android.permission.ACCESS_WIFI_STATE
	 * @return
	 */
	public static boolean isNetworkConn() {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) Plantern.getAppContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			@SuppressLint("MissingPermission") NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		return false;
	}
}
