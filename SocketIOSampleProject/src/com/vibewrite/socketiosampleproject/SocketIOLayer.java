/* Testing */

package com.vibewrite.socketiosampleproject;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Advanceable;


public class SocketIOLayer implements IOCallback{
	
	 private SocketIO socket;
	 
	 private static String logOutput;
	 
	 final String TAG = "SocketioTest";
	 final String TARGET = "Simulator";
	 
	 static boolean isConnected = false;
	 static boolean isLoggedIn = false;
	 static boolean isLetterModeAdvance = false;
	 
	 Context appContext;
	 
	 //Json Parameters//
	 String penValue;
	 String penAction;
	 
	 private Handler resultHandler;
	 
	 //private enum CommandTypeEnum {vibrate, led, handwritingsettings};
	 private enum CommandEnum {command, params};
	 
	 private enum VibrateEnum {duration, repeat};
	 private enum LedEnum {color, duration};
	 private enum HandWritingSettingsEnum {key, value};
	 //private enum AvailableHandWritingEnum {dictionary, writer};
	 
	 //private enum LetterNormalEnum {eventname, word, spelling_mistake, word_end};
	 //private enum LetterAdvancedEnum {eventname, word, word_current, letter, trueletter, spelling_mistake, word_end};

	 private static Hashtable<String, Object> letterHashObj;
	 
	 public SocketIOLayer(Context context, Handler handler)
	 {
		 logOutput = "";
		 appContext = context;
		 
		 penValue = "";
		 penAction = "";
		 
		 resultHandler = handler;
		 
	 }
	 

	 //Main function which receive server response from pen
	@Override
	public void on(String event, IOAcknowledge ack, Object... serverResponse) {
		// TODO Auto-generated method stub
		Log.i("socket", " socket Server triggered event '" + event + "'");
	    
		
		switch(event)
		{
		
			
			case "letter":
					handleLetterResponse(serverResponse);
				break;
				
			case "sensordata":
					handleSensorResponse(serverResponse);
				break;
				
			case "disconnect":
					targetHasDisconnected();
				break;
			
			case "idle":
				break;
			
			case "gesture":
				break;
			
				
				
				
		}
		

		
	}

	@Override
	public void onConnect() {
		// TODO Auto-generated method stub
		
		 Log.i("socket", "SocketIOLayer Connection established" );
		  isConnected = true;
		    
		 displayLogsOnScreen("socket Connection established");

		 initiateLogin();
		 
		
	}

	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub
		
		isConnected = false;
	    Log.i("socket", "onDisconnect : SocketIOLayer Connection terminated" );
	    displayLogsOnScreen("onDisconnect : SocketIO Connection terminated");
	    
		
	}

	@Override
	public void onError(SocketIOException error) {
		// TODO Auto-generated method stub
		
		 Log.i("socket", "SocketIOLayer an Error occured" );
		    
		 displayLogsOnScreen("socket an Error occured");
		 error.printStackTrace();
		
	}

	@Override
	public void onMessage(String data, IOAcknowledge arg1) {
		// TODO Auto-generated method stub
		Log.i("socket", "SocketIOLayer Server said: " + data );
		   
	    displayLogsOnScreen("onMessage received : " + data );
		
		
	}

	@Override
	public void onMessage(JSONObject json, IOAcknowledge arg1) {
		// TODO Auto-generated method stub
		 try {
	         //System.out.println("Server said:" + json.toString(2));
			    Log.i("socket", "SocketIOLayer Server said: " + json.toString(2) );
			    displayLogsOnScreen("socket Server said: " + json.toString(2) );


	     } catch (JSONException e) {
	         e.printStackTrace();

	         displayLogsOnScreen("onMessage : " + "Error with JSON received !" );

	     }
		
	}

	
	//Custom Methods//
	public void initiateConnection(String ipaddress, String port, String subdomain)
	{

		   String url = "http://" + ipaddress + ":" + port + subdomain; //"/sim";
		   displayLogsOnScreen("socket trying with URL : " + url);

		   try {
			   
			socket = new SocketIO(url);
			   
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		   displayLogsOnScreen("Initiate Connection : " + "Error with Url !");

		}
		   		   
		   
	       socket.connect(SocketIOLayer.this);
		   
		
	       
	       
	       
	}
	

/**
 * This is called once connection is established, to pass important parameters
 * 
 */
public void initiateLogin()
{
	displayLogsOnScreen("initiating Login");
	 
    JSONObject jsonArgs = new JSONObject();

    try {
    	
		jsonArgs.putOpt("type", "mobile");
		jsonArgs.putOpt("key", ""); //Insert key here e.g _guid_7Bvueydh75a4egul4cwAbc-8H5HNUS9YiwxeHxc676hg
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		
		displayLogsOnScreen("Initiate Login : Error, maybe Dev Key is missing or wrong !");

	}
    
    
    socket.emit("login", new IOAcknowledge() {
       
		@Override
		public void ack(Object... arg0) {
			// TODO Auto-generated method stub
            //Log.e(TAG,"Login done successfully !acknowledge: "+ arg0);
            
            isLoggedIn = true;
			Log.i("socket","SocketIOLayer login is successful !!");


		}
  }, jsonArgs);
}

