package edward.godweather.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import edward.godweather.util.NetworkUtils;

/**
 * 网络状态监听Service
 *
 */
public class NetWorkStateService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkUtils.initNetStatus(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);

        super.onDestroy();
    }

    BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){

                //初始化网络状态
                NetworkUtils.initNetStatus(context);
            }
        }
    };

}
