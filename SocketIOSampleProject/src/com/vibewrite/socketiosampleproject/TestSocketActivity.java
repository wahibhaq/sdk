package com.vibewrite.socketiosampleproject;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class TestSocketActivity extends Activity {
	
	
 private TextView textLog, textOutput;
 private EditText editTextAddress, editTextPort; 
 private Button buttonConnect, buttonDisconnect, buttonSubscribeToData, buttonUnsubscribe, buttonVibratePen, buttonBlinkPen, buttonClearLog;

 private SocketIOLayer socketioObj;
 private SocketResponseParser parserObj;
 
 boolean isConnected = false;

 Handler resultHandler;
 
 final String TAG = "SocketioTest";
 
 static String logTextConcat = "";
 static String outputTextConcat = "";


 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_testsocket);
  
  editTextAddress = (EditText)findViewById(R.id.address);
  editTextPort = (EditText)findViewById(R.id.port);
  
  buttonConnect = (Button)findViewById(R.id.connect);
  buttonDisconnect = (Button)findViewById(R.id.disconnect);
  
  buttonSubscribeToData = (Button) findViewById(R.id.subscribe);
  buttonUnsubscribe = (Button) findViewById(R.id.unsubscribe);

  buttonVibratePen = (Button) findViewById(R.id.vibratepen);
  buttonBlinkPen = (Button) findViewById(R.id.blinkpen);

  buttonClearLog = (Button) findViewById(R.id.clearlog);

  
  
  textLog = (TextView)findViewById(R.id.log);
  textOutput = (TextView) findViewById(R.id.output);
  

  checkWifiavailability();
  
  resultHandler = new HandlerExtension(this);
  parserObj = new SocketResponseParser();

  socketioObj = new SocketIOLayer(getApplicationContext(), resultHandler);
  
  buttonDisconnect.setOnClickListener(new OnClickListener(){

   @Override
   public void onClick(View v) {
	   
	   clearLogs();
	   socketioObj.disconnectSocket();
	   
   }});
  
  buttonSubscribeToData.setOnClickListener(new OnClickListener(){

	   @Override
	   public void onClick(View v) 
	   {
		   
		 //Example how to initiate subscription for "sensordata" 
		 String advanceParams[] =  {"true","true"};//{trigger_typing, advanced_mode}
		 socketioObj.initiateSubscription("letter", advanceParams);
		 
		 //Example how to initiate subscription for "sensordata" 
		 /*String params[] = {"5"};
		 socketioObj.initiateSubscription("sensordata", null);
		 */
			 
			 

	   }});
  

  buttonUnsubscribe.setOnClickListener(new OnClickListener(){

	   @Override
	   public void onClick(View v) {
		   //textResponse.setText("");
		   
		   socketioObj.initiateUnsubscription("letter");

	   }});
  
  buttonVibratePen.setOnClickListener(new OnClickListener(){

	   @Override
	   public void onClick(View v) {
		   
		   
		   ArrayList vibList = new ArrayList<>();
		   vibList.add(200);//duration
		   vibList.add(10); //repeat
		   
		   socketioObj.initiateCommand("vibrate", vibList);
		   
		   
		 

	   }});
  
  buttonBlinkPen.setOnClickListener(new OnClickListener(){

	   @Override
	   public void onClick(View v) {
		   
		   
		   ArrayList ledList = new ArrayList<>();
		   ledList.add("red");//duration
		   ledList.add(500); //repeat
		   
		   socketioObj.initiateCommand("blink", ledList);
		   

	   }});

  buttonConnect.setOnClickListener(new OnClickListener(){

   @Override
   public void onClick(View v) {
	/////SOCKET IO CODE////
	      
	   populateLogText("");//to reset log
	   populateOuputText("");
	   
		   String ip = editTextAddress.getText().toString().trim();
		   String port = editTextPort.getText().toString().trim();
		   
		   
		   socketioObj.initiateConnection(ip, port, "/sim"); //usewithsimulator
		   //socketioObj.initiateConnection(ip, port,""); //usewithpen //usewithlocalhost
		   
		   Log.i("SocketIotest", "socket URL : " + ip + " : " + port);
		   
		   //populateLogText("socket URL : " + ip + " : " + port);
	       
	     
	   
	   
   }});
  
  buttonClearLog.setOnClickListener(new OnClickListener(){

	   @Override
	   public void onClick(View v) {
		  
		   clearLogs();
		  
	   }});
  
  
  
  
  
    
 } //onCreate
 
 private void clearLogs()
 {
	 textLog.setText("");
	 textOutput.setText("");
	   
	 logTextConcat = "";
	 outputTextConcat = "";
	 
 }
 public void fetchResponse(ArrayList<String> responseList)
 {

 	if(responseList.isEmpty() == false)
 	{
 		try {
 			 			
 			switch(responseList.get(0))
 			{
 				case "Log":
 					populateLogText(responseList.get(1));
 					break;
 					
 				case "Letter":

 					if(responseList.get(1).equals("Normal"))
 					{
 						parserObj.parseLetterParameters(new JSONObject(responseList.get(2)), "Normal"); //socketOutput));
 						populateOuputText(responseList.get(2));
 					}
 					else if(responseList.get(1).equals("Advanced"))
 					{
 						parserObj.parseLetterParameters(new JSONObject(responseList.get(2)), "Advanced"); //socketOutput));
 						populateOuputText(responseList.get(2));
 					}
 						
 					
 					break;
 				
 				case "Sensordata":
 					
 					populateOuputText(responseList.get(1));
 					
 			}
 					
 					
 			
 			
 		} catch (JSONException e) {
 			// TODO Auto-generated catch block
 			Log.i(TAG, "Parse jsonoutput has error");
 			e.printStackTrace();
 		}
 	}
 	else
 	{
 		Log.i(TAG, "Parse jsonoutput is empty !");

 	}

 }
 
