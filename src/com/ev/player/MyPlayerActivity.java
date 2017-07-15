package com.ev.player;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;


import com.aplayer.vod.plus.R;
import com.aplayer.aplayerandroid.APlayerAndroid;
import com.ev.player.adapter.VideoAdapter;
import com.ev.player.history.HistoryDAO;
import com.ev.player.history.HistoryItem;
import com.ev.player.model.VodVideo;
import com.ev.player.util.DensityUtil;
import com.ev.player.util.DeviceFun;
import com.ev.player.util.Logger;
import com.ev.player.util.MACUtils;
import com.ev.player.util.MD5Util;
import com.ev.player.util.ScreenUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyPlayerActivity extends Activity implements OnKeyListener,OnTouchListener, OnGestureListener {
	private static final String SYS_MUSIC_COMMAND = "com.android.music.musicservicecommand.pause";
	private static final String S_COMMAND = "command";
	private static final String S_STOP = "stop";
	private static final String PLAY_SERVER = "http://127.0.0.1:9906/";
	private static final String GET_FORCE_PLAY_INFO = "http://127.0.0.1:9906/api?func=query_chan_info&id=";
	private SurfaceView videoPlayView;
	private APlayerAndroid aPlayerAndroid;
	private List<VodVideo> mListVideos;/* 播放列表 */
	private VodVideo now_video;/* 当前播放变量 */
	private int mPlayVodPos;/* 当前播放集数位置 */
	private String now_httpUrl = null;

	private TextView tv_info;
	private View mContainer;
	private OSDManager mOSDManager;
	private OSDControlBar mOSDControl;

	private String mForcePlayInfo;
	public boolean isLoadingVod = true;
	private RelativeLayout vodLoadAnimView;
	private RelativeLayout vodBuffer;
	private ImageView progressImageView;
	
	private TextView mBuffer_txt;
	private TextView mTvVideoSelect;
	private GridView mGvVideoList;
	private VideoAdapter mVodAdapter;
	private LinearLayout mGVList_linearLayout;
	private LinearLayout mtop_linearLayout;
	private TextView mTextVideoName;
	private TextView mTextVideofName;
	private boolean isShowVideoList = false;
	
	private String mVodTitle = "";
	boolean isPlayHistory = false;
	private long mHistoryPlayPos = 0;
	private String mCid;
	private String Programjson;
	public Logger logger = Logger.getLogger();
	
	private Timer mTimerCache;
	private TimerTask mTimerTaskCache;
	//buffer时间差
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private Date beginDate = null;
	private Date endDate = null;
	long between = 0;
	
	private RelativeLayout mBigPauseLayOut;
	
	//7-11修改
	private boolean currentDecodeType = Configs.DECODE_DEFAULT;//当前解码方式，取默认值
	private Handler aPlayerHandler;
	private final int INITDATA_COMPLETE = 1;
	private TextView mTextBufferPrompt;
	private String tvOrPad = "0";//0为机顶盒，1为pad
	private ImageButton mBtnChoice;
	
	private final int PLAY_HTTP = 2;
	private final int PLAY_FORCEUDP = 3;
	
	public void play2WichVod(boolean is2previous) {
		mOSDControl.setVisibility(View.GONE);
		mBigPauseLayOut.setVisibility(View.GONE);
		if (checkCanPlay(is2previous)) {
			now_video = mListVideos.get(mPlayVodPos);
			isLoadingVod = true;
			switchPlay();
		} else {
			finish();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		stopSysMusic();
		initData();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 设置屏幕常亮
		getScreenSize();// 取得播放器宽高
		initwidget();
		initHandler();
		initAPlayer();
		initOSDManager();
		initLoadAnim();// 初始化加载动画
		if(Configs.debug){
			System.out.println("onCreate  sendEmptyMessage  INITDATA_COMPLETE");
		}
		aPlayerHandler.sendEmptyMessage(INITDATA_COMPLETE);
	}
	
	@SuppressLint("HandlerLeak")
	private void initHandler() {
		// TODO Auto-generated method stub
		aPlayerHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case INITDATA_COMPLETE:
					mVodAdapter = new VideoAdapter(getApplicationContext(), mListVideos);
					mGvVideoList.setAdapter(mVodAdapter);
					StartPlay();
					break;
				case PLAY_HTTP:
					String videoHttpUrl = msg.obj.toString();
					if(aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_READ){
						aPlayerAndroid.open(videoHttpUrl);
						refreshSurfaceView();
					}else{
						refreshSurfaceView();
						aPlayerAndroid.pause();
						aPlayerAndroid.close();
					}
					break;
				case PLAY_FORCEUDP:
					String videoUrl = msg.obj.toString();
					if(Configs.debug){
						System.out.println("--- force download success... --- "+" getState() = "+aPlayerAndroid.getState()+"  openUrl="+videoUrl);
					}
					if(aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_READ){
						aPlayerAndroid.open(videoUrl);
						refreshSurfaceView();
					}
					break;
				}
				super.handleMessage(msg);
			}
		};
	}
	private void stopSysMusic(){
		try{
			Intent intent = new Intent();
	        intent.setAction(SYS_MUSIC_COMMAND);
	        intent.putExtra(S_COMMAND,S_STOP);
	        sendBroadcast(intent);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void initLoadAnim() {
		progressImageView.setImageResource(R.anim.vod_load_animation);
		AnimationDrawable ad = (AnimationDrawable) progressImageView.getDrawable();
		ad.start();
	}

	/**
	 * 重置播放器对象
	 */
	private void destoryAndInitAplayer() {
		// TODO Auto-generated method stub
		if(aPlayerAndroid!=null){
			aPlayerAndroid.destroy();
			initAPlayer();
		}
	}
	
	private void initAPlayer() {
		// TODO Auto-generated method stub
		System.out.println("========================initAPlayer==============");
		aPlayerAndroid = new APlayerAndroid();
		aPlayerAndroid.useSystemPlayer(currentDecodeType);// 使用迅雷播放器插件，如使用系统播放器      获取时长处需要修改方法.
		aPlayerAndroid.setView(videoPlayView);
		registerAplayerLister();// 注册播放器监听事件
	}
	
	private void initOSDManager() {
		// TODO Auto-generated method stub
		// 初始化控制条
//		if(mOSDManager!=null){
//			mOSDManager.closeAllOSD();
//			mOSDManager = null;
//		}
//		mOSDControl = null;
		mOSDManager = new OSDManager();
		mOSDControl = new OSDControlBar(MyPlayerActivity.this, mContainer, aPlayerAndroid, videoPlayView,
				mtop_linearLayout, tvOrPad);
		mOSDManager.addOSD(OSDManager.OSD_CONTROLBAR, mOSDControl);
	}
	
	private void initOSDCONTROLBAR() {
		if(mOSDManager!=null && mOSDControl!=null){
			mOSDManager.deleteOSD(OSDManager.OSD_CONTROLBAR);
			mOSDControl = new OSDControlBar(MyPlayerActivity.this, mContainer, aPlayerAndroid, videoPlayView,
					mtop_linearLayout, tvOrPad);
			mOSDManager.addOSD(OSDManager.OSD_CONTROLBAR, mOSDControl);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		tvOrPad = intent.getStringExtra("tvOrPad");
		if(tvOrPad.equals("0")){
			setContentView(R.layout.myplayer720);
		}else{
			setContentView(R.layout.myplayer);
		}
		mListVideos = (List<VodVideo>) intent.getSerializableExtra("videolist");
		mVodTitle = intent.getStringExtra("programName");
		Programjson=intent.getStringExtra("mVodProgram");
		mCid = intent.getStringExtra("Cid");
		isPlayHistory = intent.getBooleanExtra("hasHistory", false);
		if(isPlayHistory){
			mPlayVodPos = intent.getIntExtra("history_vod_index", 0);
			mHistoryPlayPos = intent.getIntExtra("history_vod_pos", 0);
		} else {
			mPlayVodPos = intent.getIntExtra("index",0);
		}
		
		if (mPlayVodPos < mListVideos.size()) {
			now_video = mListVideos.get(mPlayVodPos);
		}
	}

	private void initwidget() {
		// TODO Auto-generated method stub

		mContainer = findViewById(R.id.osd_container);
		
		// Load 动画
		vodLoadAnimView = (RelativeLayout) findViewById(R.id.vod_load_anim);
		progressImageView = (ImageView) findViewById(R.id.vod_load_img);
		
		mTextBufferPrompt = (TextView) findViewById(R.id.buffer_prompt);
		
		mBigPauseLayOut = (RelativeLayout) findViewById(R.id.container_big_pause);
		ImageButton mBugPause = (ImageButton) findViewById(R.id.pause_big);
		mBugPause.setOnClickListener(mIbtClickListener);
		
		vodBuffer = (RelativeLayout) findViewById(R.id.vod_buffer);
		mBuffer_txt = (TextView) findViewById(R.id.buffer_txt);

		mtop_linearLayout = (LinearLayout) findViewById(R.id.top_linearLayout);
		mTextVideoName = (TextView) findViewById(R.id.video_name);
		mTextVideofName = (TextView) findViewById(R.id.video_fname);

		mTvVideoSelect = (TextView) findViewById(R.id.select_video);
		mBtnChoice = (ImageButton) findViewById(R.id.choiceTv);
		mTvVideoSelect.setOnTouchListener(mtxtTouchListener);
		mBtnChoice.setOnTouchListener(mtxtTouchListener);
		if(tvOrPad.equals("0")){
			mBtnChoice.setOnClickListener(mBtClickListener);
		}
		mGvVideoList = (GridView) findViewById(R.id.video_list);
		mGvVideoList.setOnItemClickListener(mGvOnItemClickListener);
		
		mGVList_linearLayout = (LinearLayout) findViewById(R.id.gvList_linearLayout);
		mGVList_linearLayout.setOnTouchListener(this);
		
		tv_info = (TextView) findViewById(R.id.tv_info);
		videoPlayView = (SurfaceView) findViewById(R.id.aplayer_surfaceView);
		videoPlayView.setOnTouchListener(this);
		videoPlayView.setOnKeyListener(this);

		// scroll控件
		gesture_volume_layout = (RelativeLayout) findViewById(R.id.gesture_volume_layout);
		gesture_bright_layout = (RelativeLayout) findViewById(R.id.gesture_bright_layout);
		gesture_progress_layout = (RelativeLayout) findViewById(R.id.gesture_progress_layout);
		geture_tv_volume_percentage = (TextView) findViewById(R.id.geture_tv_volume_percentage);
		geture_tv_bright_percentage = (TextView) findViewById(R.id.geture_tv_bright_percentage);
		gesture_iv_player_volume = (ImageView) findViewById(R.id.gesture_iv_player_volume);
		gesture_iv_player_bright = (ImageView) findViewById(R.id.gesture_iv_player_bright);
		gestureDetector = new GestureDetector(this, this);
		gestureDetector.setIsLongpressEnabled(true);

		audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
		currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值

	}
	
	private OnClickListener mIbtClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			mBigPauseLayOut.setVisibility(View.GONE);
			playOrPauseSTB();
		}
	};
	
	private OnItemClickListener mGvOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			// TODO Auto-generated method stub
			if(mPlayVodPos == position)
				return;
			mPlayVodPos = position;
			now_video = mListVideos.get(mPlayVodPos);
			isLoadingVod = true;
			mGVList_linearLayout.setVisibility(View.GONE);
			mOSDControl.setVisibility(View.GONE);
			switchPlay();
		}
	};
	
	private OnClickListener mBtClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			mOSDControl.setVisibility(View.GONE);
			mGVList_linearLayout.setVisibility(View.VISIBLE);
			isShowVideoList = true;
			mGvVideoList.requestFocus();
			mGvVideoList.setSelection(mPlayVodPos);
		}
	};
	
	private OnTouchListener mtxtTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View e, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_UP){
				mOSDControl.setVisibility(View.GONE);
				mGVList_linearLayout.setVisibility(View.VISIBLE);
				isShowVideoList = true;
				mGvVideoList.requestFocus();
				mGvVideoList.setSelection(mPlayVodPos);
			}
			return false;
		}
	};
	
	private void StartPlay() {
		// TODO Auto-generated method stub
		showLoadAnim(true);// 开始加载动画
		if(now_video==null)
			return;
		now_httpUrl = now_video.getUrl();
		if (now_httpUrl.indexOf("http") == 0) {
			Message msg = new Message();
			msg.what = PLAY_HTTP;
			msg.obj = now_video.getUrl();
			aPlayerHandler.sendMessage(msg);
		} else {
			ForceTvToHttp(now_httpUrl);
		}
	}
	
	private boolean isSwitchChannel = false;
	/**
	 * 播放器playing中时必须先关闭，再停止forceudp下载，否则先下载则播放器无法关闭了，即使销毁也无效
	 * 切台
	 */
	private void switchPlay() {
		// TODO Auto-generated method stub
		if(now_video==null)
			return;
		now_httpUrl = now_video.getUrl();
		isSwitchChannel = true;
		vodBuffer.setVisibility(View.GONE);
		showLoadAnim(true);// 开始加载动画
		if(currentDecodeType==Configs.SOFT_DECODE){
			if(aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_READ){
				if (now_httpUrl.indexOf("http") != 0) {
					ForceTvToHttp(now_httpUrl);
				}
			}else if(aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_PLAYING || 
					aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_PLAY || 
					aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_PAUSED || 
					aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_PAUSING){
				System.out.println("switchPlay   state="+aPlayerAndroid.getState());
				aPlayerAndroid.pause();
				aPlayerAndroid.close();
			}else if(aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_OPENING || 
					aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_CLOSEING){
				destoryAndInitAplayer();//初始化
			}
		}else if(currentDecodeType==Configs.HARD_DECODE){
			if(aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_READ){
				if (now_httpUrl.indexOf("http") != 0) {
					ForceTvToHttp(now_httpUrl);
				}
			}else if(aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_PLAYING || 
					aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_PLAY || 
					aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_PAUSED || 
					aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_PAUSING){
				System.out.println("switchPlay   state="+aPlayerAndroid.getState());
				aPlayerAndroid.pause();
				aPlayerAndroid.close();
				destoryAndInitAplayer();//硬解每次切换都必须初始化，原因暂时未知
				initOSDCONTROLBAR();//初始化controlbar
				ForceTvToHttp(now_httpUrl);
			}else if(aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_OPENING || 
					aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_CLOSEING){
				destoryAndInitAplayer();//初始化
			}
		}
	}
	
	private void showLoadAnim(boolean show) {
		// TODO Auto-generated method stub
		if(show){
			if(vodLoadAnimView.getVisibility()==View.GONE){
				vodLoadAnimView.setVisibility(View.VISIBLE);
			}
		}else{
			if(vodLoadAnimView.getVisibility()==View.VISIBLE){
				vodLoadAnimView.setVisibility(View.GONE);
			}
		}
		
	}

	private void ForceTvToHttp(String Url) {

		String chnnelId = now_video.getChannelId();
		String streamIp = now_video.getStreamip();
		String link = "";
		StringBuilder sb = new StringBuilder("http://127.0.0.1:9906/cmd.xml?cmd=");
		sb.append("switch_chan").append("&id=").append(chnnelId).append("&server=").append(streamIp).append("&userid=")
				.append("$user=$mac=").append(MACUtils.getMac2()).append("$key=")
				.append(MD5Util.getStringMD5_32(MACUtils.getMac() + DeviceFun.GetCpuId(MyPlayerActivity.this)));

		if (link != null && !link.equals("")) {
			sb.append("&link=").append(link);
		}
		Log.d("http", sb.toString());
		FinalHttp fn = new FinalHttp();
		fn.get(sb.toString(), new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);
				Log.d("Error", t.toString());
			}

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				Log.d("forceRe", t.toString());
				now_httpUrl = new StringBuilder(PLAY_SERVER).append(now_video.getChannelId()).toString();
				Switch_Chan_Timer();
				Message msg = new Message();
				msg.what = PLAY_FORCEUDP;
				msg.obj = now_httpUrl;
				aPlayerHandler.sendMessage(msg);
			}
		});
	}

	private void Switch_Chan_Timer() {
		if (mTimerCache != null)
			mTimerCache.cancel();
		if (mTimerTaskCache != null)
			mTimerTaskCache.cancel();

		mTimerCache = new Timer();
		mTimerTaskCache = new TimerTask() {
			@Override
			public void run() {
				FinalHttp fn = new FinalHttp();
				fn.get(GET_FORCE_PLAY_INFO + now_video.getChannelId(), new AjaxCallBack<Object>() {

					@Override
					public void onSuccess(Object t) {
						// TODO Auto-generated method stub
						super.onSuccess(t);
						mForcePlayInfo = t.toString();
						tv_info.setText(t.toString());
						Long playInfo[] = parseForcePlayInfo(mForcePlayInfo);
						long traffic = playInfo[1] / 8;
						mTextBufferPrompt.setText((int) traffic + getString(R.string.kbs));
						mBuffer_txt.setText((int) traffic + getString(R.string.kbs));
					}
				});
			}
		};
		mTimerCache.schedule(mTimerTaskCache, 1000, 1000);
	}

	private Long[] parseForcePlayInfo(String forcePlayInfo) {
		Document doc = null;
		try {
			String parseXml = mForcePlayInfo.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
					.replace("ver=1.0", "");
			doc = DocumentHelper.parseText(parseXml);
			Node root = doc.getRootElement();
			Element element = (Element) root.selectSingleNode("/forcetv/channel");
			long cacheTime = Integer.valueOf(element.attribute("cache_time").getValue());
			long traffic = Integer.valueOf(element.attribute("download_flowkbps").getValue());
			return new Long[] { cacheTime, traffic };
		} catch (DocumentException e) {
			logger.e(e.toString());
		} catch (Exception e) {
			logger.e(e.toString());
		}
		return new Long[] { 1l, 1l };
	};

	private void setTitle() {
		mVodTitle = null == mVodTitle ? "" : mVodTitle;
		StringBuilder title = new StringBuilder(mVodTitle);
		title.append("-" + now_video.getName());
		mTextVideofName.setText(mVodTitle);
		mTextVideoName.setText(title.toString());
	}

	private void getScreenSize() {
		Integer[] screenSize = ScreenUtils.getScreenSize(this);
		if (null != screenSize)
			playerWidth = screenSize[0];
		if (null != screenSize)
			playerHeight = screenSize[1];

		Integer[] screenSizePX = ScreenUtils.getScreenSizePX(this);
		if (null != screenSize)
			screenWidth = screenSizePX[0];
		if (null != screenSize)
			screenHeight = screenSizePX[1];
	}

	/** 视频窗口的宽和高 */
	public int playerWidth, playerHeight;
	/** 屏幕宽高 */
	public int screenWidth, screenHeight;
	/** 手势改变视频进度,音量,亮度 */
	private RelativeLayout gesture_volume_layout, gesture_bright_layout;// 音量控制布局,亮度控制布局
	private TextView geture_tv_volume_percentage, geture_tv_bright_percentage;// 音量百分比,亮度百分比
	private ImageView gesture_iv_player_volume, gesture_iv_player_bright;// 音量图标,亮度图标
	private RelativeLayout gesture_progress_layout;// 进度图标
	// private TextView geture_tv_progress_time;// 播放时间进度
	private ImageView gesture_iv_progress;// 快进或快退标志
	private GestureDetector gestureDetector;
	private AudioManager audiomanager;
	private int maxVolume, currentVolume;
	private float mBrightness = -1f; // 亮度
	private static final float STEP_PROGRESS = 2f;// 设定进度滑动时的步长，避免每次滑动都改变，导致改变过快
	private static final float STEP_VOLUME = 2f;// 协调音量滑动时的步长，避免每次滑动都改变，导致改变过快
	private boolean firstScroll = false;// 每次触摸屏幕后，第一次scroll的标志
	private int GESTURE_FLAG = 0;// 1,调节进度，2，调节音量,3.调节亮度
	private static final int GESTURE_MODIFY_PROGRESS = 1;
	private static final int GESTURE_MODIFY_VOLUME = 2;
	private static final int GESTURE_MODIFY_BRIGHT = 3;

	@Override
	public boolean onDown(MotionEvent e) {
		firstScroll = true;// 设定是触摸屏幕后第一次scroll的标志
		return true;// 此处必须如此
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if (isLoadingVod || isShowVideoList)
			return false;

		float mOldX = e1.getX(), mOldY = e1.getY();
		int y = (int) e2.getRawY();
		if (firstScroll) {// 以触摸屏幕后第一次滑动为标准，避免在屏幕上操作切换混乱
			// 横向的距离变化大则调整进度，纵向的变化大则调整音量
			if (Math.abs(distanceX) >= Math.abs(distanceY)) {
				gesture_progress_layout.setVisibility(View.GONE);
				gesture_volume_layout.setVisibility(View.GONE);
				gesture_bright_layout.setVisibility(View.GONE);
				GESTURE_FLAG = GESTURE_MODIFY_PROGRESS;
			} else {
				if (mOldX >= screenWidth * 2.5 / 5) {// 音量
					gesture_volume_layout.setVisibility(View.VISIBLE);
					gesture_bright_layout.setVisibility(View.GONE);
					gesture_progress_layout.setVisibility(View.GONE);
					GESTURE_FLAG = GESTURE_MODIFY_VOLUME;
				} else if (mOldX < screenWidth * 2.5 / 5) {// 亮度
					gesture_bright_layout.setVisibility(View.VISIBLE);
					gesture_volume_layout.setVisibility(View.GONE);
					gesture_progress_layout.setVisibility(View.GONE);
					GESTURE_FLAG = GESTURE_MODIFY_BRIGHT;
				}
			}
		}
		// 如果每次触摸屏幕后第一次scroll是调节进度，那之后的scroll事件都处理音量进度，直到离开屏幕执行下一次操作
		if (GESTURE_FLAG == GESTURE_MODIFY_PROGRESS) {
			// distanceX=lastScrollPositionX-currentScrollPositionX，因此为正时是快进
			if (Math.abs(distanceX) > Math.abs(distanceY)) {// 横向移动大于纵向移动
				if (distanceX >= DensityUtil.dip2px(this, STEP_PROGRESS)) {// 快退，用步长控制改变速度，可微调
//					mOSDControl.seekFoward(false);
//					mOSDControl.seekCenterBarFoward(false);
				} else if (distanceX <= -DensityUtil.dip2px(this, STEP_PROGRESS)) {// 快进
//					mOSDControl.seekFoward(true);
//					mOSDControl.seekCenterBarFoward(true);
				}
			}
		}
		// 如果每次触摸屏幕后第一次scroll是调节音量，那之后的scroll事件都处理音量调节，直到离开屏幕执行下一次操作
		else if (GESTURE_FLAG == GESTURE_MODIFY_VOLUME) {
			currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
			if (Math.abs(distanceY) > Math.abs(distanceX)) {// 纵向移动大于横向移动
				if (distanceY >= DensityUtil.dip2px(this, STEP_VOLUME)) {// 音量调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正
					if (currentVolume < maxVolume) {// 为避免调节过快，distanceY应大于一个设定值
						currentVolume++;
					}
					gesture_iv_player_volume.setImageResource(R.drawable.souhu_player_volume);
				} else if (distanceY <= -DensityUtil.dip2px(this, STEP_VOLUME)) {// 音量调小
					if (currentVolume > 0) {
						currentVolume--;
						if (currentVolume == 0) {// 静音，设定静音独有的图片
							gesture_iv_player_volume.setImageResource(R.drawable.souhu_player_silence);
						}
					}
				}
				int percentage = (currentVolume * 100) / maxVolume;
				geture_tv_volume_percentage.setText(percentage + "%");
				audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
			}
		}
		// 如果每次触摸屏幕后第一次scroll是调节亮度，那之后的scroll事件都处理亮度调节，直到离开屏幕执行下一次操作
		else if (GESTURE_FLAG == GESTURE_MODIFY_BRIGHT) {
			gesture_iv_player_bright.setImageResource(R.drawable.souhu_player_bright);
			if (mBrightness < 0) {
				mBrightness = getWindow().getAttributes().screenBrightness;
				if (mBrightness <= 0.00f)
					mBrightness = 0.50f;
				if (mBrightness < 0.01f)
					mBrightness = 0.01f;
			}
			WindowManager.LayoutParams lpa = getWindow().getAttributes();
			lpa.screenBrightness = mBrightness + ((mOldY - y) / screenHeight) * 3;
			if (lpa.screenBrightness > 1.0f)
				lpa.screenBrightness = 1.0f;
			else if (lpa.screenBrightness < 0.01f)
				lpa.screenBrightness = 0.01f;
			getWindow().setAttributes(lpa);
			geture_tv_bright_percentage.setText((int) (lpa.screenBrightness * 100) + "%");
		}

		firstScroll = false;// 第一次scroll执行完成，修改标志
		return false;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// 手势里除了singleTapUp，没有其他检测up的方法(手指离开屏幕)
		if (event.getAction() == MotionEvent.ACTION_UP) {
			GESTURE_FLAG = 0;// 手指离开屏幕后，重置调节音量或进度的标志
			gesture_volume_layout.setVisibility(View.GONE);
			gesture_bright_layout.setVisibility(View.GONE);
			gesture_progress_layout.setVisibility(View.GONE);
			// 隐藏播放列表
			if (mGVList_linearLayout.getVisibility() == View.VISIBLE) {
				// System.out.println("R.id.aplayer_surfaceView =
				// "+R.id.aplayer_surfaceView);
				// System.out.println("v.getId() = "+v.getId());
				// System.out.println("R.id.gvList_linearLayout =
				// "+R.id.gvList_linearLayout);
				if (v.getId() != R.id.gvList_linearLayout) {
					mGVList_linearLayout.setVisibility(View.GONE);
					isShowVideoList = false;
				}
			} else {
				// 显示mOSDControl
				if (!isLoadingVod) {
					if (mOSDControl.getVisibility() == View.VISIBLE){
						isShowVideoList = false;
						mOSDControl.setVisibility(View.GONE);
					}
					else if ((mOSDControl.getVisibility() == View.GONE)){
						isShowVideoList = true;
						mOSDControl.setVisibility(View.VISIBLE);
					}
				}
			}
		}
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(vodLoadAnimView.getVisibility()==View.VISIBLE)
			return false;
		if(event.getAction() == KeyEvent.ACTION_DOWN){
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
				playOrPauseSTB();
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				mOSDControl.setVisibility(View.VISIBLE);
				return true;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				mOSDControl.setVisibility(View.VISIBLE);
				return true;
			case KeyEvent.KEYCODE_MENU :
				mOSDControl.setVisibility(View.VISIBLE);
				break;
			}
		}
		return false;
	}

	// Activity创建或者从后台重新回到前台时被调用
	@Override
	protected void onStart() {
		super.onStart();
		Log.i("", "onStart called.");
	}

	// Activity从后台重新回到前台时被调用
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i("", "onRestart called.");
	}

	// Activity创建或者从被覆盖、后台重新回到前台时被调用
	@Override
	protected void onResume() {
		super.onResume();
		Log.i("", "onResume called.");
	}

	// Activity被覆盖到下面或者锁屏时被调用
	//用户退出当前Activity：系统先调用onPause方法，然后调用onStop方法，最后调用onDestory方法，结束当前Activity
	@Override
	protected void onPause() {
		super.onPause();
		Log.i("", "onPause called.");
		// 有可能在执行完onPause或onStop后,系统资源紧张将Activity杀死,所以有必要在此保存持久数据
		savePlayHistory();
	}

	// 退出当前Activity时被调用,调用之后Activity就结束了
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// 退出当前Activity或者跳转到新Activity时被调用
	@Override
	protected void onStop() {
		if (aPlayerAndroid!=null){
			aPlayerAndroid.close();
			aPlayerAndroid.destroy();
		}
		exit();
		super.onStop();
	}

	private void exit() {
		try {
			if (null != mTimerCache)
				mTimerCache.cancel();
			if (null != mTimerTaskCache)
				mTimerTaskCache.cancel();
			if (null != mTimerUpdateStateTimer)
				mTimerUpdateStateTimer.cancel();
			if (null != mTimerUpdateStateTask)
				mTimerUpdateStateTask.cancel();
		} catch (Exception e) {
			logger.e(e.toString());
		} finally {
			finish();
		}
	}

	public void playOrPauseSTB() {
		if(aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_PLAYING){
			aPlayerAndroid.pause();
			mOSDControl.setPlayIcon(false);//暂停
			mBigPauseLayOut.setVisibility(View.VISIBLE);
		}else if(aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_PAUSED){
			aPlayerAndroid.play();
			mOSDControl.setPlayIcon(true);//播放
			mBigPauseLayOut.setVisibility(View.GONE);
		}
	}
	public void close() {
		// TODO Auto-generated method stub
		aPlayerAndroid.close();
	}

	private boolean checkCanPlay(boolean is2previous) {
//		mListVideos.get(mPlayVodPos-1).setUrl("http://192.168.31.223:9012/626_201705061902.ts");//http test
//		mListVideos.get(mPlayVodPos+1).setUrl("http://192.168.31.223:9012/102_201706021302.ts");//http test
		int pos = is2previous ? mPlayVodPos-- : mPlayVodPos++;
		return mPlayVodPos >= 0 && mPlayVodPos < mListVideos.size();
	}

	/**
	 * 刷新播放器视图
	 */
	private void refreshSurfaceView() {
		// TODO Auto-generated method stub
		if(videoPlayView!=null){
			videoPlayView.setVisibility(View.GONE);
			videoPlayView.setVisibility(View.VISIBLE);
		}
	}
	
	private void savePlayHistory() {
		System.out.println("savePlayHistory...."+aPlayerAndroid.getPosition());
    	HistoryDAO historyDAO = new HistoryDAO(getApplicationContext());
    	if(null != mListVideos.get(0))
    		historyDAO.delete(mListVideos.get(0).getSubcatid());
    	HistoryItem item = new HistoryItem();
    	item.setPlayIndex(String.valueOf(mPlayVodPos));
    	item.setPlayPos(String.valueOf(aPlayerAndroid.getPosition()));
    	item.setSubcatid(mListVideos.get(0).getSubcatid());
    	item.setProgramjson(Programjson);
    	item.setCid(mCid);
    	historyDAO.insert(getApplicationContext(), item);
	}

	private Timer mTimerUpdateStateTimer;
	private TimerTask mTimerUpdateStateTask;

	public void stopTimer() {
		if (mTimerUpdateStateTask != null) {
			mTimerUpdateStateTask.cancel();
			mTimerUpdateStateTask = null;
		}
		if (mTimerUpdateStateTimer != null) {
			mTimerUpdateStateTimer.cancel();
			mTimerUpdateStateTimer = null;
		}
	}

	public void startTimer() {
		// TODO Auto-generated method stub
		mTimerUpdateStateTimer = new Timer();
		mTimerUpdateStateTask = new TimerTask() {
			@Override
			public void run() {
				mHandlerPlayPos.sendEmptyMessage(0);
			}
		};
		try {
			mTimerUpdateStateTimer.schedule(mTimerUpdateStateTask, 0, 1000);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandlerPlayPos = new Handler() {
		public void handleMessage(Message msg) {
			int currentPlayPos = aPlayerAndroid.getPosition();
			if (currentPlayPos <= 3)
				return;
			mOSDControl.setCurrentPlayTime(currentPlayPos);
			mOSDControl.setSeekProgress(currentPlayPos);
			try {
				int readPosition = Integer.parseInt(aPlayerAndroid.getConfig(APlayerAndroid.CONFIGID.READPOSITION));
//				int readPosition = aPlayerAndroid.getBufferProgress();
				mOSDControl.setSecondaryProcessBar(readPosition);
			} catch (Exception e) {
				// TODO: handle exception
			}
		};
	};
	private void registerAplayerLister() {
		/*
		 * 文件打开成功事件 文件打开成功后，会产生该事件。如果没有设置自动播放，再次调用play()就开始播放了，要获得视频的高宽，时长信息
		 * 也需要在视频打开之后。 该事件与 OnOpenCompleteListener事件在回调函数参数为TRUE时一模一样
		 */
		aPlayerAndroid.setOnOpenSuccessListener(new APlayerAndroid.OnOpenSuccessListener() {
			@Override
			public void onOpenSuccess() {
				System.out.println("onOpenSuccess....");
				showLoadAnim(false);// 关闭加载动画
				vodBuffer.setVisibility(View.VISIBLE);
				setTitle();// 设置标题
				int i = (int) aPlayerAndroid.getDuration();
				mOSDControl.setTotalTime(i);// 设置总时长
				aPlayerAndroid.play();// 开始播放
				mVodAdapter.changvod(mPlayVodPos);
				stopTimer();
				startTimer();
				if (isPlayHistory){
					isPlayHistory = false;
					if (aPlayerAndroid.getState() == APlayerAndroid.PlayerState.APLAYER_PLAYING){
						aPlayerAndroid.pause();
						aPlayerAndroid.setPosition((int)(mHistoryPlayPos));
					}
				}
			}
		});
		/*
		 * 播放状态改变事件 nCurrentState 当前状态 nPreState 改变前的状态
		 */
		aPlayerAndroid.setOnPlayStateChangeListener(new APlayerAndroid.OnPlayStateChangeListener() {

			@Override
			public void onPlayStateChange(int nCurrentState, int nPreState) {
				// TODO Auto-generated method stub
				System.out.println("onPlayStateChange......= nCurrentState ==   "+nCurrentState);
				if(nCurrentState==APlayerAndroid.PlayerState.APLAYER_READ && isSwitchChannel && currentDecodeType==Configs.SOFT_DECODE){
					isSwitchChannel = false;
					if (now_httpUrl.indexOf("http") != 0) {
						ForceTvToHttp(now_httpUrl);
					}
				}
			}
		});
		/*
		 * 打开 调用完毕事件 isOpenSuccess 文件是否打开成功
		 */
		aPlayerAndroid.setOnOpenCompleteListener(new APlayerAndroid.OnOpenCompleteListener() {

			@Override
			public void onOpenComplete(boolean isOpenSuccess) {
				// TODO Auto-generated method stub
				System.out.println("onOpenComplete......");
			}
		});
		/*
		 * 播放完成事件 playRet
		 */
		aPlayerAndroid.setOnPlayCompleteListener(new APlayerAndroid.OnPlayCompleteListener() {

			@Override
			public void onPlayComplete(String playRet) {
				// TODO Auto-generated method stub
				System.out.println("===================onPlayComplete......  playRet = "+playRet);
				
				//手动关闭
				if (playRet.equals(APlayerAndroid.PlayCompleteRet.PLAYRE_RESULT_CLOSE)){
					System.out.println("aPlayerAndroid.getState()  = "+aPlayerAndroid.getState());
					//软解  -- 正常情况下播放器状态由 3--6--0--playRet = 0x1--0，，有时也会出现3--6--0--playRet = 0x1--6出现，原因未知，此时需要重置播放器
					if(aPlayerAndroid.getState()==APlayerAndroid.PlayerState.APLAYER_CLOSEING){
						destoryAndInitAplayer();
					}
				}
				//播放结束
				if (playRet.equals(APlayerAndroid.PlayCompleteRet.PLAYRE_RESULT_COMPLETE)){
					play2WichVod(false);
				}
				//打开失败
				if (playRet.equals(APlayerAndroid.PlayCompleteRet.PLAYRE_RESULT_OPENRROR)){
					 Toast.makeText(MyPlayerActivity.this, "PLAYRE_RESULT_OPENRROR",Toast.LENGTH_LONG).show();
				}
				if (playRet.equals(APlayerAndroid.PlayCompleteRet.PLAYRE_RESULT_SEEKERROR)){
					Toast.makeText(MyPlayerActivity.this, "PLAYRE_RESULT_SEEKERROR",Toast.LENGTH_LONG).show();				
				}
				if (playRet.equals(APlayerAndroid.PlayCompleteRet.PLAYRE_RESULT_READEFRAMERROR)){
					Toast.makeText(MyPlayerActivity.this, "PLAYRE_RESULT_READEFRAMERROR",Toast.LENGTH_LONG).show();
				}
				if (playRet.equals(APlayerAndroid.PlayCompleteRet.PLAYRE_RESULT_CREATEGRAPHERROR)){
					Toast.makeText(MyPlayerActivity.this, "PLAYRE_RESULT_CREATEGRAPHERROR",Toast.LENGTH_LONG).show();
				}
				if (playRet.equals(APlayerAndroid.PlayCompleteRet.PLAYRE_RESULT_DECODEERROR)){
					Toast.makeText(MyPlayerActivity.this, "PLAYRE_RESULT_DECODEERROR",Toast.LENGTH_LONG).show();
				}
				//硬件解码出错
				if (playRet.equals(APlayerAndroid.PlayCompleteRet.PLAYRE_RESULT_HARDDECODERROR)){
					Toast.makeText(MyPlayerActivity.this, "PLAYRE_RESULT_HARDDECODERROR",Toast.LENGTH_LONG).show();
				}
			}
		});
		/*
		 * 缓冲进度改变事件 progress 当前新的缓冲进度 注意：播放器进入缓冲后，播放器的状态并没有发生改变。
		 */
		aPlayerAndroid.setOnBufferListener(new APlayerAndroid.OnBufferListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onBuffer(int progress) {
				// TODO Auto-generated method stub
				System.out.println("onBuffer......progress = "+progress);
				
				if(progress==0){
					try {
						beginDate = dfs.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if(progress==100){
					try {
						endDate = dfs.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (beginDate!= null && endDate!=null){
					between = (endDate.getTime() - beginDate.getTime());// 得到两者的毫秒数
					System.out.println("beginDate = "+beginDate);
					System.out.println("endDate   = "+endDate);
					System.out.println("between = "+between);
					beginDate = null;
					endDate = null;
					if (between<500){
						aPlayerAndroid.setConfig(APlayerAndroid.CONFIGID.UPDATEWINDOW, "0");//非正常缓冲时刷新窗口
//						aPlayerAndroid.close();
						return;
					}
				}
				
//				if (progress == 100){
//					vodBuffer.setVisibility(View.GONE);
//				}else{
//					vodBuffer.setVisibility(View.VISIBLE);
//				}
			}
		});
		/*
		 * 媒体 Seek完成事件
		 * 
		 */
		aPlayerAndroid.setOnSeekCompleteListener(new APlayerAndroid.OnSeekCompleteListener() {

			@Override
			public void onSeekComplete() {
				// TODO Auto-generated method stub
				System.out.println("onSeekComplete......");
				aPlayerAndroid.play();
			}
		});
		/*
		 * Surface销毁事件
		 * 
		 */
		aPlayerAndroid.setOnSurfaceDestroyListener(new APlayerAndroid.OnSurfaceDestroyListener() {

			@Override
			public void onSurfaceDestroy() {
				// TODO Auto-generated method stub
				System.out.println("onSurfaceDestroy......");
			}
		});
		/*
		 * 字幕 更新事件 subtitle 内容不为空 显示新的字幕 subtitle 内容为空 清除显示的字幕
		 */
		aPlayerAndroid.setOnShowSubtitleListener(new APlayerAndroid.OnShowSubtitleListener() {

			@Override
			public void onShowSubtitle(String subtitle) {
				// TODO Auto-generated method stub
				System.out.println("onShowSubtitle......");
			}
		});
	}
	private int isBack = 0;
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(mGVList_linearLayout.getVisibility()==View.VISIBLE){
			mGVList_linearLayout.setVisibility(View.GONE);
			return;
		}
		if(mOSDControl.getVisibility()==View.VISIBLE){
			mOSDControl.setVisibility(View.GONE);
			return;
		}
		System.out.println("onBackPressed..............onBackPressed..............onBackPressed..............onBackPressed");
		
		isBack = isBack + 1;
		if (isBack>1){
			System.out.println("go back....................................................");
			super.onBackPressed();
		}
		Toast.makeText(MyPlayerActivity.this, this.getResources().getString(R.string.back_txt1),Toast.LENGTH_LONG).show();
		setBackClock();
	}
	
	private Timer mBackTimer = new Timer();
	private void setBackClock() {
		if (null != mBackTimer)
			mBackTimer.cancel();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				isBack = 0;
			}
		};
		Timer timer = new Timer();
		timer.schedule(timerTask, 3000);
		mBackTimer = timer;
	}
}
