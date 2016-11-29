package net.ddns.zivlakmilos.leddisplay;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothNetwork extends Thread {

	public static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private BluetoothSocket m_btSocket;
	private OutputStream m_btOutput;
	private BluetoothNetworkConnecHandler m_connectHandler = null;
	
	private boolean m_connecting;
	private boolean m_connected;
	
	public BluetoothNetwork() {
		m_connecting = false;
		m_connected = false;
	}
	
	/*
	 * TODO:
	 * 	Implement reciveing data and handler
	 */
	@Override
	public void run() {
	}
	
	public void connect(BluetoothDevice btDevice) throws Exception {
		try {
			ConnectingThread connecting = new ConnectingThread(btDevice);
			connecting.start();
		} catch(IOException ex) {
			throw new Exception("Connection failed");
		}
	}
	
	public void close() throws Exception {
		m_connected = false;
		try {
			m_btSocket.close();
		} catch(IOException ex) {
			throw new Exception("Can't close bluettoth socket");
		}
	}
	
	public void send(byte[] bytes) throws Exception {
		try {
			m_btOutput.write(bytes);
		} catch(IOException ex) {
			throw new Exception("Can't send data throug bluetooth");
		}
	}
	
	public void setConnectHandler(BluetoothNetworkConnecHandler connectHandler) {
		m_connectHandler = connectHandler;
	}
	
	public boolean isConnecting() {
		return m_connecting;
	}
	
	public boolean isConnected() {
		return m_connected;
	}
	
	private class ConnectingThread extends Thread {
		
		private BluetoothDevice mm_btDevice;
		private BluetoothSocket mm_btSocket;
		
		public ConnectingThread(BluetoothDevice btDevice) throws Exception {
			mm_btDevice = btDevice;
			
			try {
				mm_btSocket = mm_btDevice.createRfcommSocketToServiceRecord(DEVICE_UUID);
			} catch(IOException ex) {
				throw new Exception("Can't create REFCOMM channel");
			}
		}
		
		@Override
		public void run() {
			try {
				m_connecting = true;
				mm_btSocket.connect();
				try {
					if(m_btSocket != null)
						m_btSocket.close();
				} catch(IOException ex) {}
				m_btSocket = mm_btSocket;
				m_btOutput = m_btSocket.getOutputStream();
				m_connecting = false;
				m_connected = true;
				if(m_connectHandler != null) {
					m_connectHandler.onConnect(true);
				}
			} catch(IOException ex) {
				cancel();
			}
		}
		
		public void cancel() {
			try {
				m_connecting = false;
				mm_btSocket.close();
				if(m_connectHandler != null) {
					m_connectHandler.onConnect(false);
				}
			} catch(IOException ex) {}
		}
	}
}
