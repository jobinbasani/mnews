package com.jobinbasani.news.ml.receiver;

import com.jobinbasani.news.ml.MainActivity;
import com.jobinbasani.news.ml.service.NewsService;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class NewsReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(MainActivity.LOG_TAG, "In broadcast reciever");
		Intent sIntent = new Intent(context, NewsService.class);
		startWakefulService(context, sIntent);
	}

}
