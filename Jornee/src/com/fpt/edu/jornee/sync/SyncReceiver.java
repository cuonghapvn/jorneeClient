/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: SyncReceiver.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.jornee.sync;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.IntentSender.SendIntentException;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.sax.StartElementListener;
import android.widget.Toast;

public class SyncReceiver extends BroadcastReceiver {

	static Messenger mService = null;
	static boolean mIsBound;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String journeyId = null;
		if(intent.hasExtra("journeyID")){
			journeyId = intent.getStringExtra("journeyID");
			Toast.makeText(context, "Journey ID: "+journeyId, Toast.LENGTH_SHORT).show();
		}
		Intent intent2 = new Intent(context, SyncService.class);
		if(journeyId!=null){
			intent2.putExtra("journeyID", journeyId);
		}
		context.startService(intent2);
	}

}
