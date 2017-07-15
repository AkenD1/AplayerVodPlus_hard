package com.ev.player;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ev.player.util.Logger;
import com.ev.player.view.TimerView;
import com.aplayer.vod.plus.R;
import com.aplayer.aplayerandroid.APlayerAndroid;

public class OSDControlBar extends OSD implements OnKeyListener {

	private View mView;
	public static final int TRAFFIC_SPACNIG = 3;
	public static final int FORWARD_TIME = 30;
	public static final int DIRECTION_FOWARD = 0;
	public static final int DIRECTION_BACK = 1;
	private LinearLayout mControlBar;
	private TimerView mTextCurrentTime;
	private TimerView mTextTotalTime;
	public SeekBar mSeekVideo;
	public SeekBar mSeekCenter;
	private RelativeLayout mProgressLayOut;
	private ImageButton mBtnPauseBig;
	private ImageButton mBtnPlay;
	private ImageButton mBtnPre;
	private ImageButton mBtnNext;
	public int mTotalTime;
	private Timer mVolumeOldTimer;

	// private Button mBtnVideoSelect;

	private MyPlayerActivity mVodPlayer;

	public Timer mTimerSeek = null;
	public TimerTask mTimerTaskSeek = null;
	public Timer mTimerSeekCenter = null;
	public TimerTask mTimerTaskSeekCenter = null;
	public int descPos = 0;

	private APlayerAndroid mAPlayer;
	private SurfaceView mSurfaceView;
	private LinearLayout mTopLinearLayout;
	private int seekBarProgress=0;
	
	private String tvOrPad = "0";//0为机顶盒，1为pad

	public OSDControlBar(MyPlayerActivity vodPlayerActivity, View view, APlayerAndroid aPlayer, SurfaceView sView,
			LinearLayout topLinearLayout, String tvOrPad) {
		mVodPlayer = vodPlayerActivity;
		mView = view;
		mAPlayer = aPlayer;
		mSurfaceView = sView;
		mTopLinearLayout = topLinearLayout;
		this.tvOrPad = tvOrPad;
		initWidget();
	}

	private void initWidget() {
		mControlBar = (LinearLayout) mView.findViewById(R.id.control_bar);
		mTextCurrentTime = (TimerView) mView.findViewById(R.id.text_current_time);
		mTextTotalTime = (TimerView) mView.findViewById(R.id.text_total_time);
		mSeekVideo = (SeekBar) mView.findViewById(R.id.video_progress);
		mProgressLayOut = (RelativeLayout) mView.findViewById(R.id.gesture_progress_layout);
		mSeekCenter = (SeekBar) mView.findViewById(R.id.video_progress_center);
		mBtnPauseBig = (ImageButton) mView.findViewById(R.id.pause_big);
		mBtnPlay = (ImageButton) mView.findViewById(R.id.foward_play);
		mBtnPre = (ImageButton) mView.findViewById(R.id.foward_previous);
		mBtnNext = (ImageButton) mView.findViewById(R.id.foward_next);

		mSeekVideo.setOnKeyListener(this);
		mBtnPauseBig.setOnKeyListener(this);
		mBtnPlay.setOnKeyListener(this);
		mBtnPre.setOnKeyListener(this);
		mBtnNext.setOnKeyListener(this);
//		mBtnPlay.setOnClickListener(mPlayClickListener);
//		mBtnPauseBig.setOnClickListener(mPlayClickListener);
//		mBtnPre.setOnClickListener(mVodSelectListener);
//		mBtnNext.setOnClickListener(mVodSelectListener);
		
		mSeekVideo.setOnSeekBarChangeListener(mSeekBarChangeListener);
		
		mBtnPlay.setOnTouchListener(mBtPlayTouchListener);
		mBtnPre.setOnTouchListener(mBtPreTouchListener);
		mBtnNext.setOnTouchListener(mBtPreTouchListener);
		if(tvOrPad.equals("0")){
			mBtnPlay.setOnClickListener(mPlayClickListener);
			mBtnPre.setOnClickListener(mBtPreClickListener);
			mBtnNext.setOnClickListener(mBtNextClickListener);
		}
	}
	
