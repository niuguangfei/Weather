package weather.wu.com.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import weather.wu.com.adapter.HourDataListAdapter;
import weather.wu.com.bean.WeatherBean;
import weather.wu.com.utils.HttpUtil;
import weather.wu.com.utils.SystemUtils;
import weather.wu.com.utils.Utility;

/**
 *
 */
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.nav_button)
    Button mNavButton;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    private int mNowWeatherHeight = -1;
    @BindView(R.id.main_now_weather)
    public RelativeLayout mNowWeatherRelativeLayout;
    @BindView(R.id.weather_scrollview_layout)
    public ScrollView mScrollView;
    @BindView(R.id.hourdata_recyclerview)
    public RecyclerView mRecyclerView;
    private Context mContext = MainActivity.this;
    private HourDataListAdapter mHourDataListAdapter;
    private List<String> datas;
    //  String json;
    String a = "http://route.showapi.com/9-2?showapi_appid=28198&area=广州&showapi_sign=bd9ad7a172ee4a5a8c57618a248c63e9&needMoreDay=1&needIndex=1&needHourData=1&need3HourForcast=1&needAlarm=1";
    private List<String> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initView();
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //HttpUtil.requestWeather("广州");
                requestWeather("广州");
            }
        });

       /*  Logger.d(HttpUtil.address);

       HttpUtil.sendOkHttpRequest(a, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                 String responseText = response.body().string();
                Logger.d(responseText);

            }
        });*/
      /*  HttpUtil.sendOkHttpRequest(a, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(e);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Logger.json(response.toString());

              //  Log.d("json",response.toString());
                System.out.print(response.toString());
            }
        });*/
    }

    private void initData() {
        datas = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            datas.add("ccc" + i);
        }
    }

    private void initView() {
        //  = SystemUtils.getDisplayHeight(getActivity());
        // Logger.d("hello");
        //NowWeather主RelativeLayout
        //   mNowWeatherRelativeLayout = (RelativeLayout)findViewById(R.id.main_now_weather);
        // mScrollView = (ScrollView)findViewById(R.id.weather_scrollview_layout);
        mScrollView.smoothScrollTo(0, 0);
        //   mRecyclerView = (RecyclerView)findViewById(R.id.hourdata_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        mRecyclerView.setAdapter(new HourDataListAdapter(mContext, datas));
        mRecyclerView.scrollToPosition(datas.size() - 1);
        mSwipeRefresh.setColorSchemeResources(R.color.color_main);

        //NowWeather主RelativeLayout中的RecycleView
        // mRecyclerView = (RecyclerView)findViewById(R.id.now_weather_recyclerview);
        //获取屏幕高度
       /* int displayHeight = SystemUtils.getDisplayHeight(MainActivity.this);
        Log.e("Log displayHeight",displayHeight+"");

        TypedValue typedValue = new TypedValue();
         MainActivity.this.getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true);
         int[] attribute = new int[] { android.R.attr.textSize };
       TypedArray array =   MainActivity.this.obtainStyledAttributes(typedValue.resourceId, attribute);
        Log.e("Log array",array+"");
        int textSize = array.getDimensionPixelSize(0 *//* index *//*, -1 *//* default size *//*);
        array.recycle();
        Log.e("Log typedValue",textSize+"");
        int actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, MainActivity.this.getResources().getDisplayMetrics());
        Log.e("Log actionBarHeight",actionBarHeight+"");
       Log.e("Log System.getActionBarHeight",);*/
        // mNowWeatherHeight高度=屏幕高度-标题栏高度-状态栏高度
        mNowWeatherHeight = SystemUtils.getDisplayHeight(mContext) - SystemUtils.getActionBarSize(mContext) - SystemUtils.getStatusBarHeight(mContext);

        //设置当前天气信息RelativeLayout的高度
        mNowWeatherRelativeLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mNowWeatherHeight));
    }
    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather( String cityName) {
        String weatherUrl = "http://route.showapi.com/9-2?showapi_appid=28198&area=" + cityName + "&showapi_sign=bd9ad7a172ee4a5a8c57618a248c63e9"
                + "&needMoreDay=1&needIndex=1&needHourData=1&need3HourForcast=1&needAlarm=1";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                 String responseText = response.body().string();
                 WeatherBean weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       /* if (weather != null && "ok".equals(weather.status)) {
                           *//* SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();*//*
                           // showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }*/
                        mSwipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        mSwipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
      //  loadBingPic();
    }
    @OnClick(R.id.nav_button)
    public void onClick() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }
}
