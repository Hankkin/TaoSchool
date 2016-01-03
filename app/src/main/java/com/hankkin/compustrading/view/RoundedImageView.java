/**
 * 圆形控件，显示头像
 * by黄海杰 at：2015-4-7
 */
package com.hankkin.compustrading.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hankkin.compustrading.R;


public class RoundedImageView extends ImageView {

	public RoundedImageView(Context context) {
		super(context);
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private boolean hasBound = false;
	private int boundColor = -1;

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.RoundImageView);
			hasBound = a.getBoolean(
					R.styleable.RoundImageView_roundImageView_hasRound, false);
			boundColor = a.getColor(
					R.styleable.RoundImageView_roundImageView_roundColor,
					context.getResources().getColor(R.color.white));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Bitmap b = null;
		if (getDrawable() instanceof NinePatchDrawable) {
			NinePatchDrawable dr = (NinePatchDrawable) getDrawable();
			b = Bitmap.createBitmap(getWidth(), getHeight(),
					Config.ARGB_8888);
			Drawable drawable = getResources().getDrawable(
					R.drawable.defaut);
			Canvas canvas1 = new Canvas(b);
			drawable.setBounds(0, 0, canvas1.getWidth(), canvas1.getHeight());
			// drawable.draw(canvas);
			dr.draw(canvas1);
		} else {
			Drawable drawable = getDrawable();

			if (drawable == null) {
				return;
			}

			if (getWidth() == 0 || getHeight() == 0) {
				return;
			}
			b = ((BitmapDrawable) drawable).getBitmap();
		}
		if (b == null) {
			NinePatchDrawable drawable = (NinePatchDrawable) getResources()
					.getDrawable(R.drawable.defaut);
			b = Bitmap.createBitmap(getWidth(), getHeight(),
					Config.ARGB_8888);
			Canvas canvas1 = new Canvas(b);
			drawable.setBounds(0, 0, canvas1.getWidth(), canvas1.getHeight());
			drawable.draw(canvas1);
		}
		Bitmap bitmap = b.copy(Config.ARGB_8888, true);

		int w = getWidth(), h = getHeight();

		Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
		canvas.drawBitmap(roundBitmap, 0, 0, null);

	}

	public Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		if (bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffa19774;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f,
				sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f - 3,
				paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);

		// draw border
		if (hasBound) {
			Paint paint2 = new Paint();
			if (boundColor != -1) {
				paint2.setColor(boundColor);
			} else {
				paint2.setColor(Color.parseColor("#ffffff"));
			}
			paint2.setStyle(Paint.Style.STROKE);
			paint2.setStrokeWidth((float) pxFromDp(this.getContext(), 1));
			paint2.setAntiAlias(true);
			canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f,
					sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f - 3
							+ 1 - pxFromDp(this.getContext(), 1), paint2);
		}
		return output;
	}

	public static float pxFromDp(Context context, float dp) {
		return dp * context.getResources().getDisplayMetrics().density;
	}
}
