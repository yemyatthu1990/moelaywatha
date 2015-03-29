package com.yemyatthu.moelaywatha.ui;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.yemyatthu.moelaywatha.Config;
import com.yemyatthu.moelaywatha.sync.WeatherSyncAdapter;
import timber.log.Timber;

/**
 * Created by yemyatthu on 3/27/15.
 */

public class BaseActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,LocationListener {
  public Location mLastLocation;
  public float mLatitude=0;
  public float mLongitude=0;
  public SharedPreferences mSharedPreferences;
  private GoogleApiClient mGoogleApiClient;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    buildGoogleApiClient();
    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
  }

  protected synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
  }

  @Override public void onConnected(Bundle bundle) {
    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    Timber.d(mLastLocation==null? "null":"not null");
    if (mLastLocation != null) {
      mLatitude = (float)mLastLocation.getLatitude();
      mLongitude = (float)mLastLocation.getLongitude();
      Timber.d(mLatitude+"");
      Timber.d(mLongitude+"");
      mSharedPreferences.edit().putFloat(Config.LAST_LATITUDE, mLatitude).apply();
      mSharedPreferences.edit().putFloat(Config.LAST_LONGITUDE, mLongitude).apply();
    }else{
      LocationRequest mLocationRequest = LocationRequest.create()
          .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
          .setInterval(10 * 1000)        // 10 seconds, in milliseconds
          .setFastestInterval(1 * 1000);
      LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);

    }
  }

  @Override public void onConnectionSuspended(int i) {

  }

  @Override public void onConnectionFailed(ConnectionResult connectionResult) {

  }

  @Override protected void onResume() {
    super.onResume();
    mGoogleApiClient.connect();
  }

  @Override protected void onPause() {
    super.onPause();
    mGoogleApiClient.disconnect();
  }

  @Override public void onLocationChanged(Location location) {
    mLatitude = (float)location.getLatitude();
    mLongitude = (float)location.getLongitude();
    Timber.d(mLatitude+"");
    Timber.d(mLongitude+"");
    mSharedPreferences.edit().putFloat(Config.LAST_LATITUDE, mLatitude).apply();
    mSharedPreferences.edit().putFloat(Config.LAST_LONGITUDE, mLongitude).apply();
    WeatherSyncAdapter.syncImmediately(getApplicationContext());
  }
}
