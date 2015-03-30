package com.yemyatthu.moelaywatha.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.yemyatthu.moelaywatha.R;
import com.yemyatthu.moelaywatha.model.Weather;
import com.yemyatthu.moelaywatha.sync.WeatherSyncAdapter;
import io.realm.Realm;
import io.realm.exceptions.RealmException;
import java.util.Calendar;
import timber.log.Timber;

import static com.yemyatthu.moelaywatha.util.WeatherCodeUtil.changeEngToBur;
import static com.yemyatthu.moelaywatha.util.WeatherCodeUtil.changeWeatherBackground;
import static com.yemyatthu.moelaywatha.util.WeatherCodeUtil.getWeatherDescription;
import static com.yemyatthu.moelaywatha.util.WeatherCodeUtil.getWeatherDrawable;
import static com.yemyatthu.moelaywatha.util.WeatherCodeUtil.saveScreenShotToSd;

public class MainActivity extends BaseActivity {
  @InjectView(R.id.weather_text) TextView mWeatherTextView;
  @InjectView(R.id.date) TextView mDate;
  @InjectView(R.id.time) TextView mTime;
  @InjectView(R.id.weather_icon) ImageView mWeatherIcon;
  @InjectView(R.id.weather_background) RelativeLayout mWeatherBackground;
  @InjectView(R.id.temp_data) TextView mTempData;
  @InjectView(R.id.city) TextView mCity;
  @InjectView(R.id.expanded_menu) ImageButton mExpandedMenu;
  @InjectView(R.id.last_updated) TextView mLastUpdated;
  @InjectView(R.id.schedule_list_container) LinearLayout mScheduleListContainer;
  @InjectView(R.id.date_container) LinearLayout mDateContainer;
  @InjectView(R.id.schedule_recycler_view) RecyclerView mScheduleRecyclerView;
  private int mTodayDate;
  private Realm mRealm;
  private Weather mWeather = null;
  private int mHourOfDay;
  private int mWeatherCode;
  private boolean mSlideUp = false;
  private RecyclerView.LayoutManager mLayoutManager;
  private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      Timber.d("receive");
      mRealm.executeTransaction(new Realm.Transaction() {
        @Override public void execute(Realm realm) {
          try {
            mWeather = realm.where(Weather.class).equalTo("date", mTodayDate).findFirst();
          } catch (RealmException exception) {
            Timber.d(exception.getMessage());
          }
        }
      });
      if (mWeather != null) {
        updateWeatherUi(mWeather);
      }
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
    WeatherSyncAdapter.initializeSyncAdapter(getApplicationContext());

    mScheduleRecyclerView.setHasFixedSize(true);
    mLayoutManager = new LinearLayoutManager(this);
    mScheduleRecyclerView.setLayoutManager(mLayoutManager);

    mRealm = Realm.getInstance(getApplicationContext());
    Calendar calendar = Calendar.getInstance();
    mTodayDate = calendar.get(Calendar.DATE);
    mHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

    String hour = changeEngToBur(String.valueOf(calendar.get(Calendar.HOUR)));
    String date = changeEngToBur(String.valueOf(calendar.get(Calendar.DATE)));

    mTime.setText(hour + " နာရီ");
    mDate.setText(date + " ရက်");

    changeWeatherBackground(this, mWeatherBackground, mWeatherIcon, mExpandedMenu, mHourOfDay,
        mTempData, mWeatherTextView, mDate, mTime, mCity, mLastUpdated);

    mRealm.executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        try {
          mWeather = realm.where(Weather.class).equalTo("date", mTodayDate).findFirst();
        } catch (RealmException exception) {
          Timber.d(exception.getMessage());
        }
      }
    });

    if (mWeather != null) {
      updateWeatherUi(mWeather);
    } else {
      WeatherSyncAdapter.syncImmediately(this);
    }

    this.setTypeFace(mWeatherTextView, mDate, mTime, mTempData);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    return super.onOptionsItemSelected(item);
  }

  @Override protected void onResume() {
    super.onResume();
    this.registerReceiver(syncFinishedReceiver, new IntentFilter(WeatherSyncAdapter.SYNC_FINISHED));
  }

  @Override protected void onPause() {
    super.onPause();
    this.unregisterReceiver(syncFinishedReceiver);
  }

  private Intent getShareIntent() {
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.putExtra(Intent.EXTRA_STREAM,
        saveScreenShotToSd(mWeatherBackground));
    shareIntent.putExtra(Intent.EXTRA_TEXT,
        "https://play.google.com/store/apps/details?id=com.yemyatthu.moelaywatha");
    shareIntent.setType("image/png");
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    return shareIntent;
  }

  public void updateWeatherUi(Weather weather) {
    mWeatherCode = weather.getWeatherCode().first().getWeatherCode();
    mWeatherTextView.setText(
        getWeatherDescription(MainActivity.this, mWeatherCode, mHourOfDay));
    String tempData = changeEngToBur(
        String.valueOf(Math.round(((weather.getMaxTemp() + weather.getMinTemp()) / 2) - 271)));
    mTempData.setText(tempData + " ဒီဂရီစင်တီဂရိတ်");
    mWeatherIcon.setImageDrawable(
        getWeatherDrawable(MainActivity.this, mWeatherCode, mHourOfDay));
    mLastUpdated.setText(getLastUpdatedTime(mWeather.getLastUpdatedTime()));
    mCity.setText(mWeather.getCity());
  }

  private void setTypeFace(TextView... textViews) {
    Typeface pdsTypeface = Typeface.createFromAsset(this.getAssets(), "pyidaungsu-1.2.ttf");
    for (TextView textView : textViews) {
      textView.setTypeface(pdsTypeface);
    }
  }

  private String getLastUpdatedTime(String time) {
    return "Last Updated: " + time;
  }

  @OnClick(R.id.expanded_menu) void slideUpScheduleList() {
    Timber.d("sliding up");
    if(!mSlideUp) {
      int padding = getResources().getDimensionPixelSize(R.dimen.padding_large);
      mWeatherBackground.animate()
          .translationY(-(mWeatherBackground.getHeight() - (mDateContainer.getHeight()+padding)))
          .setDuration(1000)
          .setStartDelay(200);
      mSlideUp=true;
    }else{
      mWeatherBackground.animate()
          .translationY(0)
          .setDuration(1000)
          .setStartDelay(200);
      mSlideUp=false;
    }
  }
}
