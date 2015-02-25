package com.example.audio;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ui.ViewProxy;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	private TextView recordTimeText;
	private ImageButton audioSendButton;
	private View recordPanel;
	private View slideText;
	private float startedDraggingX = -1;
	private float distCanMove = dp(80);
	private long startTime = 0L;
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		recordPanel = findViewById(R.id.record_panel);
		recordTimeText = (TextView) findViewById(R.id.recording_time_text);
		slideText = findViewById(R.id.slideText);
		audioSendButton = (ImageButton) findViewById(R.id.chat_audio_send_button);
		TextView textView = (TextView) findViewById(R.id.slideToCancelTextView);
		textView.setText("SlideToCancel");
		audioSendButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText
							.getLayoutParams();
					params.leftMargin = dp(30);
					slideText.setLayoutParams(params);
					ViewProxy.setAlpha(slideText, 1);
					startedDraggingX = -1;
					// startRecording();
					startrecord();
					audioSendButton.getParent()
							.requestDisallowInterceptTouchEvent(true);
					recordPanel.setVisibility(View.VISIBLE);
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP
						|| motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
					startedDraggingX = -1;
					stoprecord();
					// stopRecording(true);
				} else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
					float x = motionEvent.getX();
					if (x < -distCanMove) {
						stoprecord();
						// stopRecording(false);
					}
					x = x + ViewProxy.getX(audioSendButton);
					FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText
							.getLayoutParams();
					if (startedDraggingX != -1) {
						float dist = (x - startedDraggingX);
						params.leftMargin = dp(30) + (int) dist;
						slideText.setLayoutParams(params);
						float alpha = 1.0f + dist / distCanMove;
						if (alpha > 1) {
							alpha = 1;
						} else if (alpha < 0) {
							alpha = 0;
						}
						ViewProxy.setAlpha(slideText, alpha);
					}
					if (x <= ViewProxy.getX(slideText) + slideText.getWidth()
							+ dp(30)) {
						if (startedDraggingX == -1) {
							startedDraggingX = x;
							distCanMove = (recordPanel.getMeasuredWidth()
									- slideText.getMeasuredWidth() - dp(48)) / 2.0f;
							if (distCanMove <= 0) {
								distCanMove = dp(80);
							} else if (distCanMove > dp(80)) {
								distCanMove = dp(80);
							}
						}
					}
					if (params.leftMargin > dp(30)) {
						params.leftMargin = dp(30);
						slideText.setLayoutParams(params);
						ViewProxy.setAlpha(slideText, 1);
						startedDraggingX = -1;
					}
				}
				view.onTouchEvent(motionEvent);
				return true;
			}
		});

	}

	private void startrecord() {
		// TODO Auto-generated method stub
		startTime = SystemClock.uptimeMillis();
		timer = new Timer();
		MyTimerTask myTimerTask = new MyTimerTask();
		timer.schedule(myTimerTask, 1000, 1000);
		vibrate();
	}

	private void stoprecord() {
		// TODO Auto-generated method stub
		if (timer != null) {
			timer.cancel();
		}
		if (recordTimeText.getText().toString().equals("00:00")) {
			return;
		}
		recordTimeText.setText("00:00");
		vibrate();
	}

	private void vibrate() {
		// TODO Auto-generated method stub
		try {
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int dp(float value) {
		return (int) Math.ceil(1 * value);
	}

	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			updatedTime = timeSwapBuff + timeInMilliseconds;
			final String hms = String.format(
					"%02d:%02d",
					TimeUnit.MILLISECONDS.toMinutes(updatedTime)
							- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
									.toHours(updatedTime)),
					TimeUnit.MILLISECONDS.toSeconds(updatedTime)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes(updatedTime)));
			long lastsec = TimeUnit.MILLISECONDS.toSeconds(updatedTime)
					- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
							.toMinutes(updatedTime));
			System.out.println(lastsec + " hms " + hms);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					try {
						if (recordTimeText != null)
							recordTimeText.setText(hms);
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
