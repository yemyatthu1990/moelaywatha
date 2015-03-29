package com.yemyatthu.moelaywatha.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.yemyatthu.moelaywatha.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by yemyatthu on 3/28/15.
 */
public class WeatherCodeUtil {

  public static String getWeatherDescription(Context context, int weatherCode, int timeOfDay) {
    switch (weatherCode) {
      case 201:
      case 202:
      case 203:
        return "မိုးတွေရွာ လျှပ်စီးတွေလက်နေမှာနော်..\n" + "အိမ်ထဲမှာနေတာ အကောင်းဆုံးပဲ";
      case 210:
      case 211:
      case 212:
      case 221:
        return "အပြင်မှာ မိုးကြိုးမုန်တိုင်းတွေတိုက်နေတယ်..\n" + "အပြင်မထွက်ရင် ပိုကောင်းမယ်နော်";
      case 230:
      case 231:
      case 232:
        return "မိုးကတော့ဖွဲဖွဲလေးပါ...\n" + "ဒါပေမယ့် လျှပ်တွေတော့လက်နေတယ်နော်";
      case 300:
      case 301:
      case 302:
      case 310:
      case 311:
      case 312:
      case 313:
      case 314:
      case 321:
        return "မိုးဖွဲဖွဲရွာမှာနော်...\n" + "ထီးယူသွားဖို့မမေ့နဲ့";
      case 500:
      case 501:
      case 520:
      case 521:
        return "ဒီလိုမိုးအေးအေးလေးထဲမှာ...\n" + "ကော်ဖီလေးတစ်ခွက်နဲ့ စာအုပ်လေးကတစ်ဖက်ဆို..";
      case 502:
      case 503:
      case 504:
      case 511:
      case 522:
      case 531:
        return "မိုးတွေသည်းတော့မယ်..\n" + "မိုးခိုဖို့နေရာရှာထားတော့နော်";
      case 600:
      case 601:
      case 602:
      case 611:
      case 612:
      case 615:
      case 616:
      case 620:
      case 621:
      case 622:
        return "ဟေး...\n" + "နှင်းတွေကျနေပြီဟေ့";
      case 800:
        if(timeOfDay<18 && timeOfDay>5){
          return "ကောင်းကင်ကြီးကပြာလို့...\n" + "နေမင်းကြီးကလည်းသာလို့";
        }
        else{
          return "တိမ်တွေကင်းလို့...\n" + "ကြယ်လေးတွေကို မြင်နိုင်တယ်နော်";
        }
      case 801:
      case 802:
      case 803:
        return "တိမ်တိုက်လေးတွေက..\n" + "လွင့်လို့ ပျံ့လို့";
      case 804:
        return "မိုးအုံ့နေတယ်\n" + "အပြင်သွားရင် လိုရမယ်ရ ထီးဆောင်သွားနော်";
      default:
        return "";
    }
  }

  public static Drawable getWeatherDrawable(Context context, int weatherCode, int timeOfDay) {
    switch (weatherCode) {
      case 201:
      case 202:
      case 203:
        return context.getResources().getDrawable(R.drawable.big_lightening);
      case 210:
      case 211:
      case 212:
      case 221:
        return context.getResources().getDrawable(R.drawable.big_lightening);
      case 230:
      case 231:
      case 232:
        return context.getResources().getDrawable(R.drawable.small_lightening);
      case 300:
      case 301:
      case 302:
      case 310:
      case 311:
      case 312:
      case 313:
      case 314:
      case 321:
        return context.getResources().getDrawable(R.drawable.small_rain);
      case 500:
      case 501:
      case 520:
      case 521:
        return context.getResources().getDrawable(R.drawable.medium_rain);
      case 502:
      case 503:
      case 504:
      case 511:
      case 522:
      case 531:
        return context.getResources().getDrawable(R.drawable.big_rain);
      case 600:
      case 601:
      case 602:
      case 611:
      case 612:
      case 615:
      case 616:
      case 620:
      case 621:
      case 622:
        return context.getResources().getDrawable(R.drawable.snow);
      case 800:
        if(timeOfDay<12 && timeOfDay>5){
          return context.getResources().getDrawable(R.drawable.morning_clear);
        }
        else if(timeOfDay<18 && timeOfDay>11){
          return context.getResources().getDrawable(R.drawable.afternoon_clear);
        }
        else{
          return context.getResources().getDrawable(R.drawable.night_clear);
        }
      case 801:
      case 802:
      case 803:
        if(timeOfDay<18 && timeOfDay>5){
          return context.getResources().getDrawable(R.drawable.small_cloud_day);
        }
        else{
          return context.getResources().getDrawable(R.drawable.small_cloud_night);
        }
      case 804:
        return context.getResources().getDrawable(R.drawable.big_cloud);
      default:
        return null;
    }
  }

