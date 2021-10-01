package net.oschina.gitapp.ui;

import java.io.IOException;
import java.util.HashMap;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.util.ShakeListener;
import net.oschina.gitapp.util.ShakeListener.OnShakeListener;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShakeActivity extends BaseActionBarActivity implements OnClickListener {
	
	private AppContext mAppContext;
	
	private ShakeListener mShakeListener = null;
	
	private Vibrator mVibrator;
	
	private RelativeLayout mImgUp;
	
	private RelativeLayout mImgDn;

	private LinearLayout mLoaging;
	
	private RelativeLayout mShakeRes;
	
	private ImageView mProjectFace;
	
	private TextView mProjectTitle;
	
	private TextView mProjectDescription;
	
	private TextView mProjectLanguage;
	
	private TextView mProjectStarNums;
	
	private TextView mProjectForkNums;
	
	private Project mProject;
	
	private SoundPool sndPool;
	private HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shake);
		mAppContext = getGitApplication();
		initView();
		mVibrator = (Vibrator) getApplication().getSystemService(
				VIBRATOR_SERVICE);

		loadSound();
		mShakeListener = new ShakeListener(this);
		// 监听到手机摇动
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				startAnim();
			}
		});
	}
	
	private void initView() {
		mImgUp = (RelativeLayout) findViewById(R.id.shakeImgUp);
		mImgDn = (RelativeLayout) findViewById(R.id.shakeImgDown);
		
		mLoaging = (LinearLayout) findViewById(R.id.shake_loading);
		
		mShakeRes = (RelativeLayout) findViewById(R.id.shakeres);
		
		mProjectFace = (ImageView) findViewById(R.id.exploreproject_listitem_userface);
		
		mProjectTitle = (TextView) findViewById(R.id.exploreproject_listitem_title);
		
		mProjectDescription = (TextView) findViewById(R.id.exploreproject_listitem_description);
		
		mProjectLanguage = (TextView) findViewById(R.id.exploreproject_listitem_language);
		
		mProjectStarNums = (TextView) findViewById(R.id.exploreproject_listitem_star);
		
		mProjectForkNums = (TextView) findViewById(R.id.exploreproject_listitem_fork);
		
		mShakeRes.setOnClickListener(this);
	}

	private void loadSound() {

		sndPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
		new Thread() {
			public void run() {
				try {
					soundPoolMap.put(
							0,
							sndPool.load(
									getAssets().openFd(
											"sound/shake_sound_male.mp3"), 1));

					soundPoolMap.put(1, sndPool.load(
							getAssets().openFd("sound/shake_match.mp3"), 1));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void startAnim() {
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimup0.setDuration(1000);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimup1.setDuration(1000);
		mytranslateanimup1.setStartOffset(1000);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		mImgUp.startAnimation(animup);

		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimdn0.setDuration(1000);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimdn1.setDuration(1000);
		mytranslateanimdn1.setStartOffset(1000);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		mImgDn.startAnimation(animdn);
		mytranslateanimdn0.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				mShakeRes.setVisibility(View.GONE);
				mShakeListener.stop();
				sndPool.play(soundPoolMap.get(0), (float) 0.2, (float) 0.2, 0, 0,
						(float) 0.6);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				loadProject();
			}
		});
	}

	public void startVibrato() {
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.shakeres:
			if (mProject != null) {
				UIHelper.showProjectDetail(mAppContext, null, mProject.getId());
			}
			break;

		default:
			break;
		}
	}
	
	private void beforeLoading() {
		mLoaging.setVisibility(View.VISIBLE);
		mShakeRes.setVisibility(View.GONE);
	}
	
	private void afterLoading() {
		mLoaging.setVisibility(View.GONE);
		sndPool.play(soundPoolMap.get(1), (float) 0.2, (float) 0.2,
				0, 0, (float) 0.6);
		mVibrator.cancel();
		mShakeListener.start();
	}
	
	private void loadProject() {
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					msg.obj = mAppContext.getRandomProject();
					msg.what = 1;
				} catch (AppException e) {
					msg.what = -1;
					msg.obj = e;
					e.printStackTrace();
				}
				return msg;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				beforeLoading();
			}

			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				afterLoading();
				if (msg.what == 1) {
					mShakeRes.setVisibility(View.VISIBLE);
					mProject = (Project) msg.obj;
					if (mProject !=null) {
						mProjectTitle.setText(mProject.getOwner().getName() + "/" + mProject.getName());
						mProjectDescription.setText(mProject.getDescription());
						mProjectStarNums.setText(mProject.getStars_count() + "");
						mProjectForkNums.setText(mProject.getForks_count() + "");
						if (mProject.getLanguage() != null) {
							mProjectLanguage.setText(mProject.getLanguage());
						} else {
							mProjectLanguage.setVisibility(View.GONE);
							findViewById(R.id.exploreproject_listitem_language_image).setVisibility(View.GONE);
						}
						
						if (mProject.getOwner().getPortrait() == null || mProject.getOwner().getPortrait().endsWith(".gif")) {
							mProjectFace.setBackgroundResource(R.drawable.mini_avatar);
						} else {
							String faceUrl = URLs.GITIMG + mProject.getOwner().getPortrait();
							UIHelper.showUserFace(mProjectFace, faceUrl);
						}
					}
				} else {
					UIHelper.ToastMessage(mAppContext, "红薯跟你开了个玩笑，没有为你找到项目");
				}
			}
			
		}.execute();
	}
}