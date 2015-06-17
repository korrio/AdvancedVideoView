package com.jit.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * 自动全屏的VideoView
 */
public class FullScreenSurfaceView extends SurfaceView {

	private int videoWidth;
	private int videoHeight;

	public FullScreenSurfaceView(Context context) {
		super(context);
	}

	public FullScreenSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FullScreenSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(videoWidth, widthMeasureSpec);
		int height = getDefaultSize(videoHeight, heightMeasureSpec);
		if (videoWidth > 0 && videoHeight > 0) {
			if (videoWidth * height > width * videoHeight) {
				height = width * videoHeight / videoWidth;
			} else if (videoWidth * height < width * videoHeight) {
				width = height * videoWidth / videoHeight;
			}
		}
		setMeasuredDimension(width, height);
	}

	public int getVideoWidth() {
		return videoWidth;
	}

	public void setVideoWidth(int videoWidth) {
		this.videoWidth = videoWidth;
	}

	public int getVideoHeight() {
		return videoHeight;
	}

	public void setVideoHeight(int videoHeight) {
		this.videoHeight = videoHeight;
	}

}
