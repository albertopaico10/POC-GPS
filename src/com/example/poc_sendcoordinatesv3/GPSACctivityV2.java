package com.example.poc_sendcoordinatesv3;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.example.poc_sendcoordinatesv3.services.CoordinateService;
import com.example.poc_sendcoordinatesv3.services.impl.CoordinateServiceImpl;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

public class GPSACctivityV2 extends ActionBarActivity {
	
	private LocationManager locationManager;

	private boolean gpsEnabled;
	private boolean gpsFix;
	private double latitude=0;
	private double longitude=0;
	private int satellitesTotal=0;
	private int satellitesUsed=0;
	private float accuracy;
	
	private MyGpsListener gpsListener;

	public static final String TAG = GPSACctivityV2.class.getName();
	private static final long DURATION_TO_FIX_LOST_MS = 10000;
	private static final long MINIMUM_UPDATE_TIME = 2000;
	private static final float MINIMUM_UPDATE_DISTANCE = 0.0f;

	private TextView latitudeText;
	private TextView longitudeText;
	private TextView qualityText;
	private TextView satsTotal;
	private TextView satsUsed;
	private TextView txtCountCalls;
	
	int countTotalCalls=0;
	
	CoordinateService coordinateService=new CoordinateServiceImpl();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_v2_activity);

		latitudeText = (TextView) findViewById(R.id.latitudeText);
		longitudeText = (TextView) findViewById(R.id.longitudeText);
		qualityText = (TextView) findViewById(R.id.qualityText);
		satsTotal = (TextView) findViewById(R.id.satsTotalText);
		satsUsed = (TextView) findViewById(R.id.satsUsedText);
		txtCountCalls = (TextView) findViewById(R.id.satsUsedText2);
	}

	protected void onResume() {
		super.onResume();
		// ask Android for the GPS service
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// make a delegate to receive callbacks
		gpsListener = new MyGpsListener();
		// ask for updates on the GPS status
		locationManager.addGpsStatusListener(gpsListener);
		// ask for updates on the GPS location
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_UPDATE_TIME,
				MINIMUM_UPDATE_DISTANCE, gpsListener);
	}

	protected class MyGpsListener implements GpsStatus.Listener, LocationListener {

		// the last location time is needed to determine if a fix has been lost
		private long locationTime = 0;
		private List<Float> rollingAverageData = new LinkedList<Float>();

		@Override
		public void onGpsStatusChanged(int changeType) {
			if (locationManager != null) {

				// status changed so ask what the change was
				GpsStatus status = locationManager.getGpsStatus(null);
				switch (changeType) {
				case GpsStatus.GPS_EVENT_FIRST_FIX:
					gpsEnabled = true;
					gpsFix = true;
					break;
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					gpsEnabled = true;
					// if it has been more then 10 seconds since the last
					// update, consider the fix lost
					gpsFix = System.currentTimeMillis() - locationTime < DURATION_TO_FIX_LOST_MS;
					break;
				case GpsStatus.GPS_EVENT_STARTED: // GPS turned on
					gpsEnabled = true;
					gpsFix = false;
					break;
				case GpsStatus.GPS_EVENT_STOPPED: // GPS turned off
					gpsEnabled = false;
					gpsFix = false;
					break;
				default:
					Log.w(TAG, "unknown GpsStatus event type. " + changeType);
					return;
				}

				// number of satellites, not useful, but cool
				int newSatTotal = 0;
				int newSatUsed = 0;
				for (GpsSatellite sat : status.getSatellites()) {
					newSatTotal++;
					if (sat.usedInFix()) {
						newSatUsed++;
					}
				}
				satellitesTotal = newSatTotal;
				satellitesUsed = newSatUsed;

				updateView();
			}
		}

		@Override
		public void onLocationChanged(Location location) {

			locationTime = location.getTime();
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			countTotalCalls++;
			if (location.hasAccuracy()) {
				// rolling average of accuracy so "Signal Quality" is not
				// erratic
				updateRollingAverage(location.getAccuracy());
			}

			updateView();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			/* dont need this info */
		}

		@Override
		public void onProviderEnabled(String provider) {
			/* dont need this info */
		}

		@Override
		public void onProviderDisabled(String provider) {
			/* dont need this info */
		}

		private void updateRollingAverage(float value) {
			// does a simple rolling average
			rollingAverageData.add(value);
			if (rollingAverageData.size() > 10) {
				rollingAverageData.remove(0);
			}

			float average = 0.0f;
			for (Float number : rollingAverageData) {
				average += number;
			}
			average = average / rollingAverageData.size();

			accuracy = average;
		}
	}

	private void updateView() {

		// update all data in the UI
		latitudeText.setText(String.valueOf(latitude));
		longitudeText.setText(String.valueOf(longitude));
		qualityText.setText(getGrade());
		satsTotal.setText(String.valueOf(satellitesTotal));
		satsUsed.setText(String.valueOf(satellitesUsed));
		txtCountCalls.setText(String.valueOf(countTotalCalls));
	}

	private String getGrade() {

		if (!gpsEnabled) {
			return "Disabled";
		}
		if (!gpsFix) {
			return "Waiting for Fix";
		}
		if (accuracy <= 10) {
			new RegisterService().execute();
			return "Good";
		}
		if (accuracy <= 30) {
			new RegisterService().execute();
			return "Fair";
		}
		if (accuracy <= 100) {
			new RegisterService().execute();
			return "Bad";
		}
		return "Unusable";
	}
	
	
	
	private class RegisterService extends AsyncTask<Void, Void, Void> {
		private String Content="";
		@Override
		protected void onPreExecute() {
			
		}
		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... params) {
			try {
				
				Content=coordinateService.callService(String.valueOf(latitude),String.valueOf(longitude),"car1");
			} catch (Exception e) {
//				resultServices=false;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			JSONObject jObject = null;
			try {
				jObject = new JSONObject(Content);
				String codeResponse = jObject.getString("codeResponse");
				System.out.println("RESPUESTA : "+codeResponse);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
