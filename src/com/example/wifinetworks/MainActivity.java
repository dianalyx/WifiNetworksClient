package com.example.wifinetworks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private WifiManager mainWifi;
	private WifiReceiver receiverWifi;
	private List<ScanResult> wifiList;
	private ArrayAdapter<String> adapter;
	private ListView list;
	private String[] ssids;
	private LocationManager locationManager;
	private ArrayList<Wifi> wifis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button scanBtn = (Button) findViewById(R.id.scanBtn);
		Button findBtn = (Button) findViewById(R.id.findBtn);
		list = (ListView) findViewById(R.id.listView);

		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		receiverWifi = new WifiReceiver();
		registerReceiver(receiverWifi, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		scanBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mainWifi.startScan();
			}
		});

		findBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Location location = null;

				for (int i = 0; i < locationManager.getAllProviders().size(); i++) {
					location = locationManager
							.getLastKnownLocation(locationManager
									.getAllProviders().get(i));
					if (location != null)
						break;
				}

				final double lat = location.getLatitude();
				final double lon = location.getLongitude();
				final Runnable runnable = new Runnable() {
					@Override
					public void run() {
						final String link = "http://10.0.0.11:8080/list?lat="
								+ lat + "&lon=" + lon;
						String inputLine = null;
						URL url = null;
						StringBuilder builder = null;
						try {
							url = new URL(link);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
						ArrayList<Wifi> wifis = new ArrayList<Wifi>();
						URLConnection yc = null;
						try {
							yc = url.openConnection();
							BufferedReader in;
							in = new BufferedReader(new InputStreamReader(yc
									.getInputStream()));
							builder = new StringBuilder();

							while ((inputLine = in.readLine()) != null) {
								builder.append(inputLine);
							}
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

						Wifi temp;
						String ssid = null;
						double latit = 0, longit = 0;

						Pattern pattern2 = Pattern
								.compile("<location>(.*?)</location>");
						Matcher matcher2 = pattern2.matcher(builder.toString());
						while (matcher2.find()) {
							String str = matcher2.group();
							System.out.println("str "+str);
							Pattern pattern3 = Pattern
									.compile("<ssid>(.*?)</ssid>");
							Matcher matcher3 = pattern3.matcher(str);
							if (matcher3.find()) {
								String t = matcher3.group();
								String[] arr = t.split(">");
								arr = arr[1].split("</");
								ssid = arr[0];
							}
							pattern3 = Pattern.compile("<lat>(.*?)</lat>");
							matcher3 = pattern3.matcher(str);
							if (matcher3.find()) {
								String t = matcher3.group();
								String[] arr = t.split(">");
								arr = arr[1].split("</");
								latit = Double.parseDouble(arr[0]);
							}
							pattern3 = Pattern.compile("<lon>(.*?)</lon>");
							matcher3 = pattern3.matcher(str);
							if (matcher3.find()) {
								String t = matcher3.group();
								String[] arr = t.split(">");
								arr = arr[1].split("</");
								longit = Double.parseDouble(arr[0]);
							}
							
							temp = new Wifi(ssid);
							temp.setLat(latit);
							temp.setLon(longit);
							System.out.println(ssid);
							wifis.add(temp);
						}
						
						Intent intent2 = new Intent(MainActivity.this,
								MapActivity.class);
						intent2.putExtra("lat", lat);
						intent2.putExtra("lon", lon);
						System.out.println("lat " + lat);
						intent2.putExtra("wifis", wifis);
						startActivity(intent2);
					}

				};

				Thread thread = new Thread(runnable);
				thread.start();
				
			}
		});

		list.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final AlertDialog alertDialog = new AlertDialog.Builder(
						MainActivity.this).create();
				final int pos = position;
				alertDialog.setTitle(ssids[pos]);

				alertDialog.setMessage("Do you want to register " + ssids[pos]
						+ "?");
				alertDialog.setCanceledOnTouchOutside(true);

				alertDialog.setButton("Register",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								Location location = null;
								for (int i = 0; i < locationManager
										.getAllProviders().size(); i++) {
									location = locationManager
											.getLastKnownLocation(locationManager
													.getAllProviders().get(i));
									if (location != null)
										break;
								}
								double lat = 0, lon = 0;
								if (location != null) {
									lat = location.getLatitude();
									lon = location.getLongitude();
								}
								final String link = "http://10.0.0.11:8080/register?lat="
										+ lat
										+ "&lon="
										+ lon
										+ "&ssid="
										+ ssids[pos];
								Runnable runnable = new Runnable() {
									@SuppressLint("ShowToast")
									@Override
									public void run() {
										String inputLine = null;
										URL url;
										StringBuilder builder = null;
										try {
											url = new URL(link);

											URLConnection yc = url
													.openConnection();
											BufferedReader in = new BufferedReader(
													new InputStreamReader(yc
															.getInputStream()));
											builder = new StringBuilder();

											while ((inputLine = in.readLine()) != null) {
												builder.append(inputLine);
											}
											in.close();
										} catch (MalformedURLException e) {
											e.printStackTrace();
										} catch (IOException e) {
											e.printStackTrace();
										}
										System.out.println("a");
										if (builder.toString().contains("OK")) {
											runOnUiThread(new Runnable() {
												public void run() {
													Toast.makeText(
															MainActivity.this,
															"Wifi network registered",
															Toast.LENGTH_SHORT)
															.show();
												}
											});
										} else {
											String[] s = builder.toString()
													.split("<body>");
											final String[] s2 = s[1]
													.split("</body>");
											runOnUiThread(new Runnable() {
												public void run() {
													Toast.makeText(
															MainActivity.this,
															s2[0],
															Toast.LENGTH_SHORT)
															.show();
												}
											});
										}
									}
								};
								Thread thread = new Thread(runnable);
								thread.start();

							}
						});
				alertDialog.show();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {

			wifiList = mainWifi.getScanResults();
			int count = 0;

			for (int i = 0; i < wifiList.size(); i++) {
				if (!(wifiList.get(i).capabilities.contains("WEP")
						|| wifiList.get(i).capabilities.contains("WPA") || wifiList
							.get(i).capabilities.contains("WPA2"))) {
					count++;
				}
			}

			ssids = new String[count];
			int j = 0;
			for (int i = 0; i < wifiList.size(); i++) {
				if (!(wifiList.get(i).capabilities.contains("WEP")
						|| wifiList.get(i).capabilities.contains("WPA") || wifiList
							.get(i).capabilities.contains("WPA2"))) {
					ssids[j] = wifiList.get(i).SSID;
					j++;
				}
			}

			adapter = new ArrayAdapter<String>(MainActivity.this,
					android.R.layout.simple_list_item_1, android.R.id.text1,
					ssids);

			list.setAdapter(adapter);

		}
	}
}
