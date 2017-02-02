package com.wots.lutmaar.CustomView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.wots.lutmaar.R;

import java.lang.ref.WeakReference;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.CommonUtils;

/**
 * Created by Super Star on 23-06-2015.
 */
public class TwitterLoginButton extends Button {
    static final String TAG = "Twitter";
    static final String ERROR_MSG_NO_ACTIVITY = "TwitterLoginButton requires an activity. Override getActivity to provide the activity for this button.";
    final WeakReference<Activity> activityRef;
    volatile TwitterAuthClient authClient;
    OnClickListener onClickListener;
    Callback<TwitterSession> callback;

    public TwitterLoginButton(Context context) {
        this(context, (AttributeSet)null);
    }

    public TwitterLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 16842824);
    }

    public TwitterLoginButton(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, (TwitterAuthClient)null);
    }

    TwitterLoginButton(Context context, AttributeSet attrs, int defStyle, TwitterAuthClient authClient) {
        super(context, attrs, defStyle);
        this.activityRef = new WeakReference(this.getActivity());
        this.authClient = authClient;
        this.setupButton();
        this.checkTwitterCoreAndEnable();
    }

    @TargetApi(21)
    private void setupButton() {
        Resources res = this.getResources();
     //  super.setCompoundDrawablesWithIntrinsicBounds(res.getDrawable(com.twitter.sdk.android.core.R.drawable.tw__ic_logo_default), (Drawable)null, (Drawable)null, (Drawable)null);
     //   super.setCompoundDrawablePadding(res.getDimensionPixelSize(com.twitter.sdk.android.core.R.dimen.tw__login_btn_drawable_padding));
      //  super.setText(com.twitter.sdk.android.core.R.string.tw__login_btn_txt);
       // super.setTextColor(res.getColor(com.twitter.sdk.android.core.R.color.tw__solid_white));
     //   super.setTextSize(0, (float)res.getDimensionPixelSize(com.twitter.sdk.android.core.R.dimen.tw__login_btn_text_size));
       // super.setTypeface(Typeface.DEFAULT_BOLD);
      // super.setPadding(res.getDimensionPixelSize(com.twitter.sdk.android.core.R.dimen.tw__login_btn_left_padding), 0, res.getDimensionPixelSize(com.twitter.sdk.android.core.R.dimen.tw__login_btn_right_padding), 0);
        super.setBackgroundResource(R.drawable.icon_twitter);
        super.setOnClickListener(new LoginClickListener());
        if(Build.VERSION.SDK_INT >= 21) {
            super.setAllCaps(false);
        }

    }

    public void setCallback(Callback<TwitterSession> callback) {
        if(callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        } else {
            this.callback = callback;
        }
    }

    public Callback<TwitterSession> getCallback() {
        return this.callback;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == this.getTwitterAuthClient().getRequestCode()) {
            this.getTwitterAuthClient().onActivityResult(requestCode, resultCode, data);
        }

    }

    protected Activity getActivity() {
        if(this.getContext() instanceof Activity) {
            return (Activity)this.getContext();
        } else if(this.isInEditMode()) {
            return null;
        } else {
            throw new IllegalStateException("TwitterLoginButton requires an activity. Override getActivity to provide the activity for this button.");
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    TwitterAuthClient getTwitterAuthClient() {
        if(this.authClient == null) {
            Class var1 = TwitterLoginButton.class;
            synchronized(TwitterLoginButton.class) {
                if(this.authClient == null) {
                    this.authClient = new TwitterAuthClient();
                }
            }
        }

        return this.authClient;
    }

    private void checkTwitterCoreAndEnable() {
        if(!this.isInEditMode()) {
            try {
                TwitterCore.getInstance();
            } catch (IllegalStateException var2) {
                Fabric.getLogger().e("Twitter", var2.getMessage());
                this.setEnabled(false);
            }

        }
    }

    private class LoginClickListener implements OnClickListener {
        private LoginClickListener() {
        }

        public void onClick(View view) {
            this.checkCallback(TwitterLoginButton.this.callback);
            this.checkActivity((Activity)TwitterLoginButton.this.activityRef.get());
            TwitterLoginButton.this.getTwitterAuthClient().authorize((Activity)TwitterLoginButton.this.activityRef.get(), TwitterLoginButton.this.callback);
            if(TwitterLoginButton.this.onClickListener != null) {
                TwitterLoginButton.this.onClickListener.onClick(view);
            }

        }

        private void checkCallback(Callback callback) {
            if(callback == null) {
                CommonUtils.logOrThrowIllegalStateException("Twitter", "Callback must not be null, did you call setCallback?");
            }

        }

        private void checkActivity(Activity activity) {
            if(activity == null || activity.isFinishing()) {
                CommonUtils.logOrThrowIllegalStateException("Twitter", "TwitterLoginButton requires an activity. Override getActivity to provide the activity for this button.");
            }

        }
    }
}
