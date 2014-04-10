package com.jobinbasani.news.ml;

import com.jobinbasani.news.ml.constants.NewsConstants;
import com.jobinbasani.news.ml.interfaces.NewsDataHandlers;
import com.jobinbasani.news.ml.receiver.NewsReceiver;

import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity implements LoaderCallbacks<Cursor>, NewsDataHandlers {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void launchService(View v){
		Log.d(NewsConstants.LOG_TAG, "In btn click");
		Intent bIntent = new Intent(this, NewsReceiver.class);
		sendBroadcast(bIntent);
		Log.d(NewsConstants.LOG_TAG, "After btn click");
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initLoaderWithId(int loaderId) {
		getLoaderManager().initLoader(loaderId, null, this);
	}

}
