/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.com.exoplayer.player;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.MediaController;

import com.danikula.videocache.sample.R;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.VideoSurfaceView;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.util.Util;

/**
 * An activity that plays media using {@link DemoPlayer}.
 */
public class PlayerActivity extends Activity implements SurfaceHolder.Callback, OnClickListener,
    DemoPlayer.Listener,
    AudioCapabilitiesReceiver.Listener {

  private MediaController mediaController;
  private VideoSurfaceView surfaceView;

  private DemoPlayer player;
  private boolean playerNeedsPrepare;
  private long playerPosition;

  private String VIDEO_URL;

  private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
  private AudioCapabilities audioCapabilities;

  // Activity lifecycle

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    VIDEO_URL = "http://stream-1.vdomax.com:1935/vod/__definst__/mp4:youlove/youlove_xxx_7043.mp4/playlist.m3u8";

    setContentView(R.layout.player_activity);
    View root = findViewById(R.id.root);
    root.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
          toggleControlsVisibility();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
          view.performClick();
        }
        return true;
      }
    });
    root.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
          return mediaController.dispatchKeyEvent(event);
        }
        return false;
      }
    });
    audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getApplicationContext(), this);


    surfaceView = (VideoSurfaceView) findViewById(R.id.surface_view);
    surfaceView.getHolder().addCallback(this);

    mediaController = new MediaController(this);
    mediaController.setAnchorView(root);

    preparePlayer();

    //DemoUtil.setDefaultCookieManager();
  }

  @Override
  public void onResume() {
    super.onResume();
    //configureSubtitleView();

    // The player will be prepared on receiving audio capabilities.
    audioCapabilitiesReceiver.register();
  }

  @Override
  public void onPause() {
    super.onPause();
    if (!true) {
      releasePlayer();
    } else {
      player.setBackgrounded(true);
    }
    audioCapabilitiesReceiver.unregister();

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    releasePlayer();
  }

  // OnClickListener methods

  @Override
  public void onClick(View view) {
    //if (view == retryButton) {
      //preparePlayer();
    //}
  }

  // AudioCapabilitiesReceiver.Listener methods

  @Override
  public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
    boolean audioCapabilitiesChanged = !audioCapabilities.equals(this.audioCapabilities);
    if (player == null || audioCapabilitiesChanged) {
      this.audioCapabilities = audioCapabilities;
      releasePlayer();
      preparePlayer();
    } else if (player != null) {
      player.setBackgrounded(false);
    }
  }

  // Internal methods



  private void preparePlayer() {

    if (player == null) {
      String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
      player = new DemoPlayer(new HlsRendererBuilder(this, userAgent, VIDEO_URL.toString(), null,
              audioCapabilities));
      player.addListener(this);
      player.seekTo(playerPosition);
      playerNeedsPrepare = true;
      mediaController.setMediaPlayer(player.getPlayerControl());
      mediaController.setEnabled(true);

    }
    if (playerNeedsPrepare) {
      player.prepare();
      playerNeedsPrepare = false;
      updateButtonVisibilities();
    }
    player.setSurface(surfaceView.getHolder().getSurface());
    player.setPlayWhenReady(true);
  }

  private void releasePlayer() {
    if (player != null) {
      playerPosition = player.getCurrentPosition();
      player.release();
      player = null;
      //eventLogger.endSession();
      //eventLogger = null;
    }
  }

  // DemoPlayer.Listener implementation

  @Override
  public void onStateChanged(boolean playWhenReady, int playbackState) {
    if (playbackState == ExoPlayer.STATE_ENDED) {
      showControls();
    }
    String text = "playWhenReady=" + playWhenReady + ", playbackState=";
    switch(playbackState) {
      case ExoPlayer.STATE_BUFFERING:
        text += "buffering";
        break;
      case ExoPlayer.STATE_ENDED:
        text += "ended";
        break;
      case ExoPlayer.STATE_IDLE:
        text += "idle";
        break;
      case ExoPlayer.STATE_PREPARING:
        text += "preparing";
        break;
      case ExoPlayer.STATE_READY:
        text += "ready";
        break;
      default:
        text += "unknown";
        break;
    }
    //playerStateTextView.setText(text);
    updateButtonVisibilities();
  }

  @Override
  public void onError(Exception e) {

    playerNeedsPrepare = true;
    updateButtonVisibilities();
    showControls();
  }

  @Override
  public void onVideoSizeChanged(int width, int height, float pixelWidthAspectRatio) {
    //shutterView.setVisibility(View.GONE);
    surfaceView.setVideoWidthHeightRatio(
        height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
  }

  // User controls

  private void updateButtonVisibilities() {
    //retryButton.setVisibility(playerNeedsPrepare ? View.VISIBLE : View.GONE);
    //videoButton.setVisibility(haveTracks(DemoPlayer.TYPE_VIDEO) ? View.VISIBLE : View.GONE);
    //audioButton.setVisibility(haveTracks(DemoPlayer.TYPE_AUDIO) ? View.VISIBLE : View.GONE);
    //textButton.setVisibility(haveTracks(DemoPlayer.TYPE_TEXT) ? View.VISIBLE : View.GONE);
  }


  private void toggleControlsVisibility()  {
    if (mediaController.isShowing()) {
      mediaController.hide();
      //debugRootView.setVisibility(View.GONE);
    } else {
      showControls();
    }
  }

  private void showControls() {
    mediaController.show(0);
    //debugRootView.setVisibility(View.VISIBLE);
  }

  // DemoPlayer.TextListener implementation


  // SurfaceHolder.Callback implementation

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    if (player != null) {
      player.setSurface(holder.getSurface());
    }
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    // Do nothing.
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    if (player != null) {
      player.blockingClearSurface();
    }
  }



}
