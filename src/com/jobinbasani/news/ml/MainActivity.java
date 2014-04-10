package com.jobinbasani.news.ml;

import com.jobinbasani.news.ml.constants.NewsConstants;
import com.jobinbasani.news.ml.fragments.CategorySelector;
import com.jobinbasani.news.ml.interfaces.NewsDataHandlers;
import com.jobinbasani.news.ml.provider.NewsDataContract;
import com.jobinbasani.news.ml.receiver.NewsReceiver;

import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity implements LoaderCallbacks<Cursor>, NewsDataHandlers {
	
	CategorySelector categorySelector;

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
		if(categorySelector==null){
			categorySelector = (CategorySelector) getFragmentManager().findFragmentByTag(getResources().getString(R.string.categorySelectorTag));
		}
		switch(id){
		case NewsConstants.CATEGORY_LOADER_ID:
			return new CursorLoader(MainActivity.this, NewsDataContract.CONTENT_URI_CATEGORIES, null, null, null, null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch(loader.getId()){
		case NewsConstants.CATEGORY_LOADER_ID:
			categorySelector.changeSpinnerCursor(cursor);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch(loader.getId()){
		case NewsConstants.CATEGORY_LOADER_ID:
			categorySelector.changeSpinnerCursor(null);
			break;
		}
	}

	@Override
	public void initLoaderWithId(int loaderId) {
		getLoaderManager().initLoader(loaderId, null, this);
	}

}
