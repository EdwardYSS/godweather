package edward.godweather.util;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edward.godweather.db.City;
import edward.godweather.db.County;
import edward.godweather.db.Province;

/**
 * Created by Administrator on 2017/1/10 0010.
 */

public class Utility {
    /**
     *解析从服务器请求下来的省的数据
     */

    public static boolean handleProvinceResponse(String response){

        if (!TextUtils.isEmpty(response)){

            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0;i<allProvinces.length();i++){
                    JSONObject provincesObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provincesObject.getString("name"));
                    province.setProvinceCode(provincesObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /***
     * 解析和处理城市的数据
     * @param response
     * @param proid
     * @return
     */
    public static boolean handleCityResponse(String response,int proid){

        if (!TextUtils.isEmpty(response)){

            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0;i<allProvinces.length();i++){
                    JSONObject provincesObject = allProvinces.getJSONObject(i);
                    City province = new City();
                    province.setCityName(provincesObject.getString("name"));
                    province.setCityCode(provincesObject.getInt("id"));
                    province.setProvinceId(proid);
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /***
     * 解析和处理县区的数据
     * @param response
     * @param cityid
     * @return
     */
    public static List<County> handleCountyResponse(String response,int cityid){

        List<County> list = new ArrayList<>();
        if (!TextUtils.isEmpty(response)){

            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0;i<allProvinces.length();i++){
                    JSONObject provincesObject = allProvinces.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(provincesObject.getString("name"));
                    county.setWeatherId(provincesObject.getString("weather_id"));
                    county.setCityId(cityid);
                    county.save();
                    list.add(county);
                }
                return list;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

}
