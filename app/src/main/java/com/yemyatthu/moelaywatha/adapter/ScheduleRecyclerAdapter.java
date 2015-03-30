package com.yemyatthu.moelaywatha.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.yemyatthu.moelaywatha.R;
import com.yemyatthu.moelaywatha.model.Weather;
import java.util.List;

/**
 * Created by yemyatthu on 3/31/15.
 */
public class ScheduleRecyclerAdapter
    extends RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder> {
  List<Weather> mWeathers;
  public ScheduleRecyclerAdapter() {

  }
  public void replaceAll(List<Weather> weathers){
    mWeathers = weathers;
    notifyDataSetChanged();
  }
  @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.item_schedule, viewGroup, false);
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {
    if (i % 2 == 0) {
      viewHolder.mScheduleDate.setText("Divided by 2");
    } else {
      viewHolder.mScheduleDate.setText("Not divided by 2");
    }
  }

  @Override public int getItemCount() {
    return 20;
  }

  /**
   * This class contains all butterknife-injected Views & Layouts from layout file 'item_schedule.xml'
   * for easy to all layout elements.
   *
   * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
   */

  public static class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    @InjectView(R.id.schedule_icon) ImageView mSchecduleIcon;
    @InjectView(R.id.schedule_date) TextView mScheduleDate;
    @InjectView(R.id.schedule_degree) TextView mScheduleDegree;

    public ViewHolder(View v) {
      super(v);
      ButterKnife.inject(this,v);
    }
  }

}
