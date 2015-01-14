package kz.alfa;

import me.noip.vmv857.loc.IdWhoCursorLoader;
import me.noip.vmv857.loc.LocAllDbHlp;
import kz.alfa.util.Cnt;
import kz.alfa.util.Log;
import kz.alfa.util.SystemUiHider;
import com.besaba.vmchat2.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
@SuppressLint("ClickableViewAccessibility")
public class ListWActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {
	private static String LOG_TAG = "ChatActivity";
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 10000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	public static ListWActivity ma;
	private LocAllDbHlp db;
	private ListView lvMain;
	private SimpleCursorAdapter scAdapter;

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, 1, 0, R.string.track_map_a);
		menu.add(0, 2, 0, R.string.track_map_a2);
	}

	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			// получаем из пункта контекстного меню данные по пункту списка
			AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Log.e(LOG_TAG, " onContextItemSelected acmi.id=" + acmi.id);
			Intent intent = new Intent();
			intent.setClassName("me.noip.gpstrack",
					"me.noip.gpstrack.TrackMActivity");
			intent.setData(Uri.parse(db.get_idwho(acmi.id)));
			Log.e(LOG_TAG, "intent=" + intent.getDataString());
			startActivity(intent);
			return true;
		}
		if (item.getItemId() == 2) {
			Log.e(LOG_TAG,
					"Intent intent2 = new Intent(this, TrackActivity.class);");
			AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Log.e(LOG_TAG, " onContextItemSelected acmi.id=" + acmi.id);
			Intent intent2 = new Intent(this, TrackActivity.class);
			intent2.setData(Uri.parse(db.get_idwho(acmi.id)));
			Log.e(LOG_TAG, "intent=" + intent2.getDataString());
			startActivity(intent2);
		}
		return super.onContextItemSelected(item);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Cnt.set(getApplicationContext());
		ma = this;
		// getWindow().requestFeature(Window.FEATURE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listwho);
		// находим список
		lvMain = (ListView) findViewById(R.id.lvMain);
		// добавляем контекстное меню к списку
		registerForContextMenu(lvMain);

		// открываем подключение к БД
		db = new LocAllDbHlp(this);
		// формируем столбцы сопоставления
		String[] from = new String[] { "cnt_gps", "idwho" };
		int[] to = new int[] { R.id.tvTextCnt, R.id.tvTextWho };
		// создааем адаптер и настраиваем список
		scAdapter = new SimpleCursorAdapter(this, R.xml.item_who, null, from,
				to, 0);
		lvMain.setAdapter(scAdapter);
		// создаем лоадер для чтения данных
		getSupportLoaderManager().initLoader(0, null, this);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

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
							contentView
									.animate()
									.translationY(
											visible ? 50 : 0)
									.setDuration(mShortAnimTime);

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
		mSystemUiHider.toggle();
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
		case R.id.refresh:
			db.getWritableDatabase().execSQL("delete from idwho; ");
			db.getWritableDatabase()
					.execSQL(
							"insert into idwho (idwho, cnt_gps, cnt_net) select idwho, count(_id), count(_id) from locAll group by idwho ; ");
			RefreshW();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.listwho, menu);
		return true;
	}

	public void RefreshW() {
		getSupportLoaderManager().getLoader(0).forceLoad();
	}

	protected void onDestroy() {
		super.onDestroy();
		// закрываем подключение при выходе
		db.close();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		return new IdWhoCursorLoader(this, db);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		scAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

}
