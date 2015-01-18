package kz.alfa;

import java.util.Timer;
import java.util.TimerTask;
import me.noip.vmv857.loc.LocGetTask;
import kz.alfa.util.Db_Helper;
import kz.alfa.util.LocJdbcTask;
import kz.alfa.util.LocUpTask;
import kz.alfa.util.Cnt;
import kz.alfa.util.Log;
import kz.alfa.util.Pref;
import kz.alfa.util.SendTask;
import kz.alfa.util.GetTask;
import kz.alfa.util.SystemUiHider;
import com.besaba.vmchat2.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
@SuppressLint("ClickableViewAccessibility")
public class ChatActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {
	private static String LOG_TAG = "ChatActivity";
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = false;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 10000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = false;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	public static ChatActivity ch;
	private Db_Helper db;
	private ListView lvMain;
	private ChatSimpleCursorAdapter scAdapter;
	private EditText etSend;
	private CheckBox cbScroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Cnt.set(getApplicationContext());
		ch = this;
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		// находим список
		lvMain = (ListView) findViewById(R.id.lvMain);
		// открываем подключение к БД
		db = new Db_Helper(this);
		// формируем столбцы сопоставления
		String[] from = new String[] { "who", "what", "dt_serv" };
		int[] to = new int[] { R.id.tvTextWho, R.id.tvTextWhat, R.id.tvTextWhen };
		// создааем адаптер и настраиваем список
		scAdapter = new ChatSimpleCursorAdapter(this, R.xml.item, null, from,
				to, 0);
		lvMain.setAdapter(scAdapter);
		// создаем лоадер для чтения данных
		getSupportLoaderManager().initLoader(0, null, this);

		cbScroll = (CheckBox) findViewById(R.id.cbScroll);
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);
		etSend = (EditText) findViewById(R.id.etSend);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							mControlsHeight = controlsView.getHeight();
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
							contentView.animate()
									.translationY(visible ? 25+getStatusBarHeight() : 0)
									.setDuration(mShortAnimTime);
									//.rotation(visible ? 90 : 1);
							//if (visible)
								//contentView.set
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnTouchListener(
				mDelayHideTouchListener);
		etSend.setOnTouchListener(mDelayHideTouchListener);
		mSystemUiHider.toggle();
		someTask();
		kz.alfa.util.LocListener.SetUpLocationListener();
		RefreshW();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(1000);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@SuppressLint("NewApi")
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.clear:
			Db_Helper dbHelp = new Db_Helper(Cnt.get());
			Pref.edit("show_id", dbHelp.get_max_id());
			db.del_all();
			RefreshW();
			return true;
		case R.id.restore:
			Pref.edit("show_id", 0);
			Pref.edit("last_id", 0);
			RefreshW();
			return true;
		case R.id.refresh:
			RefreshW();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void ClickCBS(View v) {
		RefreshW();
		String txt = etSend.getText().toString();
		if (txt.equals("888")) {
			Toast.makeText(this, "gps door", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, ListWActivity.class);
			startActivity(intent);
		}
	}

	public void clickOk(View v) {
		String txt = etSend.getText().toString();
		etSend.getText().clear();
		if (SendTask.taskStarted < 1) {
			SendTask taskLup = new SendTask();
			taskLup.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					new String[] { txt });
		} else {
			Toast.makeText(this, "Введите текст сначала"+getTitleBarHeight(), Toast.LENGTH_LONG)
					.show();
		}
		RefreshW();
	}

	public void RefreshW() {
		getSupportLoaderManager().getLoader(0).forceLoad();
		if (cbScroll.isChecked()) {
			lvMain.setSelected(true);
			lvMain.setSelection(lvMain.getCount());
		}
	}

	void someTask() { // число потоков ограниченно :(
		Timer myTimer = new Timer(); // Создаем таймер
		Log.v(LOG_TAG, " someTask ");
		myTimer.schedule(new TimerTask() { // Определяем задачу
					@Override
					public void run() {
						/*
						 * kz.alfa.util.Log.v(LOG_TAG, "someTask scrollPos=" +
						 * scrollPos + " SendTask = " + SendTask.taskStarted +
						 * " GetTask   = " + GetTask.taskStarted +
						 * " LocUpTask  = " + LocUpTask.taskStarted +
						 * " JdbcTask   = " + LocJdbcTask.taskStarted);
						 */
						if (GetTask.taskStarted < 1) {
							GetTask taskG = new GetTask();
							taskG.executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR,
									new String[] {});
						}
						if (LocGetTask.taskStarted < 1) {
							LocGetTask taskLG = new LocGetTask();
							taskLG.executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR,
									new String[] {});
						}
						if (LocJdbcTask.taskStarted < 1) {
							LocJdbcTask taskJ = new LocJdbcTask();
							taskJ.executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR,
									new String[] {});

						}
						if (LocUpTask.taskStarted < 1) {
							LocUpTask taskLup = new LocUpTask();
							taskLup.executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR,
									new String[] {});
						}
					}
				}, 30L * 1000L, 3L * 1000L); // интервал
	}

	protected void onDestroy() {
		super.onDestroy();
		// закрываем подключение при выходе
		db.close();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		return new ChatCursorLoader(this, db);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		scAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	 
	public int getStatusBarHeight() 
	{ 
	    int result = 0;
	    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
	    if (resourceId > 0) {
	        result = getResources().getDimensionPixelSize(resourceId);
	    } 
	    return result;
	}
	
	public int getStatusBarHeight2() {
	    Rect r = new Rect();
	    Window w = getWindow();
	    w.getDecorView().getWindowVisibleDisplayFrame(r);
	    return r.top;
	}
	 
	public int getTitleBarHeight() {
	    int viewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
	    return (viewTop - getStatusBarHeight2());
	}
}
