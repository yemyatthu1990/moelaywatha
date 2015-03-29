package com.yemyatthu.moelaywatha.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.yemyatthu.moelaywatha.R;
import com.yemyatthu.moelaywatha.model.Weather;
import com.yemyatthu.moelaywatha.sync.WeatherSyncAdapter;
import com.yemyatthu.moelaywatha.util.WeatherCodeUtil;
import io.realm.Realm;
import io.realm.exceptions.RealmException;
import java.util.Calendar;
import timber.log.Timber;

public class MainActivity extends BaseActivity {
  @InjectView(R.id.day_toolbar) Toolbar mDayToolbar;
  @InjectView(R.id.night_toolbar) Toolbar mNightToolbar;
  @InjectView(R.id.weather_text) TextView mWeatherTextView;
  @InjectView(R.id.temp_title) TextView mTempTitle;
  @InjectView(R.id.date) TextView mDate;
  @InjectView(R.id.time) TextView mTime;
  @InjectView(R.id.weather_icon) ImageView mWeatherIcon;
  @InjectView(R.id.weather_background) RelativeLayout mWeatherBackground;
  @InjectView(R.id.temp_data) TextView mTempData;
  @InjectView(R.id.plus_floating_button) FloatingActionsMenu mPlusFloatingMenu;
  @InjectView(R.id.share_fab) FloatingActionButton mShareFab;
  @InjectView(R.id.list_fab) FloatingActionButton mListFab;

  private int mTodayDate;
  private Realm mRealm;
  private Weather mWeather = null;
  private int mHourOfDay;
  private int mWeatherCode;
  private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      Timber.d("receive");
      mRealm.executeTransaction(new Realm.Transaction() {
        @Override public void execute(Realm realm) {
          try{
            mWeather = realm.where(Weather.class)
                .equalTo("date",mTodayDate).findFirst();}
          catch (RealmException exception){
            Timber.d(exception.getMessage());
          }
        }
      });

      if(mWeather!= null){
        mWeatherCode = mWeather.getWeatherCode().first().getWeatherCode();
        mWeatherTextView.setText(WeatherCodeUtil.getWeatherDescription(MainActivity.this,mWeatherCode,mHourOfDay));
        String tempData =WeatherCodeUtil.changeEngToBur(
            String.valueOf(Math.round(((mWeather.getMaxTemp() + mWeather.getMinTemp()) / 2) - 271)));
        mTempData.setText(tempData+" ဒီဂရီစင်တီဂရိတ်");
        mWeatherIcon.setImageDrawable(WeatherCodeUtil.getWeatherDrawable(MainActivity.this,mWeatherCode,mHourOfDay));
      }
    }
  };


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
    WeatherSyncAdapter.initializeSyncAdapter(getApplicationContext());
    mRealm = Realm.getInstance(getApplicationContext());
    Calendar calendar = Calendar.getInstance();
    mTodayDate = calendar.get(Calendar.DATE);
    mHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

    String hour = WeatherCodeUtil.changeEngToBur(String.valueOf(calendar.get(Calendar.HOUR)));
    String date = WeatherCodeUtil.changeEngToBur(String.valueOf(calendar.get(Calendar.DATE)));
    mTime.setText(hour+" နာရီ");
    mDate.setText(date+" ရက်");
    if(mHourOfDay<18 && mHourOfDay>5){
      setSupportActionBar(mDayToolbar);
      mNightToolbar.setVisibility(View.GONE);
      WeatherCodeUtil.changeWeatherBackground(this,mWeatherBackground,mDayToolbar,mWeatherIcon,mHourOfDay,mTempData,mWeatherTextView,mTempTitle,mDate,mTime);
    }
    else{
      setSupportActionBar(mNightToolbar);
      mDayToolbar.setVisibility(View.GONE);
      WeatherCodeUtil.changeWeatherBackground(this,mWeatherBackground,mNightToolbar,mWeatherIcon,mHourOfDay,mTempData,mWeatherTextView,mTempTitle,mDate,mTime);

    }
        mRealm.executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        try{
        mWeather = realm.where(Weather.class)
            .equalTo("date",mTodayDate).findFirst();}
        catch (RealmException exception){
          Timber.d(exception.getMessage());
        }
      }
    });

    if(mWeather!= null){
      mWeatherCode = mWeather.getWeatherCode().first().getWeatherCode();
      mWeatherTextView.setText(WeatherCodeUtil.getWeatherDescription(this,mWeatherCode,mHourOfDay));
      String tempData =WeatherCodeUtil.changeEngToBur(
          String.valueOf(Math.round(((mWeather.getMaxTemp() + mWeather.getMinTemp()) / 2) - 271)));
      mTempData.setText(tempData+" ဒီဂရီစင်တီဂရိတ်");
      mWeatherIcon.setImageDrawable(WeatherCodeUtil.getWeatherDrawable(this,mWeatherCode,mHourOfDay));
    }else{
      WeatherSyncAdapter.syncImmediately(this);
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
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
        WeatherCodeUtil.saveScreenShotToSd(mWeatherBackground));
    shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.yemyatthu.moelaywatha");
    shareIntent.setType("image/png");
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    return shareIntent;
  }
  @OnClick(R.id.share_fab)
  void onClickShareFab(){
    WeatherCodeUtil.delayButtonClick(mShareFab);
    if(mPlusFloatingMenu.isExpanded()) mPlusFloatingMenu.collapse();
    Intent.createChooser(getShareIntent(),"Share via");
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        startActivity(getShareIntent());
      }
    },1000);
  }
}