  //TODO The fuck, get a better algorithm
  public static String changeEngToBur(String number) {
    return number.replace('1', '၁')
        .replace('2', '၂')
        .replace('3', '၃')
        .replace('4', '၄')
        .replace('5', '၅')
        .replace('6', '၆')
        .replace('7', '၇')
        .replace('8', '၈')
        .replace('9', '၉')
        .replace('0', '၀');
  }

  public static void changeWeatherBackground(Context context,View background,Toolbar toolbar,ImageView weatherIcon,int timeOfDay,TextView... textViews){
    switch (timeOfDay){
      case 18:case 19:case 20:case 21:case 22:case 23:
        background.setBackgroundColor(context.getResources().getColor(R.color.evening_color));
        toolbar.setBackgroundColor(context.getResources().getColor(R.color.evening_darker_color));
        for(TextView textView:textViews){
          textView.setTextColor(context.getResources().getColor(R.color.secondary_text_color));
        }
        weatherIcon.setColorFilter(context.getResources().getColor(R.color.secondary_text_color));

        break;
      case 24:case 0:case 1:case 2:case 3:case 4:case 5:
        background.setBackgroundColor(context.getResources().getColor(R.color.night_color));
        toolbar.setBackgroundColor(context.getResources().getColor(R.color.night__darker_color));
        for(TextView textView:textViews){
          textView.setTextColor(context.getResources().getColor(R.color.secondary_text_color));
        }
        weatherIcon.setColorFilter(context.getResources().getColor(R.color.secondary_text_color));

        break;
      case 6:case 7:case 8:case 9:case 10:case 11:
        background.setBackgroundColor(context.getResources().getColor(R.color.morning_color));
        toolbar.setBackgroundColor(context.getResources().getColor(R.color.morning_darker_color));
        for(TextView textView:textViews){
          textView.setTextColor(context.getResources().getColor(R.color.primary_text_color));
        }

        break;
      case 12:case 13:case 14:case 15:case 16:case 17:
        background.setBackgroundColor(context.getResources().getColor(R.color.afternoon_color));
        toolbar.setBackgroundColor(context.getResources().getColor(R.color.afternoon_darker_color));
        for(TextView textView:textViews){
          textView.setTextColor(context.getResources().getColor(R.color.primary_text_color));
        }

        break;
    }
  }


  public static Uri saveScreenShotToSd(View view){
    // image naming and path  to include sd card  appending name you choose for file
    String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "temp_sc.jpeg";

    // create bitmap screen capture
    Bitmap bitmap;
    view.setDrawingCacheEnabled(true);
    bitmap = Bitmap.createBitmap(view.getDrawingCache());
    view.setDrawingCacheEnabled(false);

    OutputStream fout = null;
    File imageFile = new File(mPath);

    try {
      fout = new FileOutputStream(imageFile);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
      fout.flush();
      fout.close();
      return Uri.fromFile(imageFile);

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
   return null;
  }

  public static void delayButtonClick(final View view){
    view.setClickable(false);
    view.postDelayed(new Runnable() {
      @Override public void run() {
        view.setClickable(true);
      }
    },1000);
  }
}
