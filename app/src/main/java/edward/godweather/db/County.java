package edward.godweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/1/9 0009.
 */

public class County extends DataSupport {

    private String id;
    private String countyName;
    private String weatherId;
    private int cityId;

    public int getCityId() {
        return cityId;
    }

    public String getCountyName() {
        return countyName;
    }

    public String getId() {
        return id;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
