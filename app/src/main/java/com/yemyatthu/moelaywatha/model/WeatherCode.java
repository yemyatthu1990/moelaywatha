package com.yemyatthu.moelaywatha.model;

import io.realm.RealmObject;

/**
 * Created by yemyatthu on 3/28/15.
 */
public class WeatherCode extends RealmObject {
  private int weatherCode;

  public int getWeatherCode() {
    return weatherCode;
  }

  public void setWeatherCode(int weatherCode) {
    this.weatherCode = weatherCode;
  }
}
