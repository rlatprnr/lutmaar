package com.wots.lutmaar.CustomView.imageshape;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

public class TopRoundedImageView extends ShaderImageView {
    static Context context;
    public TopRoundedImageView(Context context) {
        super(context);
        TopRoundedImageView.context=context;
    }

    public TopRoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TopRoundedImageView.context=context;
    }

    public TopRoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TopRoundedImageView.context=context;
    }
    public static Bitmap getBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
	        /*Canvas canvas = new Canvas(bitmap);
	        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	        paint.setColor(Color.BLACK);
	        canvas.drawOval(new RectF(0.0f, 0.0f, width, height), paint);*/

        Canvas canvas = new Canvas(bitmap);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = convertDpToPixel(5);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// draw round
        // 4Corner

	       /* if (true)
	        {
	            Rect rectTL = new Rect(0, 0, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
	            canvas.drawRect(rectTL, paint);
	        }
	        if (true)
	        {
	            Rect rectTR = new Rect(bitmap.getWidth() / 2, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
	            canvas.drawRect(rectTR, paint);
	        }*/

        Rect rectBR = new Rect(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawRect(rectBR, paint);

        Rect rectBL = new Rect(0, bitmap.getHeight() / 2, bitmap.getWidth() / 2, bitmap.getHeight());
        canvas.drawRect(rectBL, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return bitmap;
    }

    @Override
    public Bitmap getBitmap() {
        return getBitmap(getWidth(), getHeight());
    }

    public static float convertDpToPixel(float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /*@Override
    public ShaderHelper createImageViewHelper() {
        return new RoundedShader();
    }*/
}
