package com.yemyatthu.moelaywatha.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by yemyatthu on 3/27/15.
 */
public class WeatherAuthenticatorService extends Service {
  // Instance field that stores the authenticator object
  private WeatherStubAuthenticator mWeatherStubAuthenticator;
  @Override
  public void onCreate() {
    // Create a new authenticator object
    mWeatherStubAuthenticator = new WeatherStubAuthenticator(this);
  }
  /*
   * When the system binds to this Service to make the RPC call
   * return the authenticator's IBinder.
   */
  @Override
  public IBinder onBind(Intent intent) {
    return mWeatherStubAuthenticator.getIBinder();
  }
}
