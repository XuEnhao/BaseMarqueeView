package com.example.xxdr.marqueeview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  private TextViewMarqueeView marqueeView1;
  private TextViewMarqueeView marqueeView2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initView();

    ArrayList<String> list = new ArrayList<>();
    list.add("钓鱼岛是中国的！");
    list.add("https://codestuding.github.io/");
    list.add("安卓开发了解一下");
    marqueeView1.start(list);
    marqueeView2.start(list);
  }

  private void initView() {
    marqueeView1 = (TextViewMarqueeView) findViewById(R.id.marquee_view1);
    marqueeView2 = (TextViewMarqueeView) findViewById(R.id.marquee_view2);
  }
}