/**
 * This is to subscribe to listen to events sent from pen e.g letter, word etc but for normal mode
 */
public void initiateSubscription(String eventName, String[] params)
{
	if( eventName.isEmpty() == false)
	{
		switch(eventName)
		{
			case "letter":
				displayLogsOnScreen("Initiation Subscription : For " + eventName + " Event" );
				
				if(params == null)
				{
					//Letter Mode is Normal
					isLetterModeAdvance = false;
					handleLetterSubscription(null);
				}
				else
				{
					//Letter Mode is Advanced
					isLetterModeAdvance = true;
					handleLetterSubscription(params);
				}
				
				break;
			
			case "sensordata":
				displayLogsOnScreen("Initiation Subscription : For " + eventName + " Event" );
				
				if(params == null)
					handleSensorSubscription(null);
				else
					handleSensorSubscription(params);
					
				break;
		}
	}
	else
		displayLogsOnScreen("Initiate Subscription : Event Name missing !");
	
		
}

/**
 * This is to subscribe to listen to events sent from pen e.g letter, word etc but for advance mode with params
 */
public void handleLetterSubscription(String params[])
{
	if(params == null)
	{
		JSONObject jsonArgs = new JSONObject();

	    try {
	    	
			jsonArgs.putOpt("eventname", "letter");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			displayLogsOnScreen("Initiate Subscription Letter : Error with json sent !");

		}
	    
	    socket.emit("subscribe", jsonArgs);
	}
	else if( params.length > 0)
	{
		
		JSONObject jsonParams = new JSONObject();
	    
	    try {
	    	
	    	jsonParams.putOpt("trigger_typing", Boolean.valueOf(params[0]));
			jsonParams.putOpt("advanced_mode", Boolean.valueOf(params[1]));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			displayLogsOnScreen("Initiate Subscription : Error with json sent !");			
		} 
		
		
	    JSONObject jsonMain = new JSONObject();
	    
	    try {
	    	
			jsonMain.putOpt("eventname", "letter");
			jsonMain.putOpt("params", jsonParams);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			displayLogsOnScreen("Initiate Subscription : Error with json sent !");			
		}
	    
	    socket.emit("subscribe", jsonMain);
	}
	else
		displayLogsOnScreen("Initiate Subscription Letter : Params missing or should be null !");

}

private void handleSensorSubscription(String[] params)
{
	if(params == null)
	{
		JSONObject jsonArgs = new JSONObject();

	    try {
	    	
			jsonArgs.putOpt("eventname", "sensordata");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			displayLogsOnScreen("Initiate Subscription Sensordata : Error with json sent !");

		}
	    
	    socket.emit("subscribe", jsonArgs);
	}
	else if( params.length > 0)
	{
	 	    
	    JSONObject jsonParams = new JSONObject();
	    
	    try {
	    	
			jsonParams.putOpt("imu_frequency", Integer.valueOf(params[0]));
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			displayLogsOnScreen("Initiate Subscription : Error with json sent !");			
		} 
		
		
	    JSONObject jsonMain = new JSONObject();
	    
	    try {
	    	
	    	jsonMain.putOpt("eventname", "sensordata");
			jsonMain.putOpt("params", jsonParams);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			displayLogsOnScreen("Initiate Subscription : Error with json sent !");			
		}
	    
	    socket.emit("subscribe", jsonMain);
	}
	else
		displayLogsOnScreen("Initiate Subscription Sensordata : Params missing or shoudl be null !");

}

/**
 * This is to unsubscribe to stop listening to events sent from pen e.g letter, word etc
 */
public void initiateUnsubscription(String eventName)
{
	displayLogsOnScreen("Initiation UnSubscription : For " + eventName + " Event");
	 
    JSONObject jsonArgs = new JSONObject();

    try {
    	
		jsonArgs.putOpt("eventname", eventName);
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		
		displayLogsOnScreen("Initiate Subscription : Error with json sent !");

		
	}
    
    socket.emit("unsubscribe", jsonArgs);
}

	
	public void disconnectSocket()
	{
		   socket.disconnect();

	}
	



public void finishCommunicationWithPen()
{

	disconnectSocket();
	
	//maybe there is something which needs to be stored 
}

private void targetHasDisconnected()
{
	isConnected = false;

	displayLogsOnScreen("TARGET : disconnected !");
	
	//Maybe there is something which needs to be notified
}

/**
 * This is main function which parses and decides which command type to initiate
 * @param commandType
 * @param paramList
 */