/*public void fetchResponse(String response)
{

	if(response.isEmpty() == false)
	{
		try {
			
			String socketOutput[] = response.split("#");
			
			switch(socketOutput[0])
			{
				case "Log":
					populateLogText(socketOutput[1]);
					break;
					
				case "Letter":

					String socketOutput1[] = socketOutput[1].split("@");

					if(socketOutput1[0].equals("Normal"))
					{
						parserObj.parseLetterParameters(new JSONObject(socketOutput[1]), "normal"); //socketOutput));
						populateOuputText(socketOutput[1]);
					}
					else if(socketOutput1[0].equals("Advanced"))
					{
						parserObj.parseLetterParameters(new JSONObject(socketOutput1[1]), "advanced"); //socketOutput));
						populateOuputText(socketOutput1[1]);
					}
						
					
					break;
					
			}
					
					
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "Parse jsonoutput has error");
			e.printStackTrace();
		}
	}
	else
	{
		Log.i(TAG, "Parse jsonoutput is empty !");

	}

}
*/

private void populateLogText(String logText)
{
	logTextConcat = logTextConcat.concat(logText + "\n");

    textLog.setMovementMethod(new ScrollingMovementMethod());
	textLog.setText(logTextConcat);

}

private void populateOuputText(String outputText)
{
	outputTextConcat = outputTextConcat.concat(outputText + "\n");

	textOutput.setMovementMethod(new ScrollingMovementMethod());
	textOutput.setText(outputTextConcat);

}

private void checkWifiavailability() {
	
	ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	NetworkInfo wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

	String ssid = null;
	if(wifiState.isConnected()){
		isConnected = wifiState.isConnected();
		 
		final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		    final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
		    if (connectionInfo != null) 
		    {
		      ssid = connectionInfo.getSSID();
		    }
		    
		    populateLogText("Wifi is connected with " + ssid);
	}
	else 
	{
		isConnected = false;
	    populateLogText("Wifi is disconnected !!");


	}
	
	
}


}