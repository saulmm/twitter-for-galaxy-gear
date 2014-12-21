package tweetgear.com.saulmm.executor;

import android.os.Handler;
import android.os.Looper;

/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 * @author Fernando Cejas (the android10 coder)
 */
public class UIThread implements PostExecutionThread {

  private static class LazyHolder {
    private static final UIThread INSTANCE = new UIThread();
  }

  public static UIThread getInstance() {
    return LazyHolder.INSTANCE;
  }

  private final Handler handler;

  private UIThread() {
    this.handler = new Handler(Looper.getMainLooper());
  }


  @Override
  public void post(Runnable runnable) {
    handler.post(runnable);
  }
}
