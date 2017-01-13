package edward.godweather.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/13 0013.
 */

public class AcyivityCollector {

    public static List<Activity>list = new ArrayList<>();
    public static void addActivity(Activity activity){

        list.add(activity);
    }

    public static void removeActicity(Activity activity){
        list.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity:list) {
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
