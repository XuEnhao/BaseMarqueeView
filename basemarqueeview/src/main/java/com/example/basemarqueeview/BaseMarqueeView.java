package com.example.basemarqueeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AnimRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;
import com.example.basemarqueeview.util.ValueUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xxdr on 2018/4/20.
 */
public abstract class BaseMarqueeView<T> extends ViewFlipper {

  private static final int DIRECTION_BOTTOM_TO_TOP = 0;
  private static final int DIRECTION_TOP_TO_BOTTOM = 1;
  private static final int DIRECTION_RIGHT_TO_LEFT = 2;
  private static final int DIRECTION_LEFT_TO_RIGHT = 3;

  private boolean mHasSetAnimDuration = false;
  private int mAnimDuration = 1000;
  private List<T> mData = new ArrayList<>();
  private int mPosition;
  private boolean mIsStarted = false;
  private OnItemClickListener<T> mOnItemClickListener;
  // 动画
  @AnimRes
  private int mInAnimResId = R.anim.base_marquee_bottom_in;
  @AnimRes
  private int mOutAnimResId = R.anim.base_marquee_top_out;

  public BaseMarqueeView(Context context, AttributeSet attrs) {
    super(context, attrs);

    TypedArray typedArray = context
        .obtainStyledAttributes(attrs, R.styleable.BaseMarqueeViewStyle, 0, 0);
    int interval = typedArray.getInteger(R.styleable.BaseMarqueeViewStyle_interval, 3000);
    mHasSetAnimDuration = typedArray.hasValue(R.styleable.BaseMarqueeViewStyle_duration);
    mAnimDuration = typedArray
        .getInteger(R.styleable.BaseMarqueeViewStyle_duration, mAnimDuration);
    boolean hasDirection = typedArray.hasValue(R.styleable.BaseMarqueeViewStyle_direction);
    int direction = typedArray
        .getInt(R.styleable.BaseMarqueeViewStyle_direction, DIRECTION_BOTTOM_TO_TOP);
    typedArray.recycle();

    if (hasDirection) {
      switch (direction) {
        case DIRECTION_BOTTOM_TO_TOP:
          mInAnimResId = R.anim.base_marquee_bottom_in;
          mOutAnimResId = R.anim.base_marquee_top_out;
          break;
        case DIRECTION_TOP_TO_BOTTOM:
          mInAnimResId = R.anim.base_marquee_top_in;
          mOutAnimResId = R.anim.base_marquee_bottom_out;
          break;
        case DIRECTION_RIGHT_TO_LEFT:
          mInAnimResId = R.anim.base_marquee_right_in;
          mOutAnimResId = R.anim.base_marquee_left_out;
          break;
        case DIRECTION_LEFT_TO_RIGHT:
          mInAnimResId = R.anim.base_marquee_left_in;
          mOutAnimResId = R.anim.base_marquee_right_out;
          break;
      }
    }
    setFlipInterval(interval);
  }

  /**
   * 开启动画
   */
  public void start(List<T> data) {
    start(data, mInAnimResId, mOutAnimResId);
  }

  public void start(List<T> data, @AnimRes int inAnimResId, @AnimRes int outAnimResID) {
    if (ValueUtil.isEmpty(data)) {
      return;
    }
    mData = data;
    postStart(inAnimResId, outAnimResID);
  }

  public interface OnItemClickListener<T> {

    /**
     * 设置子视图的点击事件
     */
    void onItemClick(View view, T data, int position);
  }

  protected void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
    this.mOnItemClickListener = onItemClickListener;
  }

  protected int getPosition() {
    return (int) getCurrentView().getTag();
  }

  /**
   * @return 返回 ViewFlipper 实际上需要创建的子视图数量，推荐小于等于 mData 的大小
   */
  protected abstract int getRealViewCounts();

  /**
   * 在子类中创建子视图
   */
  protected abstract View onCreateView(Context context, ViewGroup parent);

  /**
   * 在子类中对子视图进行数据绑定
   */
  protected abstract void onBind(View view, T data);

  /**
   * 开启自动滑动
   */
  private void postStart(final @AnimRes int inAnimResId, final @AnimRes int outAnimResID) {
    post(new Runnable() {
      @Override
      public void run() {
        start(inAnimResId, outAnimResID);
      }
    });
  }

  /**
   * 创建子视图
   */
  private View createView() {
    View view = getChildAt((getDisplayedChild() + 1) % getRealViewCounts());
    if (view == null) {
      view = onCreateView(getContext(), this);
    }
    view.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mOnItemClickListener != null) {
          mOnItemClickListener.onItemClick(v, mData.get(getPosition()), getPosition());
        }
      }
    });
    view.setTag(mPosition);
    return view;
  }

  private void start(final @AnimRes int inAnimResId, final @AnimRes int outAnimResID) {
    removeAllViews();// 清空子视图并设置 mWhichChild 为0
    clearAnimation();// 看名字就知道意思吧！！！

    mPosition = 0;
    View view = createView();
    addView(view);
    onBind(view, mData.get(mPosition));

    if (mData.size() > 1) {
      setAnimations(inAnimResId, outAnimResID);
      startFlipping();// 真正的开启自动滑动方法
    }

    if (getInAnimation() != null) {
      getInAnimation().setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
          if (mIsStarted) {
            animation.cancel();
          }
          mIsStarted = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
          mPosition++;
          if (mPosition >= mData.size()) {
            mPosition = 0;
          }
          View view = createView();
          if (view.getParent() == null) {// 防止重复添加
            addView(view);
          }
          onBind(view, mData.get(mPosition));
          mIsStarted = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
      });
    }
  }

  /**
   * 设置进入动画和离开动画
   */
  private void setAnimations(@AnimRes int inAnimResId, @AnimRes int outAnimResID) {
    Animation inAnim = AnimationUtils.loadAnimation(getContext(), inAnimResId);
    if (mHasSetAnimDuration) {
      inAnim.setDuration(mAnimDuration);
    }
    setInAnimation(inAnim);

    Animation outAnim = AnimationUtils.loadAnimation(getContext(), outAnimResID);
    if (mHasSetAnimDuration) {
      outAnim.setDuration(mAnimDuration);
    }
    setOutAnimation(outAnim);
  }
}