package net.ddns.zivlakmilos.leddisplay;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothNetwork extends Thread {

	public static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private BluetoothSocket m_btSocket;
	private OutputStream m_btOutput;
	private BluetoothNetworkConnecHandler m_connectHandler = null;
	
	public BluetoothNetwork(BluetoothDevice btDebice) throws Exception {
		try {
			m_btSocket = btDebice.createRfcommSocketToServiceRecord(DEVICE_UUID);
		} catch(IOException ex) {
			throw new Exception("Can't create RFCOMM channel");
		}
	}
	
	@Override
	public void run() {
		try {
			m_btSocket.connect();
			m_btOutput = m_btSocket.getOutputStream();
			if(m_connectHandler != null)
				m_connectHandler.onConnect(true);
		} catch(IOException ex) {
			if(m_connectHandler != null)
				m_connectHandler.onConnect(false);
			try {
				close();
			} catch(Exception ex2) {}
		}
	}
	
	public void close() throws Exception {
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
}
