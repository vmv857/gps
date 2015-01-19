package kz.alfa;

import java.util.ArrayList;
import java.util.Date;
import kz.alfa.util.Log;
import com.besaba.vmchat2.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class TrackActivity extends FragmentActivity implements
		SeekBar.OnSeekBarChangeListener {
	private static String LOG_TAG = "TrackActivity";
	private static final String MAP_FRAGMENT_TAG = "map";
	private GoogleMap mMap;
	private SupportMapFragment mMapFragment;
	private long backMills = 0;
	private Polyline plGps;
	private Polyline plNet;
	private SeekBar seekBar;
	private ArrayList<Marker> mrGps = new ArrayList<Marker>();
	private ArrayList<Marker> mrNet = new ArrayList<Marker>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_track);
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
			// mMapFragment.getActivity().addContentView(cb, new
			// LayoutParams(Gravity.CENTER_VERTICAL));
			// Then we add it using a FragmentTransaction.
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.add(R.id.map_fragment, mMapFragment,
					MAP_FRAGMENT_TAG);
			// mMapFragment.bring
			// fragmentTransaction.add(android.R.id.content, ,
			// MAP_FRAGMENT_TAG);
			// fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}

		// We can't be guaranteed that the map is available because Google Play
		// services might
		// not be available.
		setUpMapIfNeeded();
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU)
			setUpMap();
		if ((System.currentTimeMillis() - backMills) >= 3000)
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				// Log.e(LOG_TAG,
				// (System.currentTimeMillis()-backMills)+"onKeyDown KEYCODE_BACK");
				Toast.makeText(this, "to exit press back again (in 3 sec)",
						Toast.LENGTH_SHORT).show();
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
		// setUpMap();
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
				Log.e(LOG_TAG, "no setUpMap ");
			}
		}
	}

	private void setUpMap() {
		Log.e(LOG_TAG, "setUpMap !!!");
		mMap.setMyLocationEnabled(true);
		mMap.setIndoorEnabled(true);
		mMap.setBuildingsEnabled(true);
		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			Circle cir = null;

			@Override
			public boolean onMarkerClick(Marker arg0) {
				try {
					if (cir != null)
						cir.remove();
					if (!arg0.getSnippet().isEmpty())
						cir = mMap.addCircle(new CircleOptions()
								.center(arg0.getPosition())
								.strokeColor(Color.RED)
								.radius(2 * Float.valueOf(arg0.getSnippet()))
								.strokeWidth(2F));
				} catch (NumberFormatException e) {
					Log.e(LOG_TAG, arg0 + e.toString() + arg0.getSnippet());
				}
				return false;
			}
		});
	}

	private PolylineOptions getLine(String whoS, ArrayList<Marker> mrList,
			int res) {
		final Uri CONTACT_URI = Uri.parse("content://me.noip.allloc.prv/loc");
		PolylineOptions pl = new PolylineOptions();
		pl.width(0.5F);
		try {
			Cursor cursor = getContentResolver().query(CONTACT_URI, null, whoS,
					null, "DTime desc");
			Log.e("TrackMActivity", whoS + " getContentResolver getCount = "
					+ cursor.getCount());
			if (cursor.moveToFirst()) {
				MarkerOptions first = null;
				do {
					double lat1 = cursor.getDouble(cursor
							.getColumnIndex("Latitude"));
					double lon1 = cursor.getDouble(cursor
							.getColumnIndex("Longitude"));
					String tit = (new Date(cursor.getLong(cursor
							.getColumnIndex("DTime")))).toString();
					if (first == null) {
						first = new MarkerOptions()
								.snippet(
										cursor.getString(cursor
												.getColumnIndex("Accuracy")))
								.position(new LatLng(lat1, lon1))
								.title(tit + "\n" + whoS);
						mMap.addMarker(first);
					}
					Marker m = mMap.addMarker(new MarkerOptions()
							.anchor(0.5f, 0.5f)
							.icon(BitmapDescriptorFactory.fromResource(res))
							.position(new LatLng(lat1, lon1))
							.snippet(
									cursor.getString(cursor
											.getColumnIndex("Accuracy")))
							.title(tit + "\n" + whoS));
					mrList.add(m);
					pl.add(new LatLng(lat1, lon1));
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

	public void onClick_gps(View v) {
		PolylineOptions pl_gps;
		CheckBox cb = (CheckBox) v;
		if (cb.isChecked()) {
			String who = getIntent().getDataString();
			CameraUpdate center = null;
			pl_gps = getLine("(idwho like '" + who + "' and Provider = 'gps')",
					mrGps, R.drawable.circle_g);
			plGps = mMap.addPolyline(pl_gps);
			if (!pl_gps.getPoints().isEmpty()) {
				center = CameraUpdateFactory.newLatLng(pl_gps.getPoints()
						.get(0));
				seekBar.setMax(pl_gps.getPoints().size());
				seekBar.setProgress(pl_gps.getPoints().size());
			}
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);
			if (center != null) {
				mMap.animateCamera(zoom);
				mMap.moveCamera(center);
			}
		} else if (plGps != null) {
			plGps.remove();
			plGps.setPoints(new ArrayList<LatLng>());
			for (int i = 1; i < mrGps.size(); i++)
				mrGps.get(i).remove();
			mrGps.clear();
			seekBar.setMax(plNet.getPoints().size());
		}
	}

	public void onClick_net(View v) {
		PolylineOptions pl_net;
		CheckBox cb = (CheckBox) v;
		if (cb.isChecked()) {
			String who = getIntent().getDataString();
			CameraUpdate center = null;
			pl_net = getLine("(idwho like '" + who
					+ "' and Provider = 'network')", mrNet, R.drawable.circle);
			pl_net.color(Color.RED);
			plNet = mMap.addPolyline(pl_net);
			if (!pl_net.getPoints().isEmpty()) {
				center = CameraUpdateFactory.newLatLng(pl_net.getPoints()
						.get(0));
				seekBar.setMax(pl_net.getPoints().size());
				seekBar.setProgress(0);
			}
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
			if (center != null) {
				mMap.animateCamera(zoom);
				mMap.moveCamera(center);
			}
		} else if (plNet != null) {
			plNet.remove();
			plNet.setPoints(new ArrayList<LatLng>());
			for (int i = 1; i < mrNet.size(); i++)
				mrNet.get(i).remove();
			mrNet.clear();
			seekBar.setMax(plGps.getPoints().size());
		}

	}

	public void onClick_tst(View v) {
		Toast.makeText(this, "onClick_net", Toast.LENGTH_SHORT).show();
		CheckBox cb = (CheckBox) v;
		if (cb.isChecked()) {
			Toast.makeText(this, " Map clear ", Toast.LENGTH_SHORT).show();
			mMap.clear();
			mrGps.clear();
			mrNet.clear();
			cb.setChecked(false);
			cb = (CheckBox) findViewById(R.id.chBox_gps);
			cb.setChecked(false);
			cb = (CheckBox) findViewById(R.id.chBox_net);
			cb.setChecked(false);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		LatLng l;
		if (fromUser && (progress > 0)) {
			if ((plGps != null) && (progress < plGps.getPoints().size())) {
				l = plGps.getPoints().get(progress);
				mMap.moveCamera(CameraUpdateFactory.newLatLng(l));
				if (mrGps.size() >= progress)
					mrGps.get(progress).showInfoWindow();
			}
			if ((plNet != null) && (progress < plNet.getPoints().size())) {
				l = plNet.getPoints().get(progress);
				mMap.moveCamera(CameraUpdateFactory.newLatLng(l));
				if (mrNet.size() >= progress)
					mrNet.get(progress).showInfoWindow();
			}
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}