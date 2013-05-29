package com.gedasv.lokatorius;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public LocationManager locationManager = null;
	public LocationListener locationListener = null;
	public Geocoder gc = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gc = new Geocoder(this, Locale.ENGLISH);
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				
				float accur = location.getAccuracy();
				Log.d("mano", "Gauta nauja pozicija, tikslumas: " + accur);

				if (MainApplication.locAccuracy == 0
						|| (MainApplication.locAccuracy > accur && accur > 0)) {

					MainApplication.locAccuracy = accur;
					MainApplication.location = location;
					
					setAddress();
				}
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};
		
		try {
			//Log.d("mano", "provider: " + locationManager.getBestProvider(null, true));

			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		} catch (Exception e) {
			Log.d("mano", "error: " + e);
		}

		Button bReset = (Button)findViewById(R.id.bReset);
		bReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainApplication.locAccuracy = 0;
			}
		});
		
	}
	
	@Override
	protected void onDestroy() {

		if(locationManager != null){
			
			locationManager.removeUpdates(locationListener);
			locationManager.removeUpdates(locationListener);
			
		}
		
		super.onDestroy();
	}
	
	private void setAddress() {

		TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
		TextView tvCoords = (TextView) findViewById(R.id.tvMain);
		
		StringBuilder sbCoord = new StringBuilder("");
		sbCoord.append("Koordinatės:\n");
		sbCoord.append("Lat: " + MainApplication.location.getLatitude() + "\n");
		sbCoord.append("Lont: " + MainApplication.location.getLongitude() + "\n");
		sbCoord.append("Pakilimas: " + MainApplication.location.getAltitude() + "\n");
		sbCoord.append("Tikslumas: " + MainApplication.location.getAccuracy() + " m.");
		
		tvCoords.setText(sbCoord.toString());
		
		StringBuilder sbAddr = new StringBuilder("");
		sbAddr.append("Adresas:\n");
		
		if (MainApplication.location != null) {
			try {
				List<Address> addresses = gc.getFromLocation(
						MainApplication.location.getLatitude(),
						MainApplication.location.getLongitude(), 1);

				if (addresses != null && addresses.size() > 0) {
					
					Address returnedAddress = addresses.get(0);

					StringBuilder strReturnedAddress = new StringBuilder("");
					for (int i = 0; i < returnedAddress
							.getMaxAddressLineIndex(); i++) {
						strReturnedAddress.append(returnedAddress
								.getAddressLine(i));

						if (i < returnedAddress.getMaxAddressLineIndex() - 1)
							strReturnedAddress.append("\n");
					}

					sbAddr.append(strReturnedAddress.toString());
				} else {
					sbAddr.append("Adresas nepasiekiamas");
				}
			} catch (IOException e) {
				e.printStackTrace();
				sbAddr.append("GPS lokacija neveikia");
			}
		}
		
		tvAddress.setText(sbAddr.toString());
		
	}

}
