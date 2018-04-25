package com.example.basemarqueeview.util;

import java.util.List;

/**
 * Created by xxdr on 2018/4/20.
 */

public class ValueUtil {

  private ValueUtil() {
  }

  public static boolean isEmpty(List data) {
    if (data == null || data.isEmpty()) {
      return true;
    }
    return false;
  }

  public static String null2Value(String value) {
    return null2Value(value, "");
  }

  public static String null2Value(String value, String target) {
    if (value == null) {
      return target;
    } else {
      return value;
    }
  }
}
