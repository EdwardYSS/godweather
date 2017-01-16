package edward.godweather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edward.godweather.MainActivity;
import edward.godweather.R;
import edward.godweather.WeatherActivity;
import edward.godweather.address.Address;
import edward.godweather.db.City;
import edward.godweather.db.County;
import edward.godweather.db.Province;
import edward.godweather.util.HttpUtil;
import edward.godweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/1/10 0010.
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE= 0;
    public static final int LEVEL_CITY= 1;
    public static final int LEVEL_COUNTY=2;
    private TextView title;
    private Button back;
    private ListView lv;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> adapter;
    private List<String> list = new ArrayList<>();
    private List<Province> provinceList ;
    private List<City> cityList;
    private List<County> countyList = new ArrayList<>();
    private Province selectProvince;
    private City selectCity;
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chooseareafragment,container,false);
        title = (TextView) view.findViewById(R.id.title_tv);
        back = (Button) view.findViewById(R.id.title_back);
        lv = (ListView) view.findViewById(R.id.lv);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,list);
        lv.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectProvince = provinceList.get(position);
                    queryCity();
                }else if (currentLevel == LEVEL_CITY){
                    selectCity = cityList.get(position);
                    queryCounty();
                }else if (currentLevel == LEVEL_COUNTY){

                    final String weatherId = countyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity){
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity){//用 instanceof 来判断碎片在哪个activity中
                        final WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                        activity.changeWeatherId = weatherId;
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCity();
                }else if (currentLevel == LEVEL_CITY){
                    queryProvince();
                }
            }
        });
        queryProvince();

    }

    private void queryProvince(){
        title.setText("中国");
        back.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size()>0){
            list.clear();
            for (Province province:provinceList) {
                list.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lv.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            String address = Address.CITY_ADD;
            queryFromServer(address,"province");
        }
    }

    private void queryCity(){
        title.setText(selectProvince.getProvinceName());
        back.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId =?",String.valueOf(selectProvince.getId())).find(City.class);
        //Log.e("main",""+cityList.size());
        if (cityList.size()>0){
            list.clear();
            for (City city:cityList) {
                list.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lv.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            int provinceCode= selectProvince.getProvinceCode();
            String address = Address.CITY_ADD+"/"+provinceCode;
            queryFromServer(address ,"city");
        }
    }

    private void queryCounty(){
        title.setText(selectCity.getCityName());
        back.setVisibility(View.VISIBLE);
        //countyList = DataSupport.where("cityId = ?", String.valueOf(selectCity.getId())).find(County.class);
        //Log.e("main",""+countyList.size()+selectCity.getId()+"");
        //countyList = Utility.list;
        //由于上面那个查询不出 所以才有这种方式
        if (countyList.size()>0) {
            if (countyList.get(0).getCityId() != selectCity.getId()) {
                countyList.clear();
            }
        }
        if (countyList.size()>0){
           list.clear();
            for (County county:countyList) {
                list.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lv.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address =Address.CITY_ADD+"/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }

    }

    private void  queryFromServer(String address,final String type){
        showProgressDialog();

        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgress();

                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectProvince.getId());
                }else if ("county".equals(type)){
                    result = true;
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgress();
                            if ("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCity();
                            }else if ("county".equals(type)){
                                countyList = Utility.handleCountyResponse(responseText,selectCity.getId());
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });


    }

    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();

    }

    private void closeProgress(){
       if (progressDialog != null){
           progressDialog.dismiss();

       }
    }
}
