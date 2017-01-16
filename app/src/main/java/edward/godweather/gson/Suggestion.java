package edward.godweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/1/11 0011.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;//舒适度
    @SerializedName("cw")
    public CarWash carWash;//洗车指数
    public Sport sport;//运动指数
    public Drsg drsg;//穿衣指数
    public Flu flu;//感冒指数
    public Trav trav;//旅游指数
    public Uv uv;//紫外线指数

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
    }

    public class Drsg{
        @SerializedName("txt")
        public String info;
    }

    public class Flu{
        @SerializedName("txt")
        public String info;
    }

    public class Trav{
        @SerializedName("txt")
        public String info;
    }

    public class Uv{
        @SerializedName("txt")
        public String info;
    }
}
