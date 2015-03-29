package com.yemyatthu.moelaywatha.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.okhttp.OkHttpClient;
import com.yemyatthu.moelaywatha.Config;
import com.yemyatthu.moelaywatha.R;
import com.yemyatthu.moelaywatha.model.Weather;
import com.yemyatthu.moelaywatha.model.WeatherCode;
import com.yemyatthu.moelaywatha.retrofitservice.WeatherRetrofitService;
import io.realm.Realm;
import io.realm.RealmList;
import java.util.Calendar;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import timber.log.Timber;

/**
 * Created by yemyatthu on 3/27/15.
 */
public class WeatherSyncAdapter extends AbstractThreadedSyncAdapter{
  // Constants
  // Content provider authority
  public static final String AUTHORITY = "com.yemyatthu.moelaywatha.provider";
  // Account
  public static final String ACCOUNT = "thu.yemyat@gmail.com";
  public static final String SYNC_FINISHED = "sync finished";
  // Sync interval constants
  public static final int SECONDS_PER_MINUTE = 60;
  public static final int SYNC_INTERVAL_IN_MINUTES = 120;
  public static final int SYNC_INTERVAL =
      SYNC_INTERVAL_IN_MINUTES *
          SECONDS_PER_MINUTE;
  public static final int SYNC_FLEXTIME = SYNC_INTERVAL/2;
  ContentResolver mContentResolver;
  private RestAdapter weatherRestAdapter;
  private WeatherRetrofitService mWeatherRetrofitService;
  private SharedPreferences mSharedPreferences;
  private Realm mRealm;
  public WeatherSyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
    mContentResolver = context.getContentResolver();
    weatherRestAdapter = new RestAdapter.Builder().setClient(new OkClient(new OkHttpClient()))
        .setEndpoint("http://api.openweathermap.org")
        .build();
    mWeatherRetrofitService = weatherRestAdapter.create(WeatherRetrofitService.class);
    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    mRealm = Realm.getInstance(context.getApplicationContext());
  }

  public WeatherSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
    super(context, autoInitialize, allowParallelSyncs);
    mContentResolver = context.getContentResolver();
    weatherRestAdapter = new RestAdapter.Builder().setClient(new OkClient(new OkHttpClient()))
        .setEndpoint("http://api.openweathermap.org")
        .build();
    mWeatherRetrofitService = weatherRestAdapter.create(WeatherRetrofitService.class);
    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

  }

  public static Account getSyncAccount(Context context) {
    // Get an instance of the Android account manager
    AccountManager accountManager =
        (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

    // Create the account type and default account
    Account newAccount =
        new Account(context.getString(R.string.app_name), context.getString(R.string.account_type));

    // If the password doesn't exist, the account doesn't exist
    if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
      if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
        return null;
      }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

      onAccountCreated(newAccount, context);
    }
    return newAccount;
  }

  private static void onAccountCreated(Account newAccount, Context context) {

        /*
         * Since we've created an account
         */
    WeatherSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
    ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.account_authorities),
        true);

        /*
         * Finally, let's do a sync to get things started
         */
    syncImmediately(context);
  }

  public static void initializeSyncAdapter(Context context) {
    getSyncAccount(context);
  }

  /**
   * Helper method to have the sync adapter sync immediately
   *
   * @param context The context used to access the account service
   */
  public static void syncImmediately(Context context) {
    Bundle bundle = new Bundle();
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
    ContentResolver.requestSync(getSyncAccount(context),
        context.getString(R.string.account_authorities), bundle);
    Timber.d("syncing");
  }

  /**
   * Helper method to schedule the sync adapter periodic execution
   */
  public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
    Account account = getSyncAccount(context);
    String authority = context.getString(R.string.account_authorities);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // we can enable inexact timers in our periodic sync
      SyncRequest.Builder builder = new SyncRequest.Builder();
      Bundle extras = new Bundle();
      builder.setExtras(extras);
      SyncRequest request = builder.
          syncPeriodic(syncInterval, flexTime).
          setSyncAdapter(account, authority).build();
      ContentResolver.requestSync(request);
    } else {
      ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
    }
  }

  @Override public void onPerformSync(Account account, Bundle bundle, String s,
      ContentProviderClient contentProviderClient, SyncResult syncResult) {
    final JsonObject jsonObject = mWeatherRetrofitService.getWeatherData(mSharedPreferences.getFloat(
        Config.LAST_LATITUDE, (float) 16.8),mSharedPreferences.getFloat(Config.LAST_LONGITUDE,
        (float) 96.15));
    if(jsonObject!=null){
      mRealm = Realm.getInstance(getContext().getApplicationContext());
      final RealmList<WeatherCode> weatherCodes = new RealmList<>();
      mRealm.executeTransaction(new Realm.Transaction() {
        @Override public void execute(Realm realm) {

          for(JsonElement element: jsonObject.get("weather").getAsJsonArray()){
            WeatherCode weatherCode = realm.createObject(WeatherCode.class);
            weatherCode.setWeatherCode(element.getAsJsonObject().get("id").getAsInt());
            weatherCodes.add(weatherCode);
        }
        }
      });

      final Calendar calendar = Calendar.getInstance();
      mRealm.executeTransaction(new Realm.Transaction() {
        @Override public void execute(Realm realm) {
          Weather weather = mRealm.createObject(Weather.class);
          weather.setMaxTemp(jsonObject.get("main").getAsJsonObject().get("temp_max").getAsFloat());
          weather.setMinTemp(jsonObject.get("main").getAsJsonObject().get("temp_min").getAsFloat());
          weather.setDate(calendar.get(Calendar.DATE));
          weather.setWeatherCode(weatherCodes);
        }
      });
      getContext().sendBroadcast(new Intent(SYNC_FINISHED));
    }
  }


}
