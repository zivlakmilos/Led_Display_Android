package net.ddns.zivlakmilos.leddisplay;

import java.util.HashMap;
import java.util.Set;

import android.support.v7.app.AppCompatActivity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
	
	public static final int FadeAnimationDuration 	= 500;
	public static final int BT_ENABLE_REQUESTS		= 1;
	
	private LinearLayout m_layoutConnection;
	private LinearLayout m_layoutCommunication;
	private Spinner m_spinnerBluetooth;
	
	private BluetoothAdapter m_btAdapter;
	private BroadcastReceiver m_btReciver;
	private BluetoothNetwork m_btNetwork;
	
	private ArrayAdapter<String> m_btDeviceNames;
	private HashMap<String, String> m_btDeviceAdresses;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		m_btNetwork = new BluetoothNetwork();
		m_btNetwork.setConnectHandler(new BluetoothNetworkConnecHandler() {
						
			@Override
			public void onConnect(boolean connect) {
				final boolean connected = connect;
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if(connected) {
							Toast.makeText(getApplication(),
										   "Connection success",
										   Toast.LENGTH_LONG).show();
							showCommunication();
						} else {
							Toast.makeText(getApplication(),
										   "Connection fails",
										   Toast.LENGTH_LONG).show();
						}
					}
				});
			}
		});
		
		m_layoutConnection =
				(LinearLayout)findViewById(R.id.layoutConnection);
		m_layoutCommunication =
				(LinearLayout)findViewById(R.id.layoutCommunication);
		
		m_layoutCommunication.setVisibility(View.GONE);
		
		m_btDeviceNames = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
		m_btDeviceAdresses = new HashMap<String, String>();
		
		m_spinnerBluetooth = (Spinner)findViewById(R.id.spinnerBluetooth);
		m_spinnerBluetooth.setAdapter(m_btDeviceNames);
		
		Button btnConnect = (Button)findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View sender) {
				
				if(m_btNetwork.isConnected()) {
					try {
						m_btNetwork.close();
					} catch(Exception ex) {}
				}
				
				m_btAdapter.cancelDiscovery();
				
				int id = m_spinnerBluetooth.getSelectedItemPosition();
				String deviceName = m_btDeviceNames.getItem(id);
				String deviceAddress = m_btDeviceAdresses.get(deviceName);
				BluetoothDevice device = m_btAdapter.getRemoteDevice(deviceAddress);
				
				try {
					m_btNetwork.connect(device);
				} catch(Exception ex) {
					Toast.makeText(getApplication(),
								   ex.getMessage(),
								   Toast.LENGTH_LONG).show();
				}
			}
		});
		
		m_btReciver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					m_btDeviceNames.add(device.getName());
					m_btDeviceAdresses.put(device.getName(), device.getAddress());
				}
			}
			
		};
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(m_btReciver, filter);
		
		setupBluetooth();
	}
	
	@Override
	protected void onDestroy() {
		try {
			m_btNetwork.close();
		} catch(Exception ex) {}
		
		m_btAdapter.cancelDiscovery();
		unregisterReceiver(m_btReciver);
		super.onDestroy();
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
		if (id == R.id.action_exit) {
			System.exit(0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == BT_ENABLE_REQUESTS) {
			if(resultCode == RESULT_OK) {
				setupBluetooth();
			} else {
				System.exit(0);
			}
		}
	}
	
	private boolean setupBluetooth() {
		m_btAdapter = BluetoothAdapter.getDefaultAdapter();
		if(m_btAdapter == null)
			return false;
		
		if(!m_btAdapter.isEnabled()) {
			Intent btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(btEnableIntent, BT_ENABLE_REQUESTS);
			return false;
		}
		
		Set<BluetoothDevice> pairedDevices = m_btAdapter.getBondedDevices();
		if(pairedDevices.size() > 0) {
			for(BluetoothDevice device : pairedDevices) {
				m_btDeviceNames.add(device.getName());
				m_btDeviceAdresses.put(device.getName(), device.getAddress());
			}
		}
		
		return m_btAdapter.startDiscovery();
	}
	
	private void showCommunication() {
		m_layoutCommunication.setAlpha(0.0f);
		m_layoutConnection.setAlpha(1.0f);
		
		m_layoutConnection.animate()
						  .alpha(0.0f)
						  .setDuration(FadeAnimationDuration)
						  .setListener(new AnimatorListenerAdapter() {
							
							@Override
							public void onAnimationEnd(Animator animation) {
								m_layoutConnection.setVisibility(View.GONE);
								m_layoutCommunication.setVisibility(View.VISIBLE);
								m_layoutCommunication.animate()
													 .alpha(1.0f)
													 .setDuration(FadeAnimationDuration);
							}
						});
	}
	
}