	private OnClickListener mBtPreClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mSeekVideo.setProgress(0);
			setNewDismissClock();
			mVodPlayer.play2WichVod(true);
//			mVodPlayer.onPause();
//			mVodPlayer.close();
		}
	};
	
	private OnClickListener mBtNextClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mSeekVideo.setProgress(0);
			setNewDismissClock();
			mVodPlayer.play2WichVod(false);
		}
	};
	
	private OnTouchListener mBtPlayTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_UP){
				setNewDismissClock();
				mVodPlayer.playOrPauseSTB();
			}
			return false;
		}
	};
	private OnTouchListener mBtPreTouchListener = new OnTouchListener() {
			
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_UP){
				mSeekVideo.setProgress(0);
				setNewDismissClock();
				boolean isPreVod = v == mBtnPre ? true : false;
				mVodPlayer.play2WichVod(isPreVod);
			}
			return false;
		}
	};

	private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
		
		//拖动进度条开始拖动的时候调用
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			seekBarProgress = mAPlayer.getPosition();
		}
		
		//拖动进度条停止拖动的时候调用
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			userSeekPlayProgress(seekBarProgress);
		}
		
		//拖动进度条进度改变的时候调用
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// TODO Auto-generated method stub
			if (null == mAPlayer || !fromUser) {
                return;
            }
			seekBarProgress = progress;
			setNewDismissClock();
		}
	};
	private void userSeekPlayProgress(int seekPostionMs) {
        int currentPlayPos = mAPlayer.getPosition();
        boolean isChangeOverSeekGate = isOverSeekGate(seekPostionMs, currentPlayPos);
        if (!isChangeOverSeekGate) {
            //避免拖动粒度过细，拖动时频繁定位影响体验
            return;
        }
        seek(seekPostionMs);
    }
	private boolean isOverSeekGate(int seekBarPositionMs, int currentPlayPosMs) {
        final int SEEK_MIN_GATE_MS = 1000;
        boolean isChangeOverSeekGate = Math.abs(currentPlayPosMs - seekBarPositionMs) > SEEK_MIN_GATE_MS;
        return isChangeOverSeekGate;
    }
	
	private OnClickListener mVodSelectListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			boolean isPreVod = v == mBtnPre ? true : false;
			mVodPlayer.play2WichVod(isPreVod);
		}
	};

	private OnClickListener mPlayClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setNewDismissClock();
			mVodPlayer.playOrPauseSTB();
		}
	};

	public void setPlayIcon(boolean isPlay) {
		if (isPlay) {
			mBtnPlay.setBackgroundResource(R.drawable.bg_p_pause);
		} else {
			mBtnPlay.setBackgroundResource(R.drawable.bg_p_play);
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		setNewDismissClock();
		AudioManage audioManage = AudioManage.getInstance();
		if (event.getAction() == KeyEvent.ACTION_DOWN)
			switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
				// audioManage.upVolume(mVodPlayer);
				// mOSDVolume.setVolume();
				// mOSDManager.showOSD(mOSDVolume);
				return false;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				// audioManage.lowerVolume(mVodPlayer);
				// mOSDVolume.setVolume();
				// mOSDManager.showOSD(mOSDVolume);
				return false;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				if (v == mSeekVideo)
					mVodPlayer.playOrPauseSTB();
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (v == mSeekVideo) {
					seekFoward(true);
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if (v == mSeekVideo) {
					seekFoward(false);
					return true;
				}
				break;
			}
		return false;
	}

	public void seekFoward(boolean isfoward) {
		mVodPlayer.stopTimer();//停止进度条更新
		
		int fowardStep = isfoward ? 1 : -1;
		descPos = mSeekVideo.getProgress() + fowardStep * FORWARD_TIME * 1000;
		mSeekVideo.setProgress(descPos);
		// check for small stream while seeking
		if (descPos >= mTotalTime)
			descPos = mTotalTime;
		if (descPos <= 0)
			descPos = 0;
		/*
		 * �û��ƶ�seekbar�󣬱���̫Ƶ����SEEK��2s������ת
		 */
		if (null != mTimerSeek) {
			mTimerSeek.cancel();
			mTimerSeek = null;
			mTimerTaskSeek.cancel();
			mTimerTaskSeek = null;
		}
		mTimerSeek = new Timer();
		mTimerTaskSeek = new TimerTask() {
			@Override
			public void run() {
				if(Configs.debug){
					System.out.println("descPos = "+descPos+"     mTotalTime="+mTotalTime);
				}
				seek(descPos);
				mVodPlayer.startTimer();//开始进度条更新
			}
		};
		mTimerSeek.schedule(mTimerTaskSeek, Configs.SEEK_TIME);
	}
	
	public void seekCenterBarFoward(boolean isfoward) {
		mProgressLayOut.setVisibility(View.VISIBLE);
		int fowardStep = isfoward ? 1 : -1;
		descPos = mSeekVideo.getProgress() + fowardStep * FORWARD_TIME * 1000;
		mSeekCenter.setProgress(descPos);
		// check for small stream while seeking
		if (descPos >= mTotalTime)
			descPos = mTotalTime;
		if (descPos <= 0)
			descPos = 0;
		/*
		 * �û��ƶ�seekbar�󣬱���̫Ƶ����SEEK��2s������ת
		 */
		if (null != mTimerSeekCenter) {
			mTimerSeekCenter.cancel();
			mTimerSeekCenter = null;
			mTimerTaskSeekCenter.cancel();
			mTimerTaskSeekCenter = null;
		}
		mTimerSeekCenter = new Timer();
		mTimerTaskSeekCenter = new TimerTask() {
			@Override
			public void run() {
				mProgressLayOut.setVisibility(View.GONE);
				seek(descPos);
			}
		};
		mTimerSeekCenter.schedule(mTimerTaskSeekCenter, Configs.SEEK_TIME);
	}

	public void seek(int position) {
		//判断当前是否已由上次缓冲切换至播放状态，如已切换方可执行再次缓冲，否则出现错误
		if (mAPlayer.getState() == APlayerAndroid.PlayerState.APLAYER_PLAYING){
			mAPlayer.pause();
			mAPlayer.setPosition(position);
		}
	}
	public void setProcessBar(int progress){
		mSeekVideo.setProgress(progress);
	}
	public void setSecondaryProcessBar(int secondaryProgress){
		mSeekVideo.setSecondaryProgress(secondaryProgress);
	}

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			setVisibility(View.GONE);
		}
	};

	private void setNewDismissClock() {
		if (null != mVolumeOldTimer)
			mVolumeOldTimer.cancel();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(0);
			}
		};
		Timer timer = new Timer();
		timer.schedule(timerTask, OSD.OSD_SHOW_TIME * 1000);
		mVolumeOldTimer = timer;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setVisibility(int visible) {
		switch (visible) {
		case View.GONE:
			mSurfaceView.setBackgroundResource(0);
			break;
		case View.VISIBLE:
			mSeekVideo.requestFocus();
			mSeekVideo.requestFocusFromTouch();
			mSurfaceView.setBackgroundDrawable(mVodPlayer.getResources().getDrawable(R.drawable.surfaceview_bg));
			if(Configs.debug){
				System.out.println("mAPlayer.getPosition() ===== "+mAPlayer.getPosition());
				System.out.println("mSeekVideo.getProgress() ===== "+mSeekVideo.getProgress());
			}
			setSeekProgress(mAPlayer.getPosition());
			break;
		}
		mTopLinearLayout.setVisibility(visible);
		mControlBar.setVisibility(visible);
		setNewDismissClock();
	}
	
	public void setSeekProgress(int position){
		mSeekVideo.setProgress(position);
	}

	public int getCurrentProgress() {
		return mSeekVideo.getProgress();
	}

	public void setTotalTime(int totalTime) {
		mTotalTime = totalTime;
		mSeekVideo.setMax(totalTime);
		mTextTotalTime.setTextByMillisecond(totalTime);
	}

	public void setCurrentPlayTime(int currentPlayTime) {
		mTextCurrentTime.setTextByMillisecond(currentPlayTime);
	}

	public void setVodSelectFocusable(boolean hasPre, boolean hasNext) {
		int preDrawableRes = hasPre ? R.drawable.bg_p_previous : R.drawable.p_icon_pre_unfocus;
		mBtnPre.setFocusable(hasPre);
		mBtnPre.setFocusableInTouchMode(hasPre);
		mBtnPre.setBackgroundResource(preDrawableRes);

		int nextDrawableRes = hasNext ? R.drawable.bg_p_next : R.drawable.p_icon_next_unfocus;
		mBtnNext.setFocusable(hasNext);
		mBtnNext.setFocusableInTouchMode(hasNext);
		mBtnNext.setBackgroundResource(nextDrawableRes);
	}

	public void setSecondProgress(int second) {
		mSeekVideo.setSecondaryProgress(second);
	}

	public int getVisibility() {
		return mControlBar.getVisibility();
	}

}
