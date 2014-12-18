package com.example.wifinetworks;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MapActivity extends FragmentActivity {
	private GoogleMap map;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		
		Intent iin= getIntent();
        Bundle b = iin.getExtras();
        double lat = 0;
        double lon = 0;
        ArrayList<Wifi> wifis = new ArrayList<Wifi>();

        if(b!=null)
        {
            lat = (Double) b.get("lat");
            lon = (Double) b.get("lon");
            wifis = (ArrayList<Wifi>) b.getSerializable("wifis");
            
            LatLng position = new LatLng(lat,lon);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
            map.setMyLocationEnabled(true);
            
            for(int i = 0; i < wifis.size(); i++){

                System.out.println(wifis.get(i).getSsid());
            	LatLng pos = new LatLng(wifis.get(i).getLat(), wifis.get(i).getLon());
            	map.addMarker(new MarkerOptions()
                .position(new LatLng(wifis.get(i).getLat(), wifis.get(i).getLon()))
                .title(wifis.get(i).getSsid()));
            }
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
