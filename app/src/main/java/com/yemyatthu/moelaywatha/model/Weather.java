package com.yemyatthu.moelaywatha.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by yemyatthu on 3/28/15.
 */
public class Weather extends RealmObject {
  private float minTemp;
  private float maxTemp;
  private float windSpeed;
  private float windDirection;
  private RealmList<WeatherCode> weatherCode;
  private int date;
  private String lastUpdatedTime;

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  private String city;

  public String getLastUpdatedTime() {
    return lastUpdatedTime;
  }

  public void setLastUpdatedTime(String lastUpdatedTime) {
    this.lastUpdatedTime = lastUpdatedTime;
  }



  public float getWindDirection() {
    return windDirection;
  }

  public void setWindDirection(float windDirection) {
    this.windDirection = windDirection;
  }

  public float getWindSpeed() {
    return windSpeed;
  }

  public void setWindSpeed(float windSpeed) {
    this.windSpeed = windSpeed;
  }

  public float getMaxTemp() {
    return maxTemp;
  }

  public void setMaxTemp(float maxTemp) {
    this.maxTemp = maxTemp;
  }

  public float getMinTemp() {
    return minTemp;
  }

  public void setMinTemp(float minTemp) {
    this.minTemp = minTemp;
  }

  public int getDate() {
    return date;
  }

  public void setDate(int date) {
    this.date = date;
  }

  public RealmList<WeatherCode> getWeatherCode() {
    return weatherCode;
  }

  public void setWeatherCode(RealmList<WeatherCode> weatherCode) {
    this.weatherCode = weatherCode;
  }
}