public void initiateCommand(String commandType, ArrayList paramList)//CommandTypeEnum commandType, ArrayList paramList)
{
	boolean shouldEmit = false;
	
	if(isLoggedIn == true)
	{
		
		
		JSONObject jsonMain = new JSONObject();
		
	    try {
			jsonMain.putOpt(CommandEnum.command.toString(), commandType.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    JSONObject jsonParams = new JSONObject();
	    
		switch(commandType)
		{
			case "vibrate":
			{
				try 
				{
			    	
			    	if(paramList.size() == 1 )
			    	{
			    		jsonParams.putOpt(VibrateEnum.duration.toString(), paramList.get(0));
			    		shouldEmit = true;
			    	}
			    	else if(paramList.size() == 2)
			    	{
			    		jsonParams.putOpt(VibrateEnum.duration.toString(), paramList.get(0));
			    		jsonParams.putOpt(VibrateEnum.repeat.toString(), paramList.get(1));
			    		
			    		shouldEmit = true;

			    	}
			    	
	
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
				
			}
		case "handwritingsettings":
			
				try 
				{
			    	
			    	if(paramList.size() == 1 )
			    	{
			    		jsonParams.putOpt(HandWritingSettingsEnum.key.toString(), paramList.get(0));
			    		
			    		shouldEmit = true;

			    	}
			    	else if(paramList.size() == 2)
			    	{
			    		jsonParams.putOpt(HandWritingSettingsEnum.key.toString(), paramList.get(0));
			    		jsonParams.putOpt(HandWritingSettingsEnum.value.toString(), paramList.get(1));
			    		
			    		shouldEmit = true;

			    	}
			    	
	
		
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
				break;
				
		case "blink":
			
			try 
			{
		    	
		    	if(paramList.size() == 1 )
		    	{
		    		jsonParams.putOpt(LedEnum.color.toString(), paramList.get(0));
		    		
		    		shouldEmit = true;

		    	}
		    	else if(paramList.size() == 2)
		    	{
		    		jsonParams.putOpt(LedEnum.color.toString(), paramList.get(0));
		    		jsonParams.putOpt(LedEnum.duration.toString(), paramList.get(1));
		    		
		    		shouldEmit = true;

		    	}
		    	
	
	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		default:
			break;
		}
		
		try {
			jsonMain.putOpt(CommandEnum.params.toString(), jsonParams);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(shouldEmit)
		{
			displayLogsOnScreen("Command : " + commandType.toString() + "Params : " + jsonMain.toString());
	
			socket.emit(CommandEnum.command.toString(), jsonMain);
		}
		
	}
	else
	{
		displayLogsOnScreen("Command : Error, You are not Logged In");
		Log.i("socket","SocketIOLayer command : you are not logged in !!");

	}
}


/**
 * handleLetter() get triggers on event "letter" which is meant for managing alphabets
 * @param response
 */
private void handleLetterResponse(Object[] response)
{
	Log.i("socket","socket letter event received on on() func");
    displayLogsOnScreen("Letter : Event received from " + TARGET);
    
    JSONObject jo = null;
    Object[] parameters = response;

	ArrayList<String> responseList = new ArrayList<String>();
	responseList.add("Letter");
	
    try 
    {
        jo = new JSONObject(parameters[0].toString());
		Log.i("socket", "SocketIOLayer Server said: " + jo.toString() );
		
		if(isLetterModeAdvance == false)
		{
			responseList.add("Advanced");
			responseList.add(jo.toString());
			sendResponseBackToActivity(responseList);
						
		}
		else
		{
			responseList.add("Normal");
			responseList.add(jo.toString());
			sendResponseBackToActivity(responseList);
			
		}

    } 
    catch (JSONException e) 
    {
        e.printStackTrace();
    }
    
	
}

/**
 * handleSensorResponse() get triggers on event "sensordata" which is meant for managing motion sensor data from pen
 * @param response
 */
private void handleSensorResponse(Object[] response)
{
	Log.i("socket","socket sensor event received on on() func");
    displayLogsOnScreen("Sensordata : Event received from " + TARGET);
    
    JSONObject jo = null;
    Object[] parameters = response;

	ArrayList<String> responseList = new ArrayList<String>();
	responseList.add("Sensordata");
	
    try 
    {
        jo = new JSONObject(parameters[0].toString());
		Log.i("socket", "SocketIOLayer Server said: " + jo.toString() );
		
		
		responseList.add(jo.toString());
		sendResponseBackToActivity(responseList);
			
		

    } 
    catch (JSONException e) 
    {
        e.printStackTrace();
    }
    
	
}



private void displayLogsOnScreen(final String logData)
{
		
	ArrayList<String> logList = new ArrayList<String>();
	logList.add("Log");
	logList.add(logData);
	
	sendMessageBundle(logList);
	
	Log.i("SocketIOLayer", "SocketIOLayer logoutput : " + logData);
	
	
}

/**
 * Interface to send response from pen to sendMessageBundle function
 * 
 * @param responseList
 */
private void sendResponseBackToActivity(ArrayList<String> responseList)
{

	
	sendMessageBundle(responseList);
	
	
	Log.i("SocketIOLayer", "SocketIOLayer Response send back to screen : " + responseList);
}

/**
 * Sends response from pen to HandlerExtension.java
 * 
 * @param output
 */
private void sendMessageBundle(ArrayList<String> output)
{
	Bundle msgBundle = new Bundle();
	msgBundle.putStringArrayList("socketoutput", output);
	Message msg = new Message();
	msg.setData(msgBundle);
	resultHandler.sendMessage(msg);
	
}



	
}