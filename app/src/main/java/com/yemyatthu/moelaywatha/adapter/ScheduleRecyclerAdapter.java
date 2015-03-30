package com.yemyatthu.moelaywatha.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by yemyatthu on 3/31/15.
 */
public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder> {

  @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    return null;
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {

  }

  @Override public int getItemCount() {
    return 0;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public TextView mTextView;
    public ViewHolder(TextView v) {
      super(v);
      mTextView = v;
    }
  }
}
