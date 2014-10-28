/**
 * This Handler class acts as the middle man between SocketIOLayer class and corresponding Activity class
 */

package com.vibewrite.socketiosampleproject;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public class HandlerExtension extends Handler {
		
		private final WeakReference<TestSocketActivity> currentActivity;
		
		public HandlerExtension(TestSocketActivity activity){
			currentActivity = new WeakReference<TestSocketActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message message)
		{
			TestSocketActivity activity = currentActivity.get();
			
			if (activity!= null)
			{
				
				activity.fetchResponse(message.getData().getStringArrayList("socketoutput"));
			}
		}
	}
