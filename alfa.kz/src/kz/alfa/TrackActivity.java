package kz.alfa;

import java.util.Date;

import kz.alfa.util.Log;

import com.besaba.vmchat2.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class TrackActivity extends FragmentActivity {
	private static String LOG_TAG = "TrackActivity";
	private static final String MAP_FRAGMENT_TAG = "map";
	private GoogleMap mMap;
	private SupportMapFragment mMapFragment;
	private long backMills = 0;
	CheckBox cb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_track);
		cb = (CheckBox) findViewById(R.id.checkBox1);
		// It isn't possible to set a fragment's id programmatically so we set a
		// tag instead and
		// search for it using that.
		mMapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentByTag(MAP_FRAGMENT_TAG);

		// We only create a fragment if it doesn't already exist.
		if (mMapFragment == null) {
			// To programmatically add the map, we first create a
			// SupportMapFragment.
			mMapFragment = SupportMapFragment.newInstance();

			// Then we add it using a FragmentTransaction.
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.add(android.R.id.content, mMapFragment,
					MAP_FRAGMENT_TAG);
			// mMapFragment.bring
			// fragmentTransaction.add(android.R.id.content, ,
			// MAP_FRAGMENT_TAG);
			fragmentTransaction.commit();
		}

		// We can't be guaranteed that the map is available because Google Play
		// services might
		// not be available.
		setUpMapIfNeeded();
		cb.bringToFront();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((System.currentTimeMillis() - backMills) >= 3000)
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				//Log.e(LOG_TAG, (System.currentTimeMillis()-backMills)+"onKeyDown KEYCODE_BACK");
				Toast.makeText(this, "to exit press back again (in 3 sec)", Toast.LENGTH_SHORT).show();			
				backMills = System.currentTimeMillis();
				return false;
			}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// In case Google Play services has since become available.
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = mMapFragment.getMap();// tExtendedMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		PolylineOptions pl;
		String who = getIntent().getDataString();

		if ((who == null) || (who.length() < 3))
			who = "%355472050491531%";
		Log.e("TrackMActivity", who);
		CameraUpdate center = null;
		pl = getLine("(idwho like '" + who + "' and Provider = 'gps')");
		mMap.addPolyline(pl);
		if (!pl.getPoints().isEmpty())
			center = CameraUpdateFactory.newLatLng(pl.getPoints().get(0));
		pl = getLine("(idwho like '" + who + "' and Provider = 'network')");
		pl.color(Color.RED);
		mMap.addPolyline(pl);
		if (!pl.getPoints().isEmpty())
			center = CameraUpdateFactory.newLatLng(pl.getPoints().get(0));

		CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
		if (center != null)
			mMap.moveCamera(center);
		mMap.animateCamera(zoom);
	}

	private PolylineOptions getLine(String whoS) {
		final Uri CONTACT_URI = Uri.parse("content://me.noip.allloc.prv/loc");
		PolylineOptions pl = new PolylineOptions();
		pl.width(1F);
		try {
			Cursor cursor = getContentResolver().query(CONTACT_URI, null, whoS,
					null, "DTime desc");
			Log.e("TrackMActivity", whoS + " getContentResolver getCount = "
					+ cursor.getCount());
			if (cursor.moveToFirst()) {
				do {
					double lat1 = cursor.getDouble(cursor
							.getColumnIndex("Latitude"));
					double lon1 = cursor.getDouble(cursor
							.getColumnIndex("Longitude"));
					String tit = (new Date(cursor.getLong(cursor
							.getColumnIndex("DTime")))).toString();
					mMap.addMarker(new MarkerOptions()
							.anchor(0.5f, 0.5f)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.circle))
							.position(new LatLng(lat1, lon1))
							.snippet(
									cursor.getString(cursor
											.getColumnIndex("Accuracy")))
							.title(tit));

					double lat = cursor.getDouble(cursor
							.getColumnIndex("Latitude"));
					double lon = cursor.getDouble(cursor
							.getColumnIndex("Longitude"));
					pl.add(new LatLng(lat, lon));
				} while (cursor.moveToNext());
				cursor.close();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Не получилось " + e.toString(),
					Toast.LENGTH_LONG).show();
			Log.e("TrackMActivity", "Не получилось " + e.toString());
		}
		return pl;
	}
}
/*
 * public class TrackActivity extends FragmentActivity { private static String
 * LOG_TAG = "ChatActivity"; SupportMapFragment mMapFragment;
 * 
 * @Override protected void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState); setContentView(R.layout.activity_track);
 * String id = getIntent().getDataString(); } /* <fragment android:id="@+id/map"
 * android:layout_width="match_parent" android:layout_height="match_parent"
 * class="com.google.android.gms.maps.SupportMapFragment" />
 */

