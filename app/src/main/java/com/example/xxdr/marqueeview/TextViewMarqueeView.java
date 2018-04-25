package com.example.xxdr.marqueeview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.basemarqueeview.BaseMarqueeView;

/**
 * Created by xxdr on 2018/4/20.
 */

public class TextViewMarqueeView extends BaseMarqueeView<String> implements
    BaseMarqueeView.OnItemClickListener<String> {

  public TextViewMarqueeView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setOnItemClickListener(this);
  }

  @Override
  protected int getRealViewCounts() {
    return 2;
  }

  @Override
  protected View onCreateView(Context context, ViewGroup parent) {
    View view = LayoutInflater.from(context).inflate(R.layout.marquee_text_item, parent, false);
    return view;
  }

  @Override
  protected void onBind(View view, String data) {
    TextView textView = (TextView) view.findViewById(R.id.text);
    textView.setText(data);
  }

  @Override
  public void onItemClick(View view, String data, int position) {
    Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
  }
}
